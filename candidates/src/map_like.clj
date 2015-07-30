(ns hara.object.map-like
  (:require [hara.protocol.map :as map]
            [hara.protocol.data :as data]
            [hara.object.base :as base]
            [hara.object.util :as util]))

(defn generic-map [obj {:keys [exclude include getters] :as opts}]
  (let [fns (util/object-getters obj)
        fns (if include
              (select-keys fns include)
              fns)
        fns (apply dissoc fns :class exclude)
        fns (if getters
              (merge (eval getters) fns)
              fns)]
    (util/object-apply fns obj base/to-data)))

(defmacro extend-maplike-class [cls {:keys [tag to from meta setters getters] :as opts}]
  `(vector
    (defmethod data/-meta-object ~cls
      [type#]
      (hash-map :class     type#
                :types     #{java.util.Map}
                :to-data   map/-to-map
                ~@(if from [:from-data ~from] [])
                ~@(if getters [:getters ~getters] [])
                ~@(if setters [:setters ~getters] [])))

    (extend-protocol map/IMap
      ~cls
      ~(if to
         `(-to-map [entry#]
                   (~to entry#))
         `(-to-map [entry#]
                   (generic-map entry# ~opts)))

      ~(if meta
         `(-to-map-meta
           [entry#]
           (~meta entry#))
         `(-to-map-meta
           [entry#]
           {:class ~cls})))

    ~@(if from
        [`(defmethod map/-from-map ~cls
            [data# type#]
            (~from data# type#))])

    (defmethod print-method ~cls
      [v# ^java.io.Writer w#]
      (.write w# (str "#" ~(or tag cls) "" (map/-to-map v#))))))