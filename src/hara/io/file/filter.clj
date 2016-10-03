(ns hara.io.file.filter
  (:require [clojure.string :as string]
            [hara.io.file.option :as option])
  (:import java.util.regex.Pattern))

(defn pattern
  ""
  [s]
  (-> s
      (string/replace #"\." "\\\\\\Q.\\\\\\E")
      (string/replace #"\*" ".+")
      (re-pattern)))

(defn tag-filter
  ""
  [m]
  (let [tag (first (keys m))]
    (assoc m :tag tag)))

(defn characterise-filter
  ""
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
