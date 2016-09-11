(ns hara.io.file.path
  (:require [hara.io.file.common :as common])
  (:import (java.nio.file Files
                          Path
                          Paths)
           (java.io File
                    Writer)))

(def ^:dynamic *empty-string-array*
  (make-array String 0))

(defn relavitize
  "creates a string that takes notice of the user home
   
   (relavitize \".\")
   => (str common/*cwd* \"/\" \".\")
 
   (relavitize \"~/hello/world.txt\")
   => (str common/*home* \"/hello/world.txt\")
   
   (relavitize \"/usr/home\")
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
         (.normalize ^Path x)
         
         (string? x)
         (.normalize (Paths/get (relavitize x) *empty-string-array*))

         (vector? x)
         (apply path x)

         (instance? java.net.URI x)
         (Paths/get x)

         (instance? File x)
         (path (.toString ^File x))
         
         :else
         (throw (Exception. (str "Input " x " is not of the correct format")))))
  ([s & more]
   (.normalize (Paths/get (relavitize s) (into-array String more)))))

(defmethod print-method Path
  [^Path v ^Writer w]
  (.write w (str "#path:\"" (.toString v) "\"")))

(defmethod print-method File
  [^File v ^Writer w]
  (.write w (str "#file:\"" (.toString v) "\"")))

(defn to-file
  [^Path path]
  (.toFile path))

