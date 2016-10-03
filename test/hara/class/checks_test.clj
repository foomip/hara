(ns hara.class.checks-test
  (:use hara.test)
  (:require [hara.class.checks :refer :all]))

^{:refer hara.class.checks/interface? :added "2.1"}
(fact "returns `true` if `class` is an interface"

  (interface? java.util.Map) => true
  
  (interface? Class) => false)

^{:refer hara.class.checks/abstract? :added "2.1"}
(fact "returns `true` if `class` is an abstract class"

  (abstract? java.util.Map) => true
  
  (abstract? Class) => false)

^{:refer hara.class.checks/multimethod? :added "2.1"}
(fact "returns `true` if `obj` is a multimethod"

  (multimethod? print-method) => true
  
  (multimethod? println) => false)

^{:refer hara.class.checks/protocol? :added "2.1"}
(fact "returns `true` if `obj` is a protocol"

  (defprotocol ISomeProtocol)
  
  (protocol? ISomeProtocol) => true

  (protocol? clojure.lang.ILookup) => false)

^{:refer hara.class.checks/dispatches? :added "2.1"}
(fact "returns `true` if the multimethod contains a value for dispatch"
  
  (dispatches? print-method Class)
  => true)
