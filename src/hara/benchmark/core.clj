(ns hara.benchmark.core
  (:require [hara.data.nested :as nested]
            [hara.concurrent.procedure :as procedure]
            [hara.benchmark.core
             [runtime :as runtime]]
            [hara.time :as time]))

(def ^:dynamic *benchmarks* (atom []))

(comment
  
  (benchmark {:function     (fn [conf])
              :accumulate   (fn [bench proc])
              :config       {:url "http://www.google.com"}
              :duration     10000
              :ramp         {:interval  2       
                             :max   100}
              :framework    :core.async ;; :default
}))




(defn start-benchmark [])

(defn pause-benchmark [])

(defn stop-benchmark [])

(defn clear-benchmark [])

(defn list-all-benchmarks [])

(defn clear-all-benchmarks [])
