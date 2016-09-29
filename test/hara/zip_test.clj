(ns hara.zip-test
  (:use hara.test)
  (:require [hara.zip :refer :all]))

(comment
  (-> (vector-zip
       [1
        [[2 4] 6]
        [3 5 7]])
      (move-down))

  (= (-> (vector-zip [1 2 3 4])
         (move-down)
         (insert-left 1)
         (insert-left 2)
         (move-up))
     

     (-> (vector-zip [1 2 3 4])
         (move-down)
         (insert-left 1)
         (insert-left 2)
         (root-node)
         ))
  )
