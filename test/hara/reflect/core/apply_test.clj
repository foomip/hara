(ns hara.reflect.core.apply-test
  (:use hara.test)
  (:require [hara.reflect.core.apply :refer :all]))

^{:refer hara.reflect.core.apply/apply-element :added "2.1"}
(fact "apply the class element to arguments"

  (->> (apply-element "123" "value" [])
       (map char))
  => [\1 \2 \3])
