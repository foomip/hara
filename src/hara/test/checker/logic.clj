(ns hara.test.checker.logic
  (:require [hara.test.common :as common]
            [hara.test.checker.base :as base]))

(defn any
  "checker that allows `or` composition of checkers
 
   (mapv (any even? 1)
         [1 2 3 4 5])
   => [true true false true false]"
  {:added "2.4"}
  [& cks]
  (let [cks (mapv base/->checker cks)]
    (common/checker
     {:tag :any
      :doc "Checks if the result matches any of the checkers"
      :fn (fn [res]
            (or (->> cks
                     (map #(base/verify % res))
                     (some base/succeeded?))
                false))
      :expect cks})))

(defn all
  "checker that allows `and` composition of checkers
 
   (mapv (all even? #(< 3 %))
         [1 2 3 4 5])
   => [false false false true false]"
  {:added "2.4"}
  [& cks]
  (let [cks (mapv base/->checker cks)]
    (common/checker
     {:tag :any
      :doc "Checks if the result matches all of the checkers"
      :fn (fn [res]
            (->> cks
                 (map #(base/verify % res))
                 (every? base/succeeded?)))
      :expect cks})))
