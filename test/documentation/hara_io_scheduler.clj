(ns documentation.hara-io-scheduler
  (:use hara.test)
  (:require [hara.io.scheduler :refer :all]
            [hara.time :as time]
            [hara.concurrent.procedure.registry :as registry]))

[[:chapter {:title "Introduction"}]]

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:

    [zcaudate/hara.io.scheduler \"{{PROJECT.version}}\"]"

"All functions are in the `hara.io.scheduler` namespace."

(comment (use 'hara.io.scheduler))

[[:section {:title "Motivation"}]]

"`hara.io.scheduler` provides an easy and intuitive interface for task scheduling. Much emphasis has been placed on task management, including the ability to inspect and kill active tasks. Furthermore, testing time is reduced as the library includes a simulation framework to enable speed-up of the original schedule."

[[:section {:title "Other Libraries"}]]

"`hara.io.scheduler` is just one of many scheduling libraries in the clojure world including:

- [at-at](https://github.com/overtone/at-at)
- [chime](https://github.com/james-henderson/chime)
- [clj-cronlike](https://github.com/kognate/clj-cronlike)
- [cron4j](http://www.sauronsoftware.it/projects/cron4j)
- [monotony](https://github.com/aredington/monotony)
- [quartzite](https://github.com/michaelklishin/quartzite)
- [schejulure](https://github.com/AdamClements/schejulure)"

[[:chapter {:title "Index"}]]

[[:api {:namespace "hara.io.scheduler"
        :title ""
        :display #{:tags}}]]

[[:chapter {:title "API"}]]

[[:section {:title "Scheduler"}]]

[[:api {:namespace "hara.io.scheduler"
        :title ""
        :only ["scheduler"
               "start!"
               "stop!"
               "shutdown!"
               "running?"
               "stopped?"
               "uptime"]}]]

[[:section {:title "Tasks"}]]

[[:api {:namespace "hara.io.scheduler"
        :title ""
        :only ["add-task" 
               "delete-task" 
               "disable-task"
               "empty-tasks"
               "enable-task"
               "get-task"
               "list-tasks"
               "reparametise-task"
               "reschedule-task"]}]]
               
[[:section {:title "Instances"}]]

[[:api {:namespace "hara.io.scheduler"
        :title ""
        :only ["get-instance"
               "list-instances"
               "kill-instance"
               "kill-instances"
               "trigger!"]}]]
               
[[:section {:title "Simulation"}]]

[[:api {:namespace "hara.io.scheduler"
        :title ""
        :only ["simulate"]}]]

[[:chapter {:title "Walkthrough"}]]

"`hara.io.scheduler` has been built around a concept of a tasks and schedulers. We start off by defining basic task:"

(def print-task
  {:handler (fn [t] (println "TIME:" t))
   :schedule "/2 * * * * * *"})

"The scheduler is defined as follows"

(def print-scheduler
  (scheduler {:print-task print-task}))

"Calling `start!` on the scheduler results in the task triggering every two seconds:"

(comment
  (start! print-scheduler)

  ;; TIME: #inst "2016-10-24T04:00:00.000-00:00"
  ;; TIME: #inst "2016-10-24T04:00:02.000-00:00"
  ;; TIME: #inst "2016-10-24T04:00:04.000-00:00"
  ;; TIME: #inst "2016-10-24T04:00:06.000-00:00"
  ;; TIME: #inst "2016-10-24T04:00:08.000-00:00"
  ;; TIME: #inst "2016-10-24T04:00:10.000-00:00"
)

"Calling `stop!` will stop the scheduler from running:"

(comment
  (stop! print-scheduler)
  
  ;; TIME: #inst "2016-10-24T04:00:12.000-00:00"
  ;; <OUTPUT STOPS>
  )

[[:section {:title "Schedule"}]]

"Each task has a `:schedule` entry. The value is a string specifying when it is supposed to run. The string is of the same format as `crontab` -  seven elements seperated by spaces. The elements are used to match the time, expressed as seven numbers:

     second minute hour day-of-week day-of-month month year

The rules for a match between the crontab and the current time are:

- `A`       means match on `A`
- `*`       means match on any number
- `E1,E2`   means match on both `E1` and `E2`
- `A-B`     means match on any number between `A` and `B` inclusive
- `/N`      means match on any number divisible by `N`
- `A-B/N`   means match on any number divisible by `N` between `A` and `B` inclusive

Where `A`, `B` and `N` are numbers; `E1` and `E2` are expressions. All seven elements in the string have to match in order for the task to be triggered.
"

(comment

  ;; Triggered every 5 seconds

  "/5 * * * * * *"


  ;; Triggered every 5 seconds between 32 and 60 seconds

  "32-60/5 * * * * * *"

  ;; Triggered every 5 seconds on the 9th aand 10th
  ;; minute of every hour on every Friday from June
  ;; to August between years 2012 to 2020.

  "/5  9,10  * 5 * 6-8 2012-2020")

[[:section {:title "Simulation"}]]

"Simulations are a great way to check if the system is working correctly. This allows an entire system to be tested for correctness. How `simulate` works is that it decouples the `clock` from the task array and forces tasks to trigger on the range of date inputs provided."

(def T1 #inst "1999-12-31T23:59:50.00-00:00")
(def T2 #inst "2000-01-01T00:00:10.00-00:00")

"The simulation is then run from `T1` to `T2` and the results are shown instantaneously"

(def sch1 (scheduler {:print-task print-task}))

(simulate sch1
          {:start T1
           :end   T2})
;; > Hello There : #inst "1999-12-31T23:59:50.000-00:00"
;; > Hello There : #inst "1999-12-31T23:59:52.000-00:00"
;; > Hello There : #inst "1999-12-31T23:59:54.000-00:00"
;; > Hello There : #inst "1999-12-31T23:59:56.000-00:00"
;; > Hello There : #inst "1999-12-31T23:59:58.000-00:00"
;; > Hello There : #inst "2000-01-01T00:00:00.000-00:00"
;; > Hello There : #inst "2000-01-01T00:00:02.000-00:00"
;; > Hello There : #inst "2000-01-01T00:00:04.000-00:00"
;; > Hello There : #inst "2000-01-01T00:00:06.000-00:00"
;; > Hello There : #inst "2000-01-01T00:00:08.000-00:00"

"We can control the way the simulation is run through other params"

(simulate sch1
          {:start T1
           :end   T2
           :mode  :async
           :pause 0
           :step  3})
;; > Hello There : #inst "1999-12-31T23:59:56.000-00:00"
;; > Hello There : #inst "2000-01-01T00:00:02.000-00:00"
;; > Hello There : #inst "2000-01-01T00:00:08.000-00:00"
;; > Hello There : #inst "1999-12-31T23:59:50.000-00:00"

"`:mode` can be either `:sync` (default) or `:async`. `:step` is the number of second to wait to test again and pause is the sleep time in milliseconds."

[[:section {:title "Interval and Pause"}]]

"It can be seen that we can simulate the actual speed of outputs by keeping the step as 1 and increasing the pause time to 1000ms"

(comment
  (simulate sch1
            {:start T1
             :end   T2
             :mode  :async
             :pause 1000
             :step  1}))
;; > Hello There : #inst "1999-12-31T23:59:50.000-00:00"

;;            ... wait 2 seconds ...

;; > Hello There : #inst "1999-12-31T23:59:52.000-00:00"

;;            ... wait 2 seconds ...

;; > Hello There : #inst "1999-12-31T23:59:54.000-00:00"


[[:subsection {:title "Speeding Up"}]]
"In the following example, the step has been increased to 2 whilst the pause time has decreased to 100ms. This results in a 20x increase in the speed of outputs."

(comment
  (simulate sch1
            {:start T1
             :end   T2
             :mode  :async
             :pause 100
             :step  2})

;; > Hello There : #inst "1999-12-31T23:59:50.000-00:00"

;;            ... wait 0.1 seconds ...

;; > Hello There : #inst "1999-12-31T23:59:52.000-00:00"

;;            ... wait 0.1 seconds ...

;; > Hello There : #inst "1999-12-31T23:59:54.000-00:00"
  )

"Being able to adjust these simulation parameters are really powerful testing tools and saves an incredible amount of time in development. For example, we can quickly test the year long output of a task that is scheduled to run once an hour very quickly by making the interval 3600 seconds and the pause time to the same length of time that the task takes to finish."

[[:section {:title "Globals"}]]

"The global defaults are contained in `hara.io.scheduler/*defaults*`:"

(comment
  (println hara.io.scheduler/*defaults*)
  => {:clock {:type "java.util.Date",
              :timezone "Asia/Kolkata",
              :interval 1,
              :truncate :second},
      :registry {},
      :cache {},
      :ticker {}})

"For the purposes of the reader, only the `:clock` entry of `*defaults*` is important. To override the defaults, define the scheduler with the settings that needs to be customised. To set the time component used to be `java.time.Instant`, define the scheduler as follows:"

(comment
  (def sch2 (scheduler {:hello {:handler  (fn [t params] (println t))
                                :schedule "/2 * * * * * *"
                                :params {}}}
                       {}
                       {:clock {:type "java.time.Instant"}}
                       ))

  (start! sch2)
  ;;> #<Instant 2016-03-05T03:24:06Z>

  ;;  ... wait 2 seconds ...

  ;;> #<Instant 2016-03-05T03:24:08Z>

  ;;  ... printing out instances of java.time.Instant every 2 seconds ...

  (stop! sch2))

[[:section {:title "Date Type"}]]

"It is also possible to use the clojure map representation (the default in hara.time)"

(comment
  (def sch2 (scheduler {:hello {:handler  (fn [t params] (println t))
                                :schedule "/2 * * * * * *"
                                :params {}}}
                       {}
                       {:clock {:type "clojure.lang.PersistentArrayMap"
                                :timezone "GMT"}}))

  (start! sch2)

  ;;> {:day 6, :hour 20, :timezone GMT, :second 38, :month 3,
  ;;   :type clojure.lang.PersistentHashMap, :year 2016, :millisecond 0, :minute 30}

  ;;  ... wait 2 seconds ...

  ;;> {:day 6, :hour 20, :timezone GMT, :second 40, :month 3,
  ;;   :type clojure.lang.PersistentHashMap, :year 2016, :millisecond 0, :minute 30}

  ;;  ... printing out instances of java.time.Instant every 2 seconds ...

  (stop! sch2))

[[:section {:title "Timezone"}]]

"Having a `:timezone` value in the clock will ensure that the right timezone is set. The default will always be the system local time, but it can be set to any timezone. To see this in effect, the `Calendar` object is used and EST is applied."

(comment
  (def sch2 (scheduler {:hello {:handler  (fn [t params] (println t))
                                :schedule "/2 * * * * * *"
                                :params {}}}
                       {}
                       {:clock {:type "java.util.Calendar"
                                :timezone "EST"}}))

  (start! sch2)

  ;;> #inst "2016-03-06T15:37:38.000-05:00"

  ;;  ... wait 2 seconds ...

  ;;> #inst "2016-03-06T15:37:40.000-05:00"

  ;;  ... wait 2 seconds ...

  ;;> #inst "2016-03-06T15:37:42.000-05:00"

  (stop! sch2))

[[:section {:title "Upgrade from Cronj"}]]

"[cronj](https://github.com/zcaudate/cronj) was the original scheduling library before the concept became absorbed into the larger [hara](https://docs.caudate.me/hara) ecosystem and the `hara.ion.scheduler` library was born. Although there was a significant cleanup of the internal components within the scheduler, the architecture has not really changed at all. Most of the original cronj methods have been kept pretty much the same so it should be very quick to move from one to the other.

[cronj](https://github.com/zcaudate/cronj) uses [clj-time](https://github.com/clj-time/clj-time), a wrapper around [joda-time](http://www.joda.org/joda-time/) to provide for time manipulation. The library has been swapped out in favor of `hara.time` because it provides a more flexible option.

`hara.io.scheduler` allows the user to select from a few different time implementations and so while new projects can start with the new `java.time.Instant` entry, or even the clojure map representation of time. However, many projects will still be working with [joda-time](http://www.joda.org/joda-time/) and this has been supported through another project - [hara.time.joda](https://github.com/zcaudate/hara.time.joda), which provides joda-time extensions to `hara.time`."

"To upgrade to `cronj` to `hara.io.scheduler`, all that needs to be done is to add to `project.clj` dependencies:

    [zcaudate/hara.io.scheduler \"{{PROJECT.version}}\"]
    [joda    \"2.9.2\"]

Apart from the initial call to include the scheduler, require the `hara.time.joda` namespace to load in all the protocol and multimethod hooks."

(comment
  (require '[hara.io.scheduler :refer :all])
  (require '[hara.time.joda]))

"Most of the methods have been kept pretty much the same, except the constructor. The previous way of constructing the scheduler looks like this:"

(comment
  (require '[cronj.core :as cj])
  (def cnj (cj/cronj
            :interval 2
            :entries [{:id       :t1
                       :handler  (fn [dt opts] (println dt) (Thread/sleep 1000))
                       :schedule "* * * * * * *"
                       :enabled  true
                       :opts     {:home "/home/cronj"}}

                      {:id       :t2
                       :handler  (fn [dt opts] (println dt) (Thread/sleep 5000))
                       :schedule "* * * * * * *"
                       :enabled  true
                       :opts     {:ex "example"}}])))

"Now it looks like this:"

(comment
  (require '[hara.io.scheduler :as sch])
  (def sch (sch/scheduler
            {:t1 {:handler (fn [t params] (println dt) (Thread/sleep 1000))
                  :schedule "* * * * * * *"
                  :params   {:home "/home/cronj"}}
             :t2 {:handler (fn [t params] (println dt) (Thread/sleep 5000))
                  :schedule "* * * * * * *"
                  :params   {:ex "example"}}}
            {}
            {:clock {:type "org.joda.time.DateTime"
                     :interval 2}})))

"Or for the seperation of config and function, as well as simplification of the interface, it can also look like this:"

(comment
  (def sch (sch/scheduler
            {:t1 (fn [t] (println t) (Thread/sleep 1000))
             :t2 (fn [t] (println t) (Thread/sleep 5000))}
            {:t1 {:schedule "* * * * * * *"
                  :params   {:home "/home/cronj"}}
             :t2 {:schedule "* * * * * * *"
                  :params   {:ex "example"}}}
            {:clock {:type "org.joda.time.DateTime"
                     :interval 2}}))
  (sch/start! sch)

  ;;>#<DateTime 2016-03-07T07:47:04.000+05:30>
  ;;>#<DateTime 2016-03-07T07:47:04.000+05:30>


  ;;>#<DateTime 2016-03-07T07:47:05.000+05:30>
  ;;>#<DateTime 2016-03-07T07:47:05.000+05:30>

  ;;>#<DateTime 2016-03-07T07:47:06.000+05:30>
  ;;>#<DateTime 2016-03-07T07:47:06.000+05:30>

  (sch/stop! sch))

"The upgrade is complete."
