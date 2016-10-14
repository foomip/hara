(ns hara.object
  (:require [hara.namespace.import :as ns]
            [hara.object
             [enum :as enum]
             [read :as read]
             [write :as write]
             [map-like :as map-like]
             [string-like :as string-like]
             [vector-like :as vector-like]]))

(ns/import hara.object.read   [to-data meta-read read-getters read-all-getters read-reflect-fields]
           hara.object.write  [from-data meta-write write-setters write-all-setters write-reflect-fields])

(defmacro string-like
  ""
  [& {:as classes}]
  `(vector ~@(map (fn [[cls opts]]
                    `(string-like/extend-string-like ~cls ~opts))
                  classes)))

(defmacro map-like
  ""
  [& {:as classes}]
  `(vector ~@(map (fn [[cls opts]]
                    `(map-like/extend-map-like ~cls ~opts))
                  classes)))

(defmacro vector-like
  ""
  [& {:as classes}]
  `(vector ~@(map (fn [[cls opts]]
                    `(vector-like/extend-vector-like ~cls ~opts))
                  classes)))
