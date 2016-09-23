(ns hara.object.enum-test
  (:use hara.test)
  (:require [hara.object.write :as write])
  (:import java.lang.annotation.ElementType))

^{:refer hara.object.write/from-data!enum :added "2.2"}
(fact "from-data works with enum"
  (write/from-data "CONSTRUCTOR" ElementType)
  => ElementType/CONSTRUCTOR)
