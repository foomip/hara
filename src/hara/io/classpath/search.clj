(ns hara.io.classpath.search
  (:require [hara.io
             [archive :as archive]
             [classloader :as cls]]
            [hara.io.classpath
             [common :as common]
             [artifact :as artifact]]))

(defn all-jars
  "gets all jars, either on the classloader or coordinate
 
   (-> (all-jars)
       count)
   => 150
 
   (-> (all-jars '[org.eclipse.aether/aether-api \"1.1.0\"])
       count)
   => 1"
  {:added "2.4"}
  [& [x :as coords]]
  (cond (nil? x)
        (->> (cls/delegation cls/+rt+)
             (mapcat #(.getURLs %))
             (map #(.getFile %))
             (filter #(.endsWith % ".jar")))

        :else
        (map #(artifact/artifact :path %)
             coords)))

(defn match-jars
  "matches jars from any representation
 
   (match-jars '[org.eclipse.aether/aether-api \"1.1.0\"])
   => (\"<.m2>/org/eclipse/aether/aether-api/1.1.0/aether-api-1.1.0.jar\")"
  {:added "2.4"}
  ([names] (match-jars names []))
  ([names coords]
   (let [patterns (map (fn [name]
                         (->> [name ".*"]
                              (artifact/artifact :path)
                              (re-pattern)))
                       names)]
     (filter (fn [path]
               (some (fn [pattern]
                       (re-find pattern path))
                     patterns))
             (apply all-jars coords)))))

(defn class-seq
  "creates a sequence of class names
 
   (-> (all-jars '[org.eclipse.aether/aether-api \"1.1.0\"])
       (class-seq)
       (count))
   => 128"
  {:added "2.4"}
  ([] (class-seq nil))
  ([coords]
   (->> (for [jar (apply all-jars coords)
              item (archive/list jar)]
          (str item))
        (filter #(.endsWith % ".class"))
        (map #(.substring % 1 (- (.length %) 6)))
        (map #(.replaceAll % "/" ".")))))

(defn search
  "searches a pattern for class names
 
   (->> (.getURLs cls/+base+)
        (map #(.getFile %))
        (filter #(.endsWith % \"jfxrt.jar\"))
        (apply search #\"^javafx.*[^\\.]Builder$\")
        (take 5))
   => (javafx.animation.AnimationBuilder
      javafx.animation.FadeTransitionBuilder
       javafx.animation.FillTransitionBuilder
       javafx.animation.ParallelTransitionBuilder
       javafx.animation.PathTransitionBuilder)"
  {:added "2.4"}
  [pattern & coords]
  (->> (class-seq coords)
       (filter #(re-find pattern %))
       (sort)
       (map #(Class/forName %))))
