(ns hara.zip-test
  (:use hara.test)
  (:require [hara.zip :refer :all])
  (:refer-clojure :exclude [next]))

^{:refer hara.zip/node :added "2.4"}
(fact "accesses the node directly right of the cursor"

  (-> (from-cursor [1 '| 2 3])
      (node))
  => 2)

^{:refer hara.zip/left :added "2.4"}
(fact "move cursor left"
  
  (-> (from-cursor [1 '| 2 3])
      (left)
      (cursor))
  => '([| 1 2 3]))

^{:refer hara.zip/right :added "2.4"}
(fact "move cursor right"
  (-> (from-cursor [1 '| 2 3])
      (right)
      (cursor))
  => '([1 2 | 3]))

^{:refer hara.zip/up :added "2.4"}
(fact "move cursor up"
  
  (-> (from-cursor [1 '| 2 3])
      (up)
      (cursor))
  => '(| [1 2 3])

  (-> (from-cursor [1 [['| 2] 3]])
      (up)
      (cursor))
  => '([1 [| [2] 3]]))

^{:refer hara.zip/down :added "2.4"}
(fact "move cursor down"

  (-> (from-cursor '[1 [| [2] 3]])
      (down)
      (cursor))
  => '([1 [[| 2] 3]]))

^{:refer hara.zip/root-node :added "2.4"}
(fact "accesses the top level node"

  (-> (vector-zip [[[3] 2] 1])
      (move-bottom-most)
      (root-node))
  => [[[3] 2] 1])

^{:refer hara.zip/surround :added "2.4"}
(fact "adds additional levels to the element"
  
  (-> (vector-zip 1)
      (surround 2)
      (root-node))
  => [[1]])

^{:refer hara.zip/cursor :added "2.4"}
(fact "returns the form with the cursor showing"

  (-> (vector-zip [1 [[2] 3]])
      (find-next even?)
      (cursor))
  => '([1 [[| 2] 3]]))

^{:refer hara.zip/cursor-str :added "2.4"}
(fact "returns the string form of the cursor"

  (-> (vector-zip [1 [[2] 3]])
      (find-next even?)
      (cursor-str))
  => "[1 [[| 2] 3]]")

^{:refer hara.zip/from-cursor :added "2.4"}
(fact "returns a zipper given a data structure with | as the cursor"

  (from-cursor '[1 2 3 | 4])
  => (contains {:left '(3 2 1),
                :right '(4)}))

^{:refer hara.zip/end :added "2.4"}
(fact "move cursor to the end of the tree"
  (->> (vector-zip [1 [2 [6 7] 3] [4 5]])
       (end)
       (cursor))
  => '([1 [2 [6 7] 3] [4 | 5]]))

^{:refer hara.zip/next :added "2.4"}
(fact "move cursor through the tree in depth first order"
  
  (->> (from-cursor '[| 1 [2 [6 7] 3] [4 5]])
       (iterate next)
       (take-while identity)
       (map node))
  => '(1 [2 [6 7] 3] 2 [6 7] 6 7 3 [4 5] 4 5))

^{:refer hara.zip/prev :added "2.4"}
(fact "move cursor in reverse through the tree in depth first order"

  (->> (from-cursor '[1 [2 [6 7] 3] [4 | 5]])
       (iterate prev)
       (take 10)
       (map node))
  => '(5 4 [4 5] 3 7 6 [6 7] 2 [2 [6 7] 3] 1))

^{:refer hara.zip/find-next :added "2.4"}
(fact "move cursor through the tree in depth first order to the first matching element"

  (-> (vector-zip [1 [2 [6 7] 3] [4 5]])
      (find-next #(= 7 %))
      (cursor))
  => '([1 [2 [6 | 7] 3] [4 5]]))

^{:refer hara.zip/find-prev :added "2.4"}
(fact "move cursor through the tree in reverse order to the last matching element"

  (-> (from-cursor '[1 [2 [6 | 7] 3] [4 5]])
      (find-prev even?)
      (cursor))
  => '([1 [2 [| 6 7] 3] [4 5]]))

^{:refer hara.zip/prewalk :added "2.4"}
(fact "emulates clojure.walk/prewalk behavior with zipper"
  
  (-> (vector-zip [1 [2 [6 7] 3] [4 5]])
      (prewalk (fn [v] (if (vector? v)
                         (conj v 100)
                         (+ v 100))))
      (root-node))
  => [101 [102 [106 107 200] 103 200] [104 105 200] 200])

^{:refer hara.zip/postwalk :added "2.4"}
(fact "emulates clojure.walk/postwalk behavior with zipper"

  (-> (vector-zip [1 [2 [6 7] 3] [4 5]])
      (find-next even?)
      (up)
      (postwalk (fn [v] (if (vector? v)
                          (conj v 100)
                          (+ v 100))))
      (root-node))
  => [1 [102 [106 107 200] 103 100] [4 5]])

^{:refer hara.zip/traverse :added "2.4"}
(fact "traverse through zipper with data"

  (-> (traverse (vector-zip [1 [[2] 3]])
                :down
                :right
                [:down 2]
                [:right]
                [:insert-right 1 2 3 4])
      (cursor))
  => '([1 [[2 | 4 3 2 1] 3]]))
