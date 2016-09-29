(ns hara.io.file
  (:require [hara.io.file
             [attribute :as attr]
             [common :as common]
             [option :as option]
             [path :as path]
             [reader :as reader]
             [walk :as walk]]
            [hara.namespace.import :as ns]
            [hara.string.case :as case])
  (:import (java.nio.file CopyOption DirectoryNotEmptyException Files FileSystems LinkOption Path)
           (java.nio.file.attribute FileAttribute FileTime PosixFilePermissions))
  (:refer-clojure :exclude [list]))

(ns/import hara.io.file.path [section path path?]
           hara.io.file.attribute [attributes set-attributes]
           hara.io.file.reader [reader-types]
           hara.io.file.option [option])

(defn reader
  "creates a reader for a given input
 
   (-> (reader :pushback \"project.clj\")
       (read)
       second)
   => 'im.chit/hara"
  {:added "2.4"}
  ([input]
   (reader :buffered input {}))
  ([type input]
   (reader type input {}))
  ([type input opts]
   (reader/reader type input opts)))

(defn select
  "selects all the files in a directory
 
   (select \"src\")
   => vector?"
  {:added "2.4"}
  ([root]
   (select root nil))
  ([root opts]
   (walk/walk root opts)))

(defn permissions
  "returns the permissions for a given file
 
   (permissions \"src\")
   => string?"
  {:added "2.4"}
  [path]
  (let [path (path/path path)]
    (->> common/*no-follow*
         (Files/getPosixFilePermissions path)
         (PosixFilePermissions/toString))))

(defn shorthand
  "returns the permissions for a given file
 
   (shorthand \"src\")
   => \"d\""
  {:added "2.4"}
  [path]
  (let [path (path/path path)]
    (cond (Files/isDirectory path (LinkOption/values))
          "d"
          
          (Files/isSymbolicLink path)
          "l"
          
          :else "-")))

(defn list
  "lists the files and attributes for a given directory
 
   (list \"src\")
   => (contains {(str (path \"src\")) string?
                 (str (path \"src/hara\")) string?})
 
   (list \"src\" {:recursive true})
   => map?"
  {:added "2.4"}
  ([root] (list root {}))
  ([root opts]
   (let [gather-fn (fn [{:keys [path attrs accumulator]}]
                  (swap! accumulator
                         assoc
                         (str path)
                         (str (permissions path) "/" (shorthand path))))]
     (walk/walk root
                (merge {:depth 1
                        :directory gather-fn
                        :file gather-fn
                        :accumulator (atom {})
                        :accumulate #{}
                        :with #{}}
                       opts)))))

(defn copy
  "copies all specified files from one to another
 
   (copy \"src\" \".src\" {:include [\".clj\"]})
   => map?
 
   "
  {:added "2.4"}
  ([source target]
   (copy source target {}))
  ([source target opts]
   (let [copy-fn (fn [{:keys [root path attrs target accumulator simulate]}]
                   (let [rel   (.relativize ^Path root path)
                         dest  (.resolve ^Path target rel)
                         copts (->> [:copy-attributes :nofollow-links]
                                    (or (:options opts))
                                    (mapv option/option)
                                    (into-array CopyOption))]
                     (when-not simulate
                       (Files/createDirectories (.getParent dest) attr/*empty*)
                       (Files/copy ^Path path ^Path dest copts))
                     (swap! accumulator
                            assoc
                            (str path)
                            (str dest))))]
     (walk/walk source
                (merge {:target (path/path target)
                        :directory copy-fn
                        :file copy-fn
                        :accumulator (atom {})
                        :accumulate #{}}
                       opts)))))

(defn move
  "moves a file or directory
 
   (move \".non-existent\" \".moved\")
   => (throws)"
  {:added "2.4"}
  ([source target]
   (move source target {}))
  ([source target opts]
   (if-not (:simulate opts)
     (Files/move (path/path source)
                 (path/path target)
                 (->> [:atomic-move]
                      (or (:options opts))
                      (mapv option/option)
                      (into-array CopyOption))))))

(defn delete
  "copies all specified files from one to another
 
   (do (copy \"src\" \".src\" {:include [\".clj\"]})
       (delete \".src\" {:include [\"test.clj\"]}))
   => #{(str (path \".src/hara/test.clj\"))}
   
   (delete \".src\")
   => set?"
  {:added "2.4"}
  ([root] (delete root {}))
  ([root opts]
   (let [delete-fn (fn [{:keys [path attrs accumulator simulate]}]
                     (try (if-not simulate
                            (Files/delete path))
                          (swap! accumulator conj (str path))
                          (catch DirectoryNotEmptyException e)))]
     (walk/walk root
                (merge {:directory {:post delete-fn}
                        :file delete-fn
                        :accumulator (atom #{})
                        :accumulate #{}}
                       opts)))))

(defn create-directory
  "creates a directory on the filesystem
 
   (do (create-directory \".hello/.world/.foo\")
       (directory? \".hello/.world/.foo\"))
   => true
 
   "
  {:added "2.4"}
  ([path]
   (create-directory path {}))
  ([path attrs]
   (Files/createDirectories (path/path path)
                            (attr/map->attr-array attrs))))

(defn create-symlink
  "creates a symlink to another file
 
   (do (create-symlink \"project.lnk\" \"project.clj\")
       (link? \"project.lnk\"))
   => true
 
   "
  {:added "2.4"}
  ([path link-to]
   (create-symlink path link-to {}))
  ([path link-to attrs]
   (Files/createSymbolicLink (path/path path)
                             (path/path link-to)
                             (attr/map->attr-array attrs))))

(defn create-tmpdir
  "creates a temp directory on the filesystem
 
   (create-tmpdir)
   => path?"
  {:added "2.4"}
  ([]
   (create-tmpdir ""))
  ([prefix]
   (Files/createTempDirectory prefix (make-array FileAttribute 0))))

(defn parent
  "returns the parent of the path
   
   (str (parent \"/hello/world.html\"))
   => \"/hello\""
  {:added "2.4"}
  [path]
  (.getParent (path/path path)))

(defn relativize
  "returns the relationship between two paths
 
   (str (relativize \"hello\"
                    \"hello/world.html\"))
   => \"world.html\""
  {:added "2.4"}
  [path1 path2]
  (.relativize (path/path path1) (path/path path2)))

(defn directory?
  "checks whether a file is a directory"
  {:added "2.4"}
  [path]
  (Files/isDirectory (path/path path) common/*no-follow*))

(defn executable?
  "checks whether a file is executable"
  {:added "2.4"}
  [path]
  (Files/isExecutable (path/path path)))

(defn exists?
  "checks whether a file exists"
  {:added "2.4"}
  [path]
  (Files/exists (path/path path) common/*no-follow*))

(defn hidden?
  "checks whether a file is hidden"
  {:added "2.4"}
  [path]
  (Files/isHidden (path/path path)))

(defn file?
  "checks whether a file is not a link or directory"
  {:added "2.4"}
  [path]
  (Files/isRegularFile (path/path path) common/*no-follow*))

(defn link?
  "checks whether a file is a link"
  {:added "2.4"}
  [path]
  (Files/isSymbolicLink (path/path path)))

(defn readable?
  "checks whether a file is readable"
  {:added "2.4"}
  [path]
  (Files/isReadable (path/path path)))

(defn writable?
  "checks whether a file is writable"
  {:added "2.4"}
  [path]
  (Files/isWritable (path/path path)))

(defn code
  "takes a file and returns a lazy seq of top-level forms"
  {:added "2.4"}
  [path]
  (let [reader (reader :pushback path)]
    (take-while identity
                (repeatedly #(try (read reader)
                                  (catch Throwable e))))))

(defn copy-single
  ([source target]
   (copy-single source target {}))
  ([source target opts]
   (Files/copy (path/path source)
               (path/path target)
               (->> (:options opts)
                    (mapv option/option)
                    (into-array CopyOption)))))

(defn write
  "writes a stream to a path"
  {:added "2.4"}
  ([stream path]
   (write stream path {}))
  ([stream path opts]
   (Files/copy stream
               (path/path path)
               (->> (:options opts)
                    (mapv option/option)
                    (into-array CopyOption)))))
