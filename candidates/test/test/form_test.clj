(ns hara.test.form-test
  (:require [hara.test.common :as common]))


#_(binding [common/*settings* {:includes [{:tags #{:web}}]}]
  (fact
    2 => 1))
