(ns documentation.hara-data)
"
**hara.data** consists of utility functions that act on clojure hash-maps and map-like representations of data. The level of complexity needed for working with data increases as it becomes nested and then relational."


[[:chapter {:title "data.map" :link "hara.data.map"}]]

"Add to `project.clj` dependencies:

    [im.chit/hara.data.map \"{{PROJECT.version}}\"]

**hara.data.map** contain functions for clojure maps"

[[:api {:namespace "hara.data.map" :title ""}]]




[[:chapter {:title "data.seq"}]]

"Add to `project.clj` dependencies:

    [im.chit/hara.data.seq \"{{PROJECT.version}}\"]

**hara.data.seq** contain functions for sequences and arrays"

[[:api {:namespace "hara.data.seq" :title ""}]]




[[:chapter {:title "data.nested" :link "hara.data.nested"}]]

"Add to `project.clj` dependencies:

    [im.chit/hara.data.nested \"{{PROJECT.version}}\"]

**hara.data.nested** contain functions for updating nested hashmaps."

[[:api {:namespace "hara.data.nested" :title ""}]]




[[:chapter {:title "data.diff"
            :link "hara.data.diff"
            :exclude ["merge-or-replace"]}]]

"Add to `project.clj` dependencies:

    [im.chit/hara.data.diff \"{{PROJECT.version}}\"]

**hara.data.diff** contain functions for comparing maps, as well as functions to patch changes."

[[:api {:namespace "hara.data.diff"
        :title ""
        :exclude ["merge-or-replace"]}]]




[[:chapter {:title "data.combine" :link "hara.data.combine"}]]

"Add to `project.clj` dependencies:

    [im.chit/hara.data.combine \"{{PROJECT.version}}\"]

**hara.data.combine** contains functions for working with sets of data."

[[:api {:namespace "hara.data.combine" :title ""}]]




[[:chapter {:title "data.complex"
            :link "hara.data.complex"
            :exclude ["assocs-in-filtered"]}]]

"Add to `project.clj` dependencies:

    [im.chit/hara.data.complex \"{{PROJECT.version}}\"]

**hara.data.complex** contain functions for working with relational data such as that coming out from datomic."

[[:api {:namespace "hara.data.complex" :title ""
        :exclude ["assocs-in-filtered"]}]]




[[:chapter {:title "data.record" :link "hara.data.record"}]]

"Add to `project.clj` dependencies:

    [im.chit/hara.data.record \"{{PROJECT.version}}\"]

**hara.data.record** contain functions for working with clojure records"

[[:api {:namespace "hara.data.record" :title ""}]]




[[:chapter {:title "data.path" :link "hara.data.path"}]]

"Add to `project.clj` dependencies:

    [im.chit/hara.data.path \"{{PROJECT.version}}\"]

**hara.data.path**concerns itself with the translation between data contained in a nested versus data contained in a single map with paths as keys."

[[:api {:namespace "hara.data.path" :link "hara.data.path"}]]

