(ns hara.test.checker.logic
  (:require [hara.test.common :as common]
            [hara.test.checker.base :as base]))

(defn exactly
  [v]
  (common/checker
   {:tag :exactly
    :doc "Checks if the result exactly satisfies the condition"
    :fn (fn [res] (= (common/->data res) v))
    :expect v}))

(defn any
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