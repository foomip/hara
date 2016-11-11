(ns hara.io.classpath
  (:require [hara.namespace.import :as ns]
            [hara.class.multi :as multi]
            [hara.io
             [archive :as archive]
             [classloader :as loader]]
            [hara.io.classpath
             [artifact :as artifact]
             [common :as common]
             [search :as search]]
            [clojure.java.io :as io])
  (:refer-clojure :exclude [resolve]))

(ns/import hara.io.classpath.common [resource-entry]
           hara.io.classpath.search [all-jars match-jars class-seq search])

(defn artifact
  "converts to various artifact formats
 
   (artifact)
   => #{:path :coord :default :string}
   
   (artifact '[hello \"2.5\"])
   => {:group \"hello\",
       :artifact \"hello\",
       :extension \"jar\",
       :classifier nil,
       :version \"2.5\"}
 
   (artifact :string '[hello \"2.5\"])
   => \"hello:hello:jar:2.5\""
  {:added "2.4"}
  ([] (-> artifact/artifact
          (.getMethodTable)
          (keys)
          (set)))
  ([x]
   (artifact/artifact nil x))
  ([type x]
   (artifact/artifact type x)))

(defn resolve-classloader
  "resolves a class or namespace to a physical location
 
   (resolve-classloader String)
   => [\"<java>/jre/lib/rt.jar\" \"java/lang/String.class\"]
 
   (resolve-classloader 'hara.test)
   => [nil \"<dev>/hara/src/hara/test.clj\"]"
  {:added "2.4"}
  ([x] (resolve-classloader x loader/+rt+))
  ([x loader]
   (if-let [path (-> (common/resource-entry x)
                     (io/resource loader)
                     (.getPath))]
     (cond (.startsWith path "file:")
           (-> (subs path (count "file:"))
               (clojure.string/split #"\!/"))

           (.startsWith path "/")
           [nil path]
           
           :else (throw (Exception. (str "Cannot process: " path)))))))

(defn resolve-path
  "resolves a class or namespace within a jar
   
   (resolve-path 'hara.test
                 '[im.chit/hara.test \"2.4.8\"])
   => [\"<.m2>/im/chit/hara.test/2.4.8/hara.test-2.4.8.jar\"
       \"hara/test.clj\"]
 
   (resolve-path 'hara.test
                 \"im.chit:hara.test:2.4.8\")
   => [\"<.m2>/im/chit/hara.test/2.4.8/hara.test-2.4.8.jar\"
       \"hara/test.clj\"]"
  {:added "2.4"}
  [x artifact]
  (let [path  (artifact/artifact :path artifact)
        entry (common/resource-entry x)]
    (if (archive/has? path entry)
      [path entry])))
