(ns hara.test.runner
  (:require [hara.common.primitives :refer [uuid]]
            [hara.test.common :as common]
            [hara.test.checker.base :as checker]
            [hara.test.form.print :as print]
            [hara.test.form.listener :as listener]
            [hara.io.file :as fs]
            [hara.event :as event]
            [hara.display.ansii :as ansii])
  (:import java.io.File))

(defn project-name
  "returns the name, read from project.clj
 
   (project-name)
   => 'im.chit/hara"
  {:added "2.4"}
  []
  (-> (fs/source-seq "project.clj")
      first
      second))

(defn read-namespace
  "reads the namespace of the given path
 
   (read-namespace \"src/hara/test/runner.clj\")
   => 'hara.test.runner"
  {:added "2.4"}
  [path]
  (try
    (->> (fs/source-seq path)
         (filter #(-> % first (= 'ns)))
         first
         second)
    (catch Throwable t
      (println path "Cannot be loaded"))))

(defn all-files
  "returns all the clojure files in a directory
   
   (+ (count (all-files [\"test/hara\"]))
      (count (all-files [\"test/documentation\"])))
   => (count (all-files [\"test\"]))
 
   (-> (all-files [\"test\"])
       (get 'hara.test.runner-test))
   => #(.endsWith ^String % \"/test/hara/test/runner_test.clj\")"
  {:added "2.4"}
  ([] (all-files (:test-paths common/*settings*)))
  ([paths]
   (->> paths
        (mapcat fs/list-all-files)
        (filter (fn [^File f]
                 (let [name (.getName f)]
                   (and (.endsWith name ".clj")
                        (not (.startsWith name "."))))))
        (map #(.getCanonicalPath ^File %))
        (map (juxt read-namespace identity))
        (into {}))))

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
   (println "\n")
   (println (-> (format "---- Namespace (%s) ----" (str ns))
                (ansii/style  #{:blue :bold})))
   (binding [*warn-on-reflection* false]
     (let [facts (accumulate (fn [id sink]
                             (when-let [path (get (all-files) ns)]
                               (binding [common/*path* path]
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
  ([]
   (let [all-ns (-> (all-files) seq sort)
         proj (project-name)]
     (println "\n")
     (println (-> (format "---- Project (%s%s) ----" (if proj (str proj ":") "") (count all-ns))
                  (ansii/style  #{:blue :bold})))
     (println "")
     (binding [*warn-on-reflection* false]
       (let [facts (accumulate (fn [id sink]
                                 (doseq [[ns path] all-ns]
                                   (println (ansii/style ns  #{:blue}))
                                   (binding [common/*path* path]
                                     (load-file path)))))
             results (interim facts)]
         (event/signal {:test :bulk :results results})
         (reduce-kv (fn [out k v]
                      (assoc out k (count v)))
                    {}
                    results))))))

(comment
  (run-namespace)
  (run-namespace 'hara.common.checks-test)
  (run-namespace 'hara.test.checker.util-test))


