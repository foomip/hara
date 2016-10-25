(ns hara.io.scheduler-test
  (:use hara.test)
  (:require [hara.io.scheduler :refer :all]
            [hara.component :as component]
            [hara.concurrent.procedure :as procedure]))

^{:refer hara.io.scheduler/scheduler :added "2.2"}
(fact "creates a schedular from handlers, or both handlers and config"

  (def sch (scheduler
            {:print-task-1
             {:handler (fn [t] (Thread/sleep 2000))
              :schedule "/5 * * * * * *"}
             :print-task-2
             {:handler (fn [t] (Thread/sleep 2000))
              :schedule "/2 * * * * * *"}})))

^{:refer hara.io.scheduler/start! :added "2.2"}
(comment "starts the scheduler"

  (start! sch)
  ;; => {:ticker {:time #inst "2016-10-25T01:20:06.000-00:00",
  ;;              :array [6 20 8 2 25 10 2016]}
  ;;     :clock {:start-time #inst "2016-10-25T01:18:52.184-00:00",
  ;;             :current-time #inst "2016-10-25T01:20:06.001-00:00",
  ;;             :running true},
  ;;     :cache {},
  ;;     :registry {:print-task-2 (#inst "2016-10-25T01:20:06.000-00:00"),
  ;;                :print-task-1 (#inst "2016-10-25T01:20:05.000-00:00")},
  ;;     :array {:handlers [],
  ;;             :ticker {:time #inst "2016-10-25T01:20:06.000-00:00",
  ;;                      :array [6 20 8 2 25 10 2016]}
  ;;             :registry {:print-task-2 (#inst "2016-10-25T01:20:06.000-00:00"),
  ;;                        :print-task-1 (#inst "2016-10-25T01:20:05.000-00:00")},
  ;;             :cache {}}}
  )

^{:refer hara.io.scheduler/stop! :added "2.2"}
(comment "stops the scheduler"
         
  (stop! sch)
  ;; Schedule will stop but running instances will continue to run until completion
  ;;
  ;; => {:array {:handlers
  ;;             [{:status :ready,
  ;;               :val {:name :print-task-1,
  ;;                     :mode :async,
  ;;                     :arglist [:timestamp :params :instance]}} 
  ;;              {:status :ready,
  ;;               :val {:name :print-task-2,
  ;;                     :mode :async,
  ;;                     :arglist [:timestamp :params :instance]}}]},
  ;;     :registry #reg {},
  ;;     :cache #cache {},
  ;;     :clock #clock {:start-time nil, :current-time nil, :running false},
  ;;     :ticker {:time #inst "2016-10-25T01:22:58.000-00:00",
  ;;              :array [58 22 8 2 25 10 2016]}}
  )

^{:refer hara.io.scheduler/stopped? :added "2.2"}
(fact "checks to see if the scheduler is stopped"

  (stopped? sch)
  => true)

^{:refer hara.io.scheduler/running? :added "2.2"}
(fact "checks to see if the scheduler is running"

  (running? sch)
  => false)

^{:refer hara.io.scheduler/simulate :added "2.2"}
(fact "simulates the scheduler running for a certain interval:"

  (simulate
   (scheduler {:print-task {:handler (fn [t params instance]
                                       (str t params))
                            :schedule "/2 * * * * * *"
                            :params   {:value "hello world"}}})
   {:start (java.util.Date. 0)
    :end   (java.util.Date. 100000)
    :pause 10}))

^{:refer hara.io.scheduler/uptime :added "2.2"}
(comment "checks to see how long the scheduler has been running"

  (uptime sch) ;; when the scheduler is stopped, uptime is `nil`
  => nil

  (start! sch)

  (uptime sch) ;; uptime is from when the scheduler is started
  => 7936)

^{:refer hara.io.scheduler/list-tasks :added "2.2"}
(comment "lists all tasks in the scheduler"
         
  (list-tasks sch)
  ;; => [#proc{:name :print-task-1,
  ;;           :mode :async,
  ;;           :arglist [:timestamp :params :instance]}
  ;;     #proc{:name :print-task-2,
  ;;           :mode :async,
  ;;           :arglist [:timestamp :params :instance] }]
  )

^{:refer hara.io.scheduler/get-task :added "2.2"}
(comment "gets a specific task in the scheduler"

  (get-task sch :print-task-1)
  ;; => #proc{:name :print-task-1,
  ;;          :mode :async,
  ;;          :arglist [:timestamp :params :instance]}
  )

^{:refer hara.io.scheduler/enable-task :added "2.2"}
(comment "enables a specific task in the scheduler"

  (enable-task sch :print-task-1)
  ;; Task runs on schedule when `start!` is called
  )

^{:refer hara.io.scheduler/disable-task :added "2.2"}
(comment "disables a specific task in the scheduler"

  (disable-task sch :print-task-1)
  ;; Task is disabled when `start!` is called
  )

^{:refer hara.io.scheduler/delete-task :added "2.2"}
(comment "deletes a specific task in the scheduler"

  (delete-task sch :print-task-2))

^{:refer hara.io.scheduler/empty-tasks :added "2.2"}
(comment "clears all tasks in the scheduler"
         
  (empty-tasks sch))

^{:refer hara.io.scheduler/add-task :added "2.2"}
(comment "add a task to the scheduler"
  (add-task (scheduler {})
            :hello {:handler (fn [t params] (println params))
                    :schedule "* * * * * * *"
                    :params {:data "foo"}}))

^{:refer hara.io.scheduler/reschedule-task :added "2.2"}
(comment "changes the schedule for an already existing task"
  (-> (scheduler {:hello {:handler (fn [t params] (println params))
                                       :schedule "* * * * * * *"
                          :params {:data "foo"}}})
      (reschedule-task :hello "/5 * * * * * *")))

^{:refer hara.io.scheduler/reparametise-task :added "2.2"}
(comment "changes the schedule for an already existing task"
  (-> (scheduler {:hello {:handler (fn [t params] (println params))
                                         :schedule "* * * * * * *"
                          :params {:data "foo"}}})
      (reparametise-task :hello {:data "bar"})))


^{:refer hara.io.scheduler/trigger! :added "2.2"}
(comment "manually executes a task, bypassing the scheduler"
  
  (trigger! sch :print-task-1))

^{:refer hara.io.scheduler/list-instances :added "2.2"}
(comment "lists all running instances of a tasks in the scheduler"
         
  (list-instances sch)
  ;; lists all running instances in the scheduler
         
  (list-instances sch :print-task-1)
  ;; lists all running instances for a particular task
  )

^{:refer hara.io.scheduler/get-instance :added "2.2"}
(comment "gets an instance of the running task"
         
  (get-instance sch :print-task-1 #inst "2016-10-25T11:39:05.000-00:00")
  ;; retrieves a running instances in the scheduler
  )

^{:refer hara.io.scheduler/kill-instance :added "2.2"}
(comment "kills a single instance of the running task"
         
  (kill-instance sch :print-task-1 #inst "2016-10-25T11:39:05.000-00:00")
  ;; kills a running instances in the scheduler
  )

^{:refer hara.io.scheduler/kill-instances :added "2.2"}
(comment "kills all instances of the running task"
         
  (kill-instances sch)
  ;; kills all running instances in the scheduler
         
  (kill-instances sch :print-task-1)
  ;; kills all running instances for a particular task
 )

^{:refer hara.io.scheduler/shutdown! :added "2.2"}
(comment "forcibly shuts down the scheduler, immediately killing all running threads"

  (shutdown! sch)
  ;; All tasks will stop and all running instances killed
  )

^{:refer hara.io.scheduler/restart! :added "2.2"}
(comment "restarts the scheduler after a forced shutdown"

  (restart! sch)
  ;; All Threads will stop and restart
)
