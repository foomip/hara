(ns hara.io.file.filter
  (:require [clojure.string :as string]
            [hara.io.file.option :as option])
  (:import java.util.regex.Pattern))

(defn pattern
  "takes a string as turns it into a regex pattern
 
   (pattern \".clj\")
   => #\"\\Q.\\Eclj\"
   
   (pattern \"src/*\")
   => #\"src/.+\""
  {:added "2.4"}
  [s]
  (-> s
      (string/replace #"\." "\\\\\\Q.\\\\\\E")
      (string/replace #"\*" ".+")
      (re-pattern)))

(defn tag-filter
  "adds a tag to the filter to identify the type
 
   (tag-filter {:pattern #\"hello\"})
   => (just {:tag :pattern
             :pattern #\"hello\"})"
  {:added "2.4"}
  [m]
  (let [tag (first (keys m))]
    (assoc m :tag tag)))

(defn characterise-filter
  "characterises a filter based on type
 
   (characterise-filter \"src\")
   => (just {:tag :pattern :pattern #\"src\"})
 
   (characterise-filter (fn [_] nil))
   => (just {:tag :fn :fn fn?})"
  {:added "2.4"}
  [ft]
  (tag-filter
   (cond (map? ft)
         ft
         
         (string? ft)
         {:pattern (pattern ft)}

         (instance? Pattern ft)
         {:pattern ft}

         (fn? ft)
         {:fn ft}

         :else
         (throw (Exception. (str "Cannot process " ft))))))
