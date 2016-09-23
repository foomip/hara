(ns hara.class.enum
 (:require [hara.class.inheritance :as inheritance]))

(defn enum?
  "Check to see if class is an enum type
 
   (enum? java.lang.annotation.ElementType) => true
 
   (enum? String) => false"
  {:added "2.2"}
  [type]
  (if (-> (inheritance/ancestor-list type)
          (set)
          (get java.lang.Enum))
    true false))

(defn enum-values
  "Returns all values of an enum type
 
   (->> (enum-values ElementType)
        (map str))
   => (contains [\"TYPE\" \"FIELD\" \"METHOD\" \"PARAMETER\" \"CONSTRUCTOR\"]
                :in-any-order :gaps-ok)"
  {:added "2.2"}
  [type]
  (let [method (.getMethod type "values" (make-array Class 0))
        values (.invoke method nil (object-array []))]
    (seq values)))