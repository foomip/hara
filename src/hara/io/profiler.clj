(ns hara.io.profiler
  (:require [hara.io.scheduler :as scheduler]))

(def ^:dynamic *intervals* #{1 2 3 4 5 6 10 12 15 20 30 60})

(defn profiler [controls signals])

(defn start-profiler [profiler])

(defn stop-profiler [profiler])

(defn is-active? [profiler])

(comment
  (require '[hara.benchmark :as benchmark])
  
  (defn sleep [{:keys [mean variation]
                :or {mean 1000
                     variation 300}}]
    (doto (-> mean
              (- variation)
              (+ (rand-int variation)))
      (Thread/sleep)))
  (profiler
   {:instance (benchmark/benchmark
               {:function sleep
                :args {:mean 1000
                       :variation 800}
                :settings {:duration 100000
                           :spawn {:interval 10     
                                   :max 100}}})
    :start    benchmark/start-benchmark
    :stop     benchmark/stop-benchmark
    :stopped? benchmark/is-stopped?}
   
   {:interval 1
    :signals [{:name "client-count"
               :poll (fn [benchmark]
                       (benchmark/count-instances benchmark))
               :interval 2}
              {:name "count"
               :poll (fn [benchmark]
                       (benchmark/accumulate benchmark :count))}
              {:name "moving-10"
               :poll (fn [benchmark]
                       (->> (benchmark/history benchmark :last 10)
                            (reduce (fn [acc [_ duration]]
                                      (+ acc duration))
                                    0)
                            (/ 10)))}
              {:name "moving-3"
               :poll (fn [benchmark]
                       (->> (benchmark/history benchmark :last 3)
                            (reduce (fn [acc [_ duration]]
                                      (+ acc duration))
                                    0)
                            (/ 3)))}]})

  (def acc (atom []))
  (def bench (benchmark/benchmark
              {:function sleep
               :args {:mean 1000
                      :variation 800}
               :settings {:duration 10000
                          :spawn {:interval 10     
                                  :max 100}}}))
  (def sch2 (scheduler/scheduler
             {:client-count
              {:handler  (fn [t] (swap! acc conj 
                                        [t (benchmark/count-instances bench)]))
               :schedule "/2 * * * * * *"}}
             {}
             {:clock {:type "java.lang.Long"}}))
  (future-done? (:main @(:runtime bench)))
  (keys @(:runtime bench))
  (:running? @(:runtime bench))
  (benchmark/start-benchmark bench)
  (benchmark/is-stopped? bench)
  (scheduler/start! sch2)
  (scheduler/stop! sch2)
  
  (def sch2 (scheduler/scheduler
             {:hello {:handler  (fn [t] (println t))
                      :schedule "/2 * * * * * *"}}
             {}
             {:clock {:type "java.lang.Long"}}))
  
  (scheduler/start! sch2)
  (scheduler/stop! sch2))
