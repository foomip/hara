(ns hara.test.checker.logic-test
  (:use [hara.test :exclude [any all]])
  (:require [hara.test.checker.logic :refer :all]
            [hara.test.common :as common]))

^{:refer hara.test.checker.logic/any :added "2.4"}
(fact "checker that allows `or` composition of checkers"

  (mapv (any even? 1)
        [1 2 3 4 5])
  => [true true false true false])
  
^{:refer hara.test.checker.logic/all :added "2.4"}
(fact "checker that allows `and` composition of checkers"

  (mapv (all even? #(< 3 %))
        [1 2 3 4 5])
  => [false false false true false])
