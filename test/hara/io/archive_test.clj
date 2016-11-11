(ns hara.io.archive-test
  (:use hara.test)
  (:require [hara.io.archive :refer :all])
  (:refer-clojure :exclude [list remove]))

^{:refer hara.io.archive/open :added "2.4"}
(comment "either opens an existing archive or creates one if it doesn't exist"

  (open "hello/stuff.jar")
  ;;=> creates a zip-file
  )

^{:refer hara.io.archive/url :added "2.4"}
(comment "returns the url of the archive"

  (url (open "hello/stuff.jar"))
  => "/Users/chris/Development/chit/lucidity/hello/stuff.jar")

^{:refer hara.io.archive/path :added "2.4"}
(comment "returns the url of the archive"

  (-> (open "hello/stuff.jar")
      (path "world.java")
      (str))
  => "world.java")

^{:refer hara.io.archive/list :added "2.4"}
(comment "lists all the entries in the archive"

  (list "hello/stuff.jar")
  ;;=> [#path:"/"]
  )

^{:refer hara.io.archive/has? :added "2.4"}
(comment "checks if the archive has a particular entry"

  (has? "hello/stuff.jar" "world.java")
  => false
  )

^{:refer hara.io.archive/archive :added "2.4"}
(comment "puts files into an archive"

  (archive "hello/stuff.jar" "src"))

^{:refer hara.io.archive/extract :added "2.4"}
(comment "extracts all file from an archive"

  (extract "hello/stuff.jar")

  (extract "hello/stuff.jar" "output")

  (extract "hello/stuff.jar"
           "output"
           ["world.java"]))

^{:refer hara.io.archive/remove :added "2.4"}
(comment "removes an entry from the archive"

  (remove "hello/stuff.jar" "world.java"))

^{:refer hara.io.archive/insert :added "2.4"}
(comment "inserts a file to an entry within the archive"

  (insert "hello/stuff.jar" "world.java" "path/to/world.java"))

^{:refer hara.io.archive/stream :added "2.4"}
(comment "creates a stream for an entry wthin the archive"

  (stream "hello/stuff.jar" "world.java"))
