(ns hara.io.profiler
  (:require [hara.io.scheduler :as scheduler]))

(def ^:dynamic *intervals* #{1 2 3 4 5 6 10 12 15 20 30 60})

(defrecord Profiler [])

(defn profiler
  "creates a profiler entry
   
   (def p1 (profiler control input))
   
   (start-profiler p1)
   (pause-profiler p1)
   "
  {:added "2.4"}
  [control profile]
  (map->Profiler {:control control
                  :profile  profile
                  :result   (promise)
                  :state    (atom {})}))

(defn signals
  "returns the collected signals form the profiler
 
   (signals p1)
   => {\"client-count\" [[1487322396000 5] [1487322395000 6] [1487322394000 2] [1487322393000 1]]
       \"count\" [[1487322396000 16] [1487322395000 5] [1487322394000 0] [1487322393000 0]],
       \"average\" [[1487322396000 601.4375] [1487322395000 596.2] [1487322394000 nil] [1487322393000 nil]],
       \"moving-10\" [[1487322396000 453] [1487322395000 298] [1487322394000 0] [1487322393000 0]],
       \"moving-3\" [[1487322396000 565] [1487322395000 647] [1487322394000 0] [1487322393000 0]]}"
  {:added "2.4"}
  [profiles]
  (-> profiles
      :state
      deref
      :signals))

(defn is-active?
  "checks if the profiler is active
 
   (is-active? p1)
   => true"
  {:added "2.4"}
  [{:keys [control state profile] :as profiler}]
  (and (:instance @state)
       (:scheduler @state)
       (scheduler/running? (:scheduler @state))
       (not ((:stopped? control) (:instance @state)))))

(defn create-entry
  "single io.scheduler entry from a profiler entry
 
   (create-entry (:instance @(:state p1))
                 (:state p1)
                 {:name \"count\"
                  :poll (fn [_] 1)
                  :interval 1})
   => (contains {:id :count,
                :handler fn?
                 :schedule \"/1 * * * * * *\"})"
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
  "io.scheduler entries from profiler entries
 
   (create-entries (:instance @(:state p1))
                   (:state p1)
                   input)
   => (contains-in
       {:client-count {:id :client-count,
                       :handler fn?
                      :schedule \"/1 * * * * * *\"},
        :count        {:id :count,
                       :handler fn?
                       :schedule \"/1 * * * * * *\"},
        :average      {:id :average,
                       :handler fn?
                       :schedule \"/1 * * * * * *\"},
        :moving-10    {:id :moving-10,
                       :handler fn?
                       :schedule \"/1 * * * * * *\"},
        :moving-3     {:id :moving-3,
                       :handler fn?
                       :schedule \"/1 * * * * * *\"}})"
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
  "starts a profiler to collect information
 
   (start-profiler p1)"
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
                                              (deliver (:result profiler) (signals profiler))
                                              (swap! state #(dissoc % :scheduler :instance))))
                           :schedule "* * * * * * *"})
      (scheduler/start! scheduler)
      (swap! state #(assoc %
                           :scheduler scheduler
                           :instance instance))))
  profiler)

(defn stop-profiler
  "stops a profiler
 
   (stop-profiler p1)"
  {:added "2.4"}
  [{:keys [control state profile] :as profiler}]
  (if (is-active? profiler)
    (let [{:keys [stop stopped?]} control
          {:keys [instance scheduler]} @state]
      (stop instance)
      (scheduler/stop! scheduler)
      (swap! state #(dissoc % :scheduler :instance))))
  profiler)

(defn pause-profiler
  "pauses a profiler
 
   (pause-profiler p1)"
  {:added "2.4"}
  [{:keys [control state profile] :as profiler}]
  (if (is-active? profiler)
    (let [{:keys [pause stopped?]} control
          {:keys [instance scheduler]} @state]
      (pause instance)
      (scheduler/stop! scheduler)))
  profiler)

(defn profile
  "profiles an input, waiting until all results finish before returning the result
 
   (profile p1)
 
   (profile control input)"
  {:added "2.4"}
  ([profiler]
   (start-profiler profiler)
   @(:result profiler))
  ([control profile-input]
   (-> (profiler control profile-input)
       profile)))
