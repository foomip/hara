(ns documentation.hara-common)

"
[hara.common](https://github.com/zcaudate/hara/blob/master/src/hara/common.clj) are a set of primitive declarations and functions that extend on top of `clojure.core` and are used by many of the other namespaces in the `hara` ecosystem."

[[:chapter {:title "common.checks" :link "hara.common.checks"}]]

"Add to `project.clj` dependencies:

    [zcaudate/hara.common.checks \"{{PROJECT.version}}\"]

 **hara.common.checks** contain basic type predicates that are not included in the *clojure.core* namespace."

[[:api {:namespace "hara.common.checks" :title ""}]]

[[:chapter {:title "common.error" :link "hara.common.error"}]]

"Add to `project.clj` dependencies:

    [zcaudate/hara.common.error \"{{PROJECT.version}}\"]

**hara.common.error** contain simple macros for throwing and processing errors."

[[:api {:namespace "hara.common.error" :title ""}]]

[[:chapter {:title "common.hash" :link "hara.common.hash"}]]

"Add to `project.clj` dependencies:

    [zcaudate/hara.common.error \"{{PROJECT.version}}\"]

**hara.common.hash** contain methods for working with object hashes."

[[:api {:namespace "hara.common.hash" :title ""}]]

[[:chapter {:title "common.primitives" :link "hara.common.primitives"}]]

"Add to `project.clj` dependencies:

    [zcaudate/hara.common.primitives \"{{PROJECT.version}}\"]

**hara.common.primitives** contain constructs that are not included in the *clojure.core* namespace."

[[:api {:namespace "hara.common.primitives"}]]

[[:chapter {:title "common.state" :link "hara.common.state"}]]

"Add to `project.clj` dependencies:

    [zcaudate/hara.common.state \"{{PROJECT.version}}\"]

**hara.common.state** contain extensible methods for manipulating stateful datastructures:"

[[:api {:namespace "hara.common.state"}]]

[[:chapter {:title "common.string" :link "hara.common.string"}]]

"Add to `project.clj` dependencies:

    [zcaudate/hara.common.string \"{{PROJECT.version}}\"]

**hara.common.string** contain extensible methods for string manipulation:"

[[:api {:namespace "hara.common.string"}]]

[[:chapter {:title "common.watch" :link "hara.common.watch"
            :exclude ["process-options" "wrap-mode" "wrap-suppress"]}]]

"Add to `project.clj` dependencies:

    [zcaudate/hara.common.watch \"{{PROJECT.version}}\"]

**hara.common.watch** contain extensible methods for observation of state:"

[[:api {:namespace "hara.common.watch"
        :exclude ["process-options" "wrap-mode" "wrap-suppress"]}]]
