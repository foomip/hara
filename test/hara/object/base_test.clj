(ns hara.object.base-test
  (:use hara.test)
  (:require [hara.object.read :as read]
            [hara.object.write :as write]
            [hara.protocol.object :as object]
            [hara.reflect :as reflect])
  (:import [test PersonBuilder Person Dog DogBuilder Cat Pet]))

(defmethod object/-meta-write DogBuilder
  [_]
  {:empty (fn [_] (DogBuilder.))
   :methods (write/write-setters DogBuilder)})

(defmethod object/-meta-read DogBuilder
  [_]
  {:to-map (read/read-reflect-fields DogBuilder)})

(defmethod object/-meta-write Pet
  [_]
  {:from-map (fn [m] (case (:species m)
                       "dog" (write/from-map m Dog)
                       "cat" (write/from-map m Cat)))})

(defmethod object/-meta-read Pet
  [_]
  {:methods (read/read-getters Pet)})

(defmethod object/-meta-read Dog
  [_]
  {:methods (read/read-getters Dog)})

(defmethod object/-meta-write Dog
  [_]
  {:from-map (fn [m] (-> m
                         (write/from-map DogBuilder)
                         (.build)))})

(defmethod object/-meta-read Cat
  [_]
  {:methods (read/read-getters Cat)})

(defmethod object/-meta-write Cat
  [_]
  {:from-map (fn [m] (Cat. (:name m)))})

(defmethod object/-meta-write PersonBuilder
  [_]
  {:empty (fn [_] (PersonBuilder.))
   :methods (write/write-setters PersonBuilder)})

(defmethod object/-meta-read PersonBuilder
  [_]
  {:methods (read/read-reflect-fields PersonBuilder)})

(defmethod object/-meta-write Person
  [_]
  {:from-map (fn [m] (-> m
                         (write/from-map PersonBuilder)
                         (.build)))})

(defmethod object/-meta-read Person
  [_]
  {:methods (read/read-getters Person)})
