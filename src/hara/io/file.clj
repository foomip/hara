(ns hara.io.file
  (:require [clojure.java.io :as io])
  (:import java.io.File))

(def ^:dynamic *cwd* (.getCanonicalPath (io/file ".")))

(def ^:dynamic *sep* (System/getProperty "file.separator"))

(def ^:dynamic *home* (System/getProperty "user.home"))

(def ^:dynamic *tmp-dir (System/getProperty "java.io.tmpdir"))

(defn ^File file
  [f]
  (cond (instance? File f)
        (.getCanonicalFile ^File f)
        
        (string? f)
        (let [path (if (.startsWith ^String f (str "~" *sep*))
                     (.replace ^String f "~" ^String *home*)
                     f)]
          (.getCanonicalFile (io/file path)))))

(defn directory?
  "Return true if `path` is a directory."
  [path]
  (.isDirectory (file path)))

(defn exists?
  "Return true if `path` exists."
  [path]
  (.exists (file path)))

(defn absolute?
  "Return true if `path` is absolute."
  [path]
  (.isAbsolute (file path)))

(defn executable?
  "Return true if `path` is executable."
  [path]
  (.canExecute (file path)))

(defn readable?
  "Return true if `path` is readable."
  [path]
  (.canRead (file path)))

(defn writeable?
  "Return true if `path` is writeable."
  [path]
  (.canWrite (file path)))

(defn file?
  "Return true if `path` is a file."
  [path]
  (.isFile (file path)))

(defn hidden?
  "Return true if `path` is hidden."
  [path]
  (.isHidden (file path)))

(defn parent
  "Return the parent path."
  [path]
  (.getParentFile (file path)))

(defn last-modified
  "Return file modification time."
  [path]
  (.lastModified (file path)))

(defn size
  "Return size (in bytes) of file."
  [path]
  (.length (file path)))

(defn list-files
  "List files and directories under `path`."
  [path]
  (seq (.listFiles (file path))))

(defn list-all-files
  "Lists all files under `path`.'"
  [path]
  (tree-seq directory? list-files (file path)))

(defn pushback
  [path]
  (java.io.PushbackReader. (io/reader (file path))))

(defn source-seq
  [path]
  (let [reader (pushback path)]
    (take-while identity
                (repeatedly #(try (read reader)
                                  (catch Throwable e))))))

(comment
  (count (filter directory? (list-dir-tree "."))))
