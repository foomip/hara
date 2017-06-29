(ns hara.string.mustache
  (:require [hara.string.path :as path]
            [hara.data.path :as data])
  (:import [hara.string.mustache Mustache Context]))

(defn render
  "converts a template with mustache data
 
   (render \"{{user.name}}\" {:user {:name \"zcaudate\"}})
   => \"zcaudate\"
 
   (render \"{{# user.account}}{{name}} {{/user.account}}\"
           {:user {:account [{:name \"admin\"}
                             {:name \"user\"}]}})
   => \"admin user \"
   
   (render \"{{? user}}hello{{/user}}\" {:user true})
   => \"hello\"
 
   (render \"{{^ user.name}}hello{{/user.name}}\" {:user nil})
   => \"hello\""
  {:added "2.5"}
  [template data]
  (if (empty? template)
    template
    (let [template (Mustache/preprocess template)
          flattened (binding [path/*default-seperator* "."]
                      (data/flatten-keys-nested data))]
      (.render template (Context. flattened nil)))))
