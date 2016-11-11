(ns hara.io.encode.hex-test
  (:use hara.test)
  (:require [hara.io.encode.hex :refer :all]))

^{:refer hara.io.encode.hex/hex-chars :added "2.4"}
(fact "turns a byte into two chars"

  (hex-chars 255)
  => [\f \f]
  
  (hex-chars 42)
  => [\2 \a])

^{:refer hara.io.encode.hex/to-hex-chars :added "2.4"}
(fact "turns a byte array into a hex char array")

^{:refer hara.io.encode.hex/to-hex :added "2.4"}
(fact "turns a byte array into hex string"

  (to-hex (.getBytes "hello"))
  => "68656c6c6f")

^{:refer hara.io.encode.hex/from-hex-chars :added "2.4"}
(fact "turns two hex characters into a byte value"

  (byte (from-hex-chars \2 \a))
  => 42)

^{:refer hara.io.encode.hex/from-hex :added "2.4"}
(fact "turns a hex string into a sequence of bytes"

  (String. (from-hex "68656c6c6f"))
  => "hello")
