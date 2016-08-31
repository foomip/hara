(ns hara.test.checker.base
  (:require [hara.test.common :as common]
            [hara.common.primitives :refer [T]])
  (:import [hara.test.common Result Checker]))

(defn verify
  [ck result]
  (let [out (try
              {:type :success :data (ck result)}
              (catch Throwable t
                {:type :exception :data t}))]
    (common/result (assoc out :checker ck :actual result :from :verify))))

(defn succeeded?
  [{:keys [type data]}]
  (and (= :success type)
       (= true data)))

(defn throws
  ([]  (throws Throwable))
  ([e] (throws e nil))
  ([e msg]
   (common/checker
    {:tag :throws
     :doc "Checks if an exception has been thrown"
     :fn (fn [{:keys [^Throwable data type]}]
           (and (= :exception type)
                (instance? e data)
                (if msg
                  (= msg (.getMessage data))
                  true)))
     :expect {:exception e :message msg}})))

(defn satisfies
  [v]
  (common/checker
   {:tag :satisfies
    :doc "Checks if the result can satisfy the condition:"
    :fn (fn [res]
          (let [data (common/->data res)]
            (cond (= data v) true
                  
                  (class? v) (instance? v data)

                  (map? v) (= (into {} data) v)
                  
                  (vector? v) (= data v)
                  
                  (ifn? v) (boolean (v data))
                  
                  :else false)))
    :expect v}))

(def anything
  (satisfies T))

(defn ->checker
  [x]
  (if (instance? Checker x)
    x
    (satisfies x)))
