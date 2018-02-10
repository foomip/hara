(ns hara.reflect.core.delegate-test
  (:use hara.test)
  (:require [hara.reflect.core.delegate :refer :all]
            [hara.io.environment :as env]))

(env/run [{:java {:major 9}}
          {}]
  (def world-array (byte-array (map byte "world"))))

(env/run [{}
          {:java {:major 8}}]
  (def world-array (char-array "world")))

^{:refer hara.reflect.core.delegate/delegate :added "2.1"}
(fact "Allow transparent field access and manipulation to the underlying object."

  (def a "hello")
  (def >a  (delegate a))

  (mapv char (>a :value)) => [\h \e \l \l \o]
  
  (>a :value world-array)
  a => "world")
