(ns hara.io.encode
  (:require [hara.io.encode.hex :as hex]
            [hara.namespace.import :as ns])
  (:import java.util.Base64))

(ns/import hara.io.encode.hex [to-hex to-hex-chars from-hex])

(defn to-base64-bytes
  "turns a byte array into a base64 encoding
 
   (-> (.getBytes \"hello\")
       (to-base64-bytes)
       (String.))
   => \"aGVsbG8=\""
  {:added "2.4"}
  [bytes]
  (.encode (Base64/getEncoder)
           bytes))

(defn to-base64
  "turns a byte array into a base64 encoded string
 
   (-> (.getBytes \"hello\")
       (to-base64))
   => \"aGVsbG8=\""
  {:added "2.4"}
  [bytes]
  (.encodeToString (Base64/getEncoder)
                   bytes))

(defn from-base64
  "turns a base64 encoded string into a byte array
 
   (-> (from-base64 \"aGVsbG8=\")
       (String.))
   => \"hello\""
  {:added "2.4"}
  [input]
  (.decode (Base64/getDecoder)
           input))
