(ns hara.string.prose)

(defn has-quotes?
  "checks if a string has quotes
 
   (has-quotes? \"\\\"hello\\\"\")
   => true"
  {:added "2.4"}
  [s]
  (and (.startsWith s "\"")
       (.endsWith s "\"")))

(defn strip-quotes
  "gets rid of quotes in a string
 
   (strip-quotes \"\\\"hello\\\"\")
   => \"hello\""
  {:added "2.4"}
  [s]
  (if (has-quotes? s) 
    (subs s 1 (dec (count s)))
    s))

(defn whitespace?
  [s]
  (or (= "" s) (re-find #"^[\s\t]+$" s)))

(defn escape-dollars
  "for regex purposes, escape dollar signs in templates"
  {:added "2.4"}
  [s]
  (.replaceAll s "\\$" "\\\\\\$"))

(defn escape-newlines
  "makes sure that newlines are printable
 
   (escape-newlines \"\\\n\")
   => \"\\n\""
  {:added "2.4"}
  [s]
  (-> s
      (.replaceAll "\\n" "\\\\n")))

(defn escape-escapes
  "makes sure that newlines are printable
 
   (escape-escapes \"\\\n\")
   => \"\\\n\""
  {:added "2.4"}
  [s]
  (-> s
      (.replaceAll "(\\\\)([A-Za-z])" "$1$1$2")))

(defn escape-quotes
  "makes sure that quotes are printable in string form
 
   (escape-quotes \"\\\"hello\\\"\")
   => \"\\\"hello\\\"\""
  {:added "2.4"}
  [s]
  (-> s
      (.replaceAll "(\\\\)?\"" "$1$1\\\\\\\"")))
