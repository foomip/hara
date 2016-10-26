(ns hara.object.read-test
  (:use hara.test)
  (:require [hara.object.read :as read]
            [hara.object.write :as write]
            [hara.protocol.object :as object]
            [hara.reflect :as reflect]
            [hara.object.base-test])
  (:import [test PersonBuilder Person Dog DogBuilder Cat Pet]))

^{:refer hara.object.read/read-reflect-fields :added "2.3"}
(fact "fields of an object from reflection"
  (-> (read/read-reflect-fields Dog)
      keys)
  => [:name :species])

^{:refer hara.object.read/read-getters :added "2.3"}
(fact "returns fields of an object through getter methods"
  (-> (read/read-getters Dog)
      keys)
  => [:name :species])

^{:refer hara.object.read/read-all-getters :added "2.3"}
(fact "returns fields of an object and base classes"
  (-> (read/read-all-getters Dog)
      keys)
  => [:class :name :species])


^{:refer hara.object.read/meta-read :added "2.3"}
(fact "accesses the read-attributes of an object"

  (read/meta-read Pet)
  => (contains-in {:class test.Pet
                   :methods {:name fn?
                             :species fn?}}))

^{:refer hara.object.read/to-data :added "2.3"}
(fact "creates the object from a string or map"
  (read/to-data "hello")
  => "hello"
  
  (read/to-data (write/from-map {:name "hello" :species "dog"} Pet))
  => {:name "hello", :species "dog"})
