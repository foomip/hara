(ns hara.test.checker.collection
  (:require [hara.test.common :as common]
            [hara.test.checker.base :as base]
            [hara.test.checker.util :as util]))

(defn verify-map [ck data]
  (->> (reduce-kv (fn [out k sck]
                    (conj out (base/verify sck (get data k))))
                  []
                  ck)
       (every? base/succeeded?)))

(defn verify-seq [ck data modifiers]
  (cond (= #{} modifiers)
        (util/contains-exact data ck)

        (= #{:in-any-order} modifiers)
        (util/contains-any-order data ck)

        (= #{:gaps-ok} modifiers)
        (util/contains-with-gaps data ck)
        
        (= #{:in-any-order :gaps-ok} modifiers)
        (util/contains-all data ck)

        :else
        (throw (Exception. "modifiers can only be :gaps-only and :in-any-order"))))

(defn contains-map [x]
  (let [ck (reduce-kv (fn [out k v]
                              (assoc out k (base/->checker v)))
                            {}
                            x)]  
    (common/map->Checker
     {:tag :contains
      :doc "Checks if the result is a map having the following conditions:"
      :fn  (fn [res]
             (let [data (common/->data res)]
               (and (map? data)
                    (verify-map ck data))))
      :expect ck})))

(defn contains-vector
  ([x] (contains-vector x #{}))
  ([x modifiers]
   (let [ck (mapv base/->checker x)]  
     (common/map->Checker
      {:tag :contains
       :doc "Checks if the result is a sequential"
       :fn  (fn [res]
              (let [data (common/->data res)]
                (and (sequential? data)
                     (verify-seq ck data modifiers))))
       :expect ck}))))


(defn contains
  [x & modifiers]
  (cond (map? x)
        (contains-map x)

        (sequential? x)
        (contains-vector x (set modifiers))

        :else
        (throw (Exception. "Cannot create contains checker"))))

(defn just-map [x]
  (let [ck (reduce-kv (fn [out k v]
                              (assoc out k (base/->checker v)))
                            {}
                            x)]  
    (common/map->Checker
     {:tag :just
      :doc "Checks if the result is a map having strictly the following conditions:"
      :fn  (fn [res]
             (let [data (common/->data res)]
               (and (map? data)
                    (= (set (keys data))
                       (set (keys ck)))
                    (verify-map ck data))))
      :expect ck})))

(defn just-vector
  ([x] (just-vector x #{}))
  ([x modifiers]
   (let [ck (mapv base/->checker x)]  
     (common/map->Checker
      {:tag :just
       :doc "Checks if the result is a sequential having strictly the following conditions:"
       :fn  (fn [res]
              (let [data (common/->data res)]
                (and (sequential? data)
                     (= (count data)
                        (count ck))
                     (verify-seq ck data modifiers))))
       :expect ck}))))

(defn just
  [x & modifiers]
  (cond (map? x)
        (just-map x)

        (vector? x)
        (just-vector x (set modifiers))

        :else
        (throw (Exception. "Cannot create just checker"))))

(defmacro contains-in [x]
  "A macro for nested checking of data in the `contains` form"
  (cond (map? x)
        `(contains ~(reduce-kv (fn [out k v]
                                 (assoc out k `(contains-in ~v)))
                               {}
                               x))
        (vector? x)
        `(contains ~(reduce (fn [out v]
                              (conj out `(contains-in ~v)))
                            []
                            x))
        :else x))

(defmacro just-in [x]
  "A macro for nested checking of data in the `just` form"
  (cond (map? x)
        `(just ~(reduce-kv (fn [out k v]
                             (assoc out k `(just-in ~v)))
                           {}
                           x))
        (vector? x)
        `(just ~(reduce (fn [out v]
                          (conj out `(just-in ~v)))
                        []
                        x))
        :else x))

(defn raises
  ([]  (raises {}))
  ([m]
   (common/checker
    {:tag :raises
     :doc "Checks if an issue has been raised"
     :fn (fn [{:keys [^Throwable data type]}]
           (and (= :exception type)
                (instance? clojure.lang.ExceptionInfo data)
                ((contains m) (ex-data data))))
     :expect {:exception clojure.lang.ExceptionInfo :data m}})))
