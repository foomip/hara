(ns hara.test.form.match-test
  (:use hara.test)
  (:require [hara.test.form.match :refer :all]))

^{:refer hara.test.form.match/match-base :added "2.4"}
(fact "determines whether a term matches with a filter"
  (match-base {:tags #{:web}}
              {:tags #{:web}}
              false)
  => [true false false]
  (match-base {:refer 'user/foo
               :namespace 'user}
              {:refers '[user/other]
               :namespaces '[foo bar]}
              true)
  => [true false false])

^{:refer hara.test.form.match/match-include :added "2.4"}
(fact "determines whether inclusion is a match"
  (match-include {:tags #{:web}}
                 {:tags #{:web}})
  => true
  
  (match-include {:refer 'user/foo
                  :namespace 'user}
                 {})
  => true)

^{:refer hara.test.form.match/match-exclude :added "2.4"}
(fact "determines whether exclusion is a match"
  (match-exclude {:tags #{:web}}
                 {:tags #{:web}})
  => true
  (match-exclude {:refer 'user/foo
                  :namespace 'user}
                 {})
  => false)

^{:refer hara.test.form.match/match-options :added "2.4"}
(fact "determines whether a set of options can match"
  (match-options {:tags #{:web}
                  :refer 'user/foo}
                 {:includes [{:tages #{:web}}]
                  :excludes []})
  => true

  (match-options {:tags #{:web}
                  :refer 'user/foo}
                 {:includes [{:tages #{:web}}]
                  :excludes [{:refers '[user/foo]}]})
  => false)
