(ns hara.io.benchmark.stat-test
  (:use hara.test)
  (:require [hara.io.benchmark.stat :refer :all]
            [hara.concurrent.procedure :as concurrent]))

^{:refer hara.io.benchmark.procedure.stat/stat :added "2.4"}
(comment "extensible method for pulling stats out of procedure"
  
  (defn sleep [{:keys [mean variation]
                :or {mean 1000
                     variation 300}}]
    (doto (-> mean
              (- variation)
              (+ (rand-int variation)))
      (Thread/sleep)))

  (def sleep-proc (concurrent/procedure {:handler sleep} [:args :instance]))
  (def sleep-inst (sleep-proc {} {:mode :sync}))

  (stat :start-time sleep-inst)
  => 1487022163132
  
  (stat :duration sleep-inst)
  => 934

  (stat :result sleep-inst)
  => 912)

(comment

  (./import)
  )
