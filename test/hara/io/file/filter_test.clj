(ns hara.io.file.filter
  (:use hara.test)
  (:require [hara.io.file.filter :refer :all]))

^{:refer hara.io.file.filter/pattern :added "2.4"}
(fact "takes a string as turns it into a regex pattern"

  (pattern ".clj")
  => #"\Q.\Eclj"
  
  (pattern "src/*")
  => #"src/.+")

^{:refer hara.io.file.filter/tag-filter :added "2.4"}
(fact "adds a tag to the filter to identify the type"

  (tag-filter {:pattern #"hello"})
  => (just {:tag :pattern
            :pattern #"hello"}))

^{:refer hara.io.file.filter/characterise-filter :added "2.4"}
(fact "characterises a filter based on type"

  (characterise-filter "src")
  => (just {:tag :pattern :pattern #"src"})

  (characterise-filter (fn [_] nil))
  => (just {:tag :fn :fn fn?}))
