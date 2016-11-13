(ns hara.io.classpath-test
  (:use hara.test)
  (:require [hara.io.classpath :refer :all]))

^{:refer hara.io.classpath/artifact :added "2.4"}
(fact "converts to various artifact formats"

  (artifact)
  => #{:path :coord :default :string}
  
  (artifact '[hello "2.5"])
  => (contains {:group "hello",
                :artifact "hello",
                :extension "jar",
                :classifier nil,
                :version "2.5"})

  (artifact :string '[hello "2.5"])
  => "hello:hello:jar:2.5")

^{:refer hara.io.classpath/resolve-classloader :added "2.4"}
(comment "resolves a class or namespace to a physical location"

  (resolve-classloader String)
  => ["<java>/jre/lib/rt.jar" "java/lang/String.class"]

  (resolve-classloader 'hara.test)
  => [nil "<dev>/hara/src/hara/test.clj"])

^{:refer hara.io.classpath/resolve-jar-entry :added "2.4"}
(comment "resolves a class or namespace within a jar"
  
  (resolve-jar-entry 'hara.test
                     '[im.chit/hara.test "2.4.8"])
  => ["<.m2>/im/chit/hara.test/2.4.8/hara.test-2.4.8.jar"
      "hara/test.clj"]

  (resolve-jar-entry 'hara.test
                     "im.chit:hara.test:2.4.8"
                     {:tag :coord})
  => '[[im.chit/hara.test "2.4.8"]
       "hara/test.clj"])

^{:refer hara.io.classpath/resolve-entry :added "2.4"}
(comment "resolves a class or namespace within a context"

  (resolve-entry 'hara.test
                 "im.chit:hara.test:2.4.8")
  => ["<.m2>/im/chit/hara.test/2.4.8/hara.test-2.4.8.jar"
      "hara/test.clj"]

  (resolve-entry 'hara.test
                 ["im.chit:hara.test:2.4.8"
                  "im.chit:hara.string:2.4.8"]
                 {:tag :coord})
  => '[[im.chit/hara.test "2.4.8"] "hara/test.clj"]
  
  
  (resolve-entry 'hara.test
                 '[[im.chit/hara.test "2.4.8"]
                   [im.chit/hara.string "2.4.8"]])
  => ["<.m2>/im/chit/hara.test/2.4.8/hara.test-2.4.8.jar"
      "hara/test.clj"]

  (resolve-entry 'hara.test
                 '[im.chit/hara.test "2.4.8"])
  => ["<.m2>/im/chit/hara.test/2.4.8/hara.test-2.4.8.jar"
      "hara/test.clj"]

  (resolve-entry 'hara.test
                 '[im.chit/hara.string "2.4.8"])
  => nil)
