(ns hara.zip.base
  (:require [hara.event :as event]))

(defrecord Zipper []
  Object
  (toString [obj]
    (str "#zip " (into {} (dissoc obj :meta)))))

(defmethod print-method Zipper
  [v ^java.io.Writer w]
  (.write w (str v)))

(defn zipper-meta
  "checks that the meta contains valid functions
   
   (zipper-meta {})
   => (throws)
   
   (zipper-meta {:branch?   seq?
                 :children  identity
                 :make-node identity})
   => map?"
  {:added "2.4"}
  [meta]
  (assert (every? meta [:branch? :children :make-node]))
  (update-in meta [:history] #(or % [])))

(defn zipper?
  "checks to see if an object is a zipper
   
   (zipper? 1)
   => false"
  {:added "2.4"}
  [x]
  (instance? hara.zip.base.Zipper x))

(defn zipper
  "constructs a zipper
   
   (zipper '(1 2 3) {:branch?   seq?
                     :children  identity
                     :make-node identity})
   => zipper?"
  {:added "2.4"}
  ([root {:keys [branch? children make-node] :as meta}]
   (map->Zipper {:left ()
                 :right (list root)
                 :parent :top
                 :meta (zipper-meta meta)})))

(defn seq-zip
  "constructs a sequence zipper"
  {:added "2.4"}
  [root]
  (zipper root {:branch?   seq?
                :children  identity
                :make-node identity}))

(defn vector-zip
  "constructs a vector based zipper"
  {:added "2.4"}
  [root]
  (zipper root {:branch? vector?
                :children seq
                :make-node vec}))

(defn history
  "accesses the zipper history
   
   (-> (vector-zip [1 [2 3]])
       (move-down)
       (move-right)
       (history))
   => [[:down] [:right]]"
  {:added "2.4"}
  [zip]
  (-> zip :meta :history))

(defn add-history
  "adds elements to the zipper history"
  {:added "2.4"}
  [zip command]
  (update-in zip
             [:meta :history]
             conj command))

(defn left-node
  "element directly left of current position
   
   (-> (vector-zip 1)
       (move-right)
       (left-node))
   => 1"
  {:added "2.4"}
  [zip]
  (first (:left zip)))

(defn right-node
  "element directly right of current position
 
   (-> (vector-zip 1)
       (right-node))
   => 1"
  {:added "2.4"}
  [zip]
  (first (:right zip)))

(defn left-nodes
  "all elements left of current position
   
   (-> (vector-zip [1 2 3 4])
       (move-down)
       (move-right)
       (move-right)
       (left-nodes))
   => '(1 2)"
  {:added "2.4"}
  [zip]
  (reverse (:left zip)))

(defn right-nodes
  "all elements right of current position
   
   (-> (vector-zip [1 2 3 4])
       (move-down)
       (move-right)
       (move-right)
       (right-nodes))
   => '(3 4)"
  {:added "2.4"}
  [zip]
  (:right zip))

(defn siblings
  "all elements left and right of current position
   
   (-> (vector-zip [1 2 3 4])
       (move-down)
       (move-right)
       (move-right)
       (siblings))
   => '(1 2 3 4)"
  {:added "2.4"}
  [{:keys [left right] :as zip}]
  (concat (reverse left) right))

(defn left-most?
  "check if at left-most point of a branch"
  {:added "2.4"}
  [zip]
  (empty? (:left zip)))

(defn right-most?
  "check if at right-most point of a branch"
  {:added "2.4"}
  [zip]
  (empty? (:right zip)))

(defn move-left?
  "check if can move left from current position"
  {:added "2.4"}
  [zip]
  (not (empty? (:left zip))))

(defn move-right?
  "check if can move right from current position"
  {:added "2.4"}
  [zip]
  (not (empty? (:right zip))))

(defn top-most?
  "check if at top-most point of the tree"
  {:added "2.4"}
  [zip]
  (= :top (:parent zip)))

(defn bottom-most?
  "check if at bottom-most point of a branch"
  {:added "2.4"}
  [zip]
  (or (empty? (:right zip))
      (not ((-> zip :meta :branch?)
            (first (:right zip))))))

(defn move-up?
  "check if can move up from current position"
  {:added "2.4"}
  [zip]
  (not= :top (:parent zip)))

(defn move-down?
  "check if can move down from current position"
  {:added "2.4"}
  [zip]
  (and (move-right? zip)
       ((-> zip :meta :branch?)
        (first (:right zip)))))


(defn move-left
  "move left from current position"
  {:added "2.4"}
  ([{:keys [left right] :as zip}]
   (cond (empty? left)
         (event/raise {:fn  :move-left
                       :op  :move
                       :tag :no-left
                       :zip zip}
                      "No Left Node"
                      (option :zip [] zip)
                      (default :zip))

         :else
         (-> zip
             (assoc :left (rest left))
             (assoc :right (cons (first left) right))
             (add-history [:left]))))
  ([zip num]
   (nth (iterate move-left zip) num)))

(defn move-left-most
  "move to left-most point of current branch"
  {:added "2.4"}
  [zip]
  (cond (move-left? zip)
        (recur (move-left zip))

        :else zip))

(defn move-right
  "move right from current position"
  {:added "2.4"}
  ([{:keys [left right] :as zip}]
   (cond (empty? right)
         (event/raise {:fn  :move-right
                       :op  :move
                       :tag :no-right
                       :zip zip}
                      "No Right Node"
                      (option :zip [] zip)
                      (default :zip))

         :else
         (-> zip
             (assoc :left (cons (first right) left))
             (assoc :right (rest right))
             (add-history [:right]))))
  ([zip num]
   (nth (iterate move-right zip) num)))

(defn move-right-most
  "move to right-most point of current branch"
  {:added "2.4"}
  [zip]
  (cond (move-right? zip)
        (recur (move-right zip))

        :else zip))

(defn move-up
  "move up from current position"
  {:added "2.4"}
  ([zip]
   (cond (top-most? zip)
         (event/raise {:fn  :move-up
                       :op  :move
                       :tag :at-top
                       :zip zip}
                      "At Top Node"
                      (option :zip [] zip)
                      (default :zip))

         (not (:changed? zip))
         (->  (:parent zip)
              (assoc :meta (:meta zip))
              (add-history [:up]))
         
         :else
         (let [{:keys [left right parent levels]} zip
               children ((-> zip :meta :make-node)
                         (concat (reverse left) right))]
           (-> parent
               (assoc :changed? true :meta (:meta zip))
               (update-in [:right] #(->> % rest (cons children)))
               (add-history [:up])))))
  ([zip num]
   (nth (iterate move-up zip) num)))

(defn move-top-most
  "move to top-most point of the tree"
  {:added "2.4"}
  [zip]
  (if (top-most? zip)
    zip
    
    (recur (move-up zip))))

(defn move-down
  "move down from current position"
  {:added "2.4"}
  ([zip]
   (cond (right-most? zip)
         (event/raise {:fn  :move-down
                       :op  :move
                       :tag :no-right
                       :zip zip}
                      "No Right Node"
                      (option :zip [] zip)
                      (default :zip))

         :else
         (let [rnode   (right-node zip)
               branch?  (-> zip :meta :branch?)]
           (cond (branch? rnode)
                 (let [children (-> zip :meta :children)
                       coll (children rnode)]
                   (-> zip
                       (assoc :left () :right coll :parent zip)
                       (add-history [:down])))
                 
                 :else
                 (event/raise {:fn  :move-down
                               :op  :move
                               :tag :not-branch
                               :zip zip}
                              "Not Branch Node"
                              (option :zip [] zip)
                              (default :zip))))))
  ([zip num]
   (nth (iterate move-down zip) num)))

(defn move-bottom-most
  "move to bottom-most point of current branch"
  {:added "2.4"}
  [zip]
  (if (bottom-most? zip)
    zip
    
    (recur (move-down zip))))

(defn insert-base
  "base insert helper"
  {:added "2.4"}
  [zip element key op]
  (-> zip
      (update-in [key] conj element)
      (assoc :changed? true)
      (add-history [op element])))

(defn insert-left
  "insert element/s left of the current position
 
   (-> (vector-zip [1 2 3])
       (move-down)
       (insert-left 1 2 3)
       (zip/cursor))
   => '([1 2 3 | 1 2 3])"
  {:added "2.4"}
  ([zip element]
   (cond (top-most? zip)
         (event/raise {:fn  :insert-left
                       :op  :insert
                       :tag :at-top
                       :zip zip
                       :element element}
                      "At Top Node")

         :else
         (insert-base zip element :left :insert-left)))
  ([zip element & more]
   (apply insert-left (insert-left zip element) more)))

(defn insert-right
  "insert element/s right of the current position
 
   (-> (vector-zip [1 2 3])
       (move-down)
       (insert-right 1 2 3)
       (zip/cursor))
   => '([| 3 2 1 1 2 3])"
  {:added "2.4"}
  ([zip element]
   (cond (top-most? zip)
         (event/raise {:fn  :insert-right
                       :op  :insert
                       :tag :at-top
                       :zip zip
                       :element element}
                      "At Top Node")

         :else
         (insert-base zip element :right :insert-right)))
  ([zip element & more]
   (apply insert-right (insert-right zip element) more)))

(defn delete-base
  "base delete helper"
  {:added "2.4"}
  [zip key op]
  (-> zip
      (update-in [key] rest)
      (assoc :changed? true?)
      (add-history [op])))

(defn delete-left
  "delete element/s left of the current position
 
   (-> (vector-zip [1 2 3])
       (move-down)
       (move-right 2)
       (delete-left)
       (zip/cursor))
   => '([1 | 3])"
  {:added "2.4"}
  ([{:keys [left] :as zip}]
   (cond (empty? left)
         (event/raise {:fn  :delete-left
                       :op  :delete
                       :tag :no-left
                       :zip zip}
                      "No Left Node")
         
         (top-most? zip)
         (event/raise {:fn  :delete-left
                       :op  :delete
                       :tag :at-top
                       :zip zip}
                      "At Top Node")

         :else
         (delete-base zip :left :delete-left)))
  ([zip num]
   (nth (iterate delete-left zip) num)))

(defn delete-right
  "delete element/s right of the current position
 
   (-> (vector-zip [1 2 3])
       (move-down)
       (move-right 2)
       (delete-right)
       (zip/cursor))
   => '([1 2 |])"
  {:added "2.4"}
  ([{:keys [right] :as zip}]
   (cond (empty? right)
         (event/raise {:fn  :delete-right
                       :op  :delete
                       :tag :no-right
                       :zip zip}
                      "No Right Node")

         (top-most? zip)
         (event/raise {:fn  :delete-right
                       :op  :delete
                       :tag :at-top
                       :zip zip}
                      "At Top Node")

         :else
         (delete-base zip :right :delete-left)))
  ([zip num]
   (nth (iterate delete-right zip) num)))

(defn replace-left
  "replace element left of the current position
 
   (-> (vector-zip [1 2 3])
       (move-down)
       (move-right 2)
       (replace-left \"10\")
       (zip/cursor))
   => '([1 \"10\" | 3])"
  {:added "2.4"}
  [{:keys [left] :as zip} element]
  (cond (empty? left)
        (event/raise {:fn  :replace-left
                      :op  :replace
                      :tag :no-left
                      :zip zip}
                     "No Left Node")

        :else
        (-> zip
            (update-in [:left] #(->> % rest (cons element)))
            (update-in [:meta :history] conj :replace-left))))

(defn replace-right
  "replace element right of the current position
 
   (-> (vector-zip [1 2 3])
       (move-down)
       (move-right 2)
       (replace-right \"10\")
       (zip/cursor))
   => '([1 2 | \"10\"])"
  {:added "2.4"}
  [{:keys [right] :as zip} element]
  (cond (empty? right)
        (event/raise {:fn  :replace-right
                      :op  :replace
                      :tag :no-right
                      :zip zip}
                     "No Right Node")

        :else
        (-> zip
            (update-in [:right] #(->> % rest (cons element)))
            (update-in [:meta :history] conj :replace-right))))
