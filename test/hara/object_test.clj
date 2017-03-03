(ns hara.object-test
  (:use hara.test)
  (:require [hara.object :refer :all]))

^{:refer hara.object/map-like :added "2.3"}
(fact "creates an accessibility layer for map-like objects"

  (map-like
   org.eclipse.jgit.revwalk.RevCommit
   {:tag "commit"
    :include [:commit-time :name :author-ident :full-message]})
  
  (map-like
   org.eclipse.jgit.lib.PersonIdent
   {:tag "person"
    :exclude [:time-zone]})
  
  (map-like
   org.eclipse.jgit.api.Status
   {:tag "status"
    :display (fn [m]
               (reduce-kv (fn [out k v]
                            (if (and (or (instance? java.util.Collection v)
                                         (instance? java.util.Map v))
                                     (empty? v))
                              out
                              (assoc out k v)))
                          {}
                          m))}))

^{:refer hara.object/string-like :added "2.3"}
(fact "creates an accessibility layer for string-like objects"
  
  (string-like
   java.io.File
   {:tag "path"
    :read (fn [f] (.getPath f))
    :write (fn [^String path] (java.io.File. path))})

  (to-data (java.io.File. "/home"))
  => "/home"

  (from-data "/home" java.io.File)
  => java.io.File

  ;; Enums are automatically string-like
  
  (to-data java.lang.Thread$State/NEW)
  => "NEW")

^{:refer hara.object/vector-like :added "2.3"}
(fact "creates an accessibility layer for vector-like objects"

  (vector-like
   org.eclipse.jgit.revwalk.RevWalk
   {:tag "commits"
    :read (fn [^org.eclipse.jgit.revwalk.RevWalk walk]
            (->> walk (.iterator) to-data))}))

