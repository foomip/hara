(ns hara.security.meta
  (:import (javax.crypto.spec SecretKeySpec)
           (com.sun.crypto.provider DESKey DESedeKey)))

(defn secret-fn [type]
  (fn [bytes]
    (SecretKeySpec. bytes type)))

(def settings
  {"AES"    {:constructor (secret-fn "AES")
             :size #{128 192 256}}
   "DES"    {:constructor #(DESKey. %)
             :size #{56}}
   "DESede" {:constructor #(DESedeKey. %)
             :size #{112 168}}})


((secret-fn "DESede")
 )
