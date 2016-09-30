(ns hara.data.record)

(defn empty-record
  [v]
  (.invoke ^java.lang.reflect.Method
           (.getMethod ^Class (type v) "create"
                       (doto ^"[Ljava.lang.Object;"
                         (make-array Class 1)
                         (aset 0 clojure.lang.IPersistentMap)))
           nil
           (object-array [{}])))
