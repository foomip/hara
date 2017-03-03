(ns hara.io.scheduler
  (:require [hara
             [time :as time]
             [component :as component]]
            [hara.concurrent
             [ova :as ova]
             [procedure :as procedure]]
            [hara.concurrent.procedure
             [data :as data]
             [registry :as registry]]
            [hara.data.nested :as nested]
            [hara.io.scheduler
             [array :as array]
             [clock :as clock]
             [tab :as tab]]))

(defonce ^:dynamic *defaults*
  {:clock    {:type     "java.util.Date"
              :now-fn   time/now
              :timezone (time/local-timezone)
              :interval 1
              :truncate :second}
   :registry {}
   :cache    {}
   :ticker   {}})

(defn scheduler
  "creates a schedular from handlers, or both handlers and config
 
   (def sch (scheduler
            {:print-task-1
              {:handler (fn [t] (Thread/sleep 2000))
               :schedule \"/5 * * * * * *\"}
              :print-task-2
              {:handler (fn [t] (Thread/sleep 2000))
               :schedule \"/2 * * * * * *\"}}))"
  {:added "2.2"}
  ([handlers] (scheduler handlers {}))
  ([handlers config] (scheduler handlers config {}))
  ([handlers config global]
   (component/system
    {:array     [{:constructor (array/seed-fn handlers)
                  :initialiser array/initialise} :cache :registry :ticker]
     :clock     [clock/clock :ticker]
     :ticker    [(fn [_] (atom {:time nil :array nil}))]
     :registry  [(fn [_] (data/registry))]
     :cache     [(fn [_] (data/cache))]}
    (-> global
        (update-in [:array] nested/merge-nested config)
        (nested/merge-nil-nested *defaults*))
    "scheduler")))

(defn start!
  "starts the scheduler
 
   (start! sch)
   ;; => {:ticker {:time #inst \"2016-10-25T01:20:06.000-00:00\",
   ;;              :array [6 20 8 2 25 10 2016]}
   ;;     :clock {:start-time #inst \"2016-10-25T01:18:52.184-00:00\",
   ;;             :current-time #inst \"2016-10-25T01:20:06.001-00:00\",
   ;;             :running true},
   ;;     :cache {},
   ;;     :registry {:print-task-2 (#inst \"2016-10-25T01:20:06.000-00:00\"),
   ;;                :print-task-1 (#inst \"2016-10-25T01:20:05.000-00:00\")},
   ;;     :array {:handlers [],
   ;;             :ticker {:time #inst \"2016-10-25T01:20:06.000-00:00\",
   ;;                      :array [6 20 8 2 25 10 2016]}
   ;;             :registry {:print-task-2 (#inst \"2016-10-25T01:20:06.000-00:00\"),
   ;;                        :print-task-1 (#inst \"2016-10-25T01:20:05.000-00:00\")},
   ;;             :cache {}}}
   "
  {:added "2.2"}
  [scheduler]
  (component/start scheduler))

(defn stop!
  "stops the scheduler
          
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
   ;;     :ticker {:time #inst \"2016-10-25T01:22:58.000-00:00\",
   ;;              :array [58 22 8 2 25 10 2016]}}
   "
  {:added "2.2"}
  [scheduler]
  (component/stop scheduler))

(defn stopped?
  "checks to see if the scheduler is stopped
 
   (stopped? sch)
   => true"
  {:added "2.2"}
  [scheduler]
  (component/stopped? (:clock scheduler)))

(defn running?
  "checks to see if the scheduler is running
 
   (running? sch)
   => false"
  {:added "2.2"}
  [scheduler]
  (component/started? (:clock scheduler)))

(defn simulate
  "simulates the scheduler running for a certain interval:
 
   (simulate
   (scheduler {:print-task {:handler (fn [t params instance]
                                        (str t params))
                             :schedule \"/2 * * * * * *\"
                             :params   {:value \"hello world\"}}})
    {:start (java.util.Date. 0)
     :end   (java.util.Date. 100000)
     :pause 10})"
  {:added "2.2"}
  [scheduler {:keys [start end step pause mode]}]
  (swap! (-> scheduler :clock :state) assoc :disabled true)
  (let [scheduler (component/start scheduler)
        clk  (-> scheduler :clock :meta)
        start-val (time/to-long start)
        end-val   (time/to-long end)
        step      (cond (nil? step) 1
                        (number? step) step
                        :else (long (/ (time/to-long step) 1000)))
        pause     (or pause 0)
        mode      (or mode :sync)
        timespan  (range start-val end-val (* 1000 step))]
    (doseq [t-val timespan]
      (let [t (time/from-long t-val clk)]
        (reset! (:ticker scheduler)
                {:time t :array (tab/to-time-array t) :instance {:mode mode}})
        (if-not (zero? pause)
          (Thread/sleep pause))))
    (swap! (-> scheduler :clock :state) dissoc :disabled)
    (component/stop scheduler)))

(defn uptime
  "checks to see how long the scheduler has been running
 
   (uptime sch) ;; when the scheduler is stopped, uptime is `nil`
   => nil
 
   (start! sch)
 
   (uptime sch) ;; uptime is from when the scheduler is started
   => 7936"
  {:added "2.2"}
  [scheduler]
  (if-let [start (-> scheduler :clock deref :start-time)]
    (-  (System/currentTimeMillis)
        (time/to-long start))))

(defn list-tasks
  "lists all tasks in the scheduler
          
   (list-tasks sch)
   ;; => [#proc{:name :print-task-1,
   ;;           :mode :async,
   ;;           :arglist [:timestamp :params :instance]}
   ;;     #proc{:name :print-task-2,
   ;;           :mode :async,
   ;;           :arglist [:timestamp :params :instance] }]
   "
  {:added "2.2"}
  [scheduler]
  (persistent! (-> scheduler :array :handlers)))

(defn get-task
  "gets a specific task in the scheduler
 
   (get-task sch :print-task-1)
   ;; => #proc{:name :print-task-1,
   ;;          :mode :async,
   ;;          :arglist [:timestamp :params :instance]}
   "
  {:added "2.2"}
  [scheduler name]
  (first (ova/select (-> scheduler :array :handlers) [:name name])))

(defn enable-task
  "enables a specific task in the scheduler
 
   (enable-task sch :print-task-1)
   ;; Task runs on schedule when `start!` is called
   "
  {:added "2.2"}
  [scheduler name]
  (dosync (ova/smap! (-> scheduler :array :handlers) [:name name]
                     dissoc :disabled)))

(defn disable-task
  "disables a specific task in the scheduler
 
   (disable-task sch :print-task-1)
   ;; Task is disabled when `start!` is called
   "
  {:added "2.2"}
  [scheduler name]
  (dosync (ova/smap! (-> scheduler :array :handlers) [:name name]
                     assoc :disabled true)))

(defn delete-task
  "deletes a specific task in the scheduler
 
   (delete-task sch :print-task-2)"
  {:added "2.2"}
  [scheduler name]
  (dosync (ova/remove! (-> scheduler :array :handlers) [:name name])))

(defn empty-tasks
  "clears all tasks in the scheduler
          
   (empty-tasks sch)"
  {:added "2.2"}
  [scheduler]
  (dosync (ova/empty! (-> scheduler :array :handlers))))

(defn add-task
  "add a task to the scheduler
   (add-task (scheduler {})
             :hello {:handler (fn [t params] (println params))
                    :schedule \"* * * * * * *\"
                     :params {:data \"foo\"}})"
  {:added "2.2"}
  [scheduler name props]
  (dosync (ova/append! (-> scheduler :array :handlers)
                       (array/build-handler name props {}))))

(defn reschedule-task
  "changes the schedule for an already existing task
   (-> (scheduler {:hello {:handler (fn [t params] (println params))
                                        :schedule \"* * * * * * *\"
                          :params {:data \"foo\"}}})
       (reschedule-task :hello \"/5 * * * * * *\"))"
  {:added "2.2"}
  [scheduler name schedule]
  (dosync (ova/smap! (-> scheduler :array :handlers) [:name name]
                     assoc
                     :schedule schedule
                     :schedule-array (tab/parse-tab schedule))))

(defn reparametise-task
  "changes the schedule for an already existing task
   (-> (scheduler {:hello {:handler (fn [t params] (println params))
                                          :schedule \"* * * * * * *\"
                          :params {:data \"foo\"}}})
       (reparametise-task :hello {:data \"bar\"}))"
  {:added "2.2"}
  [scheduler name opts]
  (dosync (ova/smap! (-> scheduler :array :handlers) [:name name]
                     update-in [:params] merge opts)))

(defn trigger!
  "manually executes a task, bypassing the scheduler
   
   (trigger! sch :print-task-1)"
  {:added "2.2"}
  ([scheduler name]
   (let [opts   (-> scheduler :clock :meta)]
     (trigger! scheduler name (time/now opts))))
  ([scheduler name key]
   (if-let [{:keys [params] :as task} (get-task scheduler name)]
     ((-> task
          (assoc :registry (:registry scheduler)))
      key params {}))))

(defn list-instances
  "lists all running instances of a tasks in the scheduler
          
   (list-instances sch)
   ;; lists all running instances in the scheduler
          
   (list-instances sch :print-task-1)
   ;; lists all running instances for a particular task
   "
  {:added "2.2"}
  ([scheduler]
   (for [tsk  (list-tasks scheduler)
         inst (list-instances scheduler (:name tsk))]
     inst))
  ([scheduler name]
   (-> scheduler
       :registry
       :store
       deref
       (get name)
       vals)))

(defn get-instance
  "gets an instance of the running task
          
   (get-instance sch :print-task-1 #inst \"2016-10-25T11:39:05.000-00:00\")
   ;; retrieves a running instances in the scheduler
   "
  {:added "2.2"}
  [scheduler name id]
  (-> scheduler
       :registry
       :store
       deref
       (get-in [name id])))

(defn kill-instance
  "kills a single instance of the running task
          
   (kill-instance sch :print-task-1 #inst \"2016-10-25T11:39:05.000-00:00\")
   ;; kills a running instances in the scheduler
   "
  {:added "2.2"}
  [scheduler name id]
  (-> (get-instance scheduler name id)
      (procedure/kill)))

(defn kill-instances
  "kills all instances of the running task
          
   (kill-instances sch)
   ;; kills all running instances in the scheduler
          
   (kill-instances sch :print-task-1)
   ;; kills all running instances for a particular task
  "
  {:added "2.2"}
  ([scheduler]
   (vec (for [inst (list-instances scheduler)]
          (procedure/kill inst))))
  ([scheduler name]
   (vec (for [inst (list-instances scheduler name)]
          (procedure/kill inst)))))

(defn shutdown!
  "forcibly shuts down the scheduler, immediately killing all running threads
 
   (shutdown! sch)
   ;; All tasks will stop and all running instances killed
   "
  {:added "2.2"}
  [scheduler]
  (kill-instances scheduler)
  (stop! scheduler))

(defn restart!
  "restarts the scheduler after a forced shutdown
 
   (restart! sch)
   ;; All Threads will stop and restart"
  {:added "2.2"}
  [scheduler]
  (shutdown! scheduler)
  (start! scheduler))
