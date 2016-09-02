(ns hara.test.form-test
  (:use [hara.test :exclude [fact facts]])
  (:require [hara.test.form :refer :all]
            [hara.test.form.process :as process]))

^{:refer hara.test.form/split :added "2.4"}
(fact "creates a sequence of pairs from a loose sequence"
  (split '[(def a 1)
           (+ a 3)
           => 5])
  => (contains-in [{:type :form,
                    :form '(def a 1)}
                   {:type :test-equal,
                    :input '(+ a 3),
                    :output 5}]))

^{:refer hara.test.form/collect :added "2.4"}
(fact "makes sure that all returned verified results are true"
  (->> (split '[(+ 1 1) => 2
                (+ 1 2) => 3])
       (mapv process/process)
       (collect {}))
  => true)

^{:refer hara.test.form/skip :added "2.4"}
(fact "returns the form with no ops evaluated")

^{:refer hara.test.form/fact :added "2.4"}
(fact "top level macro for test definitions")

^{:refer hara.test.form/facts :added "2.4"}
(fact "top level macro for test definitions")
