(ns documentation.hara-group)

[[:chapter {:title "Introduction"}]]

"
[hara.group](https://github.com/zcaudate/hara/blob/master/src/hara/function.clj) allow definition of collections of the same type of items. "

[[:section {:title "Installation"}]]

"
Add to `project.clj` dependencies:

    [im.chit/hara.group \"{{PROJECT.version}}\"]

All functionality is in the `hara.group` namespace:"

(comment
  (use 'hara.group))

[[:chapter {:title "Definitions"
            :link "hara.group"
            :only ["defgroup" "defitem"]}]]

"Groups and items are defined as follows:"

[[:api {:title ""
        :namespace "hara.group" 
        :only ["defgroup" "defitem"]}]]

[[:chapter {:title "Methods"
            :link "hara.group"
            :exclude ["defgroup" "defitem"]}]]

"Methods for changing items in the defined group are:"

[[:api {:title ""
        :namespace "hara.group"
        :exclude ["defgroup" "defitem"]}]]
