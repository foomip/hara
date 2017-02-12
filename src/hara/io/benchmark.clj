(ns hara.io.benchmark
 (:require [hara.concurrent.procedure :as proc]
               [clojure.core.async :as async]))

(def ^:dynamic *benchmarks* (atom []))

(defrecord Benchmark [])

(defn benchmark [config]
 (assoc (map->Benchmark config)
        :id (str (java.util.UUID/randomUUID))
        :created (java.util.Date.)
        :state   (atom {:average {:time 0}
                        :history {:timings []}})))

(defn start-benchmark [])

(defn pause-benchmark [])

(defn stop-benchmark [])

(defn clear-benchmark [])

(defn list-all-benchmarks [])

(defn clear-all-benchmarks [])

(comment
  (defn sleep [{:keys [mean variation]
                :or {mean 1000
                     variation 300}}]
    (doto (-> mean
              (- variation)
              (+ (rand-int variation)))
      (Thread/sleep)))

  (def sleep-proc (proc/procedure {:handler sleep} [:instance :arguments]))

  @(sleep-proc {:mode :sync} {})
  
  
  (sleep {})
  
  (benchmark {:function sleep
              :args {:mean 500
                     :variation 300}
              :settings {:mode ;; :default :synchronous :core.async
                         :duration 10000
                         :count 1000
                         :load {:interval 2       
                                :max 100}}})
  
  
  (benchmark {:function     (fn [conf])
              :accumulate   (fn [bench proc])
              :config       {:url "http://www.google.com"}
              :duration     10000
              :ramp         {:interval  2       
                             :max   100}
              :framework    :core.async ;; :default
              :signal {:init (fn [bench conf])
                       :meta (fn [bench conf] m)
                       :data {:num-clients (fn [bench conf])
                              :cpu-bench   (fn [bench conf])
                              :memory-bench   (fn [bench conf])
                              :cpu-server  (fn [bench conf])
                              :memory-server  (fn [bench conf])
                              :avg-time    (fn [bench conf])}
                       :interval 500}})

 )

(def hello-world
 (proc/procedure {:name "hello"
                  :id :1
                  :mode :sync
                  :handler (fn []
                             (Thread/sleep 1000)
                             (println "Hello World"))}
                 [:mode]))

(hello-world :sync)
