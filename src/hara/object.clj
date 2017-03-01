(ns hara.object
  (:require [hara.namespace.import :as ns]
            [hara.object
             [enum :as enum]
             [read :as read]
             [write :as write]
             [map-like :as map-like]
             [string-like :as string-like]
             [vector-like :as vector-like]])
  (:refer-clojure :exclude [get set]))

(ns/import

 hara.object.access
 [get
  set]
 
 hara.object.read   
 [to-data
  to-map
  meta-read
  read-getters
  read-all-getters
  read-reflect-fields]

 hara.object.write  
 [from-data
  meta-write
  write-setters
  write-all-setters
  write-reflect-fields])

(defmacro string-like
  "creates an accessibility layer for string-like objects
   
   (string-like
    java.io.File
    {:tag \"path\"
     :read (fn [f] (.getPath f))
     :write (fn [^String path] (java.io.File. path))})
 
   (to-data (java.io.File. \"/home\"))
   => \"/home\"
 
   (from-data \"/home\" java.io.File)
   => java.io.File
 
   ;; Enums are automatically string-like
   
   (to-data java.lang.Thread$State/NEW)
   => \"NEW\""
  {:added "2.3"}
  [& {:as classes}]
  `(vector ~@(map (fn [[cls opts]]
                    `(string-like/extend-string-like ~cls ~opts))
                  classes)))

(defmacro map-like
  "creates an accessibility layer for map-like objects
 
   (map-like
    org.eclipse.jgit.revwalk.RevCommit
    {:tag \"commit\"
     :include [:commit-time :name :author-ident :full-message]})
 
   (map-like
    org.eclipse.jgit.lib.PersonIdent
    {:tag \"person\"
     :exclude [:time-zone]})

   
   (map-like
    org.eclipse.jgit.api.Status
    {:tag \"status\"
     :display (fn [m]
                (reduce-kv (fn [out k v]
                             (if (and (or (instance? java.util.Collection v)
                                          (instance? java.util.Map v))
                                      (empty? v))
                               out
                               (assoc out k v)))
                           {}
                           m))})"
  {:added "2.3"}
  [& {:as classes}]
  `(vector ~@(map (fn [[cls opts]]
                    `(map-like/extend-map-like ~cls ~opts))
                  classes)))

(defmacro vector-like
  "creates an accessibility layer for vector-like objects
 
   (vector-like
   org.eclipse.jgit.revwalk.RevWalk
    {:tag \"commits\"
     :read (fn [^org.eclipse.jgit.revwalk.RevWalk walk]
             (->> walk (.iterator) to-data))})"
  {:added "2.3"}
  [& {:as classes}]
  `(vector ~@(map (fn [[cls opts]]
                    `(vector-like/extend-vector-like ~cls ~opts))
                  classes)))
