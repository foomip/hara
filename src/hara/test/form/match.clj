(ns hara.test.form.match
  (:require [clojure.set :as set]))

(defn match-base [fmeta {:keys [tags refers namespaces] :as filter} default]
  [(if-not (empty? tags)
      (->> (:tags fmeta)
           (set/intersection tags)
           (empty?)
           (not))
      default)
   (if-not (empty? refers)
     (->> (:refer fmeta)
          (get (set refers))
          (nil?)
          (not))
     default)
   (if-not (empty? namespaces)
     (or (->> namespaces
              (map (fn [namespace]
                     (.startsWith (str (:ns fmeta))
                                  (str namespace))))
              (some true?))
         false)
     default)])

(defn match-include [fmeta filter]
  (not (some false? (match-base fmeta filter true))))

(defn match-exclude [fmeta filter]
  (or (some true? (match-base fmeta filter false))
      false))

(defn match-options [fmeta {:keys [includes excludes]}]
  (and (->> includes
            (map #(match-include fmeta %))
            (every? true?))
       (->> excludes
            (map #(match-exclude fmeta %))
            (every? false?))))
