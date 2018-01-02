(ns documentation.hara-object
  (:use hara.test)
  (:require [hara.object :as object]
            [hara.object.map-like :refer [extend-map-like]]))

[[:chapter {:title "Introduction"}]]

"
[hara.object](https://github.com/zcaudate/hara/blob/master/src/hara/object.clj) is a library for converting java classes into clojure data types. It is somewhat like the `bean` command but enables more control and customisation of the output."

[[:section {:title "Installation"}]]

"
Add to `project.clj` dependencies:

    [zcaudate/hara.object \"{{PROJECT.version}}\"]"

"All functionality is found contained in the `hara.object` namespace"

(comment (require '[hara.object :as object]))


[[:section {:title "Motivation"}]]

"`hara.object` works at the level of meta-programming. Below shows a simple example of the concept of `Dog` as an `Object` and as data:
"

[[:image {:src "img/hara_object/dog.png" :width "90%" :title "Class as Data"}]]

"There are advantages of using pure data for the representation of the `Dog` concept.

- generic methods con be used to manipulate the data
- the entire structure is transparent is better for reasoning
- simpler representation (though at the cost of Type Correctness)

In this way, many objects can be turned into maps/data for consumption by clojure methods. This makes working with many big java libraries much easier before."

[[:chapter {:title "Index"}]]

[[:api {:namespace "hara.object" 
        :title ""
        :display #{:tags}}]]

[[:chapter {:title "API"}]]

[[:section {:title "Core"}]]

[[:api {:namespace "hara.object" 
        :title ""
        :only ["map-like" "string-like" "vector-like" "meta-read" "meta-write"]}]]

[[:section {:title "Fields"}]]

[[:api {:namespace "hara.object" 
        :title ""
        :only ["to-data" "from-data" "read-all-getters" 
               "read-getters" "read-reflect-fields"
               "write-all-setters" "write-reflect-fields" "write-setters"]}]]

