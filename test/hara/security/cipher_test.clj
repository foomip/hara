(ns hara.security.cipher-test
  (:use hara.test)
  (:require [hara.security.cipher :refer :all]
            [hara.io.encode :as encode]))

^{:refer hara.security.cipher/init-cipher :added "2.4"}
(comment "initializes cipher according to options")

^{:refer hara.security.cipher/operate :added "2.4"}
(comment "base function for encrypt and decrypt")

^{:refer hara.security.cipher/encrypt :added "2.4"}
(fact "encrypts a byte array using a key"

  (-> (encrypt (.getBytes "hello world")
               {:type "AES",
                :mode :secret,
                :format "RAW",
                :encoded "euHlt5sHWhRpbKZHjrwrrQ=="})
      (encode/to-hex))
  => "30491ab4427e45909f3d2f5d600b0f93")


^{:refer hara.security.cipher/decrypt :added "2.4"}
(fact "decrypts a byte array using a key"

  (-> (decrypt (encode/from-hex  "30491ab4427e45909f3d2f5d600b0f93")
               {:type "AES",
                :mode :secret,
                :format "RAW",
                :encoded "euHlt5sHWhRpbKZHjrwrrQ=="})
      (String.))
  => "hello world")
