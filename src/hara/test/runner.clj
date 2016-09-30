(ns hara.test.runner
  (:require [hara.common.primitives :refer [uuid]]
            [hara.test.common :as common]
            [hara.test.checker.base :as checker]
            [hara.test.form.print :as print]
            [hara.test.form.listener :as listener]
            [hara.io.project :as project]
            [hara.event :as event]
            [hara.display.ansii :as ansii])
  (:import java.io.File))

(defn accumulate
  "helper function for accumulating results over disparate facts and files"
  {:added "2.4"}
  [func]
  (let [id (-> (uuid) str keyword)
        sink (atom [])
        source common/*accumulator*]
    (add-watch source id (fn [_ _ _ n]
                         (if (= (:id n) id)
                           (swap! sink conj n))))
    (binding [common/*id* id]
      (func id sink))
    (remove-watch source id)
    @sink))

(defn interim
  "summary function for accumulated results"
  {:added "2.4"}
  [facts]
  (let [results (mapcat :results facts)
        checks  (filter #(-> % :from (= :verify)) results)
        forms   (filter #(-> % :from (= :evaluate)) results)
        thrown  (filter #(-> % :type (= :exception)) forms)
        passed  (filter checker/succeeded? checks)
        failed  (filter (comp not checker/succeeded?) checks)
        facts   (filter (comp not empty? :results) facts)
        files   (->> checks
                     (map (comp :path :meta))
                     (frequencies)
                     (keys))]
    {:files  files
     :thrown thrown
     :facts  facts
     :checks checks
     :passed passed
     :failed failed}))

(defn run-namespace
  "run tests for namespace"
  {:added "2.4"}
  ([] (run-namespace (.getName *ns*)))
  ([ns]
   (run-namespace ns common/*settings*))
  ([ns settings]
   (run-namespace ns settings (project/project)))
  ([ns settings project]
   (binding [*warn-on-reflection* false
             common/*settings* (merge common/*settings* settings)
             common/*print* (or (:print settings) common/*print*)]
     (println "\n")
     (println (-> (format "---- Namespace (%s) ----" (str ns))
                  (ansii/style  #{:blue :bold})))
     (let [all-files (project/all-files (:test-paths common/*settings*)
                                        {}
                                        project)
           facts (accumulate (fn [id sink]
                               (when-let [path (get all-files ns)]
                                 (binding [common/*path* path]
                                   (prn path)
                                   (load-file path)))))
           results (interim facts)]
       (event/signal {:test :bulk :results results})
       (reduce-kv (fn [out k v]
                    (assoc out k (count v)))
                  {}
                  results)))))

(defn run
  "run tests for entire project"
  {:added "2.4"}
  ([] (run common/*settings*))
  ([settings]
   (run settings (project/project)))
  ([settings project]
   (binding [*warn-on-reflection* false
             common/*settings* (merge common/*settings* settings)
             common/*print* (or (:print settings) common/*print*)]
     (let [all-files (project/all-files (:test-paths common/*settings*)
                                        {}
                                        project)
           proj (:name project)]
       (println "\n")
       (println (-> (format "---- Project (%s%s) ----" (if proj (str proj ":") "") (count all-ns))
                    (ansii/style  #{:blue :bold})))
       (println "")
       (let [facts (accumulate (fn [id sink]
                                 (doseq [[ns path] (-> all-ns seq sort)]
                                   (println (ansii/style ns  #{:blue}))
                                   (binding [common/*path* path]
                                     (load-file path)))))
             results (interim facts)]
         (event/signal {:test :bulk :results results})
         (reduce-kv (fn [out k v]
                      (assoc out k (count v)))
                    {}
                    results))))))
