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

(defn op
  "creates an 'op' for evaluation
   
   (op {:type :form :form '(+ 1 1)})
   => hara.test.common.Op"
  {:added "2.4"}
  [m]
  (map->Op m))
  
(defn op?
  "checks to see if a datastructure is an 'Op'
   
   (op? (op {:type :form :form '(+ 1 1)}))
   => true"
  {:added "2.4"}
  [x]
  (instance? Op x))

(defrecord Result []
  Object
  (toString [res]
    (str "#result." (name (:type res)) (dissoc (into {} res) :type))))

(defmethod print-method Result
  [v ^java.io.Writer w]
  (.write w (str v)))

(defn result
  "creates a 'hara.test.common.Result' object
   
   (result {:type :success :data true})
   => hara.test.common.Result"
  {:added "2.4"}
  [m]
  (map->Result m))
  
(defn result?
  "checks to see if a datastructure is a 'hara.test.common.Result'
   
   (result? (result {:type :success :data true}))
   => true"
  {:added "2.4"}
  [x]
  (instance? Result x))
  
(defn ->data
  "coerces a checker result into data
 
   (->data 1) => 1
 
   (->data (result {:data 1}))
   => 1"
  {:added "2.4"}
  [res]
  (if (result? res) (:data res) res))

(defn function-string
  "returns the string representation of a function
 
   (function-string every?) => \"every?\"
 
   (function-string reset!) => \"reset!\""
  {:added "2.4"}
  [func]
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

 (defn checker
  "creates a 'hara.test.common.Checker' object
   
   (checker {:tag :anything :fn (fn [x] true)})
   => hara.test.common.Checker"
  {:added "2.4"}
  [m]
   (map->Checker m))
   
(defn checker?
  "checks to see if a datastructure is a 'hara.test.common.Checker'
   
   (checker? (checker {:tag :anything :fn (fn [x] true)}))
   => true"
  {:added "2.4"}
  [x]
  (instance? Checker x))

(defn evaluate
  "converts a form to a result
   
   (->> (evaluate '(+ 1 2 3))
        (into {}))
   => {:type :success, :data 6, :form '(+ 1 2 3), :from :evaluate}"
  {:added "2.4"}
  [form]
  (let [out (try
              {:type :success :data (eval form)}
              (catch Throwable t
                {:type :exception :data t}))]
    (result (assoc out :form form :from :evaluate))))
