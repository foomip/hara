(ns hara.io.classpath.search-test
  (:use hara.test)
  (:require [hara.io.classpath
             [artifact :as artifact]
             [search :refer :all]]
            [hara.io.classloader :as cls]
            [hara.io.archive :as archive]))

^{:refer hara.io.classpath.search/all-jars :added "2.4"}
(comment "gets all jars, either on the classloader or coordinate"

  (-> (all-jars)
      count)
  => 150

  (-> (all-jars '[org.eclipse.aether/aether-api "1.1.0"])
      count)
  => 1)

^{:refer hara.io.classpath.search/match-jars :added "2.4"}
(comment "matches jars from any representation"

  (match-jars '[org.eclipse.aether/aether-api "1.1.0"])
  => ("<.m2>/org/eclipse/aether/aether-api/1.1.0/aether-api-1.1.0.jar"))

^{:refer hara.io.classpath.search/class-seq :added "2.4"}
(comment "creates a sequence of class names"

  (-> (all-jars '[org.eclipse.aether/aether-api "1.1.0"])
      (class-seq)
      (count))
  => 128)

^{:refer hara.io.classpath.search/search-match :added "2.4"}
(fact "constructs a matching function for filtering"

  ((search-match #"hello") "hello.world")
  => true

  ((search-match java.util.List) java.util.ArrayList)
  => true)

^{:refer hara.io.classpath.search/search :added "2.4"}
(comment "searches a pattern for class names"

  (->> (.getURLs cls/+base+)
       (map #(-> % str (subs (count "file:"))))
       (filter #(.endsWith % "jfxrt.jar"))
       (class-seq)
       (search [#"^javafx.*[A-Za-z0-9]Builder$"])
       (take 5))
  => (javafx.animation.AnimationBuilder
      javafx.animation.FadeTransitionBuilder
      javafx.animation.FillTransitionBuilder
      javafx.animation.ParallelTransitionBuilder
      javafx.animation.PathTransitionBuilder))
