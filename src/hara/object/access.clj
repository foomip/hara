(ns hara.object.access
  (:require [hara.object
             [read :as read]
             [write :as write]]))

(defn get-with-keyword [obj k]
  (if (instance? java.util.Map obj)
    (get obj k)
    (if-let [getter (-> obj read/meta-read :methods k)]
      (getter obj))))

(defn get-with-array [obj arr]
  (if (instance? java.util.Map obj)
    (select-keys obj arr)
    (let [getters (-> obj read/meta-read :methods (select-keys arr))]
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

(defn set
  ([obj m])
  ([obj k v]))

