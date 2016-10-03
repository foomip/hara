(ns hara.concurrent.latch-test
  (:use hara.test)
  (:require [hara.concurrent.latch :refer :all]))

^{:refer hara.concurrent.latch/latch :added "2.1"}
(fact "Followes two irefs together so that when `primary`
  changes, the `follower` will also be updated."

  (def primary (atom 1))
  (def follower (atom nil))

  (latch primary follower #(* 10 %))
  (swap! primary inc)

  @primary => 2
  @follower => 20)

^{:refer hara.concurrent.latch/unlatch :added "2.1"}
(fact "Removes the latch so that updates will not be propagated"

  (def primary (atom 1))
  (def follower (atom nil))

  (latch primary follower)
  (swap! primary inc)
  @primary => 2
  @follower => 2

  (unlatch primary follower)
  (swap! primary inc)
  @primary => 3
  @follower => 2)
