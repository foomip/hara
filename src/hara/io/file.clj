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
  [path]
  (.isDirectory (file path)))

(defn exists?
  [path]
  (.exists (file path)))

(defn absolute?
  [path]
  (.isAbsolute (file path)))

(defn executable?
  [path]
  (.canExecute (file path)))

(defn readable?
  [path]
  (.canRead (file path)))

(defn writeable?
  [path]
  (.canWrite (file path)))

(defn file?
  [path]
  (.isFile (file path)))

(defn hidden?
  [path]
  (.isHidden (file path)))

(defn parent
  [path]
  (.getParentFile (file path)))

(defn last-modified
  [path]
  (.lastModified (file path)))

(defn size
  [path]
  (.length (file path)))

(defn list-files
  [path]
  (seq (.listFiles (file path))))

(defn list-all-files
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
