(ns hara.benchmark.common
  (:require [hara.time :as time]
            [hara.data.nested :as nested]
            [hara.benchmark.store :as store]
            [hara.concurrent.procedure :as procedure]
            [hara.concurrent.procedure.data :as data]))

(def ^:dynamic *default-settings*
  {:mode    :thread
   :history {:type :memory
             :metrics [:start-time :result :duration]}
   :accumulate {:type :memory
                :metrics [:result :duration]}})

(defrecord Benchmark []
  Object
  (toString [m]
    (str "#benchmark" (-> (select-keys m [:runtime :function])
                          (into {})))))

(defmethod print-method Benchmark
  [v w]
  (.write w (str v)))

(defn benchmark
  "creates a record representing a benchmark
 
   (benchmark {:function (fn [{:keys [sleep return]}]
                          (Thread/sleep sleep) return)
               :args {:sleep 100
                      :return 10}
               :settings {:duration 100000
                          :count 1000
                          :spawn {:interval 1      
                                  :max 10000}}})"
  {:added "2.4"} [{:keys [function args settings] :as config}]
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
         :accumulate (store/create-accumulate-store settings)
         :history (store/create-history-store settings)}
        (merge (dissoc config :settings :function))
        (map->Benchmark))))
