(ns hara.benchmark.core.async
  (:require [hara.benchmark.core.runtime :as runtime]
            [clojure.core.async :as async]))

(defmethod runtime/start-benchmark :core.async
  [{:keys [settings runtime] :as benchmark}]
  (runtime/init-benchmark benchmark)
  (runtime/update-benchmark-time benchmark)
  (let [thd (future
              (loop []
                (when (and (runtime/check-benchmark-time benchmark)
                           (runtime/check-benchmark-count benchmark))
                  (if (-> benchmark
                          :registry
                          deref
                          (get nil)
                          (count)
                          (< (-> settings :spawn :max))) 
                    (async/go
                      (runtime/start-single-sync benchmark)
                      (async/<! (async/timeout (or (-> settings :spawn :interval) 1)))))
                  (recur))))]
    (swap! runtime #(assoc % :main thd))
    benchmark))
