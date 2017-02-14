(ns hara.benchmark.runtime
  (:require [hara.time :as time]
            [hara.benchmark
             [common :as common]
             [stat :as stat]
             [store :as store]]))

(defn init
  "initializes the runtime entry
 
   (init {} 0)
   => {:since 0
       :running? true
       :duration {:current 0}
       :count {:current 0}}"
  {:added "2.4"}
  [runtime curr]
  (-> runtime
      (assoc :since curr :running? true)
      (assoc-in [:duration :current] 0)
      (assoc-in [:count    :current] 0)))

(defn init-benchmark
  "initializes the benchmark
 
   (-> (common/benchmark {})
       (init-benchmark 0))
   => {:running? true,
       :since 0,
       :count {:current 0, :total 0},
       :duration {:current 0, :total 0}}"
  {:added "2.4"}
  ([benchmark]
   (init-benchmark  benchmark (time/now {:type Long})))
  ([benchmark curr]
   (swap! (:runtime benchmark)
          #(init % curr))))

(defn update-time
  "updates the runtime duration entry
   
   (update-time {:since 10
                 :duration {:current 0, :total 10}}
                20)
   => {:since 10,
       :duration {:current 10, :total 20}}"
  {:added "2.4"}
  [runtime curr]
  (let [{:keys [since duration]} runtime
        past-duration (:current duration)
        new-duration  (- curr since)]
    (-> runtime
        (assoc-in [:duration :current]
                  new-duration)
        (update-in [:duration :total]
                   #(-> %
                        (+ new-duration)
                        (- past-duration))))))

(defn check-benchmark-time
  "checks to see if the current duration is within limits
 
   (-> (common/benchmark {:settings {:duration 100}})
       (doto
           (init-benchmark))
       (check-benchmark-time))
   => true"
  {:added "2.4"}
  [{:keys [settings runtime] :as benchmark}]
    (if-let [duration (:duration settings)]
      (< (-> @runtime :duration :total)
         duration)
      true))

(defn check-benchmark-count
  "checks to see if the current count is within limits
 
   (-> (common/benchmark {:settings {:duration 100}})
      (doto
           (init-benchmark))
       (check-benchmark-count))"
  {:added "2.4"}
  [{:keys [settings runtime] :as benchmark}]
    (if-let [count (:count settings)]
      (< (-> @runtime :count :total)
         count)
      true))

(defn update-count
  "updates the runtime count entry
   
   (update-count {:count {:current 0 :total 10}})
   => {:count {:current 1, :total 11}}"
  {:added "2.4"}
  [runtime]
  (-> runtime
      (update-in [:count :current] inc)
      (update-in [:count :total] inc)))

(defn update-benchmark-time
  "updates the benchmark runtime duration entry
   (-> (common/benchmark {})
       (doto
           (init-benchmark 10))
       (update-benchmark-time 110))
   => {:running? true,
       :since 10,
       :count {:current 0, :total 0},
      :duration {:current 100, :total 100}}"
  {:added "2.4"}
  ([benchmark]
   (update-benchmark-time benchmark (time/now {:type Long})))
  ([{:keys [runtime] :as benchmark} curr]
   (swap! (:runtime benchmark)
          #(update-time % curr))))

(defn update-benchmark-count
  "updates the benchmark runtime count entry
 
   (-> (common/benchmark {})
       (update-benchmark-count)
       :count)
   => {:current 1, :total 1}"
  {:added "2.4"}
  ([{:keys [runtime] :as benchmark}]
   (swap! (:runtime benchmark) update-count)))

(defn update-stats
  "updates the benchmark average and history entries
 
   (let [bench (common/benchmark {:function (fn [_] (Thread/sleep 100) 1)
                                  :args {}})
         proc (:function bench)]
     (update-stats bench (proc {} {:mode :sync})))
   => [[[1487050261356 1 105]]
       {:count 1, :total [1 105]}]"
  {:added "2.4"}
  [{:keys [settings] :as benchmark} proc]
  [(->> settings
         :history
         :metrics
         (mapv #(stat/stat % proc))
         (store/-put (:history benchmark)))
   
   (->> settings
        :average
        :metrics
        (mapv #(stat/stat % proc))
        (store/-add (:average benchmark)))])

(defn start-single-sync
  "starts a single sync instance of the benchmarking function"
  {:added "2.4"}
  [{:keys [function args] :as benchmark}]
  (let [proc (function args {:mode :sync})
        _    @proc]
    (doto benchmark
      (update-benchmark-time)
      (update-benchmark-count)
      (update-stats proc))))

(defn start-single-async
  "starts a single async instance of the benchmarking function"
  {:added "2.4"}
  [{:keys [function args] :as benchmark}]
  (let [proc (function args {:mode :async
                             :callback (fn [proc]
                                         (update-stats benchmark proc))})]
    (doto benchmark
      (update-benchmark-time)
      (update-benchmark-count))))

(defmulti start-benchmark
  "starts the benchmarking process"
  {:added "2.4"}
  (fn [benchmark]
    (-> benchmark :settings :mode)))

(defmethod start-benchmark :synchronous
  [{:keys [settings runtime] :as benchmark}]
  (init-benchmark benchmark)
  (update-benchmark-time benchmark)
  (let [thd (future (loop []
                      (when (and (check-benchmark-time benchmark)
                                 (check-benchmark-count benchmark)
                                 (:running? @runtime))
                        (start-single-sync benchmark)
                        (if-let [interval (-> settings :spawn :interval)]
                          (Thread/sleep interval))
                        (recur))))]
    (swap! runtime #(assoc % :main thd))
    benchmark))

(defmethod start-benchmark :thread
  [{:keys [settings runtime] :as benchmark}]
  (init-benchmark benchmark)
  (update-benchmark-time benchmark)
  (let [thd (future (loop []
                      (when (and (check-benchmark-time benchmark)
                                 (check-benchmark-count benchmark)
                                 (:running? @runtime))
                        (if (-> benchmark
                                :registry
                                deref
                                (get nil)
                                (count)
                                (< (-> settings :spawn :max))) 
                          (start-single-async benchmark))
                        (Thread/sleep (or (-> settings :spawn :interval) 1))
                        (recur))))]
    (swap! runtime #(assoc % :main thd))
    benchmark))
