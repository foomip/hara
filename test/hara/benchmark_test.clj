(ns hara.benchmark-test
  (:use hara.test)
  (:require [hara.benchmark :refer :all]
            [hara.benchmark.common :as common]
            [hara.benchmark.runtime :as runtime]))

(def bench
  (doto (common/benchmark
         {:function (fn [_] (Thread/sleep 100) 1)
          :args {}
          :settings {:duration 100000
                     :spawn {:interval 10     
                             :max 100}}})
    (runtime/init-benchmark)))

^{:refer hara.benchmark/accumulate :added "2.5"}
(fact "helper for accessing accumulated results"

  (accumulate bench :add 1)
  => {:count 1, :total [1]}

  (accumulate bench :count)
  => 1

  (accumulate bench :average)
  => [1.0]
  )

^{:refer hara.benchmark/history :added "2.5"}
(comment "helper for accessing history"

  (history bench :put [0 1])
  => [[0 1]]
  
  (history bench :last 1)
  => [[0 1]]

  (history bench :all)
  => [[0 1]]
  )

^{:refer hara.benchmark/benchmark :added "2.5"}
(comment "creates and registers a benchmark"
  
  (benchmark {:function (fn [_] (Thread/sleep 100) 1)
              :args {}
              :settings {:duration 100000
                         :spawn {:interval 10     
                                 :max 100}}}))

^{:refer hara.benchmark/start-benchmark :added "2.5"}
(comment "starts a newly created or paused benchmark"

  (start-benchmark bench))

^{:refer hara.benchmark/pause-benchmark :added "2.5"}
(comment "pauses a running benchmark"

  (pause-benchmark bench))

^{:refer hara.benchmark/is-stopped? :added "2.5"}
(comment "checks if benchmark is stopped"

  (is-stopped? bench))

^{:refer hara.benchmark/stop-benchmark :added "2.5"}
(comment "stops a running benchmark"

  (stop-benchmark bench))

^{:refer hara.benchmark/remove-benchmark :added "2.5"}
(comment "remove benchmark from the registry"

  (remove-benchmark (:id bench)))

^{:refer hara.benchmark/list-benchmarks :added "2.5"}
(comment "lists all benchmarks in the registry"

  (list-benchmarks))

^{:refer hara.benchmark/clear-benchmarks :added "2.5"}
(comment "cleans all benchmarks in the registry"

  (clear-benchmarks))

(comment

  (./import))
