(ns hara.io.archive
  (:require [hara.protocol.archive :as archive]
            [hara.io.archive zip]
            [hara.io.file :as fs])
  (:import (java.nio.file FileSystem
                          FileSystems
                          Paths)
           (java.net URI))
  (:refer-clojure :exclude [list remove]))

(def supported #{:zip :jar})

(extend-protocol archive/IArchive
  String
  (-url [archive]
    archive))

(defn open
  "either opens an existing archive or creates one if it doesn't exist
 
   (open \"hello/stuff.jar\")
   ;;=> creates a zip-file
   "
  {:added "2.4"}
  [archive]
  (cond (instance? FileSystem archive)
        archive

        :else
        (let [path (fs/path archive)]
          (cond (fs/exists? path)
                (FileSystems/newFileSystem path nil)

                :else
                (do (fs/create-directory (fs/parent path))
                    (FileSystems/newFileSystem
                     (URI. (str "jar:file:" path))
                     {"create" "true"}))))))

(defn url
  "returns the url of the archive
 
   (url (open \"hello/stuff.jar\"))
   => \"/Users/chris/Development/chit/lucidity/hello/stuff.jar\""
  {:added "2.4"}
  [archive]
  (archive/-url archive))

(defn path
  "returns the url of the archive
 
   (-> (open \"hello/stuff.jar\")
       (path \"world.java\")
       (str))
   => \"world.java\""
  {:added "2.4"}
  [archive entry]
  (archive/-path archive entry))

(defn list
  "lists all the entries in the archive
 
   (list \"hello/stuff.jar\")
   ;;=> [#path:\"/\"]
   "
  {:added "2.4"}
  [archive]
  (archive/-list (open archive)))

(defn has?
  "checks if the archive has a particular entry
 
   (has? \"hello/stuff.jar\" \"world.java\")
   => false
   "
  {:added "2.4"}
  [archive entry]
  (archive/-has? (open archive) entry))

(defn archive
  "puts files into an archive
 
   (archive \"hello/stuff.jar\" \"src\")"
  {:added "2.4"}
  ([archive root]
   (let [ach (open archive)
         res (archive/-archive ach
                               root
                               (fs/select root {:exclude [fs/directory?]}))]
     (.close ach)
     res))
  ([archive root inputs]
   (archive/-archive (open archive) root inputs)))

(defn extract
  "extracts all file from an archive
 
   (extract \"hello/stuff.jar\")
 
   (extract \"hello/stuff.jar\" \"output\")
 
   (extract \"hello/stuff.jar\"
            \"output\"
            [\"world.java\"])"
  {:added "2.4"}
  ([archive]
   (extract archive (fs/parent (url archive))))
  ([archive output]
   (extract archive output (list archive)))
  ([archive output entries]
   (archive/-extract (open archive) output entries)))

(defn insert
  "inserts a file to an entry within the archive
 
   (insert \"hello/stuff.jar\" \"world.java\" \"path/to/world.java\")"
  {:added "2.4"}
  [archive entry input]
  (archive/-insert (open archive) entry input))

(defn remove
  "removes an entry from the archive
 
   (remove \"hello/stuff.jar\" \"world.java\")"
  {:added "2.4"}
  [archive entry]
  (archive/-remove (open archive) entry))

(defn stream
  "creates a stream for an entry wthin the archive
 
   (stream \"hello/stuff.jar\" \"world.java\")"
  {:added "2.4"}
  [archive entry]
  (archive/-stream (open archive) entry))
