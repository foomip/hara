(ns hara.concurrent.procedure.registry-test
  (:use hara.test)
  (:require [hara.concurrent.procedure.registry :refer :all]
            [hara.concurrent.procedure.data :as data]
            [hara.common.checks :as checks]))

^{:refer hara.concurrent.procedure.registry/add-instance :added "2.2"}
(fact "adds something to the registry"

  (->> (add-instance (data/registry) "hello" :1 {:a 1})
       (into {})
       :store
       deref)
  => {"hello" {:1 {:a 1}}})


^{:refer hara.concurrent.procedure.registry/remove-instance :added "2.2"}
(fact "removes something from the registry"

  (-> (data/registry)
      (add-instance "hello" :1 {:a 1})
      (remove-instance "hello" :1)
      (into {})
      :store
      deref)
  => {})

^{:refer hara.concurrent.procedure.registry/list-instances :added "2.2"}
(fact "lists all items in the registry"

  (-> (data/registry)
      (add-instance  "hello" :1 {:a 1})
      (add-instance  "hello" :2 {:b 1})
      (list-instances "hello"))
  => [{:a 1} {:b 1}])

^{:refer hara.concurrent.procedure.registry/get-instance :added "2.2"}
(fact "lists all items in the registry"

  (-> (data/registry)
      (add-instance  "hello" :1 {:a 1})
      (add-instance "hello" :2 {:b 1})
      (get-instance "hello" :2))
  => {:b 1})

^{:refer hara.concurrent.procedure.registry/kill :added "2.2"}
(fact "kills the running instance in the registry"

  (-> (data/registry)
      (add-instance "hello" :1 {:thread (future (Thread/sleep 100000))})
      (kill "hello" :1))
  => true)
