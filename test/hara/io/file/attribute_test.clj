(ns hara.io.file.attribute-test
  (:use hara.test)
  (:require [hara.io.file.attribute :refer :all]))

^{:refer hara.io.file.attribute/owner :added "2.4"}
(fact "returns the owner of the file"

  (owner "project.clj")
  => string?)

^{:refer hara.io.file.attribute/lookup-owner :added "2.4"}
(fact "lookup the user registry for the name"

  (lookup-owner "WRONG")
  => (throws))

^{:refer hara.io.file.attribute/set-owner :added "2.4"}
(fact "sets the owner of a particular file"

  (set-owner "test" "WRONG")
  => (throws))

^{:refer hara.io.file.attribute/lookup-group :added "2.4"}
(fact "lookup the user registry for the name"

  (lookup-group "WRONG")
  => (throws))

^{:refer hara.io.file.attribute/attr :added "2.4"}
(fact "creates an attribute for input to various functions")

^{:refer hara.io.file.attribute/attr-value :added "2.4"}
(fact "adjusts the attribute value for input")

^{:refer hara.io.file.attribute/map->attr-array :added "2.4"}
(fact "converts a clojure map to an array of attrs")

^{:refer hara.io.file.attribute/attrs->map :added "2.4"}
(fact "converts the map of attributes into a clojure map")

^{:refer hara.io.file.attribute/attributes :added "2.4"}
(fact "shows all attributes for a given path")

^{:refer hara.io.file.attribute/set-attributes :added "2.4"}
(fact "sets all attributes for a given path")

