(ns hara.concurrent.ova-test
  (:use hara.test)
  (:require [hara.concurrent.ova :refer :all]
            [hara.common.watch :as watch]))

^{:refer hara.concurrent.ova/ova :added "2.1"}
(fact "constructs an instance of an ova"

  (ova []) ;=> #ova []

  (ova [1 2 3]) ;=>  #ova [1 2 3]

  (<< (ova [{:id :1} {:id :2}]))
  => [{:id :1} {:id :2}])

^{:refer hara.concurrent.ova/ova? :added "2.4"}
(fact "checks if an object is an ova instance"

  (ova? (ova [1 2 3]))
  => true)

^{:refer hara.concurrent.ova/clone :added "2.1"}
(fact "creates an exact copy of the ova, including its watches"

  (def o (ova (range 10)))
  (watch/set o {:a (fn [_ _ _ _ _])})
  
  (def other (clone o))
  
  (<< other) => (<< o)
  (watch/list other) => (just {:a fn?}))


^{:refer hara.concurrent.ova/init! :added "2.1"}
(fact "sets elements within an ova"

  (def o (ova []))
  (->> (init! o [{:id :1 :val 1} {:id :2 :val 1}])
       (dosync)
       (<<))
  => [{:val 1, :id :1} {:val 1, :id :2}])

^{:refer hara.concurrent.ova/<< :added "2.1"}
(fact "outputs outputs the entire output of an ova"
  
  (-> (ova [1 2 3 4 5])
      (append! 6 7 8 9)
      (<<))
  => [1 2 3 4 5 6 7 8 9]

  ;; can also use `persistent!`
  (-> (ova [1 2 3 4 5])
      (persistent!))
  => [1 2 3 4 5])


^{:refer hara.concurrent.ova/select :added "2.1"}
(fact "grabs the selected ova entries as a set of values"

  (def o (ova [{:id :1 :val 1} {:id :2 :val 1}
               {:id :3 :val 2} {:id :4 :val 2}]))
  
  (select o)              ;; no filters
  => #{{:id :1, :val 1}  
       {:id :2, :val 1}
       {:id :3, :val 2}
       {:id :4, :val 2}}
  
  (select o 0)            ;; by index
  => #{{:id :1 :val 1}} 

  (select o #{1 2})       ;; by indices
  => #{{:id :2 :val 1}
       {:id :3 :val 2}}

  (select o #(even? (:val %))) ;; by function
  => #{{:id :3 :val 2}
       {:id :4 :val 2}}

  (select o [:val 1])        ;; by shorthand value
  => #{{:id :1 :val 1}
       {:id :2 :val 1}}

  (select o [:val even?])    ;; by shorthand function
  => #{{:id :3 :val 2}
       {:id :4 :val 2}}

  (select o #{[:id :1]       ;; or selection
              [:val 2]})
  => #{{:id :1 :val 1}
       {:id :3 :val 2}
       {:id :4 :val 2}}
  
  (select o [:id '((name)    ;; by shorthand expression
                   (bigint)
                   (odd?))])
  => #{{:id :1 :val 1}
       {:id :3 :val 2}})

^{:refer hara.concurrent.ova/selectv :added "2.1"}
(fact "grabs the selected ova entries as vector"

  (def o (ova [{:id :1 :val 1} {:id :2 :val 1}
               {:id :3 :val 2} {:id :4 :val 2}]))
  
  (selectv o)              ;; no filters
  => [{:id :1, :val 1}  
      {:id :2, :val 1}
      {:id :3, :val 2}
      {:id :4, :val 2}]
  
  (selectv o 0)            ;; by index
  => [{:id :1 :val 1}] 

  (selectv o [:val even?])    ;; by shorthand function
  => [{:id :3 :val 2}
      {:id :4 :val 2}]
  
  (selectv o [:id '((name)    ;; by shorthand expression
                    (bigint)
                    (odd?))])
  => [{:id :1 :val 1}
      {:id :3 :val 2}])

