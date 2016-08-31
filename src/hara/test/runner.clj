(ns hara.test.runner
  (:require [hara.test.common :as common]
            [hara.io.file :as fs]
            [hara.event :as event]
            [hara.common.primitives :refer [uuid]]
            [hara.test.checker.base :as checker])
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

(defn stats [acc]
  (let [results (mapcat :results acc)
        checks  (filter #(-> % :from (= :verify)) results)
        forms   (filter #(-> % :from (= :evaluate)) results)
        thrown  (filter #(-> % :type (= :exception)) forms)
        passed  (filter checker/succeeded? checks)
        files   (->> checks
                     (map (comp :path :meta))
                     (frequencies)
                     (keys))]
    {:files  (count files)
     :thrown (count thrown)
     :facts  (count acc)
     :checks (count checks)
     :passed (count passed)
     :failed (- (count checks) (count passed))}))

(defn run-namespace
  ([] (run-namespace (.getName *ns*)))
  ([ns]
   (let [acc (accumulate (fn [id sink]
                           (when-let [path (get (all-files) ns)]
                             (binding [common/*path* path]
                               (load-file path)))))]
     (event/signal {:test :bulk :results acc})
     (stats acc))))

(defn run
  ([]
   (binding [*warn-on-reflection* false]
     (let [acc (accumulate (fn [id sink]
                             (doseq [path (vals (all-files))]
                               (binding [common/*path* path]
                                 (load-file path)))))]
       (event/signal {:test :bulk :results acc})
       (stats acc)))))

(comment
  (first (test-ns 'hara.common.checks-test))

  (test-ns 'hara.io.scheduler.tab-test)
  {:files 84, :thrown 0, :facts 567, :checks 1019, :passed 1019}
  {:files 84, :thrown 0, :facts 567, :checks 1019, :passed 1017}
  {:files 84, :thrown 1, :facts 567, :checks 1015, :passed 1012}
  {:files 84, :thrown 14, :facts 567, :checks 967, :passed 964}
  {:files 83, :thrown 21, :facts 568, :checks 956, :passed 945}
  {:files 83, :thrown 26, :facts 567, :checks 945, :passed 924}
  {:files 83, :thrown 26, :facts 567, :checks 945, :passed 909})


