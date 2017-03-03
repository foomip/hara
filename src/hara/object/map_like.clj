(ns hara.object.map-like
  (:require [hara.protocol.object :as object]
            [hara.object
             [access :as access]
             [print :as print]
             [read :as read]
             [write :as write]]))

(defn key-selection
  ""
  [m include exclude]
  (cond-> m
    include (select-keys include)
    exclude (#(apply dissoc % exclude))))

(defn read-proxy-functions [proxy]
  (reduce-kv (fn [out accessor ks]
               (reduce (fn [out k]
                         (assoc out k `(fn [~'obj]
                                         (let [~'proxy (access/get ~'obj ~accessor)]
                                           (access/get ~'proxy ~k)))))
                       out
                       ks))
             {}
             proxy))

(defn write-proxy-functions [proxy]
  (reduce-kv (fn [out accessor ks]
               (reduce (fn [out k]
                         (assoc out k `(fn [~'obj ~'v]
                                         (let [~'proxy (access/get ~'obj ~accessor)]
                                           (access/set ~'proxy ~k ~'v)))))
                       out
                       ks))
             {}
             proxy))

(defmacro extend-map-like
  "creates an entry for map-like classes
 
   (extend-map-like test.DogBuilder
                    {:tag \"build.dog\"
                     :write {:empty (fn [_] (test.DogBuilder.))}
                     :read :reflect})
 
   (extend-map-like test.Dog {:tag \"dog\"
                              :write  {:methods :reflect
                                       :from-map (fn [m] (-> m
                                                             (write/from-map test.DogBuilder)
                                                             (.build)))}
                              :exclude [:species]})
 
   (with-out-str
     (prn (write/from-data {:name \"hello\"} test.Dog)))
   => \"#dog{:name \"hello\"}\"
 
   (extend-map-like test.Cat {:tag \"cat\"
                              :write  {:from-map (fn [m] (test.Cat. (:name m)))}
                              :exclude [:species]})
 
   (extend-map-like test.Pet {:tag \"pet\"
                              :from-map (fn [m] (case (:species m)
                                                  \"dog\" (write/from-map m test.Dog)
                                                  \"cat\" (write/from-map m test.Cat)))})
 
   (with-out-str
    (prn (write/from-data {:name \"hello\" :species \"cat\"} test.Pet)))
   => \"#cat{:name \"hello\"}\""
  {:added "2.3"}
  [^Class cls {:keys [read write exclude include proxy] :as opts}]
  `[(defmethod object/-meta-read ~cls
      [~'_]
      ~(let [read (cond (map? read)
                        (update-in read [:methods]
                                   #(list 'merge %
                                          `(-> (merge (read/read-all-getters ~cls read/+read-get-template+)
                                                      (read/read-all-getters ~cls read/+read-is-template+))
                                               (key-selection (or ~include []) ~exclude))))
                        
                        (= read :reflect)
                        `{:methods (key-selection (read/read-reflect-fields ~cls) ~include ~exclude)}

                        
                        (= read :all)
                        `{:methods (-> (merge (read/read-all-getters ~cls read/+read-get-template+)
                                              (read/read-all-getters ~cls read/+read-is-template+))
                                       (key-selection ~include ~exclude))}
                        
                        (or (nil? read)
                            (= read :class))
                        `{:methods (-> (merge (read/read-getters ~cls read/+read-get-template+)
                                              (read/read-getters ~cls read/+read-is-template+))
                                       (key-selection ~include ~exclude))})
             read (update-in read [:methods] #(list 'merge % (read-proxy-functions proxy)))]
         (print/assoc-print-vars read opts)))

    ~(when (and write (map? write))
       (assert (or (:from-map write)
                   (:empty write)
                   (:construct write))
               "The :write entry requires a sub-entry for either :from-map, :construct or :empty ")
       (let [methods (:methods write)]
         `(defmethod object/-meta-write ~cls
            [~'_]
            ~(cond-> write
               (= methods :reflect)
               (assoc :methods `(write/write-reflect-fields ~cls))

               (= methods :all)
               (assoc :methods `(write/write-all-setters ~cls))

               (or (= write :class)
                   (nil? methods))
               (assoc :methods `(write/write-setters ~cls))

               :then
               (update-in [:methods] #(list 'merge % (write-proxy-functions proxy)))))))

    (print/extend-print ~cls)])

