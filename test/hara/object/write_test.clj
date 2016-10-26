(ns hara.object.write-test
  (:use hara.test)
  (:require [hara.object.read :as read]
            [hara.object.write :as write]
            [hara.protocol.object :as object]
            [hara.reflect :as reflect]
            [hara.object.base-test])
  (:import [test PersonBuilder Person Dog DogBuilder Cat Pet]))


^{:refer hara.object.write/meta-write :added "2.3"}
(fact "accesses the write-attributes of an object"

  (write/meta-write DogBuilder)
  => (contains {:class test.DogBuilder
                :empty fn?,
                :methods (contains
                          {:name
                           (contains {:type java.lang.String, :fn fn?})})}))

^{:refer hara.object.write/write-reflect-fields :added "2.3"}
(fact "write fields of an object from reflection"
  (-> (write/write-reflect-fields Dog)
      keys)
  => [:name :species])

^{:refer hara.object.write/write-setters :added "2.3"}
(fact "write fields of an object through setter methods"
  (write/write-setters Dog)
  => {}

  (keys (write/write-setters DogBuilder))
  => [:name])


^{:refer hara.object.write/write-all-setters :added "2.3"}
(fact "write all setters of an object and base classes"
  (write/write-all-setters Dog)
  => {}

  (keys (write/write-all-setters DogBuilder))
  => [:name])

^{:refer hara.object.write/from-empty :added "2.3"}
(fact "creates the object from an empty object constructor"
  (write/from-empty {:name "chris" :pet "dog"}
                    (fn [_] (java.util.Hashtable.))
                    {:name {:type String
                            :fn (fn [obj v]
                                  (.put obj "hello" (keyword v))
                                  obj)}
                     :pet  {:type String
                            :fn (fn [obj v]
                                  (.put obj "pet" (keyword v))
                                  obj)}})
  => {"pet" :dog, "hello" :chris})

^{:refer hara.object.write/from-map :added "2.3"}
(fact "creates the object from a map"
  (-> {:name "chris" :age 30 :pets [{:name "slurp" :species "dog"}
                                    {:name "happy" :species "cat"}]}
      (write/from-map Person)
      (read/to-data))
  => {:name "chris", :age 30, :pets [{:name "slurp", :species "dog"}
                                     {:name "happy", :species "cat"}]})

^{:refer hara.object.write/from-data :added "2.3"}
(fact "creates the object from data"
  (-> (write/from-data ["hello"] (Class/forName "[Ljava.lang.String;"))
      seq)
  => ["hello"])
