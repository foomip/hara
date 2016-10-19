(ns hara.io.file.path-test
  (:use hara.test)
  (:require [hara.io.file.path :refer :all]
            [hara.io.file.common :as common]
            [clojure.string :as string]))

^{:refer hara.io.file.path/normalise :added "2.4"}
(fact "creates a string that takes notice of the user home"
  
  (normalise ".")
  => (str common/*cwd* "/" ".")

  (normalise "~/hello/world.txt")
  => (str common/*home* "/hello/world.txt")
  
  (normalise "/usr/home")
  => "/usr/home")

^{:refer hara.io.file.path/path-test :added "2.4"}
(fact "returns a java.nio.file.Path object"

  (str (path "~"))
  => common/*home*

  (str (path "~/../shared/data"))
  => (str (->> (re-pattern common/*sep*)
               (string/split common/*home*)
               (butlast)
               (string/join "/"))
          "/shared/data")

  (str (path ["hello" "world.txt"]))
  => (str common/*cwd* "/hello/world.txt"))

^{:refer hara.io.file.path/path :added "2.4"}
(comment "creates a `java.nio.file.Path object"
  
 (path "project.clj")
 ;;=> #path:"/Users/chris/Development/chit/hara/project.clj"

 (path (path "project.clj"))       ;; idempotent
 ;;=> #path:"/Users/chris/Development/chit/hara/project.clj"

 (path "~")                       ;; tilda
 ;;=> #path:"/Users/chris"
 
 (path "src" "hara/time.clj")      ;; multiple arguments
 ;;=> #path:"/Users/chris/Development/chit/hara/src/hara/time.clj"

 (path ["src" "hara" "time.clj"])  ;; vector 
 ;;=> #path:"/Users/chris/Development/chit/hara/src/hara/time.clj"

 (path (java.io.File.              ;; java.io.File object 
        "src/hara/time.clj"))
 ;;=> #path:"/Users/chris/Development/chit/hara/src/hara/time.clj"

 (path (java.net.URI.              ;; java.net.URI object 
        "file:///Users/chris/Development/chit/hara/project.clj"))
 ;;=> #path:"/Users/chris/Development/chit/hara/project.clj"
 )


^{:refer hara.io.file.path/path? :added "2.4"}
(fact "checks to see if the object is of type Path"

  (path? (path "/home"))
  => true)

^{:refer hara.io.file.path/section :added "2.4"}
(fact "path object without normalisation"

  (str (section "project.clj"))
  => "project.clj"
  
  (str (section "src" "hara/time.clj"))
  => "src/hara/time.clj")

^{:refer hara.io.file.path/to-file :added "2.4"}
(fact "creates a java.io.File object"

  (to-file (section "project.clj"))
  => (all java.io.File
          #(-> % str (= "project.clj"))))
