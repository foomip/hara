(ns hara.io.file-test
  (:use hara.test)
  (:require [hara.io.file :refer :all])
  (:refer-clojure :exclude [list]))

^{:refer hara.io.file/reader :added "2.4"}
(fact "creates a reader for a given input"

  (-> (reader :pushback "project.clj")
      (read)
      second)
  => 'im.chit/hara)

^{:refer hara.io.file/select :added "2.4"}
(fact "selects all the files in a directory"

  (select "src")
  => vector?)

^{:refer hara.io.file/permissions :added "2.4"}
(fact "returns the permissions for a given file"

  (permissions "src")
  => string?)

^{:refer hara.io.file/shorthand :added "2.4"}
(fact "returns the permissions for a given file"

  (shorthand "src")
  => "d")

^{:refer hara.io.file/list :added "2.4"}
(fact "lists the files and attributes for a given directory"

  (list "src")
  => (contains {(str (path "src")) string?
                (str (path "src/hara")) string?})

  (list "src" {:recursive true})
  => map?)

^{:refer hara.io.file/copy :added "2.4"}
(fact "copies all specified files from one to another"

  (copy "src" ".src" {:include [".clj"]})
  => map?

  ^:hidden
  (delete ".src"))

^{:refer hara.io.file/move :added "2.4"}
(fact "moves a file or directory"

  (move ".non-existent" ".moved")
  => (throws))

^{:refer hara.io.file/delete :added "2.4"}
(fact "copies all specified files from one to another"

  (do (copy "src" ".src" {:include [".clj"]})
      (delete ".src" {:include ["test.clj"]}))
  => #{(str (path ".src/hara/test.clj"))}
  
  (delete ".src")
  => set?)

^{:refer hara.io.file/create-directory :added "2.4"}
(fact "creates a directory on the filesystem"

  (do (create-directory ".hello/.world/.foo")
      (directory? ".hello/.world/.foo"))
  => true

  ^:hidden
  (delete ".hello/.world/.foo"))

^{:refer hara.io.file/create-symlink :added "2.4"}
(fact "creates a symlink to another file"

  (do (create-symlink "project.lnk" "project.clj")
      (link? "project.lnk"))
  => true

  ^:hidden
  (delete "project.lnk"))

^{:refer hara.io.file/create-tmpdir :added "2.4"}
(fact "creates a temp directory on the filesystem"

  (create-tmpdir)
  => path?)

^{:refer hara.io.file/parent :added "2.4"}
(fact "returns the parent of the path"
  
  (str (parent "/hello/world.html"))
  => "/hello")

^{:refer hara.io.file/relativize :added "2.4"}
(fact "returns the relationship between two paths"

  (str (relativize "hello"
                   "hello/world.html"))
  => "world.html")

^{:refer hara.io.file/directory? :added "2.4"}
(fact "checks whether a file is a directory")

^{:refer hara.io.file/executable? :added "2.4"}
(fact "checks whether a file is executable")

^{:refer hara.io.file/exists? :added "2.4"}
(fact "checks whether a file exists")

^{:refer hara.io.file/hidden? :added "2.4"}
(fact "checks whether a file is hidden")

^{:refer hara.io.file/file? :added "2.4"}
(fact "checks whether a file is not a link or directory")

^{:refer hara.io.file/link? :added "2.4"}
(fact "checks whether a file is a link")

^{:refer hara.io.file/readable? :added "2.4"}
(fact "checks whether a file is readable")

^{:refer hara.io.file/writable? :added "2.4"}
(fact "checks whether a file is writable")

^{:refer hara.io.file/code :added "2.4"}
(fact "takes a file and returns a lazy seq of top-level forms")

^{:refer hara.io.file/write :added "2.4"}
(fact "writes a stream to a path")
