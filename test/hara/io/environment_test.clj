(ns hara.io.environment-test
  (:use hara.test)
  (:require [hara.io.environment :as env]))

^{:refer hara.io.environment/clojure-version :added "2.2"}
(fact "returns the current clojure version"
  (env/clojure-version)
  => (contains
      {:major anything,
       :minor anything,
       :incremental anything
       :qualifier anything}))

^{:refer hara.io.environment/java-version :added "2.2"}
(fact "returns the current java version"
  (env/java-version)
  => (contains
      {:major anything,
       :minor anything,
       :incremental anything
       :qualifier anything}))

^{:refer hara.io.environment/version :added "2.2"}
(fact "alternate way of getting clojure and java version"
  (env/version :clojure)
  => (env/clojure-version)

  (env/version :java)
  => (env/java-version))

^{:refer hara.io.environment/satisfied-elem :added "2.8"}
(fact "compares the :major, :minor and :incremental values of a version map"

  (env/satisfied-elem {:major 1 :minor 8 :incremental 1}
                      {:major 1 :minor 7 :incremental 6}
                      >=)
  => true
  
  (env/satisfied-elem {:major 1 :minor 7 :incremental 1}
                      {:major 1 :minor 7 :incremental 6}
                      >=)
  => false

  ^:hidden
  (env/satisfied-elem {:major 1 :minor 8 :incremental 1 :qualifier 0}
                      {:major 1 :minor 8 :incremental 1 :qualifier 0}
                      >=)
  => true)

^{:refer hara.io.environment/satisfied-compare :added "2.8"}
(fact "checks multiple values of version maps are all suitable"

  (env/satisfied-compare {:clojure {:major 1 :minor 6}
                          :java {:major 1 :minor 6}}
                         {:clojure {:major 1 :minor 7}
                          :java {:major 1 :minor 6}}
                         <=)
  => true)

^{:refer hara.io.environment/satisfied :added "2.2"}
(fact "checks to see if the current version satisfies the given constraints"
  (env/satisfied {:java    {:major 1 :minor 7}
                  :clojure {:major 1 :minor 6}})
  => true

  (env/satisfied [{:java    {:major 1 :minor 5}}
                  {}])
  => true

  (env/satisfied [{}
                  {:java    {:major 11 :minor 0}}])
  => true)

^{:refer hara.io.environment/init :added "2.2"}
(fact "only attempts to load the files when the minimum versions have been met"
  (env/init {:java    {:major 1 :minor 8}
             :clojure {:major 1 :minor 6}}
            (:require [hara.time.data.zone
                       java-time-zoneid]
                      [hara.time.data.instant
                       java-time-instant]
                      [hara.time.data.format
                       java-time-format-datetimeformatter])
            (:import java.time.Instant)))

^{:refer hara.io.environment/run :added "2.2"}
(fact "only runs the following code is the minimum versions have been met"
  (env/run {:java    {:major 1 :minor 8}
            :clojure {:major 1 :minor 6}}
    (Instant/ofEpochMilli 0)))

^{:refer hara.io.environment/properties :added "2.2"}
(fact "returns jvm properties in a nested map for easy access"
  (->> (env/properties)
       :os)
  => (contains {:arch anything
                :name anything
                :version anything}))

(comment
  (use 'lucid.unit)
  (lucid.unit/import))
