(ns hara.zip
  (:require [hara.zip.base :as base]
            [hara.event :as event]))

(defn seq-zip
  [root]
  (base/zipper root {:branch?   seq?
                     :children  identity
                     :make-node identity}))

(defn vector-zip
  [root]
  (base/zipper root {:branch? vector?
                     :children seq
                     :make-node vec}))

(defn prewalk
  [f zip]
  (let [zip (base/replace-right zip (f (base/right-node zip)))]
    (cond (base/move-down? zip)
          (loop [zip (base/move-down zip)]
            (let [zip  (prewalk f zip)
                  rzip (base/move-right zip)]
              (cond (base/move-right? rzip)
                    (recur rzip)
                    
                    :else
                    (base/move-up zip))))

          :else zip)))

(defn postwalk
  [f zip]
  (let [zip (cond (base/move-down? zip)
                  (loop [zip (base/move-down zip)]
                    (let [zip (postwalk f zip)
                          rzip (base/move-right zip)]
                      (cond (base/move-right? rzip)
                            (recur rzip)

                            :else
                            (base/move-up zip))))
                  
                  :else zip)]
    (base/replace-right zip (f (base/right-node zip)))))
