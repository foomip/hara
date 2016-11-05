(ns hara.io.classpath-test
  (:use hara.test)
  (:require [hara.io.classpath :refer :all]))

^{:refer hara.io.classpath/artifact :added "2.4"}
(fact "converts to various artifact formats"

  (artifact)
  => #{:path :coord :default :string}
  
  (artifact '[hello "2.5"])
  => {:group "hello",
      :artifact "hello",
      :extension "jar",
      :classifier nil,
      :version "2.5"}

  (artifact :string '[hello "2.5"])
  => "hello:hello:jar:2.5")
