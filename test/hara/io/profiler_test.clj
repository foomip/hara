(ns hara.io.profiler-test
  (:use hara.test)
  (:require [hara.io.profiler :refer :all]
            [hara.benchmark :as benchmark]))

(defn sleep [{:keys [mean variation]
                :or {mean 1000
                     variation 300}}]
    (doto (-> mean
              (- variation)
              (+ (rand-int variation)))
      (Thread/sleep)))

(def control
  {:config  {:function sleep
             :args {:mean 1000
                    :variation 800}
             :settings {:duration 10000
                        :spawn {:interval 100     
                                :max 100}}}
   :constructor benchmark/benchmark
   :init        nil
   :start       benchmark/start-benchmark
   :stop        benchmark/stop-benchmark
   :pause       benchmark/pause-benchmark
   :stopped?    benchmark/is-stopped?})

(def input
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
                           (#(long (/ % 3)))))}]})

^{:refer hara.io.profiler/profiler :added "2.4"}
(comment "creates a profiler entry"
  
  (def p1 (profiler control input))
  
  (start-profiler p1)
  (pause-profiler p1)
  )

^{:refer hara.io.profiler/signals :added "2.4"}
(comment "returns the collected signals form the profiler"

  (signals p1)
  => {"client-count" [[1487322396000 5] [1487322395000 6] [1487322394000 2] [1487322393000 1]]
      "count" [[1487322396000 16] [1487322395000 5] [1487322394000 0] [1487322393000 0]],
      "average" [[1487322396000 601.4375] [1487322395000 596.2] [1487322394000 nil] [1487322393000 nil]],
      "moving-10" [[1487322396000 453] [1487322395000 298] [1487322394000 0] [1487322393000 0]],
      "moving-3" [[1487322396000 565] [1487322395000 647] [1487322394000 0] [1487322393000 0]]})

^{:refer hara.io.profiler/is-active? :added "2.4"}
(fact "checks if the profiler is active"

  (is-active? p1)
  => true)

^{:refer hara.io.profiler/create-entry :added "2.4"}
(comment "single io.scheduler entry from a profiler entry"

  (create-entry (:instance @(:state p1))
                (:state p1)
                {:name "count"
                 :poll (fn [_] 1)
                 :interval 1})
  => (contains {:id :count,
                :handler fn?
                :schedule "/1 * * * * * *"}))

^{:refer hara.io.profiler/create-entries :added "2.4"}
(comment "io.scheduler entries from profiler entries"

  (create-entries (:instance @(:state p1))
                  (:state p1)
                  input)
  => (contains-in
      {:client-count {:id :client-count,
                      :handler fn?
                      :schedule "/1 * * * * * *"},
       :count        {:id :count,
                      :handler fn?
                      :schedule "/1 * * * * * *"},
       :average      {:id :average,
                      :handler fn?
                      :schedule "/1 * * * * * *"},
       :moving-10    {:id :moving-10,
                      :handler fn?
                      :schedule "/1 * * * * * *"},
       :moving-3     {:id :moving-3,
                      :handler fn?
                      :schedule "/1 * * * * * *"}}))

^{:refer hara.io.profiler/start-profiler :added "2.4"}
(comment "starts a profiler to collect information"

  (start-profiler p1))

^{:refer hara.io.profiler/pause-profiler :added "2.4"}
(comment "pauses a profiler"

  (pause-profiler p1))

^{:refer hara.io.profiler/stop-profiler :added "2.4"}
(comment "stops a profiler"

  (stop-profiler p1))

^{:refer hara.io.profiler/profile :added "2.4"}
(comment "profiles an input, waiting until all results finish before returning the result"

  (profile p1)

  (profile control input))


(comment
  (./import)

  (require '[lucid.package :as package]
           '[lucid.distribute :as distribute]
           '[hara.io.project :as project])

  (project/project)
  (package/install-project)
  
  )
