(ns hara.io.classpath
  (:require [hara.namespace.import :as ns]
            [hara.class.multi :as multi]
            [hara.io.classpath
             [artifact :as artifact]
             [common :as common]
             [search :as search]]))

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
