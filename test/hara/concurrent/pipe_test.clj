(ns hara.concurrent.pipe-test
  (:use hara.test)
  (:require [hara.concurrent.pipe :as pipe :refer [pipe]]))

^{:refer hara.concurrent.pipe/send :added "2.2"}
(fact "sends a task to the pipe for it's handler to act upon"

  (def atm (atom []))

  (def p (pipe (fn [msg]
                 (swap! atm conj msg))))

  (do (pipe/send p 1)
      (pipe/send p 2)
      (pipe/send p 3)
      (Thread/sleep 100)
      @atm)
  => [1 2 3])

^{:refer hara.concurrent.pipe/pipe :added "2.2"}
(fact "creates a pipe so that tasks can be acted upon asynchronously in order in which they were sent"

  (pipe (fn [msg] (println msg)))
  => #(instance? hara.concurrent.pipe.Pipe %))
