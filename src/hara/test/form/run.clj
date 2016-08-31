(ns hara.test.form.run
  (:require [hara.test.common :as common]
            [hara.test.checker.base :as checker]
            [hara.test.form.listener :as listener]
            [hara.event :as event]))

(defn evaluate
  [form]
  (let [out (try
              {:type :success :data (eval form)}
              (catch Throwable t
                {:type :exception :data t}))]
    (common/result (assoc out :form form :from :evaluate))))

(defmulti process :type)

(defmethod process :form
  [{:keys [form meta] :as op}]
  (let [result (assoc (evaluate form) :meta meta)]
    (event/signal {:test :form :result result})
    result))

(defmethod process :test-equal
  [{:keys [input output meta] :as op}]
  (let [actual   (evaluate input)
        expected (evaluate output)
        checker  (assoc (checker/->checker (common/->data expected))
                        :form (:form expected))
        result   (assoc (checker/verify checker actual) :meta meta)]
    (event/signal {:test :check :result result})
    result))

(defn collect [meta results]
  (event/signal {:id common/*id* :test :fact :meta meta :results results})
  (->> results
       (filter #(-> % :from :verify))
       (mapv :data)
       (every? true?)))

(defn skip [meta]
  (event/signal {:id common/*id* :test :fact :meta meta :results [] :skipped true})
  :skipped)
