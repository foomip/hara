(ns hara.io.benchmark.runtime
  (:require [hara.time :as time]))

(defmulti start-benchmark
  (fn [benchmark]
    (-> benchmark :settings :mode)))

(defn reset-time
  ([curr runtime]
   (-> runtime
       (assoc :since curr)
       (assoc-in [:duration :current] 0))))

(defn reset-benchmark-time
  ([benchmark]
   (reset-benchmark-time (time/now {:type Long}) benchmark))
  ([curr benchmark]
   (swap! (:runtime bench)
          #(reset-time curr %))))

(defn update-time
  ([curr runtime]
   (let [{:keys [since duration]} runtime
         past-duration (:current duration)
         _ (prn curr since)
         new-duration  (- curr since)]
     (-> runtime
         (assoc-in [:duration :current]
                   new-duration)
         (update-in [:duration :total]
                    #(-> %
                         (+ new-duration)
                         (- past-duration)))))))

(defn update-benchmark-time
  ([benchmark]
   (update-benchmark-time (time/now {:type Long}) benchmark))
  ([curr {:keys [runtime] :as benchmark}]
   (swap! (:runtime bench)
          #(update-time curr %))))

(defmethod start-benchmark :default
  [benchmark]
  )

(defmethod start-benchmark :synchronous
  [{:keys [settings runtime] :as benchmark}]
  (let [start-time (time/now {:type Long})
        _ (swap! runtime
                 (fn [m] (-> (assoc :running? true
                                    :since start-time))))]
    #_(loop [total-count 
           total-duration ])))

(comment
  (require '[hara.io.benchmark :as data])
  (defn sleep [{:keys [mean variation]
                :or {mean 1000
                     variation 300}}]
    (doto (-> mean
              (- variation)
              (+ (rand-int variation)))
      (Thread/sleep)))
  
  (def bench
    (data/benchmark {:function sleep
                     :args {:mean 100
                            :variation 50}
                     :settings {:duration 10000
                                :count 1000
                                :spawn {:interval 2       
                                        :max 100}}}))
  (reset-benchmark-time bench)
  (update-benchmark-time bench)
  (start-benchmark bench)
  

  )