^{:refer hara.concurrent.ova/indices :added "2.1"}
(fact "instead of data, outputs the matching indices"
      
  (def o (ova [{:id :1 :val 1} {:id :2 :val 1}
               {:id :3 :val 2} {:id :4 :val 2}]))
  
  (indices o)
  => [0 1 2 3]

  (indices o 0)
  => [0]

  (indices o [:val 1])
  => [0 1]

  (indices o [:val even?])
  => [2 3]
  
  (indices o [:val even?
              '(:id (name) (bigint)) odd?])
  => [2]

  (indices o #{4})
  => []
  
  (indices o [:id :1])
  => [0])

^{:refer hara.concurrent.ova/has? :added "2.1"}
(fact "checks that the ova contains elements matching a selector"

  (def o (ova [{:id :1 :val 1} {:id :2 :val 1}
               {:id :3 :val 2} {:id :4 :val 2}]))
  
  (has? o)
  => true

  (has? o 0)
  => true

  (has? o -1)
  => false

  (has? o [:id '((name)
                 (bigint)
                 (odd?))])
  => true)

^{:refer hara.concurrent.ova/get-filtered :added "2.1"}
(fact "gets the first element in the ova that matches the selector:"

  (def o (ova [{:id :1 :val 1} {:id :2 :val 1}]))
  
  (get-filtered o :1 nil nil)
  => {:val 1, :id :1}

  (get-filtered o :2 nil nil)
  => {:val 1, :id :2}

  (get-filtered o :3 nil :not-found)
  => :not-found)

^{:refer hara.concurrent.ova/-invoke :added "2.1"}
(fact "the ova itself can be invoked to get the first match"

  (def o (ova [{:id :1 :val 1} {:id :2 :val 1}
               {:id :3 :val 2} {:id :4 :val 2}]))    
  
  ;; Simplified indices and :id lookups

  (o 0)
  => {:val 1, :id :1}

  (o :1)
  => {:val 1, :id :1}

  (:1 o)
  => {:val 1, :id :1}

  ;; Selector lookups

  (o :id :2)
  => {:val 1, :id :2}

  (o :val 2)
  => {:val 2, :id :3}

  (o :val even?)
  => {:val 2, :id :3}

  (o (list :id name) "4")
  => {:val 2, :id :4})

^{:refer hara.concurrent.ova/!! :added "2.1"}
(fact "sets the value of selected data cells in the ova"
  
  (-> (range 5)
      (ova)
      (!! 1 0)
      (<<))
  => [0 0 2 3 4]

  (-> (range 5)
      (ova)
      (!! #{1 2} 0)
      (<<))
  => [0 0 0 3 4]

  (-> (range 5)
      (ova)
      (!! even? 0)
      (<<))
  => [0 1 0 3 0])

^{:refer hara.concurrent.ova/!> :added "2.1"}
(fact "applies a set of transformations to a selector on the ova"
  
  (<< (!> (ova [{:id :1}])
          0
          (assoc-in [:a :b] 1)
          (update-in [:a :b] inc)
          (assoc :c 3)))
  => [{:id :1 :c 3 :a {:b 2}}])

^{:refer hara.concurrent.ova/append! :added "2.1"}
(fact "like `conj!` but appends multiple array elements to the ova"

  (-> (ova [{:id :1 :val 1}])
      (append! {:id :2 :val 1}
               {:id :3 :val 2})
      (<<))
  => [{:id :1 :val 1}
      {:id :2 :val 1}
      {:id :3 :val 2}])


^{:refer hara.concurrent.ova/concat! :added "2.1"}
(fact "works like `concat`, allows both array and ova inputs"

  (<< (concat! (ova [{:id :1 :val 1}
                     {:id :2 :val 1}])
               (ova [{:id :3 :val 2}])
               [{:id :4 :val 2}]))
  => [{:val 1, :id :1}
      {:val 1, :id :2}
      {:val 2, :id :3}
      {:val 2, :id :4}])

^{:refer hara.concurrent.ova/empty! :added "2.1"}
(fact "empties an existing ova"

  (-> (ova [1 2 3 4 5])
      (empty!)
      (<<))
  => [])

^{:refer hara.concurrent.ova/filter! :added "2.1"}
(fact "keep only elements that matches the selector"

  (-> (ova [0 1 2 3 4 5 6 7 8 9])
      (filter! #{'(< 3) '(> 6)})
      (<<))
  => [0 1 2 7 8 9])

^{:refer hara.concurrent.ova/map! :added "2.1"}
(fact "applies a function on the ova with relevent arguments"

  (-> (ova [{:id :1} {:id :2}])
      (map! assoc :val 1)
      (<<))
  => [{:val 1, :id :1}
      {:val 1, :id :2}])

^{:refer hara.concurrent.ova/map-indexed! :added "2.1"}
(fact "applies a function that taking the data index as well as the data
  to all elements of the ova"

  (-> (ova [{:id :1} {:id :2}])
      (map-indexed! (fn [i m]
                      (assoc m :val i)))
      (<<))
  => [{:val 0, :id :1}
      {:val 1, :id :2}])

^{:refer hara.concurrent.ova/smap! :added "2.1"}
(fact "applies a function to only selected elements of the array"

  (-> (ova [{:id :1 :val 1}
            {:id :2 :val 1}
            {:id :3 :val 2}
            {:id :4 :val 2}])
      (smap! [:val 1]
             update-in [:val] #(+ % 100))
      (<<))
  => [{:id :1, :val 101}
      {:id :2, :val 101}
      {:id :3, :val 2}
      {:id :4, :val 2}])

^{:refer hara.concurrent.ova/smap-indexed! :added "2.1"}
(fact "applies a function that taking the data index as well as the data
  to selected elements of the ova"

  (-> (ova [{:id :1 :val 1}
            {:id :2 :val 1}
            {:id :3 :val 2}
            {:id :4 :val 2}])
      (smap-indexed! [:val 1]
                     (fn [i m]
                       (update-in m [:val] #(+ i 100 %))))
      (<<))
  => [{:id :1, :val 101}
      {:id :2, :val 102}
      {:id :3, :val 2}
      {:id :4, :val 2}])

^{:refer hara.concurrent.ova/insert! :added "2.1"}
(fact "inserts data at either the end of the ova or when given an index"

  (-> (ova (range 5))
      (insert! 6)
      (<<))
  => [0 1 2 3 4 6]

  (-> (ova (range 5))
      (insert! 6)
      (insert! 5 5)
      (<<))
  => [0 1 2 3 4 5 6])

^{:refer hara.concurrent.ova/sort! :added "2.1"}
(fact "sorts all data in the ova using a comparator function"

  (-> (ova [2 1 3 4 0])
      (sort! >)
      (<<))
  => [4 3 2 1 0]

  (-> (ova [2 1 3 4 0])
      (sort! <)
      (<<))
  => [0 1 2 3 4])

^{:refer hara.concurrent.ova/reverse! :added "2.1"}
(fact "reverses the order of elements in the ova"

  (-> (ova (range 5))
      (reverse!)
      (<<))
  => [4 3 2 1 0])

^{:refer hara.concurrent.ova/remove! :added "2.1"}
(fact "removes data from the ova that matches a selector"

  (-> (ova (range 10))
      (remove! odd?)
      (<<))
  => [0 2 4 6 8]
  
  (-> (ova (range 10))
      (remove! #{'(< 3) '(> 6)})
      (<<))
  => [3 4 5 6])

^{:refer hara.concurrent.ova/split :added "2.1"}
(fact "splits an ova into two based on a predicate"
  
  (def o (ova (range 10)))
  (def sp (dosync (split o #{'(< 3) '(> 6)})))

  (persistent! (sp true))  => [0 1 2 7 8 9]
  (persistent! (sp false)) => [3 4 5 6])

^{:refer hara.common.watch/add :added "2.1"}
(fact "testing the watch/add function with map"
      
  (def ov     (ova [1 2 3 4]))
  (def out    (atom []))
  (def cj-fn  (fn  [_ _ _ p v]
                (swap! out conj [p v])))
  (watch/add ov :conj cj-fn)

  (<< (map! ov inc))
  => [2 3 4 5]
  
  (sort @out) => [[1 2] [2 3] [3 4] [4 5]])

^{:refer hara.common.watch/add :added "2.1"}
(fact "testing the watch/add with ova functionality"

  (def ov     (ova []))
  (def out    (atom []))
  (def o-fn   (fn  [_ _ p v]
                (swap! out conj [p v])))
  (do (watch/add ov :conj o-fn
                 {:type :ova
                  :select #(mapv deref %)})
      (dosync (conj! ov 1))
      (dosync (conj! ov 2))
      (dosync (conj! ov 3)))
  
  (<< ov)
  => [1 2 3]

  (sort @out)
  => [[[] [1]]
      [[1] [1 2]]
      [[1 2] [1 2 3]]])
