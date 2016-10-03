(ns documentation.hara-io-file
  (:use hara.test)
  (:require [hara.io.file :refer :all]
            [hara.io.file.option :as option])
  (:import (java.nio.file.attribute PosixFilePermissions))
  (:refer-clojure :exclude [list]))

[[:chapter {:title "Introduction"}]]

"
[hara.io.file](https://github.com/zcaudate/hara/blob/master/src/hara/io/file.clj) contain functions and utilities for file system manipulation using the `java.nio.file` package. Efforts have been made to allow filesystem operations to be as easy and straightforward to use as possible."

[[:section {:title "Installation"}]]

"
Add to `project.clj` dependencies:

    [im.chit/hara.io.file \"{{PROJECT.version}}\"]
"

[[:section {:title "Motivation"}]]

"This library was written because of a need to gain greater control over selecting files within a directory. There are a couple of other filesystem libraries for clojure:

- [fs](https://github.com/raynes/fs) - the canonical filesystem library for clojure, based on `java.io` classes and makes use of `clojure.java.io`.
- [nio](https://github.com/pjstadig/nio) - extends `clojure.java.io` functions to java.nio classes
- [nio.file](https://github.com/ToBeReplaced/nio.file) - an early wrapper for `java.nio.file`, it holds true to the java implementation.

 `hara.io.file` wraps functionality over the `java.nio.file` package. **NIO** stands for *'New IO'* - but it's not very new. The package was introduced with Java SE 1.4 in 2002 whilst the `nio.file` was added in Java SE 7, nicknamed *NIO.2*. Here are some more comparsions of the `java.io` vs `java.nio` scenarios:

- [Java NIO vs IO](https://dzone.com/articles/java-nio-vs-io)
- [Java 7 File Revolution](http://codingjunkie.net/java7-file-revolution)
- [NIO Performance Improvement compared to traditional IO in Java](http://stackoverflow.com/questions/7611152/nio-performance-improvement-compared-to-traditional-io-in-java)
- [java.io.File vs java.nio.Files which is the preferred in new code?](http://stackoverflow.com/questions/32143633/java-io-file-vs-java-nio-files-which-is-the-preferred-in-new-code)
- [Why File Sucks](http://java7fs.wikia.com/wiki/Why_File_sucks)

[hara.io.file](https://github.com/zcaudate/hara/blob/master/src/hara/io/file.clj) written due to the belief that the `java.io` package was not consistent enough for file system operations. Furthermore, file system operations use the [FileVisitor](https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileVisitor.html) pattern as it was easier to think about file and directory manipulation as bulk operations. If an operation can be done on a whole group of files, then that operation can also be readily done on a single file, thus making the codebase smaller and more robust."

[[:chapter {:title "API" :link "hara.io.file"}]]

[[:api {:namespace "hara.io.file"}]]

[[:chapter {:title "Basics"}]]

[[:section {:title "path"}]]

"Creates a `java.nio.file.Path` object:"

(path "project.clj")
;;=> #path:"/Users/chris/Development/chit/hara/project.clj"

"And can be used in various different ways:"

(path "hello" "world")
;;=> #path:"/Users/chris/Development/chit/hara/hello/world"

(path ["hello" "world"])
;;=> #path:"/Users/chris/Development/chit/hara/hello/world"

"It also supports `java.io.File` and `java.net.URI`"

(path (java.io.File. "project.clj"))
;;=> #path:"/Users/chris/Development/chit/hara/project.clj"

(path (java.net.URI. "file:///Users/chris/Development/chit/hara/project.clj"))
;;=> #path:"/Users/chris/Development/chit/hara/project.clj"

"The function is idempotent:"

(path (path "project.clj"))
;;=> #path:"/Users/chris/Development/chit/hara/project.clj"

[[:section {:title "path?"}]]

"Checks whether as object is an instance of `java.nio.file.Path`"

(comment
  (path? (path "")) => true

  (path? "project.clj") => false)

[[:section {:title "reader"}]]

"Returns a `java.io.Reader` instance:"

(comment
  (reader "project.clj")
  ;;=> #object[java.io.BufferedReader 0x2b0c6784 "java.io.BufferedReader@2b0c6784"]
  )

"A type of reader can be specified. The most useful is the `java.io.PushbackReader`"

(comment
  (->> (reader :pushback "project.clj")
       (read)
       (take 5))
  => '(defproject im.chit/hara "2.4.2" :description "patterns and utilities"))

"Additional readers can be created through the `type` option, accessible through the `reader-types` function:"

(comment
  (reader-types)
  => (:input-stream :buffered :file :string :pushback :char-array :piped :line-number))


[[:section {:title "attributes"}]]

"Returns attributes for a given path:"

(comment
  (attributes "src")
  => {:dev 16777220
      :ino 2378194
      :gid 20
      :uid 501
      :file-key "(dev=1000004 ino=2378194)"
      :is-regular-file false
      :permissions "rwxr-xr-x"
      :group "staff"
      :is-directory true
      :is-other false
      :mode 16877
      :size 102 
      :ctime 1473732706000
      :nlink 3
      :last-access-time 1473894825000
      :is-symbolic-link false
      :last-modified-time 1472294651000
      :creation-time 1472294651000
      :owner "chris"
      :rdev 0})

[[:section {:title "set-attributes"}]]

"Sets attributes for a file or directory:"

(comment
  (set-attributes "src" {:last-access-time 0
                         :permissions "rwxr--r--"})
  ;;=> #path:"/Users/chris/Development/chit/hara/src"
)

(comment
  (-> (attributes "src")
      (select-keys [:permissions :last-access-time]))
  => {:permissions "rwxr--r--", :last-access-time 0}
  )

[[:section {:title "permissions"}]]

"Returns the permissions of a file or directory:"

(comment
  (permissions "project.clj")
  => "rw-r--r--")

"We can use `java.nio.file.attribute.PosixFilePermissions` to change it into enums"

(comment
  (->> (permissions "project.clj")
       (PosixFilePermissions/fromString)
       (mapv option/enum->keyword))
  => [:owner-read :owner-write :group-read :others-read]
)

[[:chapter {:title "Creation"}]]

[[:section {:title "create-directory"}]]

"Creates a directory by creating all nonexistent parent directories first. An exception is not thrown if the directory could not be created because it already exists."

(comment
  (create-directory "hello/world")
  ;;=> #path:"/Users/chris/Development/chit/hara/hello/world"
)

"Additional attributes can be set when in the process of creating a directory:"

(comment
  (create-directory "hello/world/foo"
                    {:permissions "rwxr--r--"})
  ;;=> #path:"/Users/chris/Development/chit/hara/hello/world/foo"
  )

"If there is already a file in the place where the directory was suppose to be created, it will throw an error:"

(comment
  (create-directory "project.clj"
                    {:permissions "rwxr--r--"})
  => (throws java.nio.file.FileAlreadyExistsException)
)

[[:section {:title "create-symlink"}]]

"Creates a symlink on the filesystem:"

(comment
  (create-symlink "project.lnk" "project.clj")
  ;;=> #path:"/Users/chris/Development/chit/hara/project.lnk"
)

"It will throw an error if the file already exists:"

(comment
  (create-symlink "project.lnk" "project.clj")
  => (throws java.nio.file.FileAlreadyExistsException))


[[:section {:title "create-tmpdir"}]]

"Creates a temporary directory:"

(comment
  (create-tmpdir)
  ;;=> #path:"/var/folders/d6/yrjldmsd4jd1h0nm970wmzl40000gn/T/6374416729058333137"
)

"Can be passed a prefix for identification purposes:"

(comment
  (create-tmpdir "PREFIX")
  ;;=> #path:"/var/folders/d6/yrjldmsd4jd1h0nm970wmzl40000gn/T/PREFIX618209873171547190"
)
  
[[:chapter {:title "Attributes"}]]

[[:section {:title "directory?"}]]

"Checks whether a path is a directory:"

(comment
  (directory? "src") => true

  (directory? "project.clj") => false

  (directory? "NON-EXISTENT") => false)

[[:section {:title "executable?"}]]

"Checks whether a path is executable:"

(comment
  (executable? "src") => true

  (executable? "project.clj") => false

  (executable? "NON-EXISTENT") => false)


[[:section {:title "exists?"}]]

"Checks whether a path exists:"

(comment
  (exists? "src") => true

  (exists? "NON-EXISTENT") => false)


[[:section {:title "file?"}]]

"Checks whether a path is a regular file:"

(comment
  (file? "src") => false

  (file? "project.clj") => true

  (file? "NON-EXISTENT") => false)

[[:section {:title "hidden?"}]]

"Checks whether a path is hidden:"

(comment
  (hidden? ".git") => true

  (hidden? "project.clj") => false

  (hidden? "NON-EXISTENT") => false)

[[:section {:title "link?"}]]

"Checks whether a file is a link:"

(comment
  (link? "project.clj") => false

  (link? "/usr/local/bin/boot") => true)

[[:section {:title "readable?"}]]

"Checks whether a file is readable:"

(comment
  (readable? "project.clj") => true

  (readable? "NON-EXISTENT") => false)

[[:section {:title "writable?"}]]

"Checks whether a file is readable:"

(comment
  (writable? "project.clj") => true

  (writable? "/usr/local/bin/boot") => false)


[[:chapter {:title "Operations"}]]

[[:section {:title "list"}]]

"Lists all the files in a given directory:"

(comment
  (list "src")
  => {"/Users/chris/Development/chit/hara/src" "rwxr--r--/d",
      "/Users/chris/Development/chit/hara/src/hara" "rwxr-xr-x/d"})

"We can look at the directory at a given depth:"

(comment
  (list "src" {:depth 2})
  => {"/Users/chris/Development/chit/hara/src" "rwxr--r--/d",
      "/Users/chris/Development/chit/hara/src/hara/data.clj" "rw-r--r--/-",
      ... ...
      "/Users/chris/Development/chit/hara/src/hara/function" "rwxr-xr-x/d"})

"It is possible to look at an entire directory by using `:recursive true`:"

(comment
  (count (list "src" {:recursive true}))
  => 182)

"Or just to filter the files that we are interested in:"

(comment
  (count (list "src" {:include [".clj"]
                      :recursive true}))
  => 147)

[[:section {:title "select"}]]

"`select` is similar to `list`, but is recursive by default and returns a list of paths:"

(comment
  (take 5 (select "src"))
  ;;=> (#path:"/Users/chris/Development/chit/hara/src/hara/class/checks.clj"
  ;;    #path:"/Users/chris/Development/chit/hara/src/hara/class/inheritance.clj"
  ;;    #path:"/Users/chris/Development/chit/hara/src/hara/class.clj"
  ;;    #path:"/Users/chris/Development/chit/hara/src/hara/common/checks.clj"
  ;;    #path:"/Users/chris/Development/chit/hara/src/hara/common/error.clj")
  )

"Using the `:file` and `:directory` keyword options, custom file walk functions can be created. In this case, we print out the map that is passed to the `:file` function everytime a file is visited:"

(comment
  (select "src" {:depth 1
                 :file (fn [m]
                         (println m))})
  ;; {:path #path:"/Users/chris/Development/chit/hara/src/hara",
  ;;  :accumulate #{:files},
  ;;  :exclude (),
  ;;  :include (),
  ;;  :directory {:pre #function[hara.common.primitives/F]},
  ;;  :file #function[documentation.hara-io-file/eval15449/fn--15450],
  ;;  :root #path:"/Users/chris/Development/chit/hara/src",
  ;;  :with #{:root},
  ;;  :attrs #object[sun.nio.fs.UnixFileAttributes]
  ;;  :depth 1,
  ;;  :options #{},
  ;;  :accumulator #atom[[#path:"/Users/chris/Development/chit/hara/src/hara"] 0x501600cc]}
  )

"So if we wish to print out the path whenever we visit, it can be written like this:"

(comment
  (select "src" {:depth 2
                 :include [".clj"]
                 :file (fn [m]
                         (prn (str (:path m))))})

  ;; "/Users/chris/Development/chit/hara/src/hara/class.clj"
  ;; "/Users/chris/Development/chit/hara/src/hara/common.clj"
  ;;  ....
  ;; "/Users/chris/Development/chit/hara/src/hara/string.clj"
  ;; "/Users/chris/Development/chit/hara/src/hara/test.clj"
  ;; "/Users/chris/Development/chit/hara/src/hara/time.clj"
  )

"For more examples, please see how [copy](https://github.com/zcaudate/hara/blob/2ad1169bb602d36fbc4fd56da5058745662ad0c0/src/hara/io/file.clj#L96-L127), [list](https://github.com/zcaudate/hara/blob/2ad1169bb602d36fbc4fd56da5058745662ad0c0/src/hara/io/file.clj#L70-L94) and [delete](https://github.com/zcaudate/hara/blob/2ad1169bb602d36fbc4fd56da5058745662ad0c0/src/hara/io/file.clj#L146-L168) have been written. [select](https://github.com/zcaudate/hara/blob/2ad1169bb602d36fbc4fd56da5058745662ad0c0/src/hara/io/file.clj#L32-L41) is the exposed interface for [hara.io.file.walk/walk](https://github.com/zcaudate/hara/blob/2ad1169bb602d36fbc4fd56da5058745662ad0c0/src/hara/io/file/walk.clj#L137-L191) - they are essentially the same function."

[[:section {:title "copy"}]]

"copies file from one location to another:"

(comment
  (copy "project.clj" "project.clj.bak"))

"copy directory, works the same as copy file:"

(comment
  (copy "src" "src.bak"))

"copy directory with depth of 2 levels:"

(comment
  (copy "src" "src.2" {:depth 2}))

"copy only images to new directory:"

(comment
  (copy "resources" "images.bak"
        {:include [".png" ".jpg"]}))

"Simulate the copy operation, list the files that will be affected:"

(comment
  (copy "resources" "images.bak"
        {:include [".png" ".jpg"]
         :simulate true}))


"copy only links to new directory:"

(comment
  (copy "" "../links" 
        {:include [(fn [{:keys [path]}]
                     (link? path))]}))

"copy everything except `.cljs` files to new directory:"

(comment
  (copy "src" "clojure.bak" {:exclude [".cljs"]}))


[[:section {:title "delete"}]]

"deletes files or directories, the selection process is similar to `copy` and can be applied to both files and directories alike:"

(comment
  (delete "project.clj"))

"recursively deletes all `.DS_Store` files in the directory:"

(comment
  (delete "" {:include [".DS_Store"]}))

"returns a list of files deleted, if no files are deleted, returns an empty set:"

(comment
  (delete "NON-EXISTENT")
  => #{})

"recursion can be turned off by explictly stating so:"

(comment
  (delete "" {:include [".DS_Store"]
              :recursive false}))

[[:section {:title "move"}]]

"Moves a file or directory. A move is a rename operation and so is quite efficient:"

(comment
  (move "src" "src.moved")
  ;;=> #path:"/Users/chris/Development/chit/hara/src.moved
  )
