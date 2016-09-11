(ns hara.io.file.path-test
  (:use hara.test)
  (:require [hara.io.file.path :refer :all]
            [hara.io.file.common :as common]
            [clojure.string :as string]))

^{:refer hara.io.file.path/relavitize :added "2.4"}
(fact "creates a string that takes notice of the user home"
  
  (relavitize ".")
  => (str common/*cwd* "/" ".")

  (relavitize "~/hello/world.txt")
  => (str common/*home* "/hello/world.txt")
  
  (relavitize "/usr/home")
  => "/usr/home")


^{:refer hara.io.file.path/path :added "2.4"}
(fact "returns a java.nio.file.Path object"

  (str (path "~"))
  => common/*home*

  (str (path "~/../shared/data"))
  => (str (->> (re-pattern common/*sep*)
               (string/split common/*home*)
               (butlast)
               (string/join "/"))
          "/shared/data")

  (str (path ["hello" "world.txt"]))
  => (str common/*cwd* "/hello/world.txt"))
