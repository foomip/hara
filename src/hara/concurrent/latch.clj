(ns hara.concurrent.latch
  (:require [hara.common.hash :refer [hash-label]]
            [hara.common.watch :as watch]
            [hara.common.state :as state]
            [clojure.string :as string]))

(defn- latch-fn
  [rf f]
  (fn [_ _ _ v]
    (state/set rf (f v))))

(defn latch
  "Followes two irefs together so that when `primary`
   changes, the `follower` will also be updated.
 
   (def primary (atom 1))
   (def follower (atom nil))
 
   (latch primary follower #(* 10 %))
   (swap! primary inc)
 
   @primary => 2
   @follower => 20"
  {:added "2.1"}
  ([primary follower] (latch primary follower identity))
  ([primary follower f] (latch primary follower f nil))
  ([primary follower f opts]
     (watch/add primary
                (keyword (hash-label primary follower))
                (latch-fn follower f)
                opts)))

(defn unlatch
  "Removes the latch so that updates will not be propagated
 
   (def primary (atom 1))
   (def follower (atom nil))
 
   (latch primary follower)
   (swap! primary inc)
   @primary => 2
   @follower => 2
 
   (unlatch primary follower)
   (swap! primary inc)
   @primary => 3
   @follower => 2"
  {:added "2.1"}
  [primary follower]
  (watch/remove primary (keyword (hash-label primary follower))))
