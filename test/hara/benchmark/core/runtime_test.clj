(ns hara.benchmark.core.runtime-test
  (:use hara.test)
  (:require [hara.benchmark.core
             [common :as common]
             [runtime :refer :all]]
            [hara.concurrent.procedure :as procedure]))

^{:refer hara.benchmark.core.runtime/init :added "2.4"}
(fact "initializes the runtime entry"

  (init {} 0)
  => {:since 0
      :running? true
      :duration {:current 0}
      :count {:current 0}})

^{:refer hara.benchmark.core.runtime/init-benchmark :added "2.4"}
(fact "initializes the benchmark"

  (-> (common/benchmark {})
      (init-benchmark 0))
  => {:running? true,
      :since 0,
      :count {:current 0, :total 0},
      :duration {:current 0, :total 0}})

^{:refer hara.benchmark.core.runtime/update-time :added "2.4"}
(fact "updates the runtime duration entry"
  
  (update-time {:since 10
                :duration {:current 0, :total 10}}
               20)
  => {:since 10,
      :duration {:current 10, :total 20}})

^{:refer hara.benchmark.core.runtime/check-benchmark-time :added "2.4"}
(fact "checks to see if the current duration is within limits"

  (-> (common/benchmark {:settings {:duration 100}})
      (doto
          (init-benchmark))
      (check-benchmark-time))
  => true)

^{:refer hara.benchmark.core.runtime/check-benchmark-count :added "2.4"}
(fact "checks to see if the current count is within limits"

  (-> (common/benchmark {:settings {:duration 100}})
      (doto
          (init-benchmark))
      (check-benchmark-count)))

^{:refer hara.benchmark.core.runtime/update-count :added "2.4"}
(fact "updates the runtime count entry"
  
  (update-count {:count {:current 0 :total 10}})
  => {:count {:current 1, :total 11}})

^{:refer hara.benchmark.core.runtime/update-benchmark-count :added "2.4"}
(fact "updates the benchmark runtime count entry"

  (-> (common/benchmark {})
      (update-benchmark-count)
      :count)
  => {:current 1, :total 1})

^{:refer hara.benchmark.core.runtime/update-benchmark-time :added "2.4"}
(fact "updates the benchmark runtime duration entry"
  (-> (common/benchmark {})
      (doto
          (init-benchmark 10))
      (update-benchmark-time 110))
  => {:running? true,
      :since 10,
      :count {:current 0, :total 0},
      :duration {:current 100, :total 100}})

^{:refer hara.benchmark.core.runtime/update-stats :added "2.4"}
(comment "updates the benchmark average and history entries"

  (let [bench (common/benchmark {:function (fn [_] (Thread/sleep 100) 1)
                                 :args {}})
        proc (:function bench)]
    (update-stats bench (proc {} {:mode :sync})))
  => [[[1487050261356 1 105]]
      {:count 1, :total [1 105]}])

^{:refer hara.benchmark.core.runtime/start-single-sync :added "2.4"}
(comment "starts a single sync instance of the benchmarking function")

^{:refer hara.benchmark.core.runtime/start-single-async :added "2.4"}
(comment "starts a single async instance of the benchmarking function")

^{:refer hara.benchmark.core.runtime/start-benchmark :added "2.4"}
(comment "starts the benchmarking process")

(comment
  (./import)

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
                       :settings {:mode :default
                                  :duration 100000
                                  ;;:count 1000
                                  :spawn {:interval 10       
                                          :max 50}}}))
  
  (start-benchmark bench)
  @(:main @(:runtime bench))
  (:average bench)
  (init-benchmark-time bench)
  (update-benchmark-time bench)
  (start-single-async bench)
  
  (let [thd ((:function bench) (:args bench))]
    @thd
    [(stat/stat :start-time thd)
     (stat/stat :duration thd)
     (stat/stat :result thd)])
  
  (:history bench)
  (:average bench)

  (start-benchmark bench)
  (:main @(:runtime bench))
  
  (def bench
    (common/benchmark {:function sleep
                       :args {:mean 1000
                              :variation 50}
                       :settings {:mode :synchronous
                                  :duration 10000
                                  :count 1000
                                  :spawn {:interval 2       
                                          :max 100}}}))
  
  (start-benchmark bench)
  (future )
  @(:main @(:runtime bench))
  (count (get @(:registry bench) nil))
  (:settings bench)
  
  {:mode :synchronous, :history {:type :memory, :metrics [:start-time :result :duration]}, :average {:type :memory, :metrics [:result :duration]}, :duration 10000, :count 1000, :spawn {:interval 2, :max 100}}
  (:id (start-single bench))
  (:average bench)
  (init-benchmark-time bench)
  (update-benchmark-time bench)
  
  (check-benchmark-time bench)
  (check-benchmark-count bench)
  (start-benchmark bench)
  (store/-average (:average bench))
  (store/-all (:history bench))
  
  )
