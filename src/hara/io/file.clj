(ns hara.io.file
  (:require [hara.io.file
             [attribute :as attr]
             [common :as common]
             [option :as option]
             [path :as path]
             [reader :as reader]
             [walk :as walk]]
            [hara.namespace.import :as ns]
            [clojure.string :as string]
            [hara.string.case :as case])
  (:import (java.nio.file CopyOption
                          DirectoryNotEmptyException
                          Files
                          FileSystems
                          LinkOption
                          OpenOption
                          Path)
           (java.nio.file.attribute FileAttribute
                                    FileTime
                                    PosixFilePermissions))
  (:refer-clojure :exclude [list]))

(ns/import hara.io.file.path [section path path? to-file]
           hara.io.file.attribute [attributes set-attributes]
           hara.io.file.reader [reader-types]
           hara.io.file.option [option])

(defn file-type
  "encodes the type of file as a keyword
 
   (file-type \"hello.clj\")
   => :clj
 
   (file-type \"hello.java\")
   => :java"
  {:added "1.2"}
  [file]
  (-> (str file)
      (string/split #"\.")
      last
      keyword))

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


(defn directory?
  "checks whether a file is a directory
 
   (directory? \"src\")
   => true
 
   (directory? \"project.clj\")
   => false"
  {:added "2.4"}
  [path]
  (Files/isDirectory (path/path path) common/*no-follow*))

(defn executable?
  "checks whether a file is executable
 
   (executable? \"project.clj\")
   => false
 
   (executable? \"/usr/bin/whoami\")
   => true"
  {:added "2.4"}
  [path]
  (Files/isExecutable (path/path path)))

(defn exists?
  "checks whether a file exists
 
   (exists? \"project.clj\")
   => true
 
   (exists? \"NON.EXISTENT\")
   => false"
  {:added "2.4"}
  [path]
  (Files/exists (path/path path) common/*no-follow*))

(defn hidden?
  "checks whether a file is hidden
 
   (hidden? \".gitignore\")
   => true
 
   (hidden? \"project.clj\")
   => false"
  {:added "2.4"}
  [path]
  (Files/isHidden (path/path path)))

(defn file?
  "checks whether a file is not a link or directory
 
   (file? \"project.clj\")
   => true
 
   (file? \"src\")
   => false"
  {:added "2.4"}
  [path]
  (Files/isRegularFile (path/path path) common/*no-follow*))

(defn link?
  "checks whether a file is a link
 
   (link? \"project.clj\")
   => false
 
   (delete \"project.bak.clj\")
   (link? (create-symlink \"project.bak.clj\"
                          \"project.clj\"))
   => true"
  {:added "2.4"}
  [path]
  (Files/isSymbolicLink (path/path path)))

(defn readable?
  "checks whether a file is readable
 
   (readable? \"project.clj\")
   => true"
  {:added "2.4"}
  [path]
  (Files/isReadable (path/path path)))

(defn writable?
  "checks whether a file is writable
 
   (writable? \"project.clj\")
   => true"
  {:added "2.4"}
  [path]
  (Files/isWritable (path/path path)))

(defn select
  "selects all the files in a directory
 
   (->> (select \"../hara/src/hara/io/file\" )
        (map #(relativize \"../hara/src/hara\" %))
        (map str)
        (sort))
   => [\"io/file\"
       \"io/file/attribute.clj\"
      \"io/file/common.clj\"
       \"io/file/filter.clj\"
       \"io/file/option.clj\"
       \"io/file/path.clj\"
       \"io/file/reader.clj\"
       \"io/file/walk.clj\"]"
  {:added "2.4"}
  ([root]
   (select root nil))
  ([root opts]
   (walk/walk root opts)))

(defn permissions
  "returns the permissions for a given file
 
   (permissions \"src\")
   => \"rwxr-xr-x\""
  {:added "2.4"}
  [path]
  (let [path (path/path path)]
    (->> common/*no-follow*
         (Files/getPosixFilePermissions path)
         (PosixFilePermissions/toString))))

(defn shorthand
  "returns the shorthand string for a given entry
 
   (shorthand \"src\")
   => \"d\"
 
   (shorthand \"project.clj\")
   => \"-\""
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
   => {\"/Users/chris/Development/chit/hara/src\" \"rwxr-xr-x/d\",
       \"/Users/chris/Development/chit/hara/src/hara\" \"rwxr-xr-x/d\"}
 
   (list \"../hara/src/hara/io\" {:recursive true})
   => {\"/Users/chris/Development/chit/hara/src/hara/io\" \"rwxr-xr-x/d\",
       \"/Users/chris/Development/chit/hara/src/hara/io/file/reader.clj\" \"rw-r--r--/-\",
       \"/Users/chris/Development/chit/hara/src/hara/io/project.clj\" \"rw-r--r--/-\",
       \"/Users/chris/Development/chit/hara/src/hara/io/file/filter.clj\" \"rw-r--r--/-\",
       ... ...
       \"/Users/chris/Development/chit/hara/src/hara/io/file/path.clj\" \"rw-r--r--/-\",
       \"/Users/chris/Development/chit/hara/src/hara/io/file/walk.clj\" \"rw-r--r--/-\",
       \"/Users/chris/Development/chit/hara/src/hara/io/file.clj\" \"rw-r--r--/-\"}"
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
 
   (delete \".src\")"
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
                        :with #{:root}
                        :accumulator (atom {})
                        :accumulate #{}}
                       opts)))))

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
                        :with #{:root}
                        :accumulator (atom #{})
                        :accumulate #{}}
                       opts)))))

(defn empty-directory?
  "" [path]
  (and (directory? path)
       (= 1 (count (list path)))))

(defn move
  "moves a file or directory
 
   (do (move \"shortlist\" \".shortlist\")
       (move \".shortlist\" \"shortlist\"))
   
   (move \".non-existent\" \".moved\")
   => {}"
  {:added "2.4"}
  ([source target]
   (move source target {}))
  ([source target opts]
   (let [move-fn (fn [{:keys [root path attrs target accumulator simulate]}]
                   (let [rel   (.relativize ^Path root path)
                         dest  (.resolve ^Path target rel)
                         copts (->> [:atomic-move]
                                    (or (:options opts))
                                    (mapv option/option)
                                    (into-array CopyOption))]
                     (when-not simulate
                       (Files/createDirectories (.getParent dest) attr/*empty*)
                       (Files/move ^Path path ^Path dest copts))
                     (swap! accumulator
                            assoc
                            (str path)
                            (str dest))))
         results (walk/walk source
                            (merge {:target (path/path target)
                                    :recursive true
                                    :directory {:post (fn [{:keys [path]}]
                                                        (if (empty-directory? path)
                                                          (delete path opts)))}
                                    :file move-fn
                                    :with #{:root}
                                    :accumulator (atom {})
                                    :accumulate #{}}
                                   opts))]
     results)))

(defn create-directory
  "creates a directory on the filesystem
 
   (do (create-directory \".hello/.world/.foo\")
       (directory? \".hello/.world/.foo\"))
   => true
 
   (delete \".hello/.world/.foo\")"
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
   ;;=> #path:\"/var/folders/d6/yrjldmsd4jd1h0nm970wmzl40000gn/T/4870108199331749225\"
  "
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

(defn code
  "takes a file and returns a lazy seq of top-level forms
 
   (->> (code \"../hara/src/hara/io/file.clj\")
        first
        (take 2))
   => '(ns hara.io.file)"
  {:added "2.4"}
  [path]
  (let [reader (reader :pushback path)]
    (take-while identity
                (repeatedly #(try (read reader)
                                  (catch Throwable e))))))

(defn copy-single
  "copies a single file to a destination
 
   (copy-single \"project.clj\"
                \"project.clj.bak\"
                {:options #{:replace-existing}})
   ;;=> #path:\"/Users/chris/Development/chit/hara/project.clj.bak\"
 
   (delete \"project.clj.bak\")"
  {:added "2.4"}
  ([source target]
   (copy-single source target {}))
  ([source target opts]
   (if-let [dir (parent target)]
     (if-not (exists? dir)
       (create-directory dir)))
   (Files/copy ^Path (path/path source)
               ^Path (path/path target)
               (->> (:options opts)
                    (mapv option/option)
                    (into-array CopyOption)))))

(defn write
  "writes a stream to a path
 
   (-> (java.io.FileInputStream. \"project.clj\")
      (write \"project.clj\"
              {:options #{:replace-existing}}))"
  {:added "2.4"}
  ([stream path]
   (write stream path {}))
  ([stream path opts]
   (Files/copy stream
               ^Path (path/path path)
               (->> (:options opts)
                    (mapv option/option)
                    (into-array CopyOption)))))

(defn input-stream
  "opens a file as an input-stream
 
   (input-stream \"project.clj\")"
  {:added "2.4"}
  ([path]
   (input-stream path {}))
  ([path opts]
   (Files/newInputStream (path/path path)
                         (->> (:options opts)
                              (mapv option/option)
                              (into-array OpenOption)))))

(defn output-stream
  "opens a file as an output-stream
 
   (output-stream \"project.clj\")"
  {:added "2.4"}
  ([path]
   (output-stream path {}))
  ([path opts]
   (Files/newOutputStream (path/path path)
                          (->> (:options opts)
                               (mapv option/option)
                               (into-array OpenOption)))))

(defn read-all-bytes
  [path]
  (Files/readAllBytes (path/path path)))

(defn read-all-lines
  [path]
  (Files/readAllLines (path/path path)))
