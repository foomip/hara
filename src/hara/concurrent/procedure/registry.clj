(ns hara.concurrent.procedure.registry
  (:require [hara.common.state :as state]
            [hara.common.checks :refer [thread?]]
            [hara.data.map :as map]
            [hara.concurrent.procedure.data :as data]
            [hara.event :as event]))

(defonce ^:dynamic *default-registry* (data/registry))

(defn add-instance
  "adds something to the registry
 
   (->> (add-instance (data/registry) \"hello\" :1 {:a 1})
        (into {})
        :store
        deref)
   => {\"hello\" {:1 {:a 1}}}"
  {:added "2.2"}
  ([name id instance]
   (add-instance *default-registry* name id instance))
  ([registry name id instance]
   (state/update registry assoc-in [name id] instance)))

(defn remove-instance
  "removes something from the registry
 
   (-> (data/registry)
       (add-instance \"hello\" :1 {:a 1})
       (remove-instance \"hello\" :1)
       (into {})
       :store
       deref)
  => {}"
  {:added "2.2"}
  ([name id]
   (remove-instance *default-registry* name id))
  ([registry name id]
   (state/update registry map/dissoc-in [name id])))

(defn list-instances
  "lists all items in the registry
 
   (-> (data/registry)
       (add-instance  \"hello\" :1 {:a 1})
       (add-instance  \"hello\" :2 {:b 1})
       (list-instances \"hello\"))
   => [{:a 1} {:b 1}]"
  {:added "2.2"}
  ([name] (list-instances *default-registry* name))
  ([registry name]
   (vals (get @registry name))))

(defn get-instance
  "lists all items in the registry
 
   (-> (data/registry)
       (add-instance  \"hello\" :1 {:a 1})
       (add-instance \"hello\" :2 {:b 1})
       (get-instance \"hello\" :2))
   => {:b 1}"
  {:added "2.2"}
  ([name id] (get-instance *default-registry* name id))
  ([registry name id]
   (get-in @registry [name id])))

(defn kill
  "kills the running instance in the registry
 
   (-> (data/registry)
       (add-instance \"hello\" :1 {:thread (future (Thread/sleep 100000))})
       (kill \"hello\" :1))
   => true"
  {:added "2.2"}
  ([name id] (kill *default-registry* name id))
    ([registry name id]
     (if-let [{:keys [thread] :as instance} (get-instance registry name id)]
       (do (cond (future? thread)
                 (future-cancel thread)

                 (and (thread? thread)
                      (not= thread (Thread/currentThread)))
                 (do (.stop ^Thread thread)
                     (Thread/sleep 1)))
           (event/signal [:log {:msg "Killed Execution" :instance instance}])
           (remove-instance registry name id)
           true)
       false)))
