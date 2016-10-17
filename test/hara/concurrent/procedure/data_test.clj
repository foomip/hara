(ns hara.concurrent.procedure.data-test
  (:use hara.test)
  (:require [hara.concurrent.procedure.data :refer :all]))

^{:refer hara.concurrent.procedure.data/registry :added "2.2"}
(fact "creates a registry for running threads"

  (into {} (registry))
  => (contains {:store clojure.lang.Atom}))


^{:refer hara.concurrent.procedure.data/cache :added "2.2"}
(fact "creates a cache for procedure results"

  (into {} (cache))
  => (contains {:store clojure.lang.Atom}))
