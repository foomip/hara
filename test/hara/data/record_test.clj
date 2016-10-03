(ns hara.data.record-test
  (:use hara.test)
  (:require [hara.data.record :as record]))

^{:refer hara.data.record/empty :added "2.1"}
(fact "creates an empty record from an existing one"
  
  (defrecord Database [host port])
  
  (record/empty (Database. "localhost" 8080))
  => (just {:host nil :port nil}))
