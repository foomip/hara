(ns hara.io.profiler
  (:require [hara.io.scheduler :as scheduler]))

(def ^:dynamic *intervals* #{1 2 3 4 5 6 10 12 15 20 30 60})

(defrecord Profiler [])

(defn profiler
  "creates a profiler entry"
  {:added "2.4"}
  [control profile]
  (map->Profiler {:control control
                  :profile  profile
                  :state    (atom {})}))

(defn signals
  "returns the collected signals form the profiler"
  {:added "2.4"}
  [profiles]
  (-> profiles
      :state
      deref
      :signals))

(defn is-active?
  "checks if the profiler is active"
  {:added "2.4"}
  [{:keys [control state profile] :as profiler}]
  (and (:instance @state)
       (:scheduler @state)
       (scheduler/running? (:scheduler @state))
       (not ((:stopped? control) (:instance @state)))))

(defn create-entry
  "single io.scheduler entry from a profiler entry"
  {:added "2.4"}
  [instance state {:keys [name poll interval]}]
  (let [id (keyword name)
        entry {:id id
               :handler (fn [t]
                          (let [result (poll instance)]
                            (swap! state #(update-in % [:signals name] conj [t result]))))
               :schedule (str "/" interval " * * * * * *")}]
    entry))

(defn create-entries
  "io.scheduler entries from profiler entries"
  {:added "2.4"}
  [instance state {:keys [interval signals] :as profile}]
  (let [signals (mapv (fn [sig]
                        (if (:interval sig)
                          sig
                          (assoc sig :interval interval)))
                      signals)
        entries (map #(create-entry instance state %) signals)]
    (zipmap (map :id entries) entries)))

(defn start-profiler
  "starts a profiler to collect information"
  {:added "2.4"}
  [{:keys [control state profile] :as profiler}]
  (if-not (is-active? profiler)
    (let [{:keys [constructor config start stopped? init]} control
          instance (cond-> (constructor config)
                     init  (init)
                     :then (start))
          scheduler (scheduler/scheduler (create-entries instance state profile)
                                         {}
                                         {:clock {:type "java.lang.Long"}})]
      (scheduler/add-task scheduler :stop-scheduler
                          {:handler (fn [t] (when (stopped? instance)
                                              (scheduler/stop! scheduler)
                                              (swap! state #(dissoc % :scheduler :instance))))
                           :schedule "* * * * * * *"})
      (scheduler/start! scheduler)
      (swap! state #(assoc %
                           :scheduler scheduler
                           :instance instance))))
  profiler)

(defn stop-profiler
  "stops a profiler manually"
  {:added "2.4"}
  [{:keys [control state profile] :as profiler}]
  (if (is-active? profiler)
    (let [{:keys [stop stopped?]} control
          {:keys [scheduler]} @state]
      (scheduler/stop! scheduler)
      (swap! state #(dissoc % :scheduler :instance))))
  profiler)
