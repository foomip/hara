(ns hara.zip.base-test
  (:use hara.test)
  (:require [hara.zip.base :refer :all]
            [hara.zip :as zip]))

^{:refer hara.zip.base/zipper-meta :added "2.4"}
(fact "checks that the meta contains valid functions"
  
  (zipper-meta {})
  => (throws)
  
  (zipper-meta {:branch?   seq?
                :children  identity
                :make-node identity})
  => map?)

^{:refer hara.zip.base/zipper? :added "2.4"}
(fact "checks to see if an object is a zipper"
  
  (zipper? 1)
  => false)

^{:refer hara.zip.base/zipper :added "2.4"}
(fact "constructs a zipper"
  
  (zipper '(1 2 3) {:branch?   seq?
                    :children  identity
                    :make-node identity})
  => zipper?)

^{:refer hara.zip.base/seq-zip :added "2.4"}
(fact "constructs a sequence zipper"

  (seq-zip '(1 2 3 4 5))
  => (contains {:left (),
                :right '((1 2 3 4 5)),
                :parent :top}))

^{:refer hara.zip.base/vector-zip :added "2.4"}
(fact "constructs a vector based zipper"

  (vector-zip [1 2 3 4 5])
  => (contains {:left (),
                :right '([1 2 3 4 5])
                :parent :top}))

^{:refer hara.zip.base/history :added "2.4"}
(fact "accesses the zipper history"
  
  (-> (vector-zip [1 [2 3]])
      (move-down)
      (move-right)
      (history))
  => [[:down] [:right]])

^{:refer hara.zip.base/add-history :added "2.4"}
(fact "adds elements to the zipper history")

^{:refer hara.zip.base/left-node :added "2.4"}
(fact "element directly left of current position"

  (-> (zip/from-cursor '[1 2 3 | 4])
      (left-node))
  => 3)

^{:refer hara.zip.base/right-node :added "2.4"}
(fact "element directly right of current position"

  (-> (zip/from-cursor '[1 2 3 | 4])
      (right-node))
  => 4)

^{:refer hara.zip.base/left-nodes :added "2.4"}
(fact "all elements left of current position"
  
  (-> (zip/from-cursor '[1 2 | 3 4])
      (left-nodes))
  => '(1 2))

^{:refer hara.zip.base/right-nodes :added "2.4"}
(fact "all elements right of current position"
  
  (-> (zip/from-cursor '[1 2 | 3 4])
      (right-nodes))
  => '(3 4))

^{:refer hara.zip.base/siblings :added "2.4"}
(fact "all elements left and right of current position"

  (-> (zip/from-cursor '[1 2 | 3 4])
      (siblings))
  => '(1 2 3 4)
  
  (-> (zip/from-cursor '[1 [2 | 3] 4])
      (siblings))
  => '(2 3))

^{:refer hara.zip.base/left-most? :added "2.4"}
(fact "check if at left-most point of a branch"

  (-> (zip/from-cursor [1 2 ['| 3 4]])
      (left-most?))
  => true)

^{:refer hara.zip.base/right-most? :added "2.4"}
(fact "check if at right-most point of a branch"

  (-> (zip/from-cursor '[1 2 [3 4 |]])
      (right-most?))
  => true)

^{:refer hara.zip.base/move-left? :added "2.4"}
(fact "check if can move left from current position"

  (-> (zip/from-cursor '[1 2 [3 | 4]])
      (move-left?))
  => true
  
  (-> (zip/from-cursor '[1 2 [| 3 4]])
      (move-left?))
  => false)

^{:refer hara.zip.base/move-right? :added "2.4"}
(fact "check if can move right from current position"

  (-> (zip/from-cursor '[1 2 [3 | 4]])
      (move-right?))
  => true
  
  (-> (zip/from-cursor '[1 2 [3 4 |]])
      (move-right?))
  => false)

^{:refer hara.zip.base/top-most? :added "2.4"}
(fact "check if at top-most point of the tree"

  (-> (zip/from-cursor [1 2 [3 4 '|]])
      (top-most?))
  => false

  (-> (zip/from-cursor '[1 2 [3 4 |]])
      (move-up)
      (move-up)
      (top-most?))
  => true)

^{:refer hara.zip.base/bottom-most? :added "2.4"}
(fact "check if at bottom-most point of a branch"

  (-> (zip/from-cursor '[1 2 [3 4 |]])
      (bottom-most?))
  => true)

^{:refer hara.zip.base/move-up? :added "2.4"}
(fact "check if can move up from current position"

  (-> (zip/from-cursor '[1 2 [3 4 |]])
      (move-up?))
  => true)

^{:refer hara.zip.base/move-down? :added "2.4"}
(fact "check if can move down from current position"

  (-> (zip/from-cursor '[1 2 [3 4 |]])
      (move-down?))
  => false

  (-> (zip/from-cursor '[1 2 | [3 4]])
      (move-down?))
  => true)

^{:refer hara.zip.base/move-left :added "2.4"}
(fact "move left from current position"

  (-> (zip/from-cursor '[1 2 [3 4 |]])
      (move-left)
      (zip/cursor))
  => '([1 2 [3 | 4]]))

^{:refer hara.zip.base/move-left-most :added "2.4"}
(fact "move to left-most point of current branch"
  
  (-> (zip/from-cursor '[1 2 [3 4 |]])
      (move-left-most)
      (zip/cursor))
  => '([1 2 [| 3 4]]))

^{:refer hara.zip.base/move-right :added "2.4"}
(fact "move right from current position"

  (-> (zip/from-cursor '[1 2 [| 3 4]])
      (move-right)
      (zip/cursor))
  => '([1 2 [3 | 4]]))

^{:refer hara.zip.base/move-right-most :added "2.4"}
(fact "move to right-most point of current branch"

  (-> (zip/from-cursor '[1 2 [| 3 4]])
      (move-right-most)
      (zip/cursor))
  => '([1 2 [3 4 |]]))

^{:refer hara.zip.base/move-up :added "2.4"}
(fact "move up from current position"

  (-> (zip/from-cursor '[1 2 [| 3 4]])
      (move-up)
      (zip/cursor))
  => '([1 2 | [3 4]]))

^{:refer hara.zip.base/move-top-most :added "2.4"}
(fact "move to top-most point of the tree"

  (-> (zip/from-cursor '[1 2 [| 3 4]])
      (move-top-most)
      (zip/cursor))
  => '(| [1 2 [3 4]]))

^{:refer hara.zip.base/move-down :added "2.4"}
(fact "move down from current position"

  (-> (zip/from-cursor '[1 2 | [3 4]])
      (move-down)
      (zip/cursor))
  => '([1 2 [| 3 4]]))

^{:refer hara.zip.base/move-bottom-most :added "2.4"}
(fact "move to bottom-most point of current branch"

  (-> (zip/from-cursor '[1 2 | [[3] 4]])
      (move-bottom-most)
      (zip/cursor))
  => '([1 2 [[| 3] 4]]))

^{:refer hara.zip.base/insert-base :added "2.4"}
(fact "base insert helper")

^{:refer hara.zip.base/insert-left :added "2.4"}
(fact "insert element/s left of the current position"

  (-> (zip/from-cursor '[1 2  [[| 3] 4]])
      (insert-left 1 2 3)
      (zip/cursor))
  => '([1 2 [[1 2 3 | 3] 4]]))

^{:refer hara.zip.base/insert-right :added "2.4"}
(fact "insert element/s right of the current position"

  (-> (zip/from-cursor '[| 1 2 3])
      (insert-right 1 2 3)
      (zip/cursor))
  => '([| 3 2 1 1 2 3]))

^{:refer hara.zip.base/delete-base :added "2.4"}
(fact "base delete helper")

^{:refer hara.zip.base/delete-left :added "2.4"}
(fact "delete element/s left of the current position"

  (-> (zip/from-cursor '[1 2 | 3])
      (delete-left)
      (zip/cursor))
  => '([1 | 3]))

^{:refer hara.zip.base/delete-right :added "2.4"}
(fact "delete element/s right of the current position"

  (-> (zip/from-cursor '[1 2 | 3])
      (delete-right)
      (zip/cursor))
  => '([1 2 |]))

^{:refer hara.zip.base/replace-left :added "2.4"}
(fact "replace element left of the current position"

  (-> (zip/from-cursor '[1 2 | 3])
      (replace-left "10")
      (zip/cursor))
  => '([1 "10" | 3]))

^{:refer hara.zip.base/replace-right :added "2.4"}
(fact "replace element right of the current position"

  (-> (zip/from-cursor '[1 2 | 3])
      (replace-right "10")
      (zip/cursor))
  => '([1 2 | "10"]))
