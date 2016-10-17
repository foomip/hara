(ns hara.concurrent.procedure-test
  (:use hara.test)
  (:require [hara.concurrent.procedure :refer :all]
            [hara.concurrent.procedure.registry :as registry]
            [hara.event :refer :all]
            [hara.common.state :as state]))

^{:refer hara.concurrent.procedure/max-inputs :added "2.2"}
(fact "finds the maximum number of inputs that a function can take"

  (max-inputs (fn ([a]) ([a b])) 4)
  => 2

  (max-inputs (fn [& more]) 4)
  => 4

  (max-inputs (fn ([a])) 0)
  => throws)

^{:refer hara.concurrent.procedure/wrap-exception :added "2.2"}
(fact "creates a handler for retrying computation")

^{:refer hara.concurrent.procedure/kill :added "2.2"}
(fact "kills the procedure if running")

^{:refer hara.concurrent.procedure/invoke-base :added "2.2"}
(fact "constructs a standard procedure"

  (def proc {:handler +
             :result (promise)})
  
  (invoke-base proc [1 2 3])

  @(:result proc)
  => {:type :success, :data 6})

^{:refer hara.concurrent.procedure/invoke-procedure :added "2.2"}
(fact "the full invocation for the procedure, incorporating middleware and retry")

^{:refer hara.concurrent.procedure/procedure :added "2.2"}
(fact "creates a procedure for computation"

  @((procedure {:name "ID"
                :handler (fn [id params instance]
                           ; (println (-> instance :retry :count))
                           (if (= 5 (-> instance :retry :count))
                             (-> instance :retry :count)
                             (throw (Exception.))))
                :retry {:handle [{:on #{Exception}
                                  :apply   (fn [state e])
                                  :limit   (fn [state count])
                                  :wait    (fn [state count])}]
                        :count 0
                        :state  {:a 1 :b 2}
                        :limit 10
                        :wait  100}}
              [:id :params :instance])
    "ID" {} {:mode :async :cached false})
  => 5)

^{:refer hara.concurrent.procedure/defprocedure :added "2.2"}
(comment "defining a procedure"

  (defprocedure hello {:mode :sync}
    []
    (Thread/sleep 1000)
    :DONE)

  (defprocedure print-hello
    {:id-fn :timestamp
     :arglist [:timestamp :params :instance]
     :params {:b 2}}
    [t params instance]
    (println "INSTANCE: " instance)
    (Thread/sleep 500)
    (println "ENDED" t)))

^{:refer hara.concurrent.procedure/kill :added "2.2"}
(fact "kills a running procedure"
  (def proc ((procedure {:name "hello"
                         :id :1
                         :handler (fn [] (Thread/sleep 1000000000))} [])))

  (Thread/sleep 100)
  (kill proc)
  => true)
