(ns hara.test
  (:require [hara.namespace.import :as ns]
            [hara.test.checker base collection logic]
            [hara.test [common :as common] form runner]))

(ns/import hara.test.checker.base
           [throws exactly satisfies anything]

           hara.test.checker.collection
           [contains just contains-in just-in throws-info]

           hara.test.checker.logic
           [any all is-not]

           hara.test.form
           [fact facts =>]
           
           hara.test.runner
           [run run-namespace])

@#'is-not


(defn print-options
  ([] (print-options :help))
  ([opts]
   (cond (set? opts)
         (alter-var-root #'common/*print*
                         (constantly opts))

         (= :help opts)
         #{:help :current :default :list :disable :reset :all}

         (= :current opts) common/*print*
         
         (= :default opts)
         #{:print-thrown :print-failure :print-bulk}
         
         (= :list opts)
         #{:print-thrown :print-success :print-facts :print-facts-success :print-failure :print-bulk}

         (= :disable opts)
         (alter-var-root #'common/*print* (constantly #{}))
         
         (= :reset opts)
         (alter-var-root #'common/*print* (constantly #(print-options :default)))
         
         (= :all opts)
         (alter-var-root #'common/*print* (constantly (print-options :list))))))

(defn process-args [args]
  (->> (map read-string args)
       (map (fn [x]
              (cond (symbol? x)
                    (keyword (name x))

                    (keyword? x) x

                    (string? x) (keyword x))))
       set))

(defn -main
  ([& args]
   (let [args (process-args args)
         {:keys [thrown failed] :as stats} (run)
         res (+ thrown failed)]
     (if (get args :exit)
       (System/exit res)
       res))))
