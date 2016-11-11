(ns hara.io.file-test
  (:use hara.test)
  (:require [hara.io.file :refer :all])
  (:refer-clojure :exclude [list]))

^{:refer hara.io.file/file-type :added "2.4"}
(fact "encodes the type of file as a keyword"

  (file-type "hello.clj")
  => :clj

  (file-type "hello.java")
  => :java)

^{:refer hara.io.file/reader :added "2.4"}
(fact "creates a reader for a given input"

  (-> (reader :pushback "project.clj")
      (read)
      second)
  => 'im.chit/hara)

^{:refer hara.io.file/select :added "2.4"}
(fact "selects all the files in a directory"

  (->> (select "../hara/src/hara/io/file" )
       (map #(relativize "../hara/src/hara" %))
       (map str)
       (sort))
  => ["io/file"
      "io/file/attribute.clj"
      "io/file/common.clj"
      "io/file/filter.clj"
      "io/file/option.clj"
      "io/file/path.clj"
      "io/file/reader.clj"
      "io/file/walk.clj"])

^{:refer hara.io.file/permissions :added "2.4"}
(comment "returns the permissions for a given file"

  (permissions "src")
  => "rwxr-xr-x")

^{:refer hara.io.file/shorthand :added "2.4"}
(fact "returns the shorthand string for a given entry"

  (shorthand "src")
  => "d"

  (shorthand "project.clj")
  => "-")

^{:refer hara.io.file/directory? :added "2.4"}
(fact "checks whether a file is a directory"

  (directory? "src")
  => true

  (directory? "project.clj")
  => false)

^{:refer hara.io.file/executable? :added "2.4"}
(fact "checks whether a file is executable"

  (executable? "project.clj")
  => false

  (executable? "/usr/bin/whoami")
  => true)

^{:refer hara.io.file/exists? :added "2.4"}
(fact "checks whether a file exists"

  (exists? "project.clj")
  => true

  (exists? "NON.EXISTENT")
  => false)

^{:refer hara.io.file/hidden? :added "2.4"}
(fact "checks whether a file is hidden"

  (hidden? ".gitignore")
  => true

  (hidden? "project.clj")
  => false)

^{:refer hara.io.file/file? :added "2.4"}
(fact "checks whether a file is not a link or directory"

  (file? "project.clj")
  => true

  (file? "src")
  => false)

^{:refer hara.io.file/link? :added "2.4"}
(fact "checks whether a file is a link"

  (link? "project.clj")
  => false

  (delete "project.bak.clj")
  (link? (create-symlink "project.bak.clj"
                         "project.clj"))
  => true)

^{:refer hara.io.file/readable? :added "2.4"}
(fact "checks whether a file is readable"

  (readable? "project.clj")
  => true)

^{:refer hara.io.file/writable? :added "2.4"}
(fact "checks whether a file is writable"

  (writable? "project.clj")
  => true)

^{:refer hara.io.file/code :added "2.4"}
(fact "takes a file and returns a lazy seq of top-level forms"

  (->> (code "../hara/src/hara/io/file.clj")
       first
       (take 2))
  => '(ns hara.io.file))


^{:refer hara.io.file/list :added "2.4"}
(comment "lists the files and attributes for a given directory"

  (list "src")
  => {"/Users/chris/Development/chit/hara/src" "rwxr-xr-x/d",
      "/Users/chris/Development/chit/hara/src/hara" "rwxr-xr-x/d"}

  (list "../hara/src/hara/io" {:recursive true})
  => {"/Users/chris/Development/chit/hara/src/hara/io" "rwxr-xr-x/d",
      "/Users/chris/Development/chit/hara/src/hara/io/file/reader.clj" "rw-r--r--/-",
      "/Users/chris/Development/chit/hara/src/hara/io/project.clj" "rw-r--r--/-",
      "/Users/chris/Development/chit/hara/src/hara/io/file/filter.clj" "rw-r--r--/-",
      ... ...
      "/Users/chris/Development/chit/hara/src/hara/io/file/path.clj" "rw-r--r--/-",
      "/Users/chris/Development/chit/hara/src/hara/io/file/walk.clj" "rw-r--r--/-",
      "/Users/chris/Development/chit/hara/src/hara/io/file.clj" "rw-r--r--/-"})

^{:refer hara.io.file/copy :added "2.4"}
(fact "copies all specified files from one to another"

  (copy "src" ".src" {:include [".clj"]})  
  => map?

  (delete ".src"))

^{:refer hara.io.file/copy-single :added "2.4"}
(comment "copies a single file to a destination"

  (copy-single "project.clj"
               "project.clj.bak"
               {:options #{:replace-existing}})
  ;;=> #path:"/Users/chris/Development/chit/hara/project.clj.bak"

  (delete "project.clj.bak"))


^{:refer hara.io.file/move :added "2.4"}
(fact "moves a file or directory"

  (do (move "shortlist" ".shortlist")
      (move ".shortlist" "shortlist"))
  
  (move ".non-existent" ".moved")
  => {})

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

  (delete ".hello/.world/.foo"))

^{:refer hara.io.file/create-symlink :added "2.4"}
(fact "creates a symlink to another file"

  (do (create-symlink "project.lnk" "project.clj")
      (link? "project.lnk"))
  => true

  ^:hidden
  (delete "project.lnk"))

^{:refer hara.io.file/create-tmpdir :added "2.4"}
(comment "creates a temp directory on the filesystem"

  (create-tmpdir)
  ;;=> #path:"/var/folders/d6/yrjldmsd4jd1h0nm970wmzl40000gn/T/4870108199331749225"
 )

^{:refer hara.io.file/parent :added "2.4"}
(fact "returns the parent of the path"
  
  (str (parent "/hello/world.html"))
  => "/hello")

^{:refer hara.io.file/relativize :added "2.4"}
(fact "returns the relationship between two paths"

  (str (relativize "hello"
                   "hello/world.html"))
  => "world.html")

^{:refer hara.io.file/write :added "2.4"}
(fact "writes a stream to a path"

  (-> (java.io.FileInputStream. "project.clj")
      (write "project.clj"
             {:options #{:replace-existing}})))

^{:refer hara.io.file/input-stream :added "2.4"}
(comment "opens a file as an input-stream"

  (input-stream "project.clj"))

^{:refer hara.io.file/output-stream :added "2.4"}
(comment "opens a file as an output-stream"

  (output-stream "project.clj"))
