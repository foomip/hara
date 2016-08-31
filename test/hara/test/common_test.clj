(ns hara.test.common-test
  (:use hara.test)
  (:require [hara.test.common :refer :all :as common]))

^{:refer hara.test.common/op :added "2.4"}
(fact "creates an 'op' for evaluation"
  
  (op {:type :form :form '(+ 1 1)})
  => hara.test.common.Op)

^{:refer hara.test.common/op? :added "2.4"}
(fact "checks to see if a datastructure is an 'Op'"
  
  (op? (op {:type :form :form '(+ 1 1)}))
  => true)

^{:refer hara.test.common/result :added "2.4"}
(fact "creates a 'hara.test.common.Result' object"
  
  (result {:type :success :data true})
  => hara.test.common.Result)

^{:refer hara.test.common/result? :added "2.4"}
(fact "checks to see if a datastructure is a 'hara.test.common.Result'"
  
  (result? (result {:type :success :data true}))
  => true)

^{:refer hara.test.common/->data :added "2.4"}
(fact "coerces a checker result into data"

  (->data 1) => 1

  (->data (result {:data 1}))
  => 1)

^{:refer hara.test.common/function-string :added "2.4"}
(fact "returns the string representation of a function"

  (function-string every?) => "every?"

  (function-string reset!) => "reset!")

^{:refer hara.test.common/checker :added "2.4"}
(fact "creates a 'hara.test.common.Checker' object"
  
  (checker {:tag :anything :fn (fn [x] true)})
  => hara.test.common.Checker)

^{:refer hara.test.common/checker? :added "2.4"}
(fact "checks to see if a datastructure is a 'hara.test.common.Checker'"
  
  (checker? (checker {:tag :anything :fn (fn [x] true)}))
  => true)
