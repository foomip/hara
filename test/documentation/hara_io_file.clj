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

    [zcaudate/hara.io.file \"{{PROJECT.version}}\"]
"

[[:section {:title "Motivation"}]]

"There are a couple of other filesystem libraries for clojure:

- [fs](https://github.com/raynes/fs) - based on `java.io` and `clojure.java.io`.
- [nio](https://github.com/pjstadig/nio) - extends `clojure.java.io` functions to java.nio classes
- [nio.file](https://github.com/ToBeReplaced/nio.file) - an early wrapper for `java.nio.file`.

[hara.io.file](https://github.com/zcaudate/hara/blob/master/src/hara/io/file.clj) aims to provide consistency of reporting for file operations. Operations are designed using the [FileVisitor](https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileVisitor.html) pattern. Therefore file and directory manipulation are considered as bulk operations by default."

[[:chapter {:title "Index"}]]

[[:api {:title ""
        :namespace "hara.io.file"
        :display #{:tags}}]]

[[:chapter {:title "API"}]]

[[:section {:title "Path"}]]

"Methods that construct and operate on the `java.nio.file.Path` object."

[[:api {:title ""
        :namespace "hara.io.file"
        :only ["path" "path?" "section" "parent" "relativize" "to-file"]}]]

[[:section {:title "Attributes"}]]

[[:api {:title ""
        :namespace "hara.io.file"
        :only ["attributes"
               "set-attributes"
               "permissions"
               "shorthand"
               "directory?"
               "executable?"
               "exists?"
               "file?"
               "hidden?"
               "link?"
               "readable?"
               "writable?"]}]]

[[:section {:title "IO"}]]

[[:api {:title ""
        :namespace "hara.io.file"
        :only ["code"
               "reader"
               "reader-types"
               "write"]}]]

[[:section {:title "Create"}]]

[[:api {:namespace "hara.io.file"
        :title ""
        :only ["create-directory"
               "create-symlink"
               "create-tmpdir"]}]]

[[:section {:title "Operation"}]]

[[:api {:namespace "hara.io.file"
        :title ""
        :only ["copy"
               "copy-single"
               "delete"
               "list"
               "move"
               "option"
               "select"]}]]

[[:chapter {:title "Advanced"}]]

"As all bulk operations are based on `hara.io.file.walk/walk`, it provides a consistent interface for working with files:"

[[:section {:title "depth"}]]

"The `:depth` option determines how far down the directory listing to move"

(comment
  ;; listing the src directory to a depth of 2
  
  (list "src" {:depth 2})
  => {"/Users/chris/Development/chit/hara/src" "rwxr--r--/d",
      "/Users/chris/Development/chit/hara/src/hara/data.clj" "rw-r--r--/-",
      ... ...
      "/Users/chris/Development/chit/hara/src/hara/function" "rwxr-xr-x/d"}

  ;; copying the src directory to a depth of 2
  (copy "src" ".src" {:depth 2})

  ;; delete the .src directory to a depth of 2
  (delete ".src" {:depth 2}))

"The `:recursive` flag enables walking to all depths"

(comment
  ;; lists contents of src directory, :recursive is false by default
  (list "src" {:recursive true})
  => {"/Users/chris/Development/chit/hara/src" "rwxr--r--/d",
      "/Users/chris/Development/chit/hara/src/hara/data.clj" "rw-r--r--/-",
      ... ...
      "/Users/chris/Development/chit/hara/src/hara/function" "rwxr-xr-x/d"}

  ;; copying the src directory, :recursive is true by default
  (copy "src" ".src" {:recursive false})
  => {"src" ".src",
      "src/hara" ".src/hara"}
  
  ;; delete the .src directory, :recursive is true by default
  (delete ".src" {:recursive false})
  => #{"/Users/chris/Development/chit/hara/.src/hara"
       "/Users/chris/Development/chit/hara/.src"})

[[:section {:title "simulate"}]]

"When the `:simulate` flag is set, the operation is not performed but will output as if the operation has been done."

(comment
  (copy "src" ".src" {:simulate true})
  => {"src" ".src",
      "src/hara" ".src/hara"}

  (move "src" ".src" {:simulate true})
  => {"/Users/chris/Development/chit/hara/src/hara/data.clj"
      "/Users/chris/Development/chit/hara/.src/hara/data.clj",
      ... ...
      "/Users/chris/Development/chit/hara/src/hara/time/data/coerce.clj"
      "/Users/chris/Development/chit/hara/.src/hara/time/data/coerce.clj"}
  
  (delete ".src" {:simulate true})
  => #{"/Users/chris/Development/chit/hara/.src/hara"
       "/Users/chris/Development/chit/hara/.src"})

[[:section {:title "filter"}]]

"Files can be included or excluded through an array of file filters. Values for `:exclude` and `:include` array elements can be either a pattern or a function:"

(comment  
  (select "." {:exclude [".clj"
                         directory?]
               :recursive false})
  => [;; #path:"/Users/chris/Development/chit/hara/.gitignore"
      ;; #path:"/Users/chris/Development/chit/hara/.gitmodules"
      ...
      ;; #path:"/Users/chris/Development/chit/hara/spring.jpg"
      ;; #path:"/Users/chris/Development/chit/hara/travis.jpg"
      ]
  
  (select "." {:include [".clj$"
                         file?]
               :recursive false})
  => [;; #path:"/Users/chris/Development/chit/hara/.midje.clj"
      ;; #path:"/Users/chris/Development/chit/hara/project.clj"
      ])

"`:accumulate` is another options to set that specifies which files are going to be included in in accumulation:"

(comment

  (select "." {:accumulate #{}})
  => []
  
  (select "src" {:depth 2
                 :accumulate #{:directories}})
  => [;; #path:"/Users/chris/Development/chit/hara/src"
      ;; #path:"/Users/chris/Development/chit/hara/src/hara"
      ]

  (select "src" {:depth 2
                 :accumulate #{:files}})
  
  => [;; #path:"/Users/chris/Development/chit/hara/src/hara/class.clj"
      ...
      ;; #path:"/Users/chris/Development/chit/hara/src/hara/time.clj"
      ;; #path:"/Users/chris/Development/chit/hara/src/hara/zip.clj"
      ])

[[:section {:title "file system"}]]

"The `:file` option takes a function which will run whenever a file is visited:"

(comment
  
  (select "src" {:include ["/class/"]
                 :file (fn [{:keys [path]}] (println (str path)))})
  ;; /Users/chris/Development/chit/hara/src/hara/class/checks.clj
  ;; /Users/chris/Development/chit/hara/src/hara/class/enum.clj
  ;; /Users/chris/Development/chit/hara/src/hara/class/inheritance.clj
  ;; /Users/chris/Development/chit/hara/src/hara/class/multi.clj
  )

"The `:directory` option takes either a function or a map of function which will run whenever a directory is visited:"

(comment
  
  (select "src" {:include ["/class"]               
                 :directory (fn [{:keys [path]}]
                              (println (str path)))})
  
  ;; /Users/chris/Development/chit/hara/src/hara/class

  (select "src" {:include ["/class"]               
                 :directory {:pre  (fn [{:keys [path]}]
                                     (println "PRE" (str path)))
                             :post (fn [{:keys [path]}]
                                     (println "POST "(str path)))}})
  
  ;; PRE /Users/chris/Development/chit/hara/src/hara/class
  ;; POST  /Users/chris/Development/chit/hara/src/hara/class
)

"`:options` are passed in for `move` and `copy`"

(comment

  ;; `:replace-existing` replaces an existing file if it exists.
  ;; `:copy-attributes`  copy attributes to the new file.
  
  (copy "project.clj" "project.clj.bak"
        {:options [:replace-existing
                   :copy-attributes]})

  ;; `:atomic-move` moves the file as an atomic file system operation.
  
  (move "project.clj.bak" "project.clj" 
        {:options [:replace-existing
                   :atomic-move]}))

"`:with` is either `#{:root}` to include the root path or `#{}` to not include the root path. It is set to `#{:root}` for `copy`, `move` and `delete`. It is set to `#{}` for `list` and `select`."

[[:section {:title "accumulator"}]]

"`:accumulator` sets the atom that contains the accumulated values during the walk:"

(comment
  
  (let [acc (atom [])]
    
    (select "src/hara/class"  {:accumulator acc})
    
    (select "src/hara/common" {:accumulator acc})
    
    (map #(str (relativize "src/hara" %))
         @acc))
  => ("class"
      "class/checks.clj"
      "class/enum.clj"
      "class/inheritance.clj"
      "class/multi.clj"
      "common"
      "common/checks.clj"
      "common/error.clj"
      "common/hash.clj"
      "common/pretty.clj"
      "common/primitives.clj"
      "common/state.clj"
      "common/string.clj"
      "common/watch.clj"))

"For more examples of how it is used, please see the source code for `copy`, `delete` `list` and `move`."
