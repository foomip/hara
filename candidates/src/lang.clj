(ns hara.lang)

(def encodings
  {:sanskrit ["0900" "094F"]})

(comment
  ("\u0910\u0900"
   (seq (.getBytes "ऐऀ"))
   (-32 -92 -112 -32 -92 -128))

  (seq (.getBytes "\u0910"))
  (-32 -92 -112)

  (seq (.getBytes "ऐ"))
  (-32 -92 -112)

  (seq (.toCharArray "ऐ"))
  (\ऐ)

  (.charValue \u0910)
  "\u0915"
  "क"

  "\u0916"
  "ख"
  
  )

(unicode "0916")
