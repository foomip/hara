(ns documentation.hara-expression)

"
[hara.expression](https://github.com/zcaudate/hara/blob/master/src/hara/expression.clj) allow transformations of forms into functions and predicates in a succint way."

[[:chapter {:title "expression.compile" :link "hara.expression.compile"}]]

"Add to `project.clj` dependencies:

    [zcaudate/hara.expression.compile \"{{PROJECT.version}}\"]

**hara.expression.compile** looks at compiler time expressions"

[[:api {:namespace "hara.expression.compile" :title ""}]]



[[:chapter {:title "expression.form"
            :link "hara.expression.form"
            :exclude ["form-prep"]}]]

"Add to `project.clj` dependencies:

     [zcaudate/hara.expression.form \"{{PROJECT.version}}\"]

**hara.expression.form** provides methods that transform forms into anonymous functions"

[[:api {:namespace "hara.expression.form"
        :title ""
        :exclude ["form-prep"]}]]



[[:chapter {:title "expression.load"
            :link "hara.expression.load"
            :only ["load"]}]]

"Add to `project.clj` dependencies:

     [zcaudate/hara.expression.load \"{{PROJECT.version}}\"]

**hara.expression.load** provides a simple mechanism for loading code that can be in a form"

[[:api {:namespace "hara.expression.load"
        :title ""
        :only ["load"]}]]



[[:chapter {:title "expression.shorthand" :link "hara.expression.shorthand"}]]

"Add to `project.clj` dependencies:

     [zcaudate/hara.expression.shorthand \"{{PROJECT.version}}\"]

**hara.expression.shorthand** provides methods that work with code as data"

[[:api {:namespace "hara.expression.shorthand" :title ""}]]
