(ns hara.object.enum
  (:require [hara.protocol.string :as string]
            [hara.protocol.object :as object]
            [hara.reflect :as reflect]
            [hara.class.enum :as enum]))

(defmethod object/-meta-read Enum
  [_]
  {:to-string string/-to-string})

(defmethod object/-meta-write Enum
  [cls]
  {:from-string (fn [arg] (string/-from-string arg cls))})

(extend-type Enum
  string/IString
  (-to-string
    [enum]
    (str enum)))

(defmethod string/-from-string Enum
  [data ^Class type]
  (if-let [field (reflect/query-class type [data :#])]
    (field type)
    (throw (Exception. (str "Options for " (.getName type) " are: "
                            (mapv str (enum/enum-values type)))))))

(defmethod print-method Enum
  [v w]
  (.write w (format "#enum[%s %s]"
                    (.getName (class v))
                    (string/-to-string v))))
