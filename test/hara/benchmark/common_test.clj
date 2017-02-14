(ns hara.benchmark.common-test
  (:use hara.test)
  (:require [hara.benchmark.common :refer :all]))

^{:refer hara.benchmark.common/benchmark :added "2.4"}
(fact "creates a record representing a benchmark"

  (benchmark {:function (fn [{:keys [sleep return]}]
                          (Thread/sleep sleep) return)
              :args {:sleep 100
                     :return 10}
              :settings {:duration 100000
                         :count 1000
                         :spawn {:interval 1      
                                 :max 10000}}}))

(comment
  (./import)
  )
