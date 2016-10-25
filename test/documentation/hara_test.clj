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

"[hara.test](https://github.com/zcaudate/hara/blob/master/src/hara/test.clj) serves to provide a light-weight test framework based upon a subset of operations defined by [midje](https://github.com/marick/Midje). The framework allows for rapid in-repl testing of the whole project as well as very easy shell integration. It abides by the following principles:

- test data should be explict for both input and output
- test cases can be read like documentation
- test suites contains information about the functions that are being tested"

[[:section {:title "Running"}]]

"[hara.test](https://github.com/zcaudate/hara/blob/master/src/hara/test.clj) can be run in the repl by:"

(comment
  (use 'hara.test)

  (run))

"or in the shell:"

(comment

  > lein run -m hara.test :exit

  )

[[:chapter {:title "Index"}]]

[[:api {:namespace "hara.test" 
        :title ""
        :display #{:tags}
        :exclude ["-main" "=>" "fact" "facts" "process-args"]}]]

[[:chapter {:title "API"}]]

[[:section {:title "Basics"}]]

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

[[:section {:title "Metadata"}]]

"Metadata can be placed on the `fact`/`facts` form in order to provide more information as to what exactly the fact expression is checking:"

(comment
  ^{:refer hara.test/fact :added "2.4" :tags #{:api}}
  (fact "adding metadata gives more information"

    (+ 1 2 3) => (+ 3 3)))

"Metadata allows the test framework to quickly filter through what test are necessary, as well as to enable generation of documentation and docstrings through external tools."

[[:section {:title "Checkers"}]]

[[:api {:namespace "hara.test" 
        :title ""
        :only ["all"
               "any"
               "anything"
               "contains"
               "contains-in"
               "exactly"
               "is-not"
               "just"
               "just-in"
               "satisfies"
               "throws"
               "throws-info"]}]]

[[:section {:title "Runner"}]]

[[:api {:namespace "hara.test" 
        :title ""
        :only ["print-options"
               "run-namespace"
               "run"]}]]


[[:section {:title "Options"}]]

"Options for run and run-namespace include:

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
