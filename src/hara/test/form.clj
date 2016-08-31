(ns hara.test.form
  (:require [hara.test.common :as common]
            [hara.test.form.run :as run]
            [hara.test.form.match :as match]
            [clojure.set :as set]))

(declare =>) 

(def arrows '{=> :test-equal})

(defn split
  ([body] (split body []))
  ([[x y z & more :as arr] out]
   (cond (empty? arr)
         out
         
         (get arrows y)
         (recur more
                (conj out {:type (get arrows y)
                           :meta (merge common/*meta*
                                        (or (meta x) (meta y) (meta z)))
                           :input x
                           :output z}))

         :else
         (recur (rest arr)
                (conj out {:type :form
                           :meta (merge common/*meta* (meta x))
                           :form x})))))

(defmacro fact
  ([& [desc? & body]]
   (let [[desc body] (if (string? desc?)
                       [desc? body]
                       [nil (cons desc? body)])
         fmeta  (assoc (meta &form) :path common/*path* :desc desc :ns (.getName *ns*))
         body   (binding [common/*meta* fmeta] (split body))]
     `(binding [common/*meta* ~(list `quote fmeta)]
        (if (match/match-options common/*meta* common/*settings*)
          (->> (mapv run/process ~(list `quote body))
               (run/collect common/*meta*))
          (run/skip common/*meta*))))))

(defmacro facts [& more]
  `(fact ~@more))
