(ns hara.io.file.path
  (:require [hara.io.file.common :as common])
  (:import (java.nio.file Files
                          Path
                          Paths)
           (java.io File
                    Writer)))

(def ^:dynamic *empty-string-array*
  (make-array String 0))

(defn normalise
  "creates a string that takes notice of the user home
   
   (normalise \".\")
   => (str common/*cwd* \"/\" \".\")
 
   (normalise \"~/hello/world.txt\")
   => (str common/*home* \"/hello/world.txt\")
   
   (normalise \"/usr/home\")
   => \"/usr/home\""
  {:added "2.4"}
  [s]
  (cond (= s "~")
        common/*home*

        (.startsWith ^String s (str "~" common/*sep*))
        (.replace ^String s "~" ^String common/*home*)
        
        (not (.startsWith ^String s common/*sep*))
        (str common/*cwd* common/*sep* s)
        
        :else s))

(defn path
  "returns a java.nio.file.Path object
 
   (str (path \"~\"))
   => common/*home*
 
   (str (path \"~/../shared/data\"))
   => (str (->> (re-pattern common/*sep*)
                (string/split common/*home*)
                (butlast)
                (string/join \"/\"))
           \"/shared/data\")
 
   (str (path [\"hello\" \"world.txt\"]))
   => (str common/*cwd* \"/hello/world.txt\")"
  {:added "2.4"}
  ([x]
   (cond (instance? Path x)
         x
         
         (string? x)
         (.normalize (Paths/get (normalise x) *empty-string-array*))

         (vector? x)
         (apply path x)

         (instance? java.net.URI x)
         (Paths/get x)

         (instance? File x)
         (path (.toString ^File x))
         
         :else
         (throw (Exception. (format "Input %s is not of the correct format" x)))))
  ([s & more]
   (.normalize (Paths/get (normalise (str s)) (into-array String more)))))

(defn path?
  "checks to see if the object is of type Path
 
   (path? (path \"/home\"))
   => true"
  {:added "2.4"}
  [x]
  (instance? Path x))

(defn section
  "creates a path object without normalisation
 
   (str (section \"home\"))
   => \"home\""
  {:added "2.4"}
  ([s & more]
   (Paths/get s (into-array String more))))

(defmethod print-method Path
  [^Path v ^Writer w]
  (.write w (str "#path:\"" (.toString v) "\"")))

(defmethod print-method File
  [^File v ^Writer w]
  (.write w (str "#file:\"" (.toString v) "\"")))

(defn to-file
  "creates a java.io.File object
 
   (str (to-file (section \"home\")))
   => \"home\""
  {:added "2.4"}
  [^Path path]
  (.toFile path))

