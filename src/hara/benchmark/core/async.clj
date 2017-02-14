(ns hara.benchmark.core.async
  (:require [hara.benchmark.core
             [common :as common]
             [runtime :as runtime]]
            [clojure.core.async :as async]))

(defmethod runtime/start-benchmark :core.async
  [{:keys [settings runtime] :as benchmark}]
  (runtime/init-benchmark benchmark)
  (runtime/update-benchmark-time benchmark)
  (let [thd (future
              (loop []
                (when (and (runtime/check-benchmark-time benchmark)
                           (runtime/check-benchmark-count benchmark)
                           (:running? @runtime))
                  (if (-> benchmark
                          :registry
                          deref
                          (get nil)
                          (count)
                          (< (-> settings :spawn :max))) 
                    (async/go
                      (runtime/start-single-sync benchmark))
                    (Thread/sleep (or (-> settings :spawn :interval) 1)))
                  (recur))))]
    (swap! runtime #(assoc % :main thd))
    benchmark))

(comment
  (def bench
    (doto (common/benchmark {:function (fn [m] (Thread/sleep 1000000) 1)
                             :args {}})
      (runtime/init-benchmark)
      (runtime/update-benchmark-time)))

  (dotimes [i 10000]
    (async/go
      (runtime/start-single-sync bench)))
  (:registry bench)
  (count (get @(:registry bench) nil)))
