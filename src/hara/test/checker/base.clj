(ns hara.test.checker.base
  (:require [hara.test.common :as common]
            [hara.common.primitives :refer [T]])
  (:import [hara.test.common Result Checker]))

(defn verify
  "verifies a value with it's associated check
   
   (verify (satisfies 2) 1)
   => (contains-in {:type :success
                    :data false
                    :checker {:tag :satisfies
                              :doc string?
                              :expect 2}
                    :actual 1
                    :from :verify})
   
   (verify (->checker #(/ % 0)) 1)
   => (contains {:type :exception
                 :data java.lang.ArithmeticException
                 :from :verify})"
  {:added "2.4"}
  [ck result]
  (let [out (try
              {:type :success :data (ck result)}
              (catch Throwable t
                {:type :exception :data t}))]
    (common/result (assoc out :checker ck :actual result :from :verify))))

(defn succeeded?
  "determines if the results of a check have succeeded
   
   (-> (satisfies Long)
       (verify 1)
       succeeded?)
   => true
   
   (-> (satisfies even?)
       (verify 1)
       succeeded?)
   => false"
  {:added "2.4"}
  [{:keys [type data]}]
  (and (= :success type)
       (= true data)))

(defn throws
  "checker that determines if an exception has been thrown
   
   ((throws Exception \"Hello There\")
    (common/map->Result
     {:type :exception
      :data (Exception. \"Hello There\")}))
   => true"
  {:added "2.4"}
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

(defn exactly
  "checker that allows exact verifications
 
   ((exactly 1) 1) => true
   
   ((exactly Long) 1) => false
 
   ((exactly number?) 1) => false"
  {:added "2.4"}
  [v]
  (common/checker
   {:tag :exactly
    :doc "Checks if the result exactly satisfies the condition"
    :fn (fn [res] (= (common/->data res) v))
    :expect v}))

(defn satisfies
  "checker that allows loose verifications
 
   ((satisfies 1) 1) => true
 
   ((satisfies Long) 1) => true
   
   ((satisfies number?) 1) => true
 
   ((satisfies #{1 2 3}) 1) => true
 
   ((satisfies [1 2 3]) 1) => false
 
   ((satisfies number?) \"e\") => false"
  {:added "2.4"}
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
  "creates a 'satisfies' checker if not already a checker
 
   ((->checker 1) 1) => true
 
   ((->checker (exactly 1)) 1) => true"
  {:added "2.4"}
  [x]
  (if (instance? Checker x)
    x
    (satisfies x)))
