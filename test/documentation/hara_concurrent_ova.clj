(ns documentation.hara-concurrent-ova
  (:use hara.test)
  (:require [hara.concurrent.ova :refer :all]
            [hara.common.watch :as watch]))

[[:chapter {:title "Introduction"}]]

"An `ova` represents a mutable array of elements. It has been designed especially for dealing with shared mutable state in multi-threaded applications. Clojure uses `refs` and `atoms` off the shelf to resolve this issue but left out methods to deal with arrays of shared elements. `ova` has been specifically designed for the following use case:

- Elements (usually clojure maps) can be added or removed from an array
- Element data are accessible and mutated from several threads.
- Array itself can also be mutated from several threads."

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:

    [zcaudate/hara.concurrent.ova \"{{PROJECT.version}}\"]"

"All functions are in the `hara.concurrent.ova` namespace."

(comment (use 'hara.concurrent.ova))

[[:section {:title "Motivation"}]]

"Coordination in multi-threaded applications have always been a pain. Most times situations are usally coordinated using a external store like a database or a cache. `hara.concurrent.ova` provides an easy to use interface for array data. The actual `ova` datastructure is a `ref` containing a `vector` containing ref and so it has a small footprint and is small."

[[:chapter {:title "Index"}]]

[[:api {:title ""
        :namespace "hara.concurrent.ova"
        :exclude ["delete-indices" "get-filtered"]
        :display #{:tags}}]]

[[:chapter {:title "API"}]]

[[:section {:title "Data"}]]

"Methods for setting up an ova and accessing it's data"

[[:api {:namespace "hara.concurrent.ova"
        :title ""
        :only ["ova" "ova?" "clone" "init!" "<<" "has?" "select" "selectv" "indices" "-invoke"]}]]

[[:section {:title "Manipulation"}]]

"Methods for changing the data within an ova"

[[:api {:namespace "hara.concurrent.ova"
        :title ""
        :only ["!!"
               "!>"
               "append!"
               "concat!"
               "empty!"
               "filter!"
               "insert!"
               "map!"
               "map-indexed!"
               "remove!"
               "reverse!"
               "smap!"
               "smap-indexed!"
               "sort!"
               "split"]}]]


[[:section {:title "Watches"}]]

"Because a ova is simply a ref, it can be watched for changes"

(fact
  (def ov (ova [0 1 2 3 4 5]))

  (def output (atom []))
  (add-watch ov
             :old-new
             (fn [ov k p n]
               (swap! output conj [(mapv deref p)
                                   (mapv deref n)])))

  (do (dosync (sort! ov >))
      (deref output))
  => [[[0 1 2 3 4 5]
       [5 4 3 2 1 0]]])

[[:subsection {:title "Element Watch"}]]

"Entire elements of the ova can be watched. A more substantial example can be seen in the [scoreboard example](#scoreboard-example):"


(fact
  (def ov (ova [0 1 2 3 4 5]))

  (def output (atom []))

  (watch/add      ;; key, ova, ref, previous, next
      ov :elem-old-new
      (fn [k o r p n]
        (swap! output conj [p n])))

  (<< (!! ov 0 :zero))
  => [:zero 1 2 3 4 5]

  (deref output)
  => [[0 :zero]]

  (<< (!! ov 3 :three))
  => [:zero 1 2 :three 4 5]

  (deref output)
  => [[0 :zero] [3 :three]])

[[:subsection {:title "Element Change Watch"}]]

"The `add-elem-change-watch` function can be used to only notify when an element has changed."


(fact
  (def ov (ova [0 1 2 3 4 5]))

  (def output (atom []))

  (watch/add   ;; key, ova, ref, previous, next
     ov :elem-old-new
     (fn [k o r p n]
       (swap! output conj [p n]))
     {:select identity
      :diff true})

  (do (<< (!! ov 0 :zero))  ;; a pair is added to output
      (deref output))
  => [[0 :zero]]

  (do (<< (!! ov 0 0))      ;; another pair is added to output
      (deref output))
  => [[0 :zero] [:zero 0]]

  (do (<< (!! ov 0 0))      ;; no change to output
      (deref output))
  => [[0 :zero] [:zero 0]])

[[:section {:title "Clojure Protocols"}]]
"`ova` implements the sequence protocol so it is compatible with all the bread and butter methods."


(fact
  (def ov (ova (map (fn [n] {:val n})
                    (range 8))))

  (seq ov)
  => '({:val 0} {:val 1} {:val 2}
       {:val 3} {:val 4} {:val 5}
       {:val 6} {:val 7})

  (map #(update-in % [:val] inc) ov)
  => '({:val 1} {:val 2} {:val 3}
       {:val 4} {:val 5} {:val 6}
       {:val 7} {:val 8})

  (last ov)
  => {:val 7}

  (count ov)
  => 8

  (get ov 0)
  => {:val 0}

  (nth ov 3)
  => {:val 3}

  (ov 0)
  => {:val 0}

  (ov [:val] #{1 2 3}) ;; Gets the first that matches
  => {:val 1})


[[:chapter {:title "Selection"}]]

[[:file {:src "test/documentation/hara_concurrent_ova/selection.clj"}]]

[[:chapter {:title "Scoreboard"}]]

[[:file {:src "test/documentation/hara_concurrent_ova/scoreboard.clj"}]]

