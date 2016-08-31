(ns hara.test.checker-test
  (:require [hara.test :refer [fact =>]]
            [hara.test.checker :refer :all]
            [hara.test.common :as common]))

^{:refer hara.test.checker/->data :added "2.4"}
(fact "coerces a checker result into data"

  (->data 1) => 1

  (->data (common/map->Result {:data 1}))
  => 2)

^{:refer hara.test.checker/throws :added "2.4"}
(fact "checker that determines if an exception has been thrown"
  
  ((throws Exception "Hello There")
   (common/map->Result
    {:type :exception
     :data (Exception. "Hello There")}))
  => true)

^{:refer hara.test.checker/satisfies :added "2.4"}
(fact "checker that allows loose verifications"

  ((satisfies 1) 1) => true

  ((satisfies Long) 1) => true
  
  ((satisfies number?) 1) => true

  ((satisfies #{1 2 3}) 1) => true

  ((satisfies [1 2 3]) 1) => false

  ((satisfies number?) "e") => false)

^{:refer hara.test.checker/->checker :added "2.4"}
(fact "creates a 'satisfies' checker if not already a checker"

  ((->checker 1) 1) => true

  ((->checker (exactly 1)) 1) => true)

^{:refer hara.test.checker/verify :added "2.4"}
(fact "verifies result of the check"
  (-> (verify (->checker #(/ % 0)) 1)
      :data
      type)
  => java.lang.ArithmeticException)


^{:refer hara.test.checker/exactly :added "2.4"}
(fact "checker that allows exact verifications"

  ((exactly 1) 1) => true
  
  ((exactly Long) 1) => false

  ((exactly number?) 1) => false)


^{:refer hara.test.checker/any :added "2.4"}
(fact "checker that allows 'or' composition of checkers"

  ((any (satisfies 1)
        (satisfies 2)) 1)
  => true

  ((any (exactly number?)
        (satisfies 2)) 1)
  => false)

^{:refer hara.test.checker/all :added "2.4"}
(fact "checker that allows 'and' composition of checkers"

  ((all (satisfies 1)
        (satisfies number?)) 1)
  => true

  ((all (exactly number?)
        (satisfies 1)) 1)
  => false)

^{:refer hara.test.checker/contains :added "2.4"}
(fact "checker for maps and vectors"

  ((contains {:a odd? :b even?}) {:a 1 :b 4})
  => true

  ((contains {:a 1 :b even?}) {:a 2 :b 4})
  => false

  ((contains [1 2 3]) [1 2 3 4])
  => true

  ((contains [1 3]) [1 2 3 4])
  => false

  ^:hidden
  ((contains [1 3] :gaps-ok) [1 2 3 4])
  => true

  ((contains [3 1] :gaps-ok) [1 2 3 4])
  => false

  ((contains [3 1] :in-any-order) [1 2 3 4])
  => false

  ((contains [3 1 2] :in-any-order) [1 2 3 4])
  => true
  
  ((contains [3 1] :in-any-order :gaps-ok) [1 2 3 4])
  => true)

^{:refer hara.test.checker/just :added "2.4"}
(fact "exact checker for maps and vectors"

  ((just {:a odd? :b even?}) {:a 1 :b 4})
  => true

  ((just {:a 1 :b even?}) {:a 1 :b 2 :c 3})
  => false

  ((just [1 2 3 4]) [1 2 3 4])
  => true
  
  ((just [1 2 3]) [1 2 3 4])
  => false

  ((just [3 2 4 1] :in-any-order) [1 2 3 4])
  => true)

^{:refer hara.test.checker/contains-in :added "2.4"}
(fact "shorthand for checking nested maps and vectors"

  ((contains-in {:a {:b {:c odd?}}}) {:a {:b {:c 1 :d 2}}})
  => true

  ((contains-in [odd? {:a {:b even?}}]) [3 {:a {:b 4 :c 5}}])
  => true)

^{:refer hara.test.checker/contains-in :added "2.4"}
(fact "shorthand for exactly checking nested maps and vectors"

  ((just-in {:a {:b {:c odd?}}}) {:a {:b {:c 1 :d 2}}})
  => false

  ((just-in [odd? {:a {:b even?}}]) [3 {:a {:b 4}}])
  => true)
