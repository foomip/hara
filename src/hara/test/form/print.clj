(ns hara.test.form.print
  (:require [hara.display.ansii :as ansii]
            [hara.test.checker.base :as checker]
            [hara.test.common :as common]
            [clojure.string :as string]))

(defn print-success [{:keys [name ns line desc form check] :as summary}]
  (let [name (if name (str name " @ ") "")
        file (-> (string/split (str ns) #"\.") last munge (str ".clj"))
        line (if line (str ":" line) "")]
    (println
     "\n"
     (str (ansii/style "Success" #{:green :bold}) "  " name "[" file  line "]"
          (if desc (str "\n    " (ansii/white "Info") "  \"" desc ""\") "")
          (str "\n    " (ansii/white "Form") "  " form)
          (str "\n   " (ansii/white "Check") "  " check)))))

(defn print-failure [{:keys [name ns line desc form check actual] :as summary}]
  (let [name (if name (str name " @ ") "")
        file (-> (string/split (str ns) #"\.") last munge (str ".clj"))
        line (if line (str ":" line) "")]
    (println
     "\n"
     (str (ansii/style "Failure" #{:red :bold}) "  " name "[" file  line "]"
          (if desc (str "\n    " (ansii/white "Info") "  \"" desc ""\") "")
          (str "\n    " (ansii/white "Form") "  " form)
          (str "\n   " (ansii/white "Check") "  " check)
          (str "\n  " (ansii/white "Actual") "  " actual)))))

(defn print-thrown [{:keys [name ns line desc form] :as summary}]
  (let [name (if name (str name " @ ") "")
        file (-> (string/split (str ns) #"\.") last munge (str ".clj"))
        line (if line (str ":" line) "")]
    (println
     "\n"
     (str (ansii/style " Thrown" #{:yellow :bold}) "  " name "[" file  line "]"
          (if desc (str "\n    " (ansii/white "Info") "  \"" desc ""\") "")
          (str "\n    " (ansii/white "Form") "  " form)))))

(defn print-fact [{:keys [name ns line desc] :as meta}  results]
  (let [name   (if name (str name " @ ") "")
        file   (-> (string/split (str ns) #"\.") last munge (str ".clj"))
        line   (if line (str ":" line) "")
        all    (->> results (filter #(-> % :from (= :verify))))
        passed (->> all (filter checker/succeeded?))
        num    (count passed)
        total  (count all)
        ops    (->> results (filter #(-> % :from (= :evaluate))))
        errors (->> ops (filter #(-> % :type (= :exception))))
        thrown (count errors)]
    (if (or (common/*print* :print-facts-success)
            (not (and (= num total)
                      (pos? thrown))))
      (println
       "\n"
       (str (ansii/style "   Fact" #{:blue :bold}) "  " name "[" file  line "]"
            (if desc (str "\n    " (ansii/white "Info") "  \"" desc ""\") "")
            (str "\n  " (ansii/white "Passed") "  "
                 (str (ansii/style num (if (= num total) #{:blue} #{:green}))
                      " of "
                      (ansii/blue total)))
            (if (pos? thrown)
              (str "\n  " (ansii/white "Thrown") "  " (ansii/yellow thrown))
              ""))))))


(comment
  (print-failure {:name 'hara.test.form/arrows :ns 'hara.test.form-test :line 10
                  :desc "Hello There"
                  :form 1
                  :actual 2
                  :check 1
                  })

  (print-thrown {:name 'hara.test.form/arrows :ns 'hara.test.form-test :line 10
                 :desc "Hello There"
                 :form 1
                 :actual 2
                 :check 1
                 })

  (print-success {:ns 'hara.test.form-test :line 10}))


