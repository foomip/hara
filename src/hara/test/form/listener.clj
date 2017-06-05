(ns hara.test.form.listener
  (:require [hara.test.common :as common]
            [hara.test.form.print :as print]
            [hara.event :as event]))

(defn summarise-verify
  ""
  [result]
  {:type    (if (and (= :success(-> result :type))
                     (= true (-> result :data)))
              :success
              :failed) 
   :name    (-> result :meta :refer)
   :ns      (-> result :meta :ns)
   :line    (-> result :meta :line)
   :desc    (-> result :meta :desc)
   :form    (-> result :actual :form)
   :check   (-> result :checker :form)
   :actual  (-> result :actual :data)})

(defn summarise-evaluate
  ""
  [result]
  {:type    (-> result :type) 
   :name    (-> result :meta :refer)
   :ns      (-> result :meta :ns)
   :line    (-> result :meta :line)
   :desc    (-> result :meta :desc)
   :form    (-> result :form)
   :actual  (-> result :data)})

(event/deflistener form-printer {:test :form}
  [result]
  (if (and (-> result :type (= :exception))
           (common/*print* :print-thrown))
    (print/print-thrown (summarise-evaluate result))))

(event/deflistener check-printer {:test :check}
  [result]
  (if (or (and (-> result :type (= :exception))
               (common/*print* :print-thrown))
          (and (-> result :data (= false))
               (common/*print* :print-failure)))
    (do (.beep (java.awt.Toolkit/getDefaultToolkit)) 
        (print/print-failure (summarise-verify result))))
  (if (and (-> result :data (= true))
           (common/*print* :print-success))
    (print/print-success (summarise-verify result))))

(event/deflistener fact-printer {:test :fact}
  [meta results skipped]
  (if (and (common/*print* :print-facts)
           (not skipped))
    (print/print-fact meta results)))

(event/deflistener fact-accumulator {:test :fact}
  [id meta results]
  (reset! common/*accumulator* {:id id :meta meta :results results}))

(event/deflistener bulk-printer {:test :bulk}
  [results]
  (if (common/*print* :print-bulk)
    (print/print-summary results)))
