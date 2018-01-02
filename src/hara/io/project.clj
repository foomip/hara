(ns hara.io.project
  (:require [hara.io.file :as fs]))

(defn project
  "returns `project.clj` as a map"
  {:added "2.4"}
  ([] (project "project.clj"))
  ([path]
   (let [path  (fs/path path)
         root  (.getParent path)
         pform (read-string (slurp (str path)))
         [_ full version] (take 3 pform)
         group    (or (namespace full)
                      (str full))
         artifact (name full)
         proj  (->> (drop 3 pform)
                    (concat [:name full
                             :artifact artifact
                             :group group
                             :version version
                             :root (str root)])
                    (apply hash-map))
         proj (-> proj
                  (update-in [:source-paths] (fnil identity ["src"]))
                  (update-in [:test-paths] (fnil identity ["test"])))]
     proj)))

(defn project-name
  "returns the name, read from project.clj
 
   (project-name)
   => 'zcaudate/hara"
  {:added "2.4"}
  ([] (project-name "project.clj"))
  ([path]
   (-> (fs/code path)
       first
       second)))

(defn file-namespace
  "reads the namespace of the given path
 
   (file-namespace \"src/hara/io/project.clj\")
   => 'hara.io.project"
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
       (get 'hara.io.project-test))
   => #(.endsWith ^String % \"/test/hara/io/project_test.clj\")"
  {:added "2.4"}
  ([] (all-files ["."]))
  ([paths] (all-files paths {}))
  ([paths opts]
   (all-files paths opts (project)))
  ([paths opts project]
   (let [filt (-> {:include [#".clj$"]}
                  (merge opts)
                  (update-in [:exclude]
                             conj
                             fs/link?))
         result (->> paths
                     (map #(fs/path (:root project) %))
                     (mapcat #(fs/select % filt))
                     (map str)
                     (map (juxt file-namespace identity))
                     (into {}))]
     (dissoc result nil))))

(defn file-lookup
  ""
  ([] (file-lookup (project)))
  ([project]
   (all-files (concat (:source-paths project)
                      (:test-paths project))
              {}
              project)))

(comment
  (all-files ["."]
             {}
             (project "../../chit/hara/project.clj")))
