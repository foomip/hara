(ns hara.test.checker.util)

(defn contains-exact
  [seq pattern]
  (let [len (count pattern)
        n (- (count seq) len)
        seq (vec seq)]
    (or (->> (range (+ n 1))
             (map (fn [i]
                    (->> (subvec seq i (+ i len))
                         (map #(%1 %2) pattern)
                         (every? true?))))
             (some true?))
        false)))

(defn contains-with-gaps [seq pattern]
  (cond (empty? pattern) true
          
          (empty? seq) false

          ((first pattern) (first seq))
          (recur (next seq) (next pattern))
          
          :else
          (recur (next seq) pattern)))

(defn perm-check
  ([perm all]
   (perm-check perm all (zipmap all (repeat nil))))
  ([perm all selection]
   (cond (empty? all)
         true

         (->> (vals selection)
              (every? (comp nil? not)))
         true
         
         :else
         (let [stats (reduce (fn [out v]
                               (let [cnt (->> (keep-indexed (fn [i set]
                                                              (if (get set v) i)) perm)
                                              set)]
                                 (assoc-in out [v] cnt)))
                             {}
                             all)
               order (->> stats
                          (into [])
                          (sort-by (comp count second)))
               [i matches] (first order)
               col   (first matches)]
           (if (nil? col)
             false
             (recur (-> (mapv #(disj % i) perm)
                        (update-in [col] empty))
                    (disj all i)
                    (assoc selection i col)))))))

(defn perm-build [seq pattern]
  (let [idx (->> pattern
                 (map-indexed (fn [i pat] [i pat]))
                 (into {}))]
    (mapv (fn [ele]
            (reduce-kv (fn [acc k v]
                         (if (v ele)
                           (conj acc k)
                           acc))
                       #{}
                       idx))
          seq)))

(defn contains-any-order [seq pattern]
  (let [seq (vec seq)
        len (count pattern)
        n (- (count seq) len)
        indices (->> (range (+ n 1))
                     (map (fn [i]
                            (perm-build (subvec seq i (+ i len))
                                        pattern))))]
    (or (->> indices
              (map #(perm-check % (-> len range set)))
              (some true?))
        false)))

(defn contains-all [seq pattern]
  (let [index (perm-build seq pattern)]
    (perm-check index (-> pattern count range set))))
