(ns hara.io.benchmark
  (:require [hara.concurrent.procedure :as proc]
            [hara.data.nested :as nested]
            [hara.time :as time]
            ;;[clojure.core.async :as async]
            ))

(def ^:dynamic *benchmarks* (atom []))

(def ^:dynamic *defaults*
  {:settings {:mode :default ;; :synchronous :core.async
              :history {:type :memory
                        :metrics [:start-time :result :duration]}
              :average {:type :memory
                        :metrics [:result :duration]}}})

(defmulti procedure-stat (fn [tag _] tag))

(defmethod procedure-stat :start-time
  [_ proc]
  (let [_     @proc
        runtime @(:runtime proc)]
    (-> runtime :started :long)))

(defmethod procedure-stat :duration
  [_ proc]
  (let [_     @proc
        runtime @(:runtime proc)]
    (- (-> runtime :ended :long)
       (-> runtime :started :long))))

(defmethod procedure-stat :result
  [_ proc]
  (let [_     @proc]
    (-> @(:result proc) :data)))

(defrecord Benchmark []
  Object
  (toString [m]
    (str "#benchmark" (into {} m))))

(defmethod print-method Benchmark
  [v w]
  (.write w (str v)))

(defn benchmark [{:keys [settings] :as config}]
  (-> {:id (str (java.util.UUID/randomUUID))
       :created (time/now)
       :state   (atom )
       :history (create-average)}
      (merge config)
      (map->Benchmark)))




(defn start-benchmark [])

(defn pause-benchmark [])

(defn stop-benchmark [])

(defn clear-benchmark [])

(defn list-all-benchmarks [])

(defn clear-all-benchmarks [])

(comment
  (defn sleep [{:keys [mean variation]
                :or {mean 1000
                     variation 300}}]
    (doto (-> mean
              (- variation)
              (+ (rand-int variation)))
      (Thread/sleep)))
  
  (def sleep-proc (proc/procedure {:handler sleep} [:args :instance]))
  
  (into {} (sleep-proc {} {:mode :sync}))
  
  
  (sleep {})
  
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
                          :variation 300}
                   
                   :settings {:mode :default ;; :synchronous :core.async
                              :duration 10000
                              :count 1000
                              :spawn {:interval 2       
                                      :max 100}
                              :metrics [:result :duration]
                              :history {:type :memory}
                              :average {:type :memory}}
                   
                   :state   (atom {:duration 10000})
                   :average (create-average :memory :settings)
                   :history (create-history :memory :settings)})

  (defn benchmark
    [{:keys [settings] :as config}])
  
  (defmulti create-average #(-> % :average :type))
  
  (defmethod create-average :default
    [settings]
    (-> settings
        (assoc-in [:average :type] :memory)
        (create-average)))
  
  (defmethod create-average :memory
    [settings]
    (atom {:count 0
           :total nil}))
  
  (defprotocol IAverageStore
    (-add [store result])
    (-count [store])
    (-average [store]))

  (extend-protocol IAverageStore
    clojure.lang.Atom
    (-add [store result]
      (let []))
    (-count [store])
    (-average [store]))
  
  (defmulti create-history identity)

  (defmethod create-average :memory
    [_]
    (atom {:count 0}))

  

  
  (defprotocol IHistoryStore
    (-put [store result])
    (-last [store])
    (-get [store id])
    (-all [store]))
  
   
  (loop [start (.getTime (java.util.Date.))
         ])

  (def inst (into {} (sleep-proc {} {:mode :sync})))

  (let [times @(:runtime inst)]
    [
     (- (-> times :ended :long)
         (-> times :started :long))])
  
 
  
  
  
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


