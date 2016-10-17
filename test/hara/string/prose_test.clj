(ns hara.string.prose-test
  (:use hara.test)
  (:require [hara.string.prose :refer :all]))

^{:refer hara.string.prose/has-quotes? :added "2.4"}
(fact "checks if a string has quotes"

  (has-quotes? "\"hello\"")
  => true)

^{:refer hara.string.prose/strip-quotes :added "2.4"}
(fact "gets rid of quotes in a string"

  (strip-quotes "\"hello\"")
  => "hello")

^{:refer hara.string.prose/whitespace? :added "2.4"}
(fact "checks if the string is all whitespace"

  (whitespace? "        ")
  => true)

^{:refer hara.string.prose/escape-dollars :added "2.4"}
(fact "for regex purposes, escape dollar signs in strings"

  (escape-dollars "$")
  => string?)

^{:refer hara.string.prose/escape-newlines :added "2.4"}
(fact "makes sure that newlines are printable"

  (escape-newlines "\\n")
  => "\\n")

^{:refer hara.string.prose/escape-escapes :added "2.4"}
(fact "makes sure that newlines are printable"

  (escape-escapes "\\n")
  => "\\\\n")

^{:refer hara.string.prose/escape-quotes :added "2.4"}
(fact "makes sure that quotes are printable in string form"

  (escape-quotes "\"hello\"")
  => "\\\"hello\\\"")