(ns hara.io.classpath.artifact-test
  (:use hara.test)
  (:require [hara.io.classpath.artifact :refer :all]
            [hara.io.classpath.common :as common]))

^{:refer hara.io.classpath.artifact/rep->coord :added "2.4"}
(fact "encodes the rep to a coordinate"

  (-> {:group "im.chit" :artifact "hara" :version "2.4.0"}
      (map->Rep)
      (rep->coord))
  => '[im.chit/hara "2.4.0"])

^{:refer hara.io.classpath.artifact/rep->path :added "2.4"}
(comment "encodes the rep to a path"

  (-> {:group "im.chit" :artifact "hara" :version "2.4.0"}
      (map->Rep)
      (rep->path))
  => "<.m2>/im/chit/hara/2.4.0/hara-2.4.0.jar")

^{:refer hara.io.classpath.artifact/rep->string :added "2.4"}
(fact "encodes the rep to a string"

  (-> {:group "im.chit" :artifact "hara" :version "2.4.0"}
      (map->Rep)
      (rep->string))
  => "im.chit:hara:2.4.0")

^{:refer hara.io.classpath.artifact/string->rep :added "2.4"}
(fact "converts a string to a rep instance"

  (string->rep "im.chit:hara:2.4.0")
  => (contains {:group "im.chit"
                :artifact "hara"
                :version "2.4.0"}))


^{:refer hara.io.classpath.artifact/coord->rep :added "2.4"}
(fact "converts a coord to a rep instance"

  (coord->rep '[im.chit/hara "2.4.0"])
  => (contains {:group "im.chit"
                :artifact "hara"
                :version "2.4.0"}))

^{:refer hara.io.classpath.artifact/path->rep :added "2.4"}
(fact "converts a path to a rep instance"

  (path->rep (str common/*local-repo* "/im/chit/hara/2.4.0/hara-2.4.0.jar"))
  => (contains {:group "im.chit"
                :artifact "hara"
                :version "2.4.0"}))

^{:refer hara.io.classpath.artifact/rep :added "2.4"}
(comment "converts various formats to a rep"

  (rep '[im.chit/hara "2.4.0"])
  => 'im.chit:hara:jar:2.4.0

  (rep "im.chit:hara:2.4.0")
  => 'im.chit:hara:jar:2.4.0)

^{:refer hara.io.classpath.artifact/artifact :added "2.4"}
(fact "converts various artifact formats"

  (artifact :string '[im.chit/hara "2.4.0"])
  => "im.chit:hara:jar:2.4.0"

  (artifact :path "im.chit:hara:2.4.0")
  => (str common/*local-repo*
          "/im/chit/hara/2.4.0/hara-2.4.0.jar"))
