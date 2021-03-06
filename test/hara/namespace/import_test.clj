(ns hara.namespace.import-test
  (:use hara.test)
  (:require [hara.namespace.import :refer :all]
            [hara.common.checks])
  (:refer-clojure :exclude [import]))

^{:refer hara.namespace.import/import-var :added "2.0"}
(fact "Imports a single var from one namespace to the current one."

  (import-var 'ifl #'clojure.core/if-let)
  => anything ; #'hara.namespace.import-test/ifl
  (eval '(ifl [a 1] (inc a))) => 2

  ^:hidden
  (ns-unmap *ns* 'ifl))

^{:refer hara.namespace.import/import-namespace :added "2.0"}
(fact "Imports all or a selection of vars from one namespace to the current one."

  (import-namespace 'hara.common.checks '[bytes? long?])
  (eval '(long? 1))  => true
  (eval '(bytes? 1)) => false

  ^:hidden
  (ns-unmap *ns* 'long?)
  (ns-unmap *ns* 'bytes?))

^{:refer hara.namespace.import/import :added "2.0"}
(fact "Imports all or a selection of vars from one namespace to the current one."

  (import hara.common.checks [bytes? long?]) => nil
  (eval '(long? 1))  => true
  (eval '(bytes? 1)) => false

  (import hara.common.checks :all) => nil
  (eval '(bigint? 1)) => false

  ^:hidden
  (doseq [k (keys (ns-interns *ns*))]
    (ns-unmap *ns* k)))
