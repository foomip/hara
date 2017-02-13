(ns hara.io.benchmark
  (:require [hara.data.nested :as nested]
            [hara.concurrent.procedure :as procedure]
            [hara.io.benchmark
             ;;[runtime :as runtime]
             [store :as store]]
            [hara.time :as time]))

(def ^:dynamic *benchmarks* (atom []))

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
  (let [settings (nested/merge-nested *default-settings* settings)]
    (-> {:id (str (java.util.UUID/randomUUID))
         :function (procedure/procedure {:handler function} [:args :instance])
         :created (time/now {:type Long})
         :runtime (atom {:running? false
                         :since    nil
                         :count    {:current 0
                                    :total   0}
                         :duration {:current 0
                                    :total   0}})
         :settings settings
         :average (store/create-average-store settings)
         :history (store/create-history-store settings)}
        (merge (dissoc config :settings :function))
        (map->Benchmark))))

(comment
  
  (benchmark {:function sleep
              :args {:mean 500
                     :variation 300}
              :settings {:mode :default ;; :synchronous :core.async
                         :duration 10000
                         :count 1000
                         :spawn {:interval 2       
                                 :max 100}}})
  
  
  (map->Benchmark {:function sleep
                   :args {:mean 500
                          :variation 300}})
   
  (benchmark {:function     (fn [conf])
              :accumulate   (fn [bench proc])
              :config       {:url "http://www.google.com"}
              :duration     10000
              :ramp         {:interval  2       
                             :max   100}
              :framework    :core.async ;; :default
              :signal {:init (fn [bench conf])
                       :meta (fn [bench conf] m)
                       :data {:num-clients (fn [bench conf])
                              :cpu-bench   (fn [bench conf])
                              :memory-bench   (fn [bench conf])
                              :cpu-server  (fn [bench conf])
                              :memory-server  (fn [bench conf])
                              :avg-time    (fn [bench conf])}
                       :interval 500}})

  (def hello-world
    (proc/procedure {:name "hello"
                     :id :1
                     :mode :sync
                     :handler (fn []
                                (Thread/sleep 1000)
                                (println "Hello World"))}
                    [:mode]))
  
  (hello-world :sync)
 )




(defn start-benchmark [])

(defn pause-benchmark [])

(defn stop-benchmark [])

(defn clear-benchmark [])

(defn list-all-benchmarks [])

(defn clear-all-benchmarks [])
