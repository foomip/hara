(ns hara.test.common
  (:require [clojure.string :as string]
            [clojure.main :as main]))

(def ^:dynamic *settings* {:test-paths ["test"]})

(def ^:dynamic *meta* nil)

(def ^:dynamic *desc* nil)

(def ^:dynamic *path* nil)

(def ^:dynamic *id* nil)

(defonce ^:dynamic *accumulator* (atom nil))

(def ^:dynamic *print* #{:print-thrown :print-failure})

(defrecord Op []
  Object
  (toString [op]
    (str "#op." (name (:type op)) (dissoc (into {} op) :type))))

(defmethod print-method Op
  [v ^java.io.Writer w]
  (.write w (str v)))

(defn op [m]
  (map->Op m))
  
(defn op? [x]
  (instance? Op x))

(defrecord Result []
  Object
  (toString [res]
    (str "#result." (name (:type res)) (dissoc (into {} res) :type))))

(defmethod print-method Result
  [v ^java.io.Writer w]
  (.write w (str v)))

(defn result [m]
  (map->Result m))
  
(defn result? [x]
  (instance? Result x))
  
(defn ->data [res]
  (if (result? res) (:data res) res))

(defn function-string [func]
  (-> (type func)
      str
      (string/split #"\$")
      last
      main/demunge))

(defrecord Checker [fn]
  Object
  (toString [{:keys [expect tag]}]
    (str "#" (name tag) (cond (coll? expect)
                              expect

                              (fn? expect)
                              (str "<" (function-string expect) ">")

                              :else
                              (str "<" expect ">"))))
  
  clojure.lang.IFn
  (invoke [ck data] (let [func (:fn ck)] (func data))))

(defmethod print-method Checker
  [v ^java.io.Writer w]
  (.write w (str v)))

 (defn checker [m]
   (map->Checker m))
   
(defn checker? [x]
  (instance? Checker x))
