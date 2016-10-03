(ns hara.test.form.process
  (:require [hara.test.common :as common]
            [hara.test.checker.base :as checker]
            [hara.event :as event]))

(defmulti process
  ""
  :type)

(defmethod process :form
  [{:keys [form meta] :as op}]
  (let [result (assoc (common/evaluate form) :meta meta)]
    (event/signal {:test :form :result result})
    result))

(defmethod process :test-equal
  [{:keys [input output meta] :as op}]
  (let [actual   (common/evaluate input)
        expected (common/evaluate output)
        checker  (assoc (checker/->checker (common/->data expected))
                        :form (:form expected))
        result   (assoc (checker/verify checker actual) :meta meta)]
    (event/signal {:test :check :result result})
    result))

