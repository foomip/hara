(ns hara.io.encode-test
  (:use hara.test)
  (:require [hara.io.encode :refer :all]))

^{:refer hara.io.encode/to-base64-bytes :added "2.4"}
(fact "turns a byte array into a base64 encoding"

  (-> (.getBytes "hello")
      (to-base64-bytes)
      (String.))
  => "aGVsbG8=")

^{:refer hara.io.encode/to-base64 :added "2.4"}
(fact "turns a byte array into a base64 encoded string"

  (-> (.getBytes "hello")
      (to-base64))
  => "aGVsbG8=")

^{:refer hara.io.encode/from-base64 :added "2.4"}
(fact "turns a base64 encoded string into a byte array"

  (-> (from-base64 "aGVsbG8=")
      (String.))
  => "hello")
