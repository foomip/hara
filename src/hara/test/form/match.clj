(ns hara.test.form.match
  (:require [clojure.set :as set]))

(defn match-base
  "determines whether a term matches with a filter
   (match-base {:tags #{:web}}
               {:tags #{:web}}
               false)
   => [true false false]
   (match-base {:refer 'user/foo
                :namespace 'user}
               {:refers '[user/other]
                :namespaces '[foo bar]}
               true)
   => [true false false]"
  {:added "2.4"}
  [fmeta {:keys [tags refers namespaces] :as filter} default]
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

(defn match-include
  "determines whether inclusion is a match
   (match-include {:tags #{:web}}
                  {:tags #{:web}})
   => true
   
   (match-include {:refer 'user/foo
                   :namespace 'user}
                  {})
   => true"
  {:added "2.4"}
  [fmeta filter]
  (not (some false? (match-base fmeta filter true))))

(defn match-exclude
  "determines whether exclusion is a match
   (match-exclude {:tags #{:web}}
                  {:tags #{:web}})
   => true
   (match-exclude {:refer 'user/foo
                   :namespace 'user}
                  {})
   => false"
  {:added "2.4"}
  [fmeta filter]
  (or (some true? (match-base fmeta filter false))
      false))

(defn match-options
  "determines whether a set of options can match
   (match-options {:tags #{:web}
                   :refer 'user/foo}
                  {:includes [{:tages #{:web}}]
                   :excludes []})
   => true
 
   (match-options {:tags #{:web}
                   :refer 'user/foo}
                  {:includes [{:tages #{:web}}]
                   :excludes [{:refers '[user/foo]}]})
   => false"
  {:added "2.4"}
  [fmeta {:keys [includes excludes]}]
  (and (->> includes
            (map #(match-include fmeta %))
            (every? true?))
       (->> excludes
            (map #(match-exclude fmeta %))
            (every? false?))))
