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

(defn print-fact [{:keys [name ns line desc refer] :as meta}  results]
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
            (if refer (str " - " refer))
            (if desc (str "\n    " (ansii/white "Info") "  \"" desc ""\") "")
            (str "\n  " (ansii/white "Passed") "  "
                 (str (ansii/style num (if (= num total) #{:blue} #{:green}))
                      " of "
                      (ansii/blue total)))
            (if (pos? thrown)
              (str "\n  " (ansii/white "Thrown") "  " (ansii/yellow thrown))
              ""))))))

(defn print-summary [{:keys [files thrown facts checks passed failed] :as result}]
  (let [npassed (count passed)
        nchecks (count checks)
        nfailed (count failed)
        nthrown (count thrown)]
    (println
     "\n"
     (str (ansii/style (str "Summary (" (count files) ")") #{:blue :bold})
          (str "\n  " (ansii/white " Files") "  " (ansii/blue (count files)))
          (str "\n  " (ansii/white " Facts") "  " (ansii/blue (count facts)))
          (str "\n  " (ansii/white "Checks") "  " (ansii/blue nchecks))
          (str "\n  " (ansii/white "Passed") "  " ((if (= npassed nchecks)
                                                     ansii/blue
                                                     ansii/yellow) npassed))
          (str "\n  " (ansii/white "Thrown") "  " ((if (pos? nthrown)
                                                     ansii/yellow
                                                     ansii/blue) nthrown))))
    
    (if (pos? nfailed)
      (println
       "\n"
       (ansii/style (str "Failed  (" nfailed ")") #{:red :bold}))

      (println
       "\n"
       (ansii/style (str "Success (" npassed ")") #{:cyan :bold})))))


