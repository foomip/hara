(ns hara.test.runner-test
  (:use [hara.test :exclude [run run-namespace]])
  (:require [hara.test.runner :refer :all]))

^{:refer hara.test.runner/project-name :added "2.4"}
(fact "returns the name, read from project.clj"

  (project-name)
  => 'im.chit/hara)

^{:refer hara.test.runner/read-namespace :added "2.4"}
(fact "reads the namespace of the given path"

  (read-namespace "src/hara/test/runner.clj")
  => 'hara.test.runner)

^{:refer hara.test.runner/all-files :added "2.4"}
(fact "returns all the clojure files in a directory"
  
  (+ (count (all-files ["test/hara"]))
     (count (all-files ["test/documentation"])))
  => (count (all-files ["test"]))

  (-> (all-files ["test"])
      (get 'hara.test.runner-test))
  => #(.endsWith ^String % "/test/hara/test/runner_test.clj"))

^{:refer hara.test.runner/accumulate :added "2.4"}
(fact "helper function for accumulating results over disparate facts and files")

^{:refer hara.test.runner/interim :added "2.4"}
(fact "summary function for accumulated results")

^{:refer hara.test.runner/run-namespace :added "2.4"}
(fact "run tests for namespace")

^{:refer hara.test.runner/run :added "2.4"}
(fact "run tests for entire project")
