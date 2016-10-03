(ns hara.io.file.reader
  (:require [hara.io.file
             [path :as path]])
  (:import (java.io File Reader InputStream PipedWriter
                    CharArrayReader BufferedReader FileReader 
                    InputStreamReader LineNumberReader
                    PipedReader PushbackReader StringReader)
           (java.nio.file Files)
           (java.nio.charset Charset)))

(defn charset-default
  ""
  []
  (str (Charset/defaultCharset)))

(defn charset-list
  ""
  []
  (keys (Charset/availableCharsets)))

(defn charset
  ""
  [s]
  (Charset/forName s))

(defmulti reader
  ""
  (fn [type path opts] type))

(defmethod reader :buffered
  [_ io opts]
  (Files/newBufferedReader (path/path io)
                           (charset (or (:charset opts)
                                        (charset-default)))))

(defmethod reader :char-array
  [_ io opts]
  (let [arr    io
        offset (or (:offset opts) 0)
        length (or (:length opts) (count arr))]
    (CharArrayReader. arr offset length)))

(defmethod reader :file
  [_ io opts]
  (let [path (path/path io)]
    (FileReader. (str path))))

(defmethod reader :input-stream
  [_ io opts]
  (let [stream  io
        charset (or (:charset opts) (charset-default))]
    (InputStreamReader. ^InputStream stream  ^String charset)))

(defmethod reader :line-number
  [_ io opts]
  (LineNumberReader. (reader :buffered io opts)))

(defmethod reader :piped
  [_ io opts]
  (PipedReader. ^PipedWriter io))

(defmethod reader :pushback
  [_ io opts]
  (PushbackReader. (reader :buffered io opts)))

(defmethod reader :string
  [_ io opts]
  (StringReader. ^String io))

(defn reader-types
  ""
  []
  (keys (.getMethodTable reader)))
