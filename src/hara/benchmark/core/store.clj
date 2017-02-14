(ns hara.benchmark.core.store)

(defprotocol IAverageStore
    (-add [store result])
    (-count [store])
    (-average [store]))

(defmulti create-average-store
  "creates a store to put count averages
 
   (def avgs (create-average-store {}))
   
   (do (store/-add avgs 4)
       (store/-add avgs 5)
       (store/-add avgs [6]))
 
   (store/-count avgs)
   => 3
 
   (store/-average avgs)
   => [5.0]"
  {:added "2.4"}
  #(-> % :average :type))
  
(defmethod create-average-store :default
  [settings]
  (-> settings
      (assoc-in [:average :type] :memory)
      (create-average-store)))

(defmethod create-average-store :memory
  [settings]
  (atom {:count 0
         :total nil}))

(extend-protocol IAverageStore
  clojure.lang.Atom
  (-add [store result]
    (let [result (if (sequential? result)
                   (vec result)
                   [result])]
      (swap! store
             (fn [m]
               (-> m
                   (update-in [:count] inc)
                   (update-in [:total]
                              (fn [total]
                                (if total
                                  (mapv + total result)
                                  result))))))))
  (-count [store]
    (:count @store))
  (-average [store]
    (let [{:keys [count total]} @store]
      (mapv #(double (/ % count)) total))))


(defprotocol IHistoryStore
  (-put   [store [t :as result]])
  (-last  [store n])
  (-from  [store t-start])
  (-until [store t-end])
  (-between [store t-start t-end])
  (-all  [store]))

(defmulti create-history-store
  "creates a store to put count history
 
   (def hist (create-history-store {}))
   
   (do (store/-put hist [0 :hello])
       (store/-put hist [1 :world])
       (store/-put hist [2 :again])
       (store/-put hist [3 :again]))
   
   (store/-last hist 2)
   => [[2 :again] [3 :again]]
 
   (store/-from hist 2)
   => [[2 :again] [3 :again]]
 
   (store/-until hist 2)
   => [[0 :hello] [1 :world] [2 :again]]
 
   (store/-between hist 1 2)
   => [[1 :world] [2 :again]]
 
   (store/-all hist)
   => [[0 :hello] [1 :world] [2 :again] [3 :again]]"
  {:added "2.4"}  
  #(-> % :history :type))

(defmethod create-history-store :default
  [settings]
  (-> settings
      (assoc-in [:history :type] :memory)
      (create-history-store)))

(defmethod create-history-store :memory
  [settings]
  (atom ()))

(extend-protocol IHistoryStore
  clojure.lang.Atom
  (-put   [store [t :as result]]
    (swap! store conj result))
  (-last  [store n]
    (->> @store (take n) (reverse)))
  (-from  [store t-start]
    (->> @store
         (take-while (fn [[t & _]]
                       (>= t t-start)))
         (reverse)))
  (-until [store t-end]
    (->> @store
         (drop-while (fn [[t & _]]
                       (> t t-end)))
         (reverse)))
  (-between [store t-start t-end]
    (->> @store
         (drop-while (fn [[t & _]]
                       (> t t-end)))
         (take-while (fn [[t & _]]
                       (>= t t-start)))
         (reverse)))
  (-all  [store]
    (reverse @store)))
