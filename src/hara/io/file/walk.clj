(ns hara.io.file.walk
  (:require [hara.common.primitives :refer [F T]]
            [hara.io.file
             [filter :as filter]
             [option :as option]
             [path :as path]])
  (:import (java.nio.file Files FileVisitor)))

(defn match-single
  "matches according to the defined filter
 
   (match-single {:root (path/path \".\")
                  :path (path/path \"src/hara/test.clj\")}
                 {:tag :pattern
                  :pattern #\"src\"})
   => true
   
   (match-single {:root (path/path \"src\")
                  :path (path/path \"src/hara/test.clj\")}
                 {:tag :pattern
                  :pattern #\"src\"})
   => false
 
   (match-single {:path (path/path \"src/hara/test.clj\")}
                 {:tag :fn
                  :fn (fn [m]
                        (re-find #\"hara\" (str (:path m))))})
   => true"
  {:added "2.4"}
  [{:keys [root path attrs] :as m} {:keys [tag] :as single}]
  (boolean (case tag
             :fn      (let [f (:fn single)]
                        (f m))
             :pattern (let [pat (:pattern single)]
                        (->> (str root)
                             (count)
                             (inc)
                             (subs (str path))
                             (re-find pat)))
             :mode    (do (:mode single)
                          (throw (Exception. "TODO"))))))

(defn match-filter
  "matches according to many filters
 
   (match-filter {})
   => true
 
   (match-filter {:root (path/path \"\")
                  :path (path/path \"src/hara/test.clj\")
                  :include [{:tag :pattern
                             :pattern #\"test\"}]})
   => true
 
   (match-filter {:root (path/path \"\")
                  :path (path/path \"src/hara/test.clj\")
                  :exclude [{:tag :pattern
                             :pattern #\"test\"}]})
   => false"
  {:added "2.4"}
  [{:keys [path attrs root include exclude] :as m}]
  (or (= (str root) (str path))
      (let [include (if (empty? include)
                      [{:tag :fn :fn T}]
                      include)
            exclude (if (empty? exclude)
                      [{:tag :fn :fn F}]
                      exclude)]
        (and (some #(match-single m %) include)
             (not (some #(match-single m %) exclude))))))

(defn visit-directory-pre
  "helper function, triggers before visiting a directory"
  {:added "2.4"}
  [{:keys [path attrs accumulate] :as m}]
  (let [f      (-> m :directory :pre)
        run?   (match-filter m)
        result (try
                 (when run?
                   (if (accumulate :directories)
                     (swap! (:accumulator m) conj path))
                   (if f (f m)))
                 :continue
                 (catch clojure.lang.ExceptionInfo e
                   (or (:command (ex-data e))
                       (throw e))))]
    (option/lookup result)))

(defn visit-directory-post
  "helper function, triggers after visiting a directory"
  {:added "2.4"}
  [m]
  (option/lookup
   (or (if-let [f (get-in m [:directory :post])]
         (f m))
       :continue)))

(defn visit-file
  "helper function, triggers on visiting a file"
  {:added "2.4"}
  [{:keys [path attrs accumulate] :as m}]
  (let [f      (:file m)
        run?   (match-filter m)
        result (try
                 (when run?
                   (if (accumulate :files)
                     (swap! (:accumulator m) conj path))
                   (if f (f m)))
                 :continue
                 (catch clojure.lang.ExceptionInfo e
                   (or (:command (ex-data e))
                       (throw e))))]
    (option/lookup result)))

(defn visit-file-failed
  "helper function, triggers on after a file cannot be visited"
  {:added "2.4"}
  [m]
  (option/lookup
   (or (if-let [f (-> m :failed)]
         (f m))
       :continue)))

(defn visitor
  "contructs the clojure wrapper for `java.nio.file.FileVisitor`"
  {:added "2.4"}
  [m]
  (reify FileVisitor
    (preVisitDirectory  [_ path attrs]
      (visit-directory-pre  (assoc m :path path :attrs attrs)))
    (postVisitDirectory [_ path error]
      (visit-directory-post (assoc m :path path :error error)))
    (visitFile          [_ path attrs]
      (visit-file           (assoc m :path path :attrs attrs)))
    (visitFileFailed    [_ path error]
      (visit-file-failed    (assoc m :path path :error error)))))

(defn walk
  "visits files based on a directory
   
   (walk \"src\" {:accumulate #{:directories}})
   => vector?
 
   (walk \"src\" {:accumulator (atom {})
                :accumulate  #{}
                :file (fn [{:keys [path attrs accumulator]}]
                        (swap! accumulator
                               assoc
                               (str path)
                               (.toMillis (.lastAccessTime attrs))))})
   => map?"
  {:added "2.4"}
  [root {:keys [directory file include 
                exclude recursive depth options 
				accumulator accumulate] 
		 :as m}]
  (let [directory (cond (nil? directory)
                        {:pre F}

                        (fn? directory)
                        {:pre directory}

                        :else directory)
        file      (cond (nil? file) F

                        :else directory)
        options   (->> (seq options) (map option/lookup) (set))
        depth     (or (if (false? recursive) 1)
                      depth
                      Integer/MAX_VALUE)
        root        (path/path root)
        accumulate  (or accumulate #{:files})
        accumulator (or accumulator (atom []))
        include   (map filter/characterise-filter include)
        exclude   (map filter/characterise-filter exclude)
        state     (merge m {:root root
                            :directory directory
                            :depth depth  
                            :include include
                            :exclude exclude
                            :options options
                            :accumulate accumulate
                            :accumulator accumulator})
        visitor    (visitor state)]
    (Files/walkFileTree (:root state)
                        (:options state)
                        (:depth state)
                        visitor)
    @accumulator))
