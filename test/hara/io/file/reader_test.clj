(ns hara.io.file.reader-test
  (:use hara.test)
  (:require [hara.io.file.reader :refer :all]))

^{:refer hara.io.file.reader/charset-default :added "2.4"}
(fact "returns the default charset"
  
  (charset-default)
  => "UTF-8")

^{:refer hara.io.file.reader/charset-list :added "2.4"}
(comment "returns the list of available charset"
  
  (charset-list)
  => ("Big5" "Big5-HKSCS" ... "x-windows-iso2022jp"))

^{:refer hara.io.file.reader/charset :added "2.4"}
(comment "constructs a charset object from a string"
  (charset "UTF-8")
  => java.nio.charset.Charset)

^{:refer hara.io.file.reader/reader :added "2.4"}
(fact "creates a reader for a given input"

  (-> (reader :pushback "project.clj")
      (read)
      second)
  => 'im.chit/hara)

^{:refer hara.io.file.reader/reader-types :added "2.4"}
(fact "returns the types of readers"

  (reader-types)
  => (contains [:input-stream :buffered :file
                :string :pushback :char-array
                :piped :line-number]))
