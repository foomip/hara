(ns hara.io.encode.hex)

(def hex-array [\0 \1 \2 \3 \4 \5 \6 \7 \8 \9 \a \b \c \d \e \f])

(defn hex-chars
  "turns a byte into two chars
 
   (hex-chars 255)
   => [\\f \\f]
   
   (hex-chars 42)
   => [\2 \\a]"
  {:added "2.4"}
  [b]
  (let [v (bit-and b 0xFF)]
    [(hex-array (bit-shift-right v 4))
     (hex-array (bit-and v 0x0F))]))

(defn to-hex-chars
  "turns a byte array into a hex char array"
  {:added "2.4"}
  [bytes]
  (char-array (mapcat hex-chars bytes)))

(defn to-hex
  "turns a byte array into hex string
 
   (to-hex (.getBytes \"hello\"))
   => \"68656c6c6f\""
  {:added "2.4"}
  [bytes]  
  (String. (to-hex-chars bytes)))

(defn from-hex-chars
  "turns two hex characters into a byte value
 
   (byte (from-hex-chars \2 \\a))
   => 42"
  {:added "2.4"}
  [c1 c2]
  (unchecked-byte 
   (+ (bit-shift-left (Character/digit c1 16) 4)
      (Character/digit c2 16))))

(defn from-hex
  "turns a hex string into a sequence of bytes
 
   (String. (from-hex \"68656c6c6f\"))
   => \"hello\""
  {:added "2.4"}
  [s] 
  (byte-array (map #(apply from-hex-chars %) (partition 2 s))))
 

