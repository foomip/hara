(ns hara.test.runner-test
  (:use [hara.test :exclude [run run-namespace]])
  (:require [hara.test.runner :refer :all]))

^{:refer hara.test.runner/accumulate :added "2.4"}
(fact "helper function for accumulating results over disparate facts and files")

^{:refer hara.test.runner/interim :added "2.4"}
(fact "summary function for accumulated results")

^{:refer hara.test.runner/run-namespace :added "2.4"}
(comment "run tests for namespace"

  (run-namespace 'hara.class.checks-test)
  => {:files 1, :thrown 0, :facts 5, :checks 9, :passed 9, :failed 0}
  
  ;; ---- Namespace (hara.class.checks-test) ----
  ;;
  ;; Summary (1)
  ;;   Files  1
  ;;   Facts  5
  ;;  Checks  9
  ;;  Passed  9
  ;;  Thrown  0
  ;;
  ;; Success (9)
  )
  
^{:refer hara.test.runner/run :added "2.4"}
(comment "run tests for entire project"
         
  (run)
  ;;  ---- Project (zcaudate/hara:124) ----
  ;;  documentation.hara-api
  ;;  documentation.hara-class
  ;;  documentation.hara-common
  ;;  documentation.hara-component
  ;;   ....
  ;;   ....
  ;;  hara.time.data.vector-test
  ;;
  ;;  Summary (99)
  ;;    Files  99
  ;;    Facts  669
  ;;   Checks  1151
  ;;   Passed  1150
  ;;   Thrown  0
  ;;
  ;;   Failed  (1)
  
  (run {:test-paths ["test/hara"]
        :include    ["^time"]
        :print      #{:print-facts}})
  => {:files 8, :thrown 0, :facts 54, :checks 127, :passed 127, :failed 0}
  ;;   Fact  [time_test.clj:9] - hara.time/representation?
  ;;   Info  "checks if an object implements the representation protocol"
  ;; Passed  2 of 2

  ;;   Fact  [time_test.clj:16] - hara.time/duration?
  ;;   Info  "checks if an object implements the duration protocol"
  ;; Passed  2 of 2

  ;; ...
)
