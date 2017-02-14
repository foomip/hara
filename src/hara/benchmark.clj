(ns hara.benchmark
  (:require [hara.benchmark
             [common :as common]
             [runtime :as runtime]
             [stat :as stat]
             [store :as store]]
            [hara.namespace.import :as ns]))

(ns/import hara.benchmark.common
           [benchmark]

           hara.benchmark.stat
           [stat]

           hara.benchmark.runtime
           [count-instances])

(def ^:dynamic *benchmarks* (atom []))

(comment
  (defn sleep [{:keys [mean variation]
                :or {mean 1000
                     variation 300}}]
    (doto (-> mean
              (- (quot variation 2))
              (+ (rand-int variation)))
      (Thread/sleep)))
  
  (def bench
    (benchmark {:function sleep
                :args {:mean 1000
                       :variation 800}
                :settings {:duration 100000
                           ;;:count 1000
                           :spawn {:interval 10     
                                   :max 100}}}))
  
  (:accumulate bench)
  (start-benchmark bench)
  (stop-benchmark bench)
  (count-instances bench)
  (->> (history bench :last 5)
       (reduce (fn [acc [_ duration]]
                 (+ acc duration))
               0))
  (accumulate bench :count)
  (accumulate bench :average)
  (:average bench))

(def accumulate-functions
  {:add     store/-add
   :count   store/-count
   :average store/-average})

(defn accumulate [{:keys [accumulate] :as benchmark} & [k & args]]
  (if-let [func (accumulate-functions k)]
    (apply func accumulate args)
    (println "OPTIONS: " (-> (keys accumulate-functions) sort))))

(def history-functions
  {:put     store/-put
   :last    store/-last
   :from    store/-from
   :until   store/-until
   :between store/-between
   :all     store/-all})

(defn history [{:keys [history] :as benchmark} & [k & args]]
  (if-let [func (history-functions k)]
    (apply func history args)
    (println "OPTIONS: " (-> (keys history-functions) sort))))

(defn start-benchmark [benchmark]
  (runtime/start-benchmark benchmark))

(defn pause-benchmark [benchmark]
  (swap! (:runtime benchmark)
         #(assoc % :running? false)))

(defn is-stopped? [benchmark]
  (or (-> @(:runtime benchmark) :running? false?)
      (if-let [thd (:main @(:runtime bench))]
        (future-done? thd))))

(defn stop-benchmark [benchmark]
  (swap! (:runtime benchmark)
         #(-> %
              (assoc :running? false)
              (assoc-in [:count :total] 0)
              (assoc-in [:duration :total] 0))))

(defn clear-benchmark [])

(defn list-all-benchmarks [])

(defn clear-all-benchmarks [])
