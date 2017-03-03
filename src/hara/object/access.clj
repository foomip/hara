(ns hara.object.access
  (:require [hara.object
             [read :as read]
             [write :as write]])
  (:refer-clojure :exclude [get set]))

(defn get-with-keyword
  [obj k]
  (if (instance? java.util.Map obj)
    (clojure.core/get obj k)
    (if-let [getter (-> obj type read/meta-read :methods k)]
      (getter obj))))

(defn get-with-array
  [obj arr]
  (if (instance? java.util.Map obj)
    (select-keys obj arr)
    (let [getters (-> obj type read/meta-read :methods (select-keys arr))]
      (reduce-kv (fn [i k v]
                   (assoc i k (v obj)))
                 {}
                 getters))))

(defn get
  [obj k]
  (cond (keyword? k)
        (get-with-keyword obj k)
        
        (sequential? k)
        (get-with-array obj k)))

(defn set-with-keyword
  [obj k v]
  (if-let [setter (-> obj type write/meta-write :methods k)]
    (setter obj v)
    (throw (Exception. (str "key " k " does not exist for " (.getName (type obj)))))))

(defn set
  ([obj m]
   (reduce-kv (fn [obj k v]
                (set-with-keyword obj k v)
                obj)
              obj
              m)
   obj)
  ([obj k v]
   (set-with-keyword obj k v)
   obj))

