(ns hara.zip
  (:require [hara.zip.base :as base]
            [hara.event :as event]
            [clojure.string :as string]
            [hara.namespace.import :as ns])
  (:refer-clojure :exclude [next find]))

(ns/import hara.zip.base
           [zipper seq-zip vector-zip history
            left-node right-node left-nodes right-nodes siblings
            left-most? right-most? move-left? move-right?
            top-most? bottom-most? move-up? move-down?
            move-left move-left-most move-right move-right-most
            move-up move-top-most move-down move-bottom-most
            insert-left insert-right delete-left delete-right
            replace-left replace-right])

(defn node
  "accesses the node directly right of the cursor
 
   (-> (vector-zip [1 2 3])
       (find-next even?)
       (node))
   => 2"
  {:added "2.4"}
  [zip]
  (base/right-node zip))

(defn left
  "move cursor left"
  {:added "2.4"}
  [zip]
  (base/move-left zip))

(defn right
  "move cursor right"
  {:added "2.4"}
  [zip]
  (base/move-right zip))

(defn up
  "move cursor up"
  {:added "2.4"}
  [zip]
  (base/move-up zip))

(defn down
  "move cursor down"
  {:added "2.4"}
  [zip]
  (base/move-down zip))

(defn root-node
  "accesses the top level node
 
   (-> (vector-zip [[[3] 2] 1])
       (move-bottom-most)
       (root-node))
   => [[[3] 2] 1]"
  {:added "2.4"}
  [zip]
  (-> zip base/move-top-most base/right-node))

(defn surround
  "adds additional levels to the element
   
   (-> (vector-zip 1)
       (surround 2)
       (root-node))
   => [[1]]"
  {:added "2.4"}
  ([zip]
   (cond (base/right-most? zip)
         (event/raise {:fn  :surround
                       :op  :insert
                       :tag :no-right
                       :zip zip}
                      "No Right Node")

         :else
         (let [rnode (base/right-node zip)
               make-node (-> zip :meta :make-node)
               new-node (make-node [rnode])]
           (-> zip
               (base/replace-right new-node)
               (base/move-down)))))
  ([zip num]
   (nth (iterate surround zip) num)))

