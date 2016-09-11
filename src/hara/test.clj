(ns hara.test
  (:require [hara.namespace.import :as ns]
            [hara.test.checker base collection logic]
            [hara.test form runner]))

(ns/import hara.test.checker.base
           [throws exactly satisfies anything]

           hara.test.checker.collection
           [contains just contains-in just-in throws-info]

           hara.test.checker.logic
           [any all]

           hara.test.form
           [fact facts =>]
           
           hara.test.runner
           [run run-namespace])

(defn -main
  []
  (let [{:keys [thrown failed] :as stats} (run)]
    (System/exit (+ thrown failed))))

(comment
  (require '[hara.event :refer :all])
  (-main)
  )
