(ns documentation.hara-group)

[[:chapter {:title "Introduction"}]]

"
[hara.group](https://github.com/zcaudate/hara/blob/master/src/hara/function.clj) allow definition of collections of the same type of items. "

[[:section {:title "Installation"}]]

"
Add to `project.clj` dependencies:

    [zcaudate/hara.group \"{{PROJECT.version}}\"]

All functionality is in the `hara.group` namespace:"

(comment
  (use 'hara.group))

[[:chapter {:title "Index"}]]

[[:api {:title ""
        :namespace "hara.group"
        :display #{:tags}}]]

[[:chapter {:title "API"}]]

[[:section {:title "Definitions"}]]

"Groups and items are defined as follows:"

[[:api {:title ""
        :namespace "hara.group" 
        :only ["defgroup" "defitem"]}]]

[[:section {:title "Methods"}]]

"Methods for changing items in the defined group are:"

[[:api {:title ""
        :namespace "hara.group"
        :exclude ["defgroup" "defitem"]}]]
