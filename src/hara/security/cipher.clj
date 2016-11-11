(ns hara.security.cipher
  (:require [hara.security
             [key :as key]
             [provider :as provider]])
  (:import (java.security Key
                          SecureRandom)
           (javax.crypto Cipher)
           (javax.crypto.spec IvParameterSpec)))

(defn init-cipher
  "initializes cipher according to options"
  {:added "2.4"}
  [^Cipher cipher
   ^long mode
   ^Key key {:keys [params random iv]}]
  (cond iv
        (.init cipher mode key (IvParameterSpec. iv))

        (and params random)
        (.init cipher mode key params ^SecureRandom random)

        params
        (.init cipher mode key params)
        
        random
        (.init cipher mode key ^SecureRandom random)

        :else
        (.init cipher mode key)))

(defn operate
  "base function for encrypt and decrypt"
  {:added "2.4"}
  ([mode bytes key {:keys [algorithm] :as opts}]
   (let [key  (key/->key key)
         algo (or algorithm (key/key-type key))
         cipher (doto (provider/cipher algo)
                  (init-cipher mode
                               key
                               opts))]
     (.doFinal ^Cipher cipher bytes))))

(defn encrypt
  "encrypts a byte array using a key
 
   (-> (encrypt (.getBytes \"hello world\")
                {:type \"AES\",
                 :mode :secret,
                 :format \"RAW\",
                 :encoded \"euHlt5sHWhRpbKZHjrwrrQ==\"})
       (encode/to-hex))
  => \"30491ab4427e45909f3d2f5d600b0f93\""
  {:added "2.4"}
  ([bytes key]
   (encrypt bytes key {}))
  ([bytes key {:keys [algorithm params random iv] :as opts}]
   (operate Cipher/ENCRYPT_MODE bytes key opts)))

(defn decrypt
  "decrypts a byte array using a key
 
   (-> (decrypt (encode/from-hex  \"30491ab4427e45909f3d2f5d600b0f93\")
                {:type \"AES\",
                 :mode :secret,
                 :format \"RAW\",
                 :encoded \"euHlt5sHWhRpbKZHjrwrrQ==\"})
       (String.))
  => \"hello world\""
  {:added "2.4"}
  ([bytes key]
   (decrypt bytes key {}))
  ([bytes key {:keys [algorithm params random iv] :as opts}]
   (operate Cipher/DECRYPT_MODE bytes key opts)))

(defn operate-stream
  ""
  ([mode bytes key {:keys [algorithm] :as opts}]
   (let [algo (or algorithm (key/key-type key))
         cipher (doto (provider/cipher algo)
                  (init-cipher mode
                               key
                               opts))])))

(defn encrypt-stream
  ""
  ([stream key]
   (encrypt-stream stream key {}))
  ([stream key {:keys [algorithm params random iv] :as opts}]
   (operate-stream Cipher/ENCRYPT_MODE stream key opts)))

(defn decrypt-stream
  ""
  ([stream key]
   (decrypt-stream stream key {}))
  ([stream key {:keys [algorithm params random iv] :as opts}]
   (operate-stream Cipher/DECRYPT_MODE stream key opts)))
