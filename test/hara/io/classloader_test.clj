(ns hara.io.classloader-test
  (:use hara.test)
  (:require [hara.io.classloader :refer :all]
            [clojure.java.io :as io]))

^{:refer hara.io.classloader/delegation :added "2.2"}
(fact "returns a list of classloaders in order of top to bottom"
  
  (-> (Thread/currentThread)
      (.getContextClassLoader)
      (delegation))
  => list?)

^{:refer hara.io.classloader/to-url :added "2.2"}
(fact "constructs a `java.net.URL` object from a string"
  
  (str (to-url "/dev/null"))
  => "file:/dev/null")

^{:refer hara.io.classloader/url-classloader :added "2.2"}
(fact "returns a `java.net.URLClassLoader` from a list of strings"
  
  (->> (url-classloader ["/dev/null"])
       (.getURLs)
       (map str))
  => ["file:/dev/null"])

^{:refer hara.io.classloader/eval-in :added "2.2"}
(fact "given an environment, evaluates a form"
  
  (-> (url-classloader [+clojure-jar+
                        (-> (io/file "scripts")
                            (.getAbsolutePath)
                            (str "/"))])
      (new-env)
      (eval-in 
       '(do (require 'other-code)
            (eval '(other-code/add 1 2 3 4 5)))))
  => 15)

^{:refer hara.io.classloader/new-env :added "2.2"}
(fact "creates an new environment for isolated class loading"

  (new-env)
  => (contains-in
      {:classloader #(instance? ClassLoader %)
       :rt clojure.lang.RT
       :compiler clojure.lang.Compiler
       :fn {:read-string ifn? 
            :eval ifn?}})

  (new-env (url-classloader [+clojure-jar+]))
  => (contains-in
      {:classloader ClassLoader
       :rt Class 
       :compiler Class
       :fn {:read-string ifn?,
            :eval ifn?}}))
