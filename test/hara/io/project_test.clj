(ns hara.io.project-test
  (:use hara.test)
  (:require [hara.io.project :refer :all]))

^{:refer hara.io.project/project :added "2.4"}
(fact "returns `project.clj` as a map")

^{:refer hara.io.project/project-name :added "2.4"}
(fact "returns the name, read from project.clj"

  (project-name)
  => 'zcaudate/hara)

^{:refer hara.io.project/file-namespace :added "2.4"}
(fact "reads the namespace of the given path"

  (file-namespace "src/hara/io/project.clj")
  => 'hara.io.project)

^{:refer hara.io.project/all-files :added "2.4"}
(fact "returns all the clojure files in a directory"
  
  (+ (count (all-files ["test/hara"]))
     (count (all-files ["test/documentation"])))
  => (count (all-files ["test"]))

  (-> (all-files ["test"])
      (get 'hara.io.project-test))
  => #(.endsWith ^String % "/test/hara/io/project_test.clj"))
