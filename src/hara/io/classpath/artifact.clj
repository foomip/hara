(ns hara.io.classpath.artifact
  (:require [clojure.string :as string]
            [hara.io.classpath.common :as common]))

(defn rep->coord
  "encodes the rep to a coordinate
 
   (-> {:group \"im.chit\" :artifact \"hara\" :version \"2.4.0\"}
       (map->Rep)
       (rep->coord))
   => '[im.chit/hara \"2.4.0\"]"
  {:added "2.4"}
  [{:keys [group artifact version]}]
  [(symbol group artifact) version])

(defn rep->path
  "encodes the rep to a path
 
   (-> {:group \"im.chit\" :artifact \"hara\" :version \"2.4.0\"}
       (map->Rep)
       (rep->path))
   => \"<.m2>/im/chit/hara/2.4.0/hara-2.4.0.jar\""
  {:added "2.4"}
  [{:keys [group artifact version extension]}]
  (string/join common/*sep*
               [common/*local-repo* (.replaceAll group "\\." common/*sep*)
                artifact version (str artifact "-" version "." (or extension "jar"))]))

(defn rep->string
  "encodes the rep to a string
 
   (-> {:group \"im.chit\" :artifact \"hara\" :version \"2.4.0\"}
       (map->Rep)
       (rep->string))
   => \"im.chit:hara:2.4.0\""
  {:added "2.4"}
  [{:keys [group artifact extension version]}]
  (string/join ":" [group
                    artifact
                    (if extension
                      (str extension ":" version)
                      version)]))

(defrecord Rep [group artifact extension classifier version properties file]
  Object
  (toString [rep] (rep->string rep)))

(defmethod print-method Rep
  [v w]
  (.write w (str "'" v)))

(defn coord->rep
  "converts a coord to a rep instance
 
   (coord->rep '[im.chit/hara \"2.4.0\"])
   => (contains {:group \"im.chit\"
                 :artifact \"hara\"
                 :version \"2.4.0\"})"
  {:added "2.4"}
  [[name version]]
  (let [[group artifact] (string/split (str name) #"/")
        artifact (or artifact
                     group)]
    (Rep. group artifact "jar" nil version {} nil)))

(defn string->rep
  "converts a string to a rep instance
 
   (string->rep \"im.chit:hara:2.4.0\")
   => (contains {:group \"im.chit\"
                 :artifact \"hara\"
                 :version \"2.4.0\"})"
  {:added "2.4"}
  [s]
  (let [[group artifact extension? classifer? version :as array] (string/split s #":")]
    (case (count array)
      3 (Rep. group artifact "jar" nil extension? {} nil)
      4 (Rep. group artifact extension? nil classifer? {} nil)
      5 (Rep. group artifact extension? classifer? version {} nil))))

(defn path->rep
  "converts a path to a rep instance
 
   (path->rep (str common/*local-repo* \"/im/chit/hara/2.4.0/hara-2.4.0.jar\"))
   => (contains {:group \"im.chit\"
                 :artifact \"hara\"
                 :version \"2.4.0\"})"
  {:added "2.4"}
  [x]
  (let [arr (->> (re-pattern common/*sep*)
                 (string/split (.replaceAll x common/*local-repo* ""))
                 (remove empty?))
        extension (-> (last arr)
                      (string/split #"\.")
                      last)
        version   (last (butlast arr))
        artifact  (last (butlast (butlast arr)))
        group     (string/join "." (butlast (butlast (butlast arr))))]
    (Rep. group artifact extension nil version {} x)))

(defmulti rep
  "converts various formats to a rep
 
   (rep '[im.chit/hara \"2.4.0\"])
   => 'im.chit:hara:jar:2.4.0
 
   (rep \"im.chit:hara:2.4.0\")
   => 'im.chit:hara:jar:2.4.0"
  {:added "2.4"}
  type)

(defmethod rep :default
  [x]
  (cond (instance? Rep x) x

        (map? x) (map->Rep x)
        
        (vector? x) (coord->rep x)
        
        (string? x)
        (if (.startsWith x common/*local-repo*)
          (path->rep x)
          (string->rep x))

        :else
        (throw (Exception. (str "Invalid form: " x)))))

(defmulti artifact
  "converts various artifact formats
 
   (artifact :string '[im.chit/hara \"2.4.0\"])
   => \"im.chit:hara:jar:2.4.0\"
 
   (artifact :path \"im.chit:hara:2.4.0\")
   => (str common/*local-repo*
           \"/im/chit/hara/2.4.0/hara-2.4.0.jar\")"
  {:added "2.4"}
  (fn [tag x] tag))

(defmethod artifact :default
  [_ x]
  (rep x))

(defmethod artifact :string
  [_ x]
  (-> (rep x) rep->string))

(defmethod artifact :path
  [_ x]
  (if (and (string? x)
           (.endsWith x "jar"))
    x
    (-> (rep x)
        rep->path)))

(defmethod artifact :coord
  [_ x]
  (-> (rep x) rep->coord))
