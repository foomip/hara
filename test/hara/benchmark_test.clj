(ns hara.benchmark-test
  (:use hara.test)
  (:require [hara.benchmark :refer :all]))

(comment
  (defn sleep [{:keys [mean variation]
                :or {mean 1000
                     variation 300}}]
    (doto (-> mean
              (- (quot variation 2))
              (+ (rand-int variation)))
      (Thread/sleep)))
  
  (def bench
    (benchmark {:function sleep
                :args {:mean 1000
                       :variation 800}
                :settings {:duration 100000
                           ;;:count 1000
                           :spawn {:interval 10     
                                   :max 100}}}))
  
  (:accumulate bench)
  (start-benchmark bench)
  (stop-benchmark bench)
  (count-instances bench)
  (->> (history bench :last 5)
       (reduce (fn [acc [_ duration]]
                 (+ acc duration))
               0))
  (accumulate bench :count)
  (accumulate bench :average)
  (:average bench))
