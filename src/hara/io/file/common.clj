(ns hara.io.file.common)

(def ^:dynamic *cwd* (.getCanonicalPath (java.io.File. ".")))

(def ^:dynamic *sep* (System/getProperty "file.separator"))

(def ^:dynamic *home* (System/getProperty "user.home"))

(def ^:dynamic *tmp-dir (System/getProperty "java.io.tmpdir"))