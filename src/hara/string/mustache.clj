(ns hara.string.mustache
  (:require [hara.string.path :as path]
            [hara.data.path :as data])
  (:import [hara.string.mustache Mustache Context]))

(defn render
  {:added "2.5"}
  [template data]
  (let [template (Mustache/preprocess template)
        flattened (binding [path/*default-seperator* "."]
                    (data/flatten-keys-nested data))]
    (.render template (Context. flattened nil))))
