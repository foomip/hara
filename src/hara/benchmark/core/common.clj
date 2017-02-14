(ns hara.benchmark.core.common
  (:require [hara.time :as time]
            [hara.data.nested :as nested]
            [hara.benchmark.core.store :as store]
            [hara.concurrent.procedure :as procedure]
            [hara.concurrent.procedure.data :as data]))

(def ^:dynamic *default-settings*
  {:mode :default ;; :synchronous :core.async
   :history {:type :memory
             :metrics [:start-time :result :duration]}
   :average {:type :memory
             :metrics [:result :duration]}})

(defrecord Benchmark []
  Object
  (toString [m]
    (str "#benchmark" (-> (select-keys m [:runtime :function])
                          (into {})))))

(defmethod print-method Benchmark
  [v w]
  (.write w (str v)))

(defn benchmark [{:keys [function args settings] :as config}]
  (let [settings (nested/merge-nested *default-settings* settings)
        registry (data/registry)]
    (-> {:id (str (java.util.UUID/randomUUID))
         :function (procedure/procedure {:handler function
                                         :registry registry}
                                        [:args :instance])
         :created (time/now {:type Long})
         :runtime (atom {:running? false
                         :since    nil
                         :count    {:current 0
                                    :total   0}
                         :duration {:current 0
                                    :total   0}})
         :registry registry
         :settings settings
         :average (store/create-average-store settings)
         :history (store/create-history-store settings)}
        (merge (dissoc config :settings :function))
        (map->Benchmark))))


(comment

  (def reg (data/registry))
  (def proc (procedure/procedure {:handler (fn [] (Thread/sleep 5000))
                                  :registry reg}
                                 []))

  (proc)
  reg)
