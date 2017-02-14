(ns hara.benchmark
  (:require [hara.benchmark
             [async :as async]
             [common :as common]
             [runtime :as runtime]]))

(def ^:dynamic *benchmarks* (atom []))

(comment
  (defn sleep [{:keys [mean variation]
                :or {mean 1000
                     variation 300}}]
    (doto (-> mean
              (- variation)
              (+ (rand-int variation)))
      (Thread/sleep)))

  (def bench
    (common/benchmark {:function sleep
                       :args {:mean 1000
                              :variation 50}
                       :settings {:mode :core.async
                                  :duration 100000
                                  :count 1000
                                  :spawn {:interval 200      
                                          :max 100}}}))
  
  (runtime/start-benchmark bench)
  (count (get (:registry bench) nil)))

(defn start-benchmark [])

(defn pause-benchmark [])

(defn stop-benchmark [])

(defn clear-benchmark [])

(defn list-all-benchmarks [])

(defn clear-all-benchmarks [])
