(ns hara.io.classloader
  (:require [hara.common.checks :as checks]
            [clojure.string :as string]
            [hara.reflect :as reflect])
  (:import [java.net URLClassLoader]
           [java.lang ClassLoader]))

(def +rt+ (.getClassLoader clojure.lang.RT))

(def +base+ (.getParent ^ClassLoader +rt+))

(def +clojure-jar+
  (let [paths (if (instance? URLClassLoader +rt+)
                (->> (.getURLs ^ClassLoader +rt+)
                     (map #(.getFile ^java.net.URL %)))
                
                (string/split (System/getProperty "java.class.path") #":"))]
    (->> paths
         (filter #(.contains ^String % "/org/clojure/clojure/"))
         (first))))

(defn delegation
  "returns a list of classloaders in order of top to bottom
   
   (-> (Thread/currentThread)
       (.getContextClassLoader)
       (delegation))
   => list?"
  {:added "2.2"}
  [cl]
  (->> cl
       (iterate (fn [^ClassLoader cl] (.getParent cl)))
       (take-while identity)
       (reverse)))

(defn to-url
  "constructs a `java.net.URL` object from a string
   
   (str (to-url \"/dev/null\"))
   => \"file:/dev/null\""
  {:added "2.2"}
  [path]
  (cond (checks/url? path)
        path

        (string? path)
        (java.net.URL. (str "file:" path))

        :else (throw (Exception. "Not Implemented"))))

(defn url-classloader
  "returns a `java.net.URLClassLoader` from a list of strings
   
   (->> (url-classloader [\"/dev/null\"])
        (.getURLs)
        (map str))
   => [\"file:/dev/null\"]"
  {:added "2.2"}
  ([urls]
   (url-classloader urls +base+))
  ([urls parent]
   (URLClassLoader. (->> urls
                         (map to-url)
                         (into-array java.net.URL))
                    parent)))

(defmethod print-method URLClassLoader
  [^URLClassLoader v ^java.io.Writer w]
  (.write w (str "#loader@"
                 (.hashCode v)
                 (->> (.getURLs v)
                      (mapv #(-> (str %)
                                 (string/split #"/")
                                 last))))))

(defn eval-in
  "given an environment, evaluates a form
   
   (-> (url-classloader [+clojure-jar+
                         (-> (io/file \"scripts\")
                             (.getAbsolutePath)
                             (str \"/\"))])
       (new-env)
       (eval-in 
        '(do (require 'other-code)
             (eval '(other-code/add 1 2 3 4 5)))))
  => 15"
  {:added "2.2"}
  [env form]
  (let [thrd (Thread/currentThread)
        curr (.getContextClassLoader thrd)
        _ (.setContextClassLoader thrd (:classloader env))
        string (pr-str form)
        [obj data] (try
                     (-> (str "(let [res " string "] [res (pr-str res)])")
                         ((-> env :fn :read-string))
                         ((-> env :fn :eval)))
                     (finally
                       (.setContextClassLoader thrd curr)))]
    (try (read-string data)
         (catch Exception e
           obj))))

(defn new-env
  "creates an new environment for isolated class loading
 
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
            :eval ifn?}})"
  {:added "2.2"}
  ([] (new-env +rt+))
  ([^ClassLoader cl]
   (let [rt (.loadClass cl "clojure.lang.RT")
         cp (.loadClass cl "clojure.lang.Compiler")
         read-string (reflect/query-class rt ["readString" 1 :#])
         eval-form   (reflect/query-class cp ["eval" 1 :#])
         env {:classloader cl
              :rt rt
              :compiler cp
              :fn {:read-string read-string
                   :eval eval-form}}]
     (do (eval-in env '(require (quote clojure.main)
                                (quote clojure.core)))
         env))))
