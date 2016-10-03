(ns hara.class.multi-test
  (:use hara.test)
  (:require [hara.class.enum :refer :all]))

^{:refer hara.class.multi/multimethod :added "2.4"}
(fact "creates a multimethod from an existing one"

  (defmulti hello :type)

  (defmethod hello :a
    [e] (assoc e :a 1))

  (def world (clone hello "world"))

  (defmethod world :b
    [e] (assoc e :b 2))

  (world {:type :b})
  => {:type :b :b 2} 

  ;; original method should not be changed
  (hello {:type :b})
  => (throws))