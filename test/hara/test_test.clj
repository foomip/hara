(ns hara.test-test
  (:use hara.test))

^{:refer hara.test/print-options :added "2.4"}
(fact "output options for test results"

  (print-options)
  => #{:disable :reset :default :all :list :current :help}

  (print-options :default)
  => #{:print-bulk :print-failure :print-thrown}

  (print-options :all)
  => #{:print-bulk
       :print-facts-success
       :print-failure
       :print-thrown
       :print-facts
       :print-success})

^{:refer hara.test/process-args :added "2.4"}
(fact "processes input arguments")

^{:refer hara.test/-main :added "2.4"}
(fact "main entry point for leiningen")

