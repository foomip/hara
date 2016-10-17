(ns hara.concurrent.procedure.middleware-test
  (:use hara.test)
  (:require [hara.concurrent.procedure.middleware :refer :all]
            [hara.concurrent.procedure.data :as data]
            [hara.concurrent.procedure.registry :as registry]
            [hara.concurrent.procedure :refer [procedure]]
            [hara.common.checks :as checks]
            [hara.common.state :as state]))

^{:refer hara.concurrent.procedure.middleware/wrap-id :added "2.2"}
(fact "creates an id for the instance"

  ((wrap-id (fn [inst args] (:id inst)))
   {:id-fn (constantly :hello)}
   [])
  => :hello)

^{:refer hara.concurrent.procedure.middleware/wrap-mode :added "2.2"}
(fact "establishes how the computation is going to be run - `:sync` or `:async`"

  (-> ((wrap-mode (fn [inst args] inst))
       {:mode :async}
       [])
      :thread
      deref)
  => (just {:mode :async,
            :result checks/promise?}))

^{:refer hara.concurrent.procedure.middleware/wrap-instance :added "2.2"}
(fact "enables the entire instance to be passed in"

  ((wrap-instance (fn [_ args] (first args)))
   {:arglist [:instance]}
   [nil])
  => {:arglist [:instance]}
  
  
  ((wrap-instance (fn [_ [inst]] (:params inst)))
   {:arglist [:instance] :params :b}
   [nil])
  => :b)

^{:refer hara.concurrent.procedure.middleware/wrap-registry :added "2.2"}
(fact "updates the registry of the procedure"

  (->> ((wrap-registry (fn [inst _] inst))
        {:registry (data/registry) :name "hello" :id :1}
        [])
       (into {}))
  => (contains {:store clojure.lang.Atom}))

^{:refer hara.concurrent.procedure.middleware/wrap-cached :added "2.2"}
(fact "adds a caching layer to the procedure"
  
  (-> ((wrap-cached (fn [inst args] (Thread/sleep 1000000)))
       {:result (promise)
        :cached true
        :cache (state/update (data/cache)
                             assoc-in ["hello" :1 []]
                             {:result (atom {:success true :value 42})
                              :runtime (atom {:ended 0})})
        :name "hello"
        :id :1}
       [])
      deref)
  => {:success true, :value 42, :cached 0})

^{:refer hara.concurrent.procedure.middleware/wrap-timing :added "2.2"}
(fact "adds timing to the instance"
  (->> ((wrap-timing (fn [inst args] (Thread/sleep 100)))
        {:mode :async
         :runtime (atom {})}
        [])
       ((juxt #(-> % :ended :long)
              #(-> % :started :long)))
       (apply -))
  => #(< 50 % 150))

^{:refer hara.concurrent.procedure.middleware/wrap-timeout :added "2.2"}
(fact "adds timeout to the procedure"
  
  @((procedure {:handler (fn [] (Thread/sleep 1000000))
                :timeout 100} []))
  => (throws java.lang.InterruptedException))

^{:refer hara.concurrent.procedure.middleware/wrap-interrupt :added "2.2"}
(fact "interrupts the existing procedure"

  (do ((procedure {:name "hello"
                   :id :1
                   :handler (fn [] (Thread/sleep 1000000000))} []))

      (map :id (registry/list-instances
                registry/*default-registry*
                "hello")))
  => '(:1)

  (do ((procedure {:name "hello"
                   :id :1
                   :interrupt true
                   :handler (fn [] :FAST)} []))
      (map :id (registry/list-instances
                registry/*default-registry*
                "hello")))
  => ())
