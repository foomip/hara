(ns documentation.hara-extend)

"
[hara.extend](https://github.com/zcaudate/hara/blob/master/src/hara/extend.clj) provide additional functionality on top of `defrecord` and `defmulti`/`defmethod`."

[[:chapter {:title "extend.abstract"
            :link "hara.extend.abstract"
            :only ["extend-abstract" "extend-implementations"]}]]

"Add to `project.clj` dependencies:

    [zcaudate/hara.extend.abstract \"{{PROJECT.version}}\"]

**hara.extend.abstract** provides the implementation for the [abstract container pattern](http://z.caudate.me/the-abstract-container-pattern/)"

[[:api {:namespace "hara.extend.abstract"
        :title ""
        :only ["extend-abstract" "extend-implementations"]}]]

[[:chapter {:title "extend.all"
            :link "hara.extend.all"
            :only ["extend-all"]}]]

"
Add to `project.clj` dependencies:

    [zcaudate/hara.extend.all \"{{PROJECT.version}}\"]

**hara.extend.all** promotes code reuse by providing a template for `extend-type`"

[[:api {:namespace "hara.extend.all"
        :only ["extend-all"]}]]
