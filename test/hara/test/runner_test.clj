(ns hara.test.runner-test
  (:use [hara.test :exclude [run run-namespace]])
  (:require [hara.test.runner :refer :all]))

^{:refer hara.test.runner/accumulate :added "2.4"}
(fact "helper function for accumulating results over disparate facts and files")

^{:refer hara.test.runner/interim :added "2.4"}
(fact "summary function for accumulated results")

^{:refer hara.test.runner/run-namespace :added "2.4"}
(fact "run tests for namespace")

^{:refer hara.test.runner/run :added "2.4"}
(fact "run tests for entire project")
