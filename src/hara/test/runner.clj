(ns hara.test.runner
  (:require [hara.test.common :as common]
            [hara.io.file :as fs]
            [hara.event :as event]
            [hara.common.primitives :refer [uuid]]
            [hara.test.checker.base :as checker]
            [hara.test.form.print :as print]
            [hara.display.ansii :as ansii])
  (:refer-clojure :exclude [test])
  (:import java.io.File))
 
(defn read-namespace [path]
  (->> (fs/source-seq path)
       (filter #(-> % first (= 'ns)))
       first
       second))

(defn all-files
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

(defn bind-id [source sink id]
  (add-watch source id (fn [_ _ _ n]
                         (if (= (:id n) id)
                           (swap! sink conj n)))))

(defn unbind-id [source id]
  (remove-watch source id))

(defn accumulate [func]
  (let [id (-> (uuid) str keyword)
        sink (atom [])]
    (bind-id common/*accumulator* sink id)
    (binding [common/*id* id]
      (func id sink))
    (unbind-id common/*accumulator* id)
    @sink))

(defn interim [facts]
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

(defn project-name []
  (-> (fs/source-seq "project.clj")
      first
      second))

(defn run
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
  (run-namespace 'hara.common.checks-test))


