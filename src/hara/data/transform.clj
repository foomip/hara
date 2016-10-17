(ns hara.data.transform
  (:require [hara.data.nested :as nested]
            [hara.data.map :as map]
            [clojure.string :as string])
  (:refer-clojure :exclude [apply]))

(defn template-rel
  "creates the id for a relation
 
   (template-rel [:authority :username])
   => :<authority/username>"
  {:added "2.4"}
  [v]
  (->> (map name v)
       (string/join "/")
       keyword))

(defn forward-rel
  "returns the template for a forward relation
 
   (forward-rel {:authority {:username [:user]
                             :password [:pass]}})
   
   => {:authority {:username :<authority/username>,
                   :password :<authority/password>}}"
  {:added "2.4"}
  ([trans] (forward-rel trans [] {}))
  ([trans kv out]
   (reduce-kv (fn [out k v]
                (let [nkv (conj kv k)]
                  (cond (map? v)
                        (forward-rel v nkv out)

                        :else
                        (assoc-in out nkv (template-rel nkv)))))
              out
              trans)))

(defn backward-rel
  "returns the template for a back relation
   
   (backward-rel {:authority {:username [:user]
                              :password [:pass]}})
   => {:user :<authority/username>, :pass :<authority/password>}"
  {:added "2.4"}
  ([trans] (backward-rel trans [] {}))
  ([trans kv out]
   (reduce-kv (fn [out k v]
                (let [nkv (conj kv k)]
                  (cond (map? v)
                        (backward-rel v nkv out)

                        :else
                        (assoc-in out v (template-rel nkv)))))
              out
              trans)))

(defn collect
  "collects nested keys for transform
   
   (collect {:authority {:username :<authority/username>,
                         :password :<authority/password>}})
   => {:<authority/username> [:authority :username],
       :<authority/password> [:authority :password]}"
  {:added "2.4"}
  ([m] (collect m [] {}))
  ([m kv out]
   (reduce-kv (fn [out k v]
                (cond (map? v)
                      (collect  v (conj kv k) out)

                      :else
                      (assoc out v (conj kv k))))
              out
              m)))

(defn relation
  "creates template for the transform relationship
 
   (relation {:authority {:username [:user]
                          :password [:pass]}})
   => {:<authority/username> [[:authority :username] [:user]],
       :<authority/password> [[:authority :password] [:pass]]}"
  {:added "2.4"}
  [trans]
  (let [rel [(forward-rel trans)
             (backward-rel trans)]]
    (->> (map collect rel)
         (apply merge-with vector))))


(defn apply
  "applies the relation to a map
 
   (apply {:user \"chris\" :pass \"hello\"}
          {:<authority/username> [[:authority :username] [:user]],
           :<authority/password> [[:authority :password] [:pass]]})
   => {:authority {:username \"chris\", :password \"hello\"}}"
  {:added "2.4"}
  [m rel]
  (reduce-kv (fn [out k [to from]]
               (let [v (get-in m from)]
                 (-> out
                     (assoc-in to v)
                     (map/dissoc-in from))))
             m
             rel))

(defn retract
  "retracts the relation from the map
 
   (retract {:authority {:username \"chris\", :password \"hello\"}}
            {:<authority/username> [[:authority :username] [:user]],
             :<authority/password> [[:authority :password] [:pass]]})
   => {:user \"chris\" :pass \"hello\"}"
  {:added "2.4"}
  [m rel]
  (reduce-kv (fn [out k [to from]]
               (let [v (get-in m to)]
                 (-> out
                     (assoc-in from v)
                     (map/dissoc-in to))))
             m
             rel))
