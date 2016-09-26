(ns hara.io.file.option
  (:require [hara.class.enum :as enum]
            [hara.string.case :as case])
  (:import (java.nio.file AccessMode
                          FileVisitOption
                          FileVisitResult
                          LinkOption
                          StandardCopyOption
                          StandardOpenOption)
           (java.nio.file.attribute PosixFilePermission
                                    PosixFilePermissions)))

(defn enum->keyword
  [enum]
  (-> enum
      str
      case/lower-case
      case/spear-case
      keyword))

(defn enum-lookup
  "creates lookup table for enumerations
   (enum-lookup (enum/enum-values AccessMode))
   => {:read    AccessMode/READ
       :write   AccessMode/WRITE
       :execute AccessMode/EXECUTE}"
  {:added "2.4"}
  [enums]
  (->> enums
       (map (juxt (comp keyword case/spear-case case/lower-case)
                  identity))
       (into {})))

(def file-permissions
  (enum-lookup (enum/enum-values PosixFilePermission)))

(defn to-mode-string
  "transforms mode numbers to mode strings
 
   (to-mode-string \"455\")
   => \"r--r-xr-x\"
 
   (to-mode-string \"777\")
   => \"rwxrwxrwx\""
  {:added "2.4"}
  [s]
  (->> s
       (map (fn [ch]
              (case ch
                \0 "---"
                \1 "--x"
                \2 "-w-"
                \3 "-wx"
                \4 "r--"
                \5 "r-x"
                \6 "rw-"
                \7 "rwx")))
       (apply str)))

(defn to-mode-number
  "transforms mode numbers to mode strings
 
   (to-mode-number \"r--r-xr-x\")
   => \"455\"
 
   (to-mode-number \"rwxrwxrwx\")
   => \"777\""
  {:added "2.4"}
  [s]
  (->> (partition 3 s)
       (map #(apply str %))
       (map (fn [mode]
              (let [hist (frequencies mode)]
                (reduce-kv (fn [out k v]
                             (+ out (or (if (= 1 v)
                                          (case k
                                            \r 4
                                            \w 2
                                            \x 1
                                            nil))
                                        0)))
                        0
                        hist))))
       (apply str)))

(defn to-permissions
  "transforms mode to permissions
 
   (to-permissions \"455\")
   => (contains [:owner-read
                 :group-read
                 :group-execute
                 :others-read
                 :others-execute] :in-any-order)"
  {:added "2.4"}
  [s]
  (->> (to-mode-string s)
       (PosixFilePermissions/fromString)
       (map (comp keyword case/spear-case case/lower-case))))

(defn from-permissions
  "transforms permissions to mode
 
   (from-permissions [:owner-read
                      :group-read
                      :group-execute
                      :others-read
                      :others-execute])
   => \"455\""
  {:added "2.4"}
  [modes]
  (->> (map file-permissions modes)
       set
       (PosixFilePermissions/toString)
       (to-mode-number)))

(def access-modes
  (enum-lookup (enum/enum-values AccessMode)))

(def access-modes
  (enum-lookup (enum/enum-values AccessMode)))

(def copy-options
  (enum-lookup (enum/enum-values StandardCopyOption)))

(def file-visit-options
  (enum-lookup (enum/enum-values FileVisitOption)))

(def file-visit-results
  (enum-lookup (enum/enum-values FileVisitResult)))

(def link-options
  (enum-lookup (enum/enum-values LinkOption)))

(def open-options
  (enum-lookup (enum/enum-values StandardOpenOption)))

(def lookup
  (merge copy-options
         file-visit-options
         file-visit-results
         link-options
         open-options))
