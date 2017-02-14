(ns hara.concurrent.procedure.middleware
  (:require [hara.common.state :as state]
            [hara.concurrent.procedure.registry :as registry]
            [hara.data.map :as map]
            [hara.time :as time]))

(defn wrap-id
  "creates an id for the instance
 
   ((wrap-id (fn [inst args] (:id inst)))
    {:id-fn (constantly :hello)}
    [])
   => :hello"
  {:added "2.2"}
  [f]
  (fn [instance args]
    (let [instance (update-in instance [:id]
                              (fn [id] (or id
                                           ((:id-fn instance) instance))))]
      (f instance args))))

(defn wrap-mode
  "establishes how the computation is going to be run - `:sync` or `:async`
 
   (-> ((wrap-mode (fn [inst args] inst))
        {:mode :async}
        [])
       :thread
       deref)
   => (just {:mode :async,
            :result checks/promise?})"
  {:added "2.2"}
  [f]
  (fn [instance args]
    (let [instance  (update-in instance [:result]
                              (fn [result] (or result
                                               (promise))))]
      (case (:mode instance)
        :sync   (let [instance (assoc instance :thread (Thread/currentThread))]
                  (f instance args)
                  instance)
        :async  (let [thread (future (f instance args))]
                  (assoc instance :thread thread))))))

(defn wrap-instance
  "enables the entire instance to be passed in
 
   ((wrap-instance (fn [_ args] (first args)))
    {:arglist [:instance]}
    [nil])
   => {:arglist [:instance]}
   
   
   ((wrap-instance (fn [_ [inst]] (:params inst)))
    {:arglist [:instance] :params :b}
    [nil])
   => :b"
  {:added "2.2"}
  [f]
  (fn [{:keys [arglist] :as instance} args]
    (let [args (map (fn [arg desc]
                      (if (= desc :instance)
                        instance
                        arg))
                    args
                    arglist)]
      (f instance args))))

(defn wrap-registry
  "updates the registry of the procedure
 
   (->> ((wrap-registry (fn [inst _] inst))
         {:registry (data/registry) :name \"hello\" :id :1}
         [])
        (into {}))
   => (contains {:store clojure.lang.Atom})"
  {:added "2.2"}
  [f]
  (fn [{:keys [registry name id] :as instance} args]
    (registry/add-instance registry name id instance)
    (f instance args)
    (registry/remove-instance registry name id)))

(defn wrap-cached
  "adds a caching layer to the procedure
   
   (-> ((wrap-cached (fn [inst args] (Thread/sleep 1000000)))
        {:result (promise)
         :cached true
         :cache (state/update (data/cache)
                              assoc-in [\"hello\" :1 []]
                              {:result (atom {:success true :value 42})
                               :runtime (atom {:ended 0})})
         :name \"hello\"
        :id :1}
        [])
       deref)
   => {:success true, :value 42, :cached 0}"
  {:added "2.2"}
  [f]
  (fn [{:keys [cached cache overwrite name id] :as instance} args]
    (if-not cached
      (f instance args)
      (let [prev (get-in @cache [name id args])]
        (if (and prev (not overwrite))
          (deliver (:result instance)
                   (assoc @(:result prev)
                          :cached (-> prev :runtime deref :ended)))
          (let [current (f instance args)]
            (state/update cache assoc-in [name id args] instance)))))))

(defn wrap-timing
  "adds timing to the instance
   (->> ((wrap-timing (fn [inst args] (Thread/sleep 100)))
         {:mode :async
          :runtime (atom {})}
         [])
        ((juxt #(-> % :ended :long)
               #(-> % :started :long)))
        (apply -))
  => #(< 50 % 150)"
  {:added "2.2"}
  [f]
  (fn [instance args]
    (swap! (:runtime instance) assoc :started (time/now))
    (f instance args)
    (swap! (:runtime instance) assoc :ended (time/now))))

(defn wrap-timeout
  "adds timeout to the procedure
   
   @((procedure {:handler (fn [] (Thread/sleep 1000000))
                 :timeout 100} []))
   => (throws java.lang.InterruptedException)"
  {:added "2.2"}
  [f]
  (fn [{:keys [timeout mode] :as instance} args]
    (cond (nil? timeout)
          (f instance args)

          (and timeout (= :mode :sync))
          (throw (Exception. "Cannot perform timeout on synchronous mode"))

          :else
          (let [{:keys [registry name id thread] :as instance} (f instance args)]
            (future (Thread/sleep timeout)
                    (registry/kill registry name id)
                    (future-cancel thread))
            instance))))

(defn wrap-interrupt
  "interrupts the existing procedure
 
   (do ((procedure {:name \"hello\"
                    :id :1
                    :handler (fn [] (Thread/sleep 1000000000))} []))
 
       (map :id (registry/list-instances
                 registry/*default-registry*
                 \"hello\")))
   => '(:1)
 
   (do ((procedure {:name \"hello\"
                    :id :1
                    :interrupt true
                    :handler (fn [] :FAST)} []))
       (map :id (registry/list-instances
                registry/*default-registry*
                 \"hello\")))
   => ()"
  {:added "2.2"}
  [f]
  (fn [{:keys [registry name id interrupt] :as instance} args]
    (let [existing (get-in @registry [name id])]
      (cond (and interrupt existing)
            (do (registry/kill registry name id)
                (f instance args))

            existing
            existing

            :else
            (f instance args)))))

(defn wrap-callback
  [f]
  (fn [{:keys [callback] :as instance} args]
    (let [res (f instance args)]
      (if callback
        (callback instance))
      res)))
