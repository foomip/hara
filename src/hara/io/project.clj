(ns hara.io.project
  (:require [hara.io.file :as fs]))

(def ^:dynamic *current* nil)

(defn project
  ([] (project "project.clj"))
  ([path]
   (let [path  (fs/path path)
         root  (.getParent path)
         pform (read-string (slurp (str path)))
         [_ name version] (take 3 pform)
         proj  (->> (drop 3 pform)
                    (concat [:name name
                             :version version
                             :root (str root)])
                    (apply hash-map))
         proj (-> proj
                  (update-in [:source-paths] (fnil identity ["src"]))
                  (update-in [:test-paths] (fnil identity ["test"])))]
     (alter-var-root #'*current* (constantly proj))
     proj)))

(defn project-name
  "returns the name, read from project.clj
 
   (project-name)
   => 'im.chit/hara"
  {:added "2.4"}
  ([] (project-name "project.clj"))
  ([path]
   (-> (fs/code path)
       first
       second)))

(defn file-namespace
  "reads the namespace of the given path
 
   (read-namespace \"src/hara/test/runner.clj\")
   => 'hara.test.runner"
  {:added "2.4"}
  [path]
  (try
    (->> (fs/code path)
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
  ([] (all-files ["."]))
  ([paths] (all-files paths {}))
  ([paths opts]
   (let [filt (-> {:include [#".clj$"]}
                  (merge opts)
                  (update-in [:exclude]
                             conj
                             (fn [{:keys [path] :as m}]
                               (fs/link? path))))
         result (->> paths
                     (mapcat #(fs/select % filt))
                     (map str)
                     (map (juxt file-namespace identity))
                     (into {}))]
     (dissoc result nil))))
