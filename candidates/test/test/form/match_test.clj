(ns hara.test.form.match-test
  ;;(:use hara.test)
  (:require [hara.test.form.match :refer :all]))

(match-options
 '{:line 57,
   :column 0,
   :refer hara.test.checker.logic/any,
   :desc nil,
   :ns hara.test.form
   :tags #{}}
 '{:includes [{:refers [hara.test.checker.logic/any]}]
   :excludes [{:refers [hara.test.checker.logic/any]}]})

(match-base
 '{:line 57,
   :column 0,
   :refer hara.test.checker.logic/any,
   :desc nil,
   :ns hara.test.form
   :tags #{}}
 '{:tags        [:web]
   :refers      [hara.test.checker.logic/any]
   :namespaces  [hara.test
                 hara.data]}
 false)
