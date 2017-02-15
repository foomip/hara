(ns hara.benchmark
  (:require [hara.benchmark
             [common :as common]
             [runtime :as runtime]
             [stat :as stat]
             [store :as store]]
            [hara.namespace.import :as ns]))

(ns/import hara.benchmark.stat
           [stat]

           hara.benchmark.runtime
           [count-instances])

(def ^:dynamic *benchmarks* (atom {}))

(def accumulate-functions
  {:add     store/-add
   :count   store/-count
   :average store/-average})

(defn accumulate
  "helper for accessing accumulated results
 
   (accumulate bench :add 1)
   => {:count 1, :total [1]}
 
   (accumulate bench :count)
   => 1
 
   (accumulate bench :average)
   => [1.0]
   "
  {:added "2.5"}
  [{:keys [accumulate] :as benchmark} & [k & args]]
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

(defn history
  "helper for accessing history
 
   (history bench :put [0 1])
   => [[0 1]]
   
   (history bench :last 1)
   => [[0 1]]
 
   (history bench :all)
   => [[0 1]]
   "
  {:added "2.5"}
  [{:keys [history] :as benchmark} & [k & args]]
  (if-let [func (history-functions k)]
    (apply func history args)
    (println "OPTIONS: " (-> (keys history-functions) sort))))

(defn benchmark
  "creates and registers a benchmark
   
   (benchmark {:function (fn [_] (Thread/sleep 100) 1)
               :args {}
               :settings {:duration 100000
                         :spawn {:interval 10     
                                  :max 100}}})"
  {:added "2.5"}
  [config]
  (let [benchmark (common/benchmark config)]
    (swap! *benchmarks*
           assoc 
           (:id benchmark)
           benchmark)
    benchmark))

(defn start-benchmark
  "starts a newly created or paused benchmark
 
   (start-benchmark bench)"
  {:added "2.5"}
  [benchmark]
  (runtime/start-benchmark benchmark))

(defn pause-benchmark
  "pauses a running benchmark
 
   (pause-benchmark bench)"
  {:added "2.5"}
  [benchmark]
  (swap! (:runtime benchmark)
         assoc :running? false))

(defn is-stopped?
  "checks if benchmark is stopped
 
   (is-stopped? bench)"
  {:added "2.5"}
  [benchmark]
  (or (-> @(:runtime benchmark) :running? false?)
      (if-let [thd (:main @(:runtime benchmark))]
        (future-done? thd))))

(defn stop-benchmark
  "stops a running benchmark
 
   (stop-benchmark bench)"
  {:added "2.5"}
  [benchmark]
  (swap! (:runtime benchmark)
         #(-> %
              (assoc :running? false)
              (assoc-in [:count :total] 0)
              (assoc-in [:duration :total] 0))))

(defn remove-benchmark
  "remove benchmark from the registry
 
   (remove-benchmark (:id bench))"
  {:added "2.5"}
  [id]
  (swap! *benchmarks*
         dissoc id))

(defn list-benchmarks
  "lists all benchmarks in the registry
 
   (list-benchmarks)"
  {:added "2.5"}
  []
  (sort-by :created (vals *benchmarks*)))

(defn clear-benchmarks
  "cleans all benchmarks in the registry
 
   (clear-benchmarks)"
  {:added "2.5"}
  []
  (reset! *benchmarks* {}))
