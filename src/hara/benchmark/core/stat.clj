(ns hara.benchmark.core.stat)

(defmulti stat
  "extensible method for pulling stats out of procedure
   
   (defn sleep [{:keys [mean variation]
                 :or {mean 1000
                      variation 300}}]
     (doto (-> mean
               (- variation)
               (+ (rand-int variation)))
       (Thread/sleep)))
 
   (def sleep-proc (concurrent/procedure {:handler sleep} [:args :instance]))
 
   (def sleep-inst (sleep-proc {} {:mode :sync}))
 
   (proc/stat :start-time sleep-inst)
   => 1487022163132
   
   (proc/stat :duration sleep-inst)
   => 934
 
   (proc/stat :result sleep-inst)
   => 912"
  {:added "2.4"} (fn [tag _] tag))

(defmethod stat :start-time
  [_ proc]
  (-> proc :runtime deref :started :long))

(defmethod stat :duration
  [_ proc]
  (let [_     @proc
        runtime @(:runtime proc)]
    (- (or (-> runtime :ended :long) 0)
       (or (-> runtime :started :long) 0))))

(defmethod stat :result
  [_ proc]
  (let [_     @proc]
    (-> @(:result proc) :data)))

