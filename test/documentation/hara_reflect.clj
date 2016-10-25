(ns documentation.hara-reflect
  (:use hara.test)
  (:require [hara.reflect :refer :all]))

[[:chapter {:title "Introduction"}]]

"`hara.reflect` contains methods for class reflection and method invocation."

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:

    [im.chit/hara.reflect \"{{PROJECT.version}}\"]"

"All functionality is found contained in the `hara.reflect` namespace"

(comment (use 'hara.reflect))

[[:chapter {:title "API" :link "hara.reflect"}]]

[[:api {:namespace "hara.reflect" 
        :title ""}]]

[[:chapter {:title "Selectors"}]]

"
The option array takes selectors and filters can be used to customise the results returned by the two query calls.

- attribute selection
- name filtering
- parameter filtering
- modifier filtering
- return type filtering

For example
"

(fact
  (query-class Long [:name "MIN_VALUE" :#])
  => "MIN_VALUE"

  (query-class Long [:params "MIN_VALUE" :#])
  => [java.lang.Class]

  (query-class Long [:params :name "MIN_VALUE" :#])
  => {:name "MIN_VALUE", :params [java.lang.Class]})

"Additional selector keywords include `:container`, `:hash`, `:delegate`, `:origins`, `:name`, `:modifiers`, `:tag` and `:type` and used as follows:"

(fact
  (query-class Long [:params :name :modifiers "MIN_VALUE" :#])
  => {:modifiers #{:public :static :field :final},
      :name "MIN_VALUE",
      :params [java.lang.Class]})

[[:section {:title "Name Filtering"}]]

"We can filter on the name of the class member using two methods - exact matches using strings and regex matchesusing regexs:"

(fact
  (query-class Long [:name "value"])
  => '("value")

  (query-class Long [:name #"value"])
  => '("value" "valueOf")

  (query-class Long [:name #"VALUE"])
  => '("MAX_VALUE" "MIN_VALUE"))

[[:section {:title "Parameter Filtering"}]]

[[:subsection {:title "Number of Inputs"}]]

"Input parameters can be filtered through specifying the number of inputs:"

(comment
  (query-class Long [:name :params 2])
  => [{:name "compare", :params [Long/TYPE Long/TYPE]}
      {:name "compareTo", :params [Long Long]}
      {:name "compareTo", :params [Long Object]}
      {:name "equals", :params [Long Object]}
      {:name "getLong", :params [String Long]}
      {:name "getLong", :params [String Long/TYPE]}
      {:name "parseLong", :params [String Integer/TYPE]}
      {:name "rotateLeft", :params [Long/TYPE Integer/TYPE]}
      {:name "rotateRight", :params [Long/TYPE Integer/TYPE]}
      {:name "toString", :params [Long/TYPE Integer/TYPE]}
      {:name "toUnsignedString", :params [Long/TYPE Integer/TYPE]}
      {:name "valueOf", :params [String Integer/TYPE]}])

[[:subsection {:title "Exact Inputs"}]]

"Exact inputs can be specified by using a vector with input types:"

(comment
  (query-class Long [:name :params [Long/TYPE]])
  => [{:name "bitCount", :params [Long/TYPE]}
      {:name "highestOneBit", :params [Long/TYPE]}
      {:name "lowestOneBit", :params [Long/TYPE]}
      {:name "new", :params [Long/TYPE]}
      {:name "numberOfLeadingZeros", :params [Long/TYPE]}
      {:name "numberOfTrailingZeros", :params [Long/TYPE]}
      {:name "reverse", :params [Long/TYPE]}
      {:name "reverseBytes", :params [Long/TYPE]}
      {:name "signum", :params [Long/TYPE]}
      {:name "stringSize", :params [Long/TYPE]}
      {:name "toBinaryString", :params [Long/TYPE]}
      {:name "toHexString", :params [Long/TYPE]}
      {:name "toOctalString", :params [Long/TYPE]}
      {:name "toString", :params [Long/TYPE]}
      {:name "valueOf", :params [Long/TYPE]}])

[[:subsection {:title "Partial Inputs"}]]

"Using a vector with `:any` as the first input will output all functions with any of the types as input arguments"

(comment
  (query-class Long [:name [:any String Long]])
  => ["bitCount" "compare" "decode" "getChars" "getLong" "highestOneBit" "lowestOneBit" "new" "numberOfLeadingZeros" "numberOfTrailingZeros" "parseLong" "reverse" "reverseBytes" "rotateLeft" "rotateRight" "signum" "stringSize" "toBinaryString" "toHexString" "toOctalString" "toString" "toUnsignedString" "valueOf"])

"Using a vector with `:all` as the first input will output all functions having all of the types as input arguments"

(fact
  (query-class Long [:name :params [:all String Long]])
  => [{:name "getLong", :params [String Long/TYPE]}])

[[:section {:title "Modifier Filtering"}]]

"The following are all the modifier keywords that can be used for filtering, most are directly related to flags, four have been defined for completeness of filtering:"

(comment
  :public         1      ;; java.lang.reflect.Modifier/PUBLIC
  :private        2      ;; java.lang.reflect.Modifier/PRIVATE
  :protected      4      ;; java.lang.reflect.Modifier/PROTECTED
  :static         8      ;; java.lang.reflect.Modifier/STATIC
  :final          16     ;; java.lang.reflect.Modifier/FINAL
  :synchronized   32     ;; java.lang.reflect.Modifier/SYNCHRONIZE
  :native         256    ;; java.lang.reflect.Modifier/NATIVE
  :interface      512    ;; java.lang.reflect.Modifier/INTERFACE
  :abstract       1024   ;; java.lang.reflect.Modifier/ABSTRACT
  :strict         2048   ;; java.lang.reflect.Modifier/STRICT
  :synthetic      4096   ;; java.lang.Class/SYNTHETIC
  :annotation     8192   ;; java.lang.Class/ANNOTATION
  :enum           16384  ;; java.lang.Class/ENUM
  :volatile       64     ;; java.lang.reflect.Modifier/VOLATILE
  :transient      128    ;; java.lang.reflect.Modifier/TRANSIENT
  :bridge         64     ;; java.lang.reflect.Modifier/BRIDGE
  :varargs        128    ;; java.lang.reflect.Modifier/VARARGS

  :plain          0      ;; not :public, :private or :protected
  :instance       0      ;; not :static
  :field          0      ;; is field
  :method         0      ;; is method
)

[[:subsection {:title "Modifier Examples"}]]

"Find all the fields in `java.lang.Long`:"
(comment
  (query-class Long [:name :field])
  => ["MAX_VALUE" "MIN_VALUE" "SIZE" "TYPE" "serialVersionUID" "value"])

"Find all the static fields in `java.lang.Long`:"
(comment
  (query-class Long [:name :static :field])
  => ["MAX_VALUE" "MIN_VALUE" "SIZE" "TYPE" "serialVersionUID"])

"Find all the non-static fields in `java.lang.Long`:"
(comment
  (query-class Long [:name :instance :field])
  => ["value"])

"Find all public fields in `java.lang.Long`:"
(comment
  (query-class Long [:name :public :field])
  => ["MAX_VALUE" "MIN_VALUE" "SIZE" "TYPE"])

"Find all private members in `java.lang.Long`:"
(comment
  (query-class Long [:name :private])
  => ["serialVersionUID" "toUnsignedString" "value"])

"Find all private fields in `java.lang.Long`:"
(comment
  (query-class Long [:name :private :field])
  => ["serialVersionUID" "value"])

"Find all private methods in `java.lang.Long`:"
(comment
  (query-class Long [:name :private :method])
  => ["toUnsignedString"])

"Find all protected members in `java.lang.Long`:"
(comment
  (query-class Long [:name :protected])
  => [])

"Find all members in `java.lang.Long` with no security attribute:"
(comment
  (query-class Long [:name :plain])
  => ["getChars" "stringSize"])

[[:section {:title "Return Type Filtering"}]]

"Return types signatures can be filtered by giving a class in the options, again all filters can be mixed and matched as needed. In the following example, we query for the name of all methods having a return type of `Long/TYPE`:"

(comment
  (query-class Long [:name :type :method Long/TYPE])
  => [{:name "highestOneBit", :type Long/TYPE}
      {:name "longValue", :type Long/TYPE}
      {:name "lowestOneBit", :type Long/TYPE}
      {:name "parseLong", :type Long/TYPE}
      {:name "parseLong", :type Long/TYPE}
      {:name "reverse", :type Long/TYPE}
      {:name "reverseBytes", :type Long/TYPE}
      {:name "rotateLeft", :type Long/TYPE}
      {:name "rotateRight", :type Long/TYPE}])
