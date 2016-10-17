(ns hara.time.data.common-test
  (:use hara.test)
  (:require [hara.time.data.common :refer :all]
            [hara.protocol.time :as time])
  (:import [java.util Date TimeZone Calendar]))

^{:refer hara.time.data.common/calendar :added "2.2"}
(fact "creates a calendar to be used by the base date classes"
  (-> ^Calendar (calendar (Date. 0) (TimeZone/getTimeZone "GMT"))
      (.getTime))
  => #inst "1970-01-01T00:00:00.000-00:00")

^{:refer hara.time.data.common/local-timezone :added "2.2"}
(comment "returns the current timezone as a string"

  (local-timezone)
  => "Asia/Ho_Chi_Minh")

^{:refer hara.time.data.common/default-timezone :added "2.2"}
(comment "accesses the default timezone as a string"
         
  (default-timezone)  ;; getter
  => "Asia/Ho_Chi_Minh"

  (default-timezone "GMT")  ;; setter
  => "GMT")

^{:refer hara.time.data.common/default-type :added "2.2"}
(comment "accesses the default type for datetime"
  
  (default-type) ;; getter
  => clojure.lang.PersistentArrayMap

  (default-type Long) ;; setter
  => java.lang.Long)
