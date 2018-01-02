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
  (:import (clojure.lang IPersistentVector
                         Symbol))
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
           [nil path]))))

(defn resolve-jar-entry
  "resolves a class or namespace within a jar
   
   (resolve-jar-entry 'hara.test
                      '[zcaudate/hara.test \"2.4.8\"])
   => [\"<.m2>/im/chit/hara.test/2.4.8/hara.test-2.4.8.jar\"
       \"hara/test.clj\"]
 
   (resolve-jar-entry 'hara.test
                      \"zcaudate:hara.test:2.4.8\")
   => [\"<.m2>/im/chit/hara.test/2.4.8/hara.test-2.4.8.jar\"
       \"hara/test.clj\"]"
  {:added "2.4"}
  ([x artifact]
   (resolve-jar-entry x artifact {}))
  ([x artifact {:keys [tag] :or {tag :path}}]
   (let [path  (artifact/artifact :path artifact)
         entry (common/resource-entry x)]
     (if (archive/has? path entry)
       [(if (= :path tag)
          path
          (artifact/artifact tag path))
        entry]))))

(defn resolve-entry
  "resolves a class or namespace within a context
 
   (resolve-entry 'hara.test
                  \"zcaudate:hara.test:2.4.8\")
   => [\"<.m2>/im/chit/hara.test/2.4.8/hara.test-2.4.8.jar\"
       \"hara/test.clj\"]
 
   (resolve-entry 'hara.test
                  [\"zcaudate:hara.test:2.4.8\"
                   \"zcaudate:hara.string:2.4.8\"])
   => [\"<.m2>/im/chit/hara.test/2.4.8/hara.test-2.4.8.jar\"
       \"hara/test.clj\"]
   
   (resolve-entry 'hara.test
                  '[[zcaudate/hara.test \"2.4.8\"]
                    [zcaudate/hara.string \"2.4.8\"]])
   => [\"<.m2>/im/chit/hara.test/2.4.8/hara.test-2.4.8.jar\"
       \"hara/test.clj\"]
 
   (resolve-entry 'hara.test
                  '[zcaudate/hara.test \"2.4.8\"])
   => [\"<.m2>/im/chit/hara.test/2.4.8/hara.test-2.4.8.jar\"
       \"hara/test.clj\"]
 
   (resolve-entry 'hara.test
                  '[zcaudate/hara.string \"2.4.8\"])
   => nil"
  {:added "2.4"}
  ([x]
   (resolve-entry x nil))
  ([x context]
   (resolve-entry x context {}))
  ([x context {:keys [tag] :or {tag :path} :as opts}]
   (cond (nil? context)
         (resolve-classloader x)
         
         (string? context)
         (resolve-jar-entry x (artifact/artifact tag context) opts)

         (vector? context)
         (let [i (first context)]
           (cond (or (string? i)
                     (instance? IPersistentVector i))
                 (first (keep (fn [context]
                                (->> (artifact/artifact tag context)
                                     (#(resolve-jar-entry x % opts))))
                              context))
                 
                 Symbol (->> (artifact/artifact tag context)
                             (#(resolve-jar-entry x % opts)))

                 :else
                 (throw (Exception. (str "Not supported: " x " " context)))))

         :else
         (throw (Exception. (str "Not supported: " x " " context))))))
