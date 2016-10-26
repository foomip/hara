(ns hara.test.runner
  (:require [hara.common.primitives :refer [uuid]]
            [hara.test.common :as common]
            [hara.test.checker.base :as checker]
            [hara.test.form.print :as print]
            [hara.test.form.listener :as listener]
            [hara.io.project :as project]
            [hara.event :as event]
            [hara.io.ansii :as ansii])
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
  "run tests for namespace
 
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
   "
  {:added "2.4"}
  ([] (run-namespace (.getName *ns*)))
  ([ns]
   (run-namespace ns (project/project)))
  ([ns project]
   (run-namespace ns common/*settings* project))
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
  "run tests for entire project
          
   (run)
   ;;  ---- Project (im.chit/hara:124) ----
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
   
   (run {:test-paths [\"test/hara\"]
         :include    [\"^time\"]
         :print      #{:print-facts}})
   => {:files 8, :thrown 0, :facts 54, :checks 127, :passed 127, :failed 0}
   ;;   Fact  [time_test.clj:9] - hara.time/representation?
   ;;   Info  \"checks if an object implements the representation protocol\"
   ;; Passed  2 of 2
 
   ;;   Fact  [time_test.clj:16] - hara.time/duration?
   ;;   Info  \"checks if an object implements the duration protocol\"
   ;; Passed  2 of 2
 
   ;; ..."
  {:added "2.4"}
  ([] (run (project/project)))
  ([project]
   (run common/*settings* project))
  ([settings project]
   (binding [*warn-on-reflection* false
             common/*settings* (merge common/*settings* settings)
             common/*print* (or (:print settings) common/*print*)]
     (let [all-files (project/all-files (:test-paths common/*settings*)
                                        {}
                                        project)
           proj (:name project)]
       (println "\n")
       (println (-> (format "---- Project (%s%s) ----" (if proj (str proj ":") "") (count all-files))
                    (ansii/style  #{:blue :bold})))
       (println "")
       (let [facts (accumulate (fn [id sink]
                                 (doseq [[ns path] (-> all-files seq sort)]
                                   (println (ansii/style ns  #{:blue}))
                                   (binding [common/*path* path]
                                     (load-file path)))))
             results (interim facts)]
         (event/signal {:test :bulk :results results})
         (reduce-kv (fn [out k v]
                      (assoc out k (count v)))
                    {}
                    results))))))
