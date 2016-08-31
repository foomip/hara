(ns hara.reflect.core.extract-test
  (:use hara.test)
  (:require [hara.reflect.core.extract :refer :all]
            [clojure.repl :as repl]))

^{:refer hara.reflect.core.extract/extract-to-var :added "2.1"}
(fact "extracts a class method into a namespace."

  (extract-to-var 'hash-without clojure.lang.IPersistentMap 'without [])

  (with-out-str (eval '(clojure.repl/doc hash-without)))
  => (str "-------------------------\n"
          "hara.reflect.core.extract-test/hash-without\n"
          "[[clojure.lang.IPersistentMap java.lang.Object]]\n"
          "  \n"
          "member: clojure.lang.IPersistentMap/without\n"
          "type: clojure.lang.IPersistentMap\n"
          "modifiers: instance, method, public, abstract\n")

  (eval '(hash-without {:a 1 :b 2} :a))
  => {:b 2})

^{:refer hara.reflect.core.extract/extract-to-ns :added "2.1"}
(fact "extracts all class methods into its own namespace."

  (map #(.sym %)
       (extract-to-ns 'test.string String [:private #"serial"]))
  => '[serialPersistentFields serialVersionUID])