(defn cursor
  "returns the form with the cursor showing
 
   (-> (vector-zip [1 [[2] 3]])
       (find-next even?)
       (cursor))
   => '([1 [[| 2] 3]])"
  {:added "2.4"}
  [zip]
  (event/manage
   (-> zip
       (base/insert-left '|)
       (base/move-top-most)
       (base/siblings))
   (on {:fn :insert-left :tag :at-top} [zip element]
       (event/continue (base/insert-base zip element :left :insert-left)))))

(defn cursor-str
  "returns the string form of the cursor
 
   (-> (vector-zip [1 [[2] 3]])
       (find-next even?)
       (cursor-str))
   => \"[1 [[| 2] 3]]\""
  {:added "2.4"}
  [zip]
  (->> (cursor zip)
       (apply prn-str)
       (string/trim)))

(defn end
  "move cursor to the end of the tree
   (->> (vector-zip [1 [2 [6 7] 3] [4 5]])
        (end)
        (cursor))
   => '([1 [2 [6 7] 3] [4 | 5]])"
  {:added "2.4"}
  [zip]
  (cond (nil? zip) nil

        (base/move-right? (base/move-right zip))
        (recur (base/move-right zip))

        (base/move-down? zip)
        (recur (base/move-down zip))

        :else zip))

(defn next
  "move cursor through the tree in depth first order
   
   (->> (vector-zip [1 [2 [6 7] 3] [4 5]])
        (iterate next)
        (drop 1)
        (take 11)
        (map node))
   => '(1 [2 [6 7] 3] 2 [6 7] 6 7 3 [4 5] 4 5 nil)"
  {:added "2.4"}
  [zip]
  (cond (nil? zip) nil

        (base/move-down? zip)
        (base/move-down zip)

        :else
        (let [zip (base/move-right zip)]
          (if (base/move-right? zip)
            zip
            
            (loop [zip (-> zip
                           (base/move-up)
                           (base/move-right))]
              (cond (base/top-most? zip)
                    nil
                    
                    (base/move-right? zip)
                    zip
                    
                    :else
                    (recur (-> zip
                               (base/move-up)
                               (base/move-right)))))))))

(defn prev
  "move cursor in reverse through the tree in depth first order
 
   (->> (vector-zip [1 [2 [6 7] 3] [4 5]])
        (end)
        (iterate prev)
        (take 10)
        (map node))
   => '(5 4 [4 5] 3 7 6 [6 7] 2 [2 [6 7] 3] 1)"
  {:added "2.4"}
  [zip]
  (cond (nil? zip) nil
        
        (base/move-left? zip)
        (loop [zip (base/move-left zip)]
          (if (base/move-down? zip)
            (recur (-> zip
                       base/move-down
                       base/move-right-most
                       base/move-left))
            zip))

        (base/top-most? zip)
        nil

        :else
        (base/move-up zip)))

(defn find-next
  "move cursor through the tree in depth first order to the first matching element
 
   (-> (vector-zip [1 [2 [6 7] 3] [4 5]])
       (find-next #(= 7 %))
       (cursor))
   => '([1 [2 [6 | 7] 3] [4 5]])
   "
  {:added "2.4"}
  [zip pred]
  (->> (iterate next zip)
       (drop 1)
       (take-while identity)
       (filter #(try (pred (node %))
                     (catch Throwable t)))
       (first)))

(defn find-prev
  "move cursor through the tree in reverse order to the last matching element
 
   (-> (vector-zip [1 [2 [6 7] 3] [4 5]])
       (find-next #(= 7 %))
       (find-prev even?)
       (cursor))
   => '([1 [2 [| 6 7] 3] [4 5]])"
  {:added "2.4"}
  [zip pred]
  (->> (iterate prev zip)
       (drop 1)
       (take-while identity)
       (filter #(try (pred (node %))
                     (catch Throwable t)))
       (first)))

(defn prewalk
  "emulates clojure.walk/prewalk behavior with zipper
   
   (-> (vector-zip [1 [2 [6 7] 3] [4 5]])
       (prewalk (fn [v] (if (vector? v)
                          (conj v 100)
                          (+ v 100))))
       (root-node))
   => [101 [102 [106 107 200] 103 200] [104 105 200] 200]"
  {:added "2.4"}
  [zip f]
  (let [zip (base/replace-right zip (f (base/right-node zip)))]
    (cond (base/move-down? zip)
          (loop [zip (base/move-down zip)]
            (let [zip  (-> (prewalk zip f)
                           (base/move-right))]
              (cond (base/move-right? zip)
                    (recur zip)
                    
                    :else
                    (base/move-up zip))))
          
          :else zip)))

(defn postwalk
  "emulates clojure.walk/postwalk behavior with zipper
 
   (-> (vector-zip [1 [2 [6 7] 3] [4 5]])
       (find-next even?)
       (up)
       (postwalk (fn [v] (if (vector? v)
                           (conj v 100)
                           (+ v 100))))
      (root-node))
   => [1 [102 [106 107 200] 103 100] [4 5]]"
  {:added "2.4"}
  [zip f]
  (let [zip (cond (base/move-down? zip)
                  (loop [zip (base/move-down zip)]
                    (let [zip  (-> (prewalk zip f)
                                   (base/move-right))]
                      (cond (base/move-right? zip)
                            (recur zip)
                            
                            :else
                            (base/move-up zip))))
                  
                  :else zip)]
    (base/replace-right zip (f (base/right-node zip)))))

(def op-lookup
  {:up             base/move-up
   :down           base/move-down
   :left           base/move-left
   :right          base/move-right
   :insert         base/insert-right
   :delete         base/delete-right
   :replace        base/replace-right
   :insert-right   base/insert-right
   :delete-right   base/delete-right
   :replace-right  base/replace-right
   :insert-left    base/insert-left
   :replace-left   base/replace-left
   :delete-left    base/delete-left
   :top-most       base/move-top-most
   :left-most      base/move-left-most
   :right-most     base/move-right-most
   :bottom-most    base/move-bottom-most
   :surround       surround
   :prewalk        prewalk
   :postwalk       postwalk
   :next           next
   :prev           prev
   :find-next      find-next
   :find-prev      find-prev
   :show           (fn [zip & comments] (apply println
                                               (concat comments [(cursor-str zip)])) zip)
   :print          (fn [zip & fs]
                     (->> fs
                          (map #(if (ifn? %) (% zip) %))
                          (apply println))
                     zip)
   :do             (fn [zip f & args]
                     (apply f zip args)
                     zip)})

(defn traverse
  "traverse through zipper with data
 
   (-> (traverse (vector-zip [1 [[2] 3]])
                 :down
                 :right
                 [:down 2]
                 [:right]
                 [:insert-right 1 2 3 4])
      (cursor))
   => '([1 [[2 | 4 3 2 1] 3]])"
  {:added "2.4"}
  ([zip command]
   (let [[op args] (cond (keyword? command)
                         [command ()]

                         (vector? command)
                         [(first command) (rest command)]

                         :else (throw (Exception. (str "Invalid input: " command))))
         op-fn (op-lookup op)]
     (if (nil? op-fn)
       (throw (Exception. (str "Cannot find operation: " op
                               "values are: \n"
                               (sort (keys op-lookup)))))
       (apply op-fn zip args))))
  ([zip command & more]
   (apply traverse (traverse zip command) more)))
