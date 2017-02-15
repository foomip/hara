(ns hara.io.profiler-test
  (:use hara.test)
  (:require [hara.io.profiler :refer :all]
            [hara.benchmark :as benchmark]))

^{:refer hara.io.profiler/profiler :added "2.4"}
(fact "creates a profiler entry"

  (defn sleep [{:keys [mean variation]
                :or {mean 1000
                     variation 300}}]
    (doto (-> mean
              (- variation)
              (+ (rand-int variation)))
      (Thread/sleep)))
  
  (def p1 (profiler
           {:config   {:function sleep
                       :args {:mean 1000
                              :variation 800}
                       :settings {:duration 10000
                                  :spawn {:interval 100     
                                          :max 100}}}
            :constructor benchmark/benchmark
            :init        nil
            :start       benchmark/start-benchmark
            :stop        benchmark/stop-benchmark
            :stopped?    benchmark/is-stopped?}
           
           {:interval 1
            :signals [{:name "client-count"
                       :poll (fn [benchmark]
                               (benchmark/count-instances benchmark))}
                      {:name "count"
                       :poll (fn [benchmark]
                               (benchmark/accumulate benchmark :count))}
                      {:name "average"
                       :poll (fn [benchmark]
                               (let [res (benchmark/accumulate benchmark :average)]
                                 (first res)))}
                      {:name "moving-10"
                       :poll (fn [benchmark]
                               (->> (benchmark/history benchmark :last 10)
                                    (reduce (fn [acc [_ duration]]
                                              (+ acc duration))
                                            0)
                                    (#(long (/ % 10)))))}
                      {:name "moving-3"
                       :poll (fn [benchmark]
                               (->> (benchmark/history benchmark :last 3)
                                    (reduce (fn [acc [_ duration]]
                                              (+ acc duration))
                                            0)
                                    (#(long (/ % 3)))))}]}))

  (start-profiler p1)
  (stop-profiler p1))

^{:refer hara.io.profiler/signals :added "2.4"}
(fact "returns the collected signals form the profiler")

^{:refer hara.io.profiler/is-active? :added "2.4"}
(fact "checks if the profiler is active")

^{:refer hara.io.profiler/create-entry :added "2.4"}
(fact "single io.scheduler entry from a profiler entry")

^{:refer hara.io.profiler/create-entries :added "2.4"}
(fact "io.scheduler entries from profiler entries")

^{:refer hara.io.profiler/start-profiler :added "2.4"}
(fact "starts a profiler to collect information")

^{:refer hara.io.profiler/stop-profiler :added "2.4"}
(fact "stops a profiler manually")

(comment
  (./import)

  (require '[lucid.package :as package]
           '[lucid.distribute :as distribute]
           '[hara.io.project :as project])

  (project/project)
  (package/install-project)
  
  )
