(ns hara.zip.base
  (:require [hara.event :as event]))

(defrecord Zipper []
  Object
  (toString [obj]
    (str "#zip " (-> (into {} obj)
                     (dissoc :meta)))))

(defmethod print-method Zipper
  [v ^java.io.Writer w]
  (.write w (str v)))

(defn zipper
  ([root {:keys [branch? children make-node] :as meta}]
   (map->Zipper {:left ()
                 :right (list root)
                 :levels ()
                 :parent :top
                 :meta meta})))

(defn left-node
  [zip]
  (first (:left zip)))

(defn right-node
  [zip]
  (first (:right zip)))

(defn is-left-most?
  [zip]
  (empty? (:left zip)))

(defn is-right-most?
  [zip]
  (empty? (:right zip)))

(defn move-left?
  [zip]
  (not (empty? (:left zip))))

(defn move-right?
  [zip]
  (not (empty? (:right zip))))

(defn move-down?
  [zip]
  (and (move-right? zip)
       ((-> zip :meta :branch?)
        (first (:right zip)))))

(defn move-up?
  [zip]
  (not= :top (:parent zip)))

(defn is-top-most?
  [zip]
  (= :top (:parent zip)))

(defn is-bottom-most?
  [zip]
  (or (empty? (:right zip))
      (not ((-> zip :meta :branch?)
            (first (:right zip))))))

(defn move-left
  [{:keys [left right] :as zip}]
  (cond (empty? left)
        (event/raise {:fn   :move-left
                      :op   :move
                      :tag  :no-left
                      :data zip}
                     "No Left Node"
                     (option :zip [] zip)
                     (default :zip))

        :else
        (-> zip
            (assoc :left (rest left))
            (assoc :right (cons (first left) right)))))

(defn move-left-most
  [{:keys [left right] :as zip}]
  (assoc zip
         :left  (list)
         :right (reduce (fn [right v]
                          (cons v right))
                        right
                        left)))

(defn move-right
  [{:keys [left right] :as zip}]
  (cond (empty? right)
        (event/raise {:fn   :move-right
                      :op   :move
                      :tag  :no-right
                      :data zip}
                     "No Right Node"
                     (option :zip [] zip)
                     (default :zip))

        :else
        (-> zip
            (assoc :left (cons (first right) left))
            (assoc :right (rest right)))))

(defn move-right-most
  [{:keys [left right] :as zip}]
  (assoc zip
         :right (list)
         :left  (reduce (fn [left v]
                          (cons v left))
                        left
                        right)))

(defn insert-left
  [{:keys [left] :as zip} & elements]
  (cond (is-top-most? zip)
        (event/raise {:fn   :insert-left
                      :op   :insert
                      :tag  :at-top
                      :data zip
                      :elements elements}
                     "At Top Node")
        :else
        (assoc zip
               :left (reduce (fn [left e]
                               (cons e left))
                             left
                             elements)
               :changed? true)))

(defn insert-right
  [{:keys [right] :as zip} & elements]
  (cond (is-top-most? zip)
        (event/raise {:fn   :insert-right
                      :op   :insert
                      :tag  :at-top
                      :data zip
                      :elements elements}
                     "At Top Node")

        :else
        (assoc zip
               :right (reduce (fn [right e]
                                (cons e right))
                              right
                              elements)
               :changed? true)))

(defn delete-left
  ([{:keys [left] :as zip}]
   (cond (empty? left)
         (event/raise {:fn   :delete-left
                       :op   :delete
                       :tag  :no-left
                       :data zip}
                      "No Left Node")
         
         (is-top-most? zip)
         (event/raise {:fn   :delete-left
                       :op   :delete
                       :tag  :at-top
                       :data zip}
                      "At Top Node")

         :else
         (assoc zip :left (rest left) :changed? true?)))
  ([zip num]
   (nth (iterate delete-left zip) num)))

(defn delete-right
  ([{:keys [right] :as zip}]
   (cond (empty? right)
         (event/raise {:fn  :delete-right
                       :op  :delete
                       :tag :no-right
                       :data zip}
                      "No Right Node")

         (is-top-most? zip)
         (event/raise {:fn   :delete-right
                       :op   :delete
                       :tag  :at-top
                       :data zip}
                      "At Top Node")

         :else
         (assoc zip :right (rest right) :changed? true?)))
  ([zip num]
   (nth (iterate delete-right zip) num)))

(defn move-down
  [zip]
  (cond (is-right-most? zip)
        (event/raise {:fn   :move-down
                      :op   :move
                      :tag  :no-right
                      :data zip}
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
                      (update-in [:levels] conj rnode)))
                
                :else
                (event/raise {:fn   :move-down
                              :op   :move
                              :tag  :not-branch
                              :data zip}
                             "Not Branch Node"
                             (option :zip [] zip)
                             (default :zip))))))

(defn move-up
  [zip]
  (cond (is-top-most? zip)
        (event/raise {:fn   :move-up
                      :op   :move
                      :tag  :at-top
                      :data zip}
                     "At Top Node"
                     (option :zip [] zip)
                     (default :zip))

        (not (:changed? zip)) (assoc (:parent zip) :meta (:meta zip))
 
        :else
        (let [{:keys [left right parent levels]} zip
              children ((-> zip :meta :make-node)
                        (concat (reverse left) right))]
          (-> parent
              (assoc :changed? true :meta (:meta zip))
              (update-in [:right] #(->> % rest (cons children)))))))

(defn move-top-most 
  [zip]
  (if (is-top-most? zip)
    zip
    
    (recur (move-up zip))))

(defn root-node
  [zip]
  (-> zip move-top-most right-node))

(defn replace-left
  [zip element]
  (event/manage
   (-> zip
       (delete-left)
       (insert-left element)
       (assoc :changed? true))
   (on {:fn :delete-left :tag :at-top} [data]
       (event/continue (update-in data [:left] rest)))
   (on {:fn :insert-left :tag :at-top} [data elements]
       (event/continue (update-in data
                            [:left]
                            #(reduce (fn [left e]
                                       (cons e left))
                                     %
                                     elements))))))

(defn replace-right
  [zip element]
  (event/manage
   (-> zip
       (delete-right)
       (insert-right element)
       (assoc :changed? true))
   (on {:fn :delete-right :tag :at-top} [data]
       (event/continue (update-in data [:right] rest)))
   (on {:fn :insert-right :tag :at-top} [data elements]
       (event/continue (update-in data
                            [:right]
                            #(reduce (fn [right e]
                                       (cons e right))
                                     %
                                     elements))))))

(defn envelope
  [zip]
  (cond (is-right-most? zip)
        (event/raise {:fn   :evelope
                      :op   :insert
                      :tag  :no-right
                      :data zip}
                     "No Right Node")

        :else
        (let [rnode (right-node zip)
              make-node (-> zip :meta :make-node)
              new-node (make-node [rnode])]
          (-> zip
              (replace-right new-node)
              (move-down)))))
