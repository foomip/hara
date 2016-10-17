(ns hara.concurrent.propagate-test
  (:use hara.test)
  (:require [hara.concurrent.propagate :refer :all]))

^{:refer hara.concurrent.propagate/nothing? :added "2.1"}
(fact "checks if the value is nothing"

  (nothing? nil) => false

  (nothing? :hara.concurrent.propagate/nothing)
  => true)

^{:refer hara.concurrent.propagate/straight-through :added "2.1"}
(fact "passes the first input through"

  (straight-through 1) => 1

  (straight-through 1 2 3) => 1)

^{:refer hara.concurrent.propagate/cell-state :added "2.1"}
(fact "prepares the state of the cell"

  (cell-state {:label "a" :content "hello" :ref-fn atom})
  => (just {:label clojure.lang.Atom
            :content clojure.lang.Atom
            :propagators clojure.lang.Atom}))

^{:refer hara.concurrent.propagate/propagator-state :added "2.1"}
(fact "prepares the state of the propagator")

^{:refer hara.concurrent.propagate/propagation-transfer :added "2.1"}
(fact "propagates values to the out-cells according to transfer function"
  
  (def out-cell (cell))

  (propagation-transfer
   [1 2 3]
   {:tf + :tdamp = :out-cell out-cell})

  @out-cell => 6)

^{:refer hara.concurrent.propagate/format-cells :added "2.1"}
(fact "styles the cells so they become easier to read")

^{:refer hara.concurrent.propagate/cell :added "2.1"}
(fact "creates a propogation cell"

  (def cell-a (cell))
  @cell-a => :hara.concurrent.propagate/nothing

  (def cell-b (cell "Hello"))
  @cell-b => "Hello"

  (cell-b "World")    ;; invoking sets the state of the cell
  @cell-b => "World")

^{:refer hara.concurrent.propagate/link :added "2.1"}
(fact "creates a propogation link between a set of input cells and an output cell"

  (def in-a  (cell 1))
  (def in-b  (cell 2))
  (def inter (cell))
  (def in-c  (cell 3))
  (def out   (cell))

  (link [in-a in-b] inter +)
  (link [inter in-c] out +)

  (in-a 10)
  @inter => 12
  @out => 15

  (in-b 100)
  @inter => 110
  @out => 113

  (in-c 1000)
  @inter => 110
  @out => 1110)


^{:refer hara.concurrent.propagate/unlink :added "2.1"}
(fact "removes the propagation link between a set of cells"

  (def in-a  (cell 1))
  (def out   (cell))

  (def lk (link [in-a] out))
  (in-a 10)
  @out => 10

  (unlink lk)
  (in-a 100)
  @in-a 100
  @out => 10)
