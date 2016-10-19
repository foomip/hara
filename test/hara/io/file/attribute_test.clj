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
(comment "shows all attributes for a given path"
  (attributes "project.clj")
  => {:owner "chris",
      :group "staff",
      :permissions "rw-r--r--",
      :file-key "(dev=1000004,ino=2351455)",
      :ino 2351455,
      :is-regular-file true.
      :is-directory false, :uid 501,
      :is-other false, :mode 33188, :size 4342,
      :gid 20, :ctime 1476755481000,
      :nlink 1,
      :last-access-time 1476755481000,
      :is-symbolic-link false,
      :last-modified-time 1476755481000,
      :creation-time 1472282953000,
      
      :dev 16777220, :rdev 0})

^{:refer hara.io.file.attribute/set-attributes :added "2.4"}
(comment "sets all attributes for a given path"

  (set-attributes "project.clj"
                  {:owner "chris",
                   :group "staff",
                   :permissions "rw-rw-rw-"})
  ;;=> #path:"/Users/chris/Development/chit/lucidity/project.clj"
  )

