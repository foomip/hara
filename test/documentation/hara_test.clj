(ns documentation.hara-test
  (:use hara.test))

[[:chapter {:title "Introduction"}]]

"
[hara.test](https://github.com/zcaudate/hara/blob/master/src/hara/test.clj) is a test framework based off of the [midje](https://github.com/marick/Midje) syntax, containing macros and helpers for easy testing and verification of functions"

[[:section {:title "Installation"}]]

"
Add to `project.clj` dependencies:

    [im.chit/hara.test \"{{PROJECT.version}}\"]
"

[[:section {:title "Motivation"}]]

"[hara.test](https://github.com/zcaudate/hara/blob/master/src/hara/test.clj) serves to provide a light-weight test framework based upon a subset of operations defined by [midje](https://github.com/marick/Midje). It abides by the following principles:

- test data should be explict for both input and output
- test cases can be read like documentation
- test suites contains information about the functions that are being tested

There are many test frameworks and test runners for clojure. Why write another one? The use case of [hara.test](https://github.com/zcaudate/hara/blob/master/src/hara/test.clj) is to be a very light-weight test framework that gets out of the way and allows fast development and testing within the repl. Being based around midje, the test runner was intregrated into the test library itself and so [hara.test](https://github.com/zcaudate/hara/blob/master/src/hara/test.clj) contains all he necessary ingredients to do rapid in-repl testing of the whole project as well as very easy shell integration.
"

[[:section {:title "Running"}]]

"[hara.test](https://github.com/zcaudate/hara/blob/master/src/hara/test.clj) can be run in the repl by:"

(comment
  (use 'hara.test)

  (run))

"or in the shell:"

(comment

  > lein run -m hara.test :exit

  )

[[:chapter {:title "API" :link "hara.test"}]]

[[:api {:namespace "hara.test" :title ""}]]

[[:chapter {:title "Basics"}]]

[[:section {:title "fact"}]]

"For those that are familiar with [midje](https://github.com/marick/midje), it's pretty much the same thing. We define tests in a `fact` expression:"

(comment
  (fact "lets test to see which if numbers are odd"

    1 => odd?

    2 => odd?)

  ;; Failure  [hara_test.clj]
  ;;    Info  "lets test to see which if numbers are odd"
  ;;    Form  2
  ;;   Check  odd?
  ;;  Actual  2
  )

"Or for the more grammatically correct, a `facts` expression:"

(comment
  (facts "lets test to see which if numbers are even?"

    1 => even?

    2 => even?)
  
  ;; Failure  [hara_test.clj]
  ;;    Info  "lets test to see which if numbers are even?"
  ;;    Form  1
  ;;   Check  even?
  ;;  Actual  1
  )

"The arrow `=>` forms an input/output syntax and can only be in the top level fact form. This means that it cannot be nested arbitrarily in `let` forms as in [midje](https://github.com/marick/midje). This has the effect simplifying the codebase as well as forcing each individual test to be more self-sufficient. The arrow is flexible and is designed so that the input/output syntax can be kept as succinct as possible. Other checks that can be performed are given as follows:"

(comment
  (facts "Random checks that show-off the range of the `=>` checker"

    ;; check for equality
    1 => 1         

    ;; check for being in a set
    1 => #{1 2 3}  

    ;; check for class
    1 => Long
    
    ;; check for function
    1 => number?  

    ;; check for pattern
    "one" => #"ne"))

[[:section {:title "metadata"}]]

"Metadata can be placed on the `fact`/`facts` form in order to provide more information as to what exactly the fact expression is checking:"

(comment
  ^{:refer hara.test/fact :added "2.4" :tags #{:api}}
  (fact "adding metadata gives more information"

    (+ 1 2 3) => (+ 3 3)))

"Metadata allows the test framework to quickly filter through what test are necessary, as well as to enable generation of documentation and docstrings through external tools."

[[:chapter {:title "Checkers"}]]

[[:section {:title "anything"}]]

"Returns successful for any input, this is good for composing with other checkers:"

(comment
  (fact "testing `anything`"
    
    nil => anything

    (range 1 6) => anything))

[[:section {:title "satisfies"}]]

"This is the default checker used by the `=>` form. There is no difference between using it and leaving it out:"

(comment
  (fact "testing `satisfies`"

    1 => (satisfies 1) 

    1 => (satisfies #{1 2 3})  

    1 => (satisfies Long)      

    1 => (satisfies number?)   

    "one" => (satisfies #"ne"))
  ;;=> true
  )

[[:section {:title "exactly"}]]

"This is a strict checker, making sure that the ouput is exactly as written"

(comment
  (fact "testing `exactly`"

    ;; succeeds
    1 => (exactly 1)         

    ;; fails
    1 => (exactly #{1 2 3})  

    ;; fails
    1 => (exactly Long)      

    ;; fails
    1 => (exactly number?)

    ;; fails
    "one" => (exactly #"ne")))

[[:section {:title "throws"}]]

"This is the checker to use when testing for a thrown exception. There are three different checks that can be used:"

(comment
  (fact "testing `throws`"

    (/ 1 0) => (throws)

    (/ 1 0) => (throws java.lang.ArithmeticException)

    (/ 1 0) => (throws java.lang.ArithmeticException "Divide by zero")))

[[:section {:title "throws-info"}]]

"This is the checker to use when testing for an `ex-info` is thrown:"

(comment
  (fact "testing `throws-info`"

    (throw (ex-info "" {:a 1 :b 2 :c 3}))
    => (throws-info {:a 1})

    (throw (ex-info "" {:a 1 :b 2 :c 3}))
    => (throws-info {:b 2 :c 3}))
  ;;=> true
  )


[[:section {:title "is-not"}]]

"Checker that makes sure to negate it's arguments:"

(comment
  (fact "testing `is-not`"

    1 => (is-not 2)          

    1 => (is-not even?)      

    1 => (is-not String))
  ;;=> true
  )

[[:section {:title "any"}]]

"Checker that allows `or` composition of other checkers"

(comment
  (fact "testing `any`"

    1 => (any 2 3 4 5 (is-not 2)))
  ;;=> true
  )

[[:section {:title "all"}]]

"Checker that allows `and` composition of other checkers"

(comment
  (fact "testing `all`"

    1 => (all 2 3 4 5 (is-not 2)))
  ;;=> true
  )

[[:section {:title "contains"}]]

"Checker for maps and vectors:"

(comment
  (fact "testing `contains`"

    {:a 1 :b 4}
    => (contains {:a odd? :b even?})

    {:a {:b {:c 1}}}
    => (contains {:a (contains {:b (contains {:c odd?})})})

    [1 2 3 4]
    => (contains [1 2 3])

    [1 2 3 4]
    => (contains [1 3] :gaps-ok)

    [1 2 3 4]
    => (contains [3 1 2] :in-any-order)
    
    [1 2 3 4]
    => (contains [3 1] :in-any-order :gaps-ok))
  ;;=> true
  )  

[[:section {:title "contains-in"}]]

"Checker for checking nested maps and vectors"

(comment
  (fact "testing `contains-in`"
    
    {:a {:b {:c 1}}}
    => (contains-in {:a {:b {:c odd?}}})

    {:a {:b {:c [1 2 3 4]}}}
    => (contains-in {:a {:b {:c [2 3]}}})
    )
  ;;=> true
  )

[[:section {:title "just"}]]

"Checker for exact matching of maps and collections"

(comment
  (fact "testing `just` will fail for not enough input:"

    ;; passes
    {:a 1 :b 4} => (contains {:a odd?})

    ;; fails
    {:a 1 :b 4} => (just {:a odd?})))

"`just` is much like `contains` except that it expects all outputs to be explictly stated:"

(comment
  (fact "testing `just`"
    
    {:a {:b {:c 1}}}
    => (just {:a (just {:b (just {:c odd?})})})
    
    [1 2 3 4]
    => (just [1 2 3 4])
    
    [1 2 3 4]
    => (just [3 1 2 4] :in-any-order))
  ;;=> true
  )
    
[[:section {:title "just-in"}]]

"Checker for checking nested maps and vectors exactly"

(comment
  (fact "testing `just-in`"
    
    {:a {:b {:c 1}}}
    => (just-in {:a {:b {:c odd?}}})

    {:a {:b {:c [1 2 3 4]}}}
    => (just-in {:a {:b {:c vector?}}}))
  ;;=> true
  )

[[:chapter {:title "Test Runner"}]]

[[:section {:title "print-options"}]]

"This manages different options for outputting check results. Calling it with no arguments is the same as `:help` and will bring up a list of options:"

(comment
  (print-options)
  => #{:disable :reset :default :all :list :current :help})

"calling `:list` will give all the different options avaliable for printing"

(comment
  (print-options :list)
  => #{:print-bulk :print-facts-success :print-failure
       :print-thrown :print-facts :print-success})

"calling `:current` will give all the current options set:"

(comment
  (print-options :current)
  => #{:print-bulk :print-failure :print-thrown}
  )

"calling with a set of options will set the options, `:disable` stops printing, `:all` prints everything and `:reset` sets it to the default option:"

(comment
  (print-options #{:print-bulk :print-facts-success :print-failure})
  (print-options :current)
  => #{:print-bulk :print-facts-success :print-failure})

""

[[:section {:title "run-namespace"}]]

"`run-namespace` runs all facts for current namespace if no argument is given:"

(comment
  (run-namespace)
  => {:files 0, :thrown 0, :facts 0, :checks 0, :passed 0, :failed 0})

"or if a namespace is specified, it will run tests on that particular file:"

(comment
  (run-namespace 'hara.class.checks-test)
  => {:files 1, :thrown 0, :facts 5, :checks 9, :passed 9, :failed 0}

  ;; ---- Namespace (hara.class.checks-test) ----
  ;;
  ;; Summary (1)
  ;;   Files  1
  ;;   Facts  5
  ;;  Checks  9
  ;;  Passed  9
  ;;  Thrown  0
  ;;
  ;; Success (9)
  )

"settings can be put in to overwrite defaults:"

(comment
  (run-namespace 'hara.class.checks-test {:print #{:print-success}})
  => {:files 1, :thrown 0, :facts 5, :checks 9, :passed 9, :failed 0}
  ;; Success  hara.class.checks/interface? @ [checks_test.clj:8]
  ;;  Info  "Returns `true` if `class` is an interface"
  ;;  Form  (interface? java.util.Map)
  ;; Check  true

  ;; Success  hara.class.checks/interface? @ [checks_test.clj:9]
  ;;  Info  "Returns `true` if `class` is an interface"
  ;;  Form  (interface? Class)
  ;; Check  false
  )

[[:section {:title "run"}]]

"`run` will load all files and test all the `fact` and `facts` forms in the project:"

(comment
  (run)
  => {:files 99, :thrown 0, :facts 669, :checks 1151, :passed 1150, :failed 1}
  
  ;;  ---- Project (im.chit/hara:124) ----
  ;;  documentation.hara-api
  ;;  documentation.hara-class
  ;;  documentation.hara-common
  ;;  documentation.hara-component
  ;;   ....
  ;;   ....
  ;;  hara.time.data.vector-test
  ;;
  ;;  Summary (99)
  ;;    Files  99
  ;;    Facts  669
  ;;   Checks  1151
  ;;   Passed  1150
  ;;   Thrown  0
  ;;
  ;;   Failed  (1)
  )

"Other options for run (as well as run-namespace) include:

- specifing the `:test-paths` option (by default it is *\"test\"*)
- specifing `:include` and `:exclude` entries for file selection
- specifing `:check` options:
  - `:include` and `:exclude` entries:
    - `:tags` so that only the `:tags` that are there on the meta data will run.
    - `:refers` can be a specific function or a namespace
    - `:namespaces` refers to specific test namespaces
- specifing `:print` options for checks

Some examples can be seen below:"

(comment
  (run {:checks {:include [{:tags #{:web}}]} ;; only test for :web tags
        :test-paths ["test/hara"]}) ;; check out "test/hara" as the main path
  => {:files 0, :thrown 0, :facts 0, :checks 0, :passed 0, :failed 0})

"Only test the `hara.time-test` namespace"

(comment
  (run {:checks {:include [{:namespaces #{'hara.time-test}}]}})
  => {:files 1, :thrown 0, :facts 32, :checks 53, :passed 53, :failed 0}

  ;; Summary (1)
  ;;   Files  1
  ;;   Facts  32
  ;;  Checks  53
  ;;  Passed  53
  ;;  Thrown  0
  ;;
  ;; Success (53)
  )

"Only test facts that refer to methods with `hara.time` namespace:"

(comment
  (run {:test-paths ["test/hara"]
        :checks {:include [{:refers '#{hara.time}}]}})
  => {:files 1, :thrown 0, :facts 32, :checks 53, :passed 53, :failed 0}
  ;; Summary (1)
  ;;   Files  1
  ;;   Facts  32
  ;;  Checks  53
  ;;  Passed  53
  ;;  Thrown  0
  ;;
  ;; Success (53)
  )

"Only pick one file to test, and suppress the final summary:"

(comment
  (run {:test-paths ["test/hara"]
        :include    ["^time"]
        :print      #{:print-facts}})
  => {:files 8, :thrown 0, :facts 54, :checks 127, :passed 127, :failed 0}
  ;;   Fact  [time_test.clj:9] - hara.time/representation?
  ;;   Info  "checks if an object implements the representation protocol"
  ;; Passed  2 of 2

  ;;   Fact  [time_test.clj:16] - hara.time/duration?
  ;;   Info  "checks if an object implements the duration protocol"
  ;; Passed  2 of 2

  ;; ...
  )
