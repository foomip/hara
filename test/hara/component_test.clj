(ns hara.component-test
  (:use hara.test)
  (:require [hara.component :refer :all]))

(defrecord Database []
    IComponent
    (-start [db]
      (assoc db :status "started"))
    (-stop [db]
      (dissoc db :status)))

(defrecord Filesystem []
    IComponent
    (-start [sys]
      (assoc sys :status "started"))
    (-stop [sys]
      (dissoc sys :status)))

(defrecord Catalog []
    IComponent
    (-start [store]
      (assoc store :status "started"))
    (-stop [store]
      (dissoc store :status)))

^{:refer hara.component/started? :added "2.1"}
(fact "checks if a component has been started"

  (started? (Database.))
  => false

  (started? (start (Database.)))
  => true

  (started? (stop (start (Database.))))
  => false)

^{:refer hara.component/stopped? :added "2.1"}
(fact "checks if a component has been stopped"

  (stopped? (Database.))
  => true

  (stopped? (start (Database.)))
  => false

  (stopped? (stop (start (Database.))))
  => true)


^{:refer hara.component/start :added "2.1"}
(fact "starts a component/array/system"

  (start (Database.))
  => (just {:status "started"}))

^{:refer hara.component/stop :added "2.1"}
(fact "stops a component/array/system"

  (stop (start (Database.)))
  => (just {}))

^{:refer hara.component/array :added "2.1"}
(fact "creates an array of components"

  (def recs (start (array map->Database [{:id 1} {:id 2}])))
  (count (seq recs)) => 2
  (first recs) => (just {:id 1 :status "started"}))

^{:refer hara.component/array? :added "2.1"}
(fact "checks if object is a component array"

  (array? (array map->Database []))
  => true)


^{:refer hara.component/system :added "2.1"}
(fact "creates a system of components"
  
  ;; The topology specifies how the system is linked
  (def topo {:db        [map->Database]
             :files     [[map->Filesystem]]
             :catalogs  [[map->Filestore] [:files :fs] :db]})

  ;; The configuration customises the system
  (def cfg  {:db     {:type :basic
                      :host "localhost"
                      :port 8080}
             :files [{:path "/app/local/1"}
                     {:path "/app/local/2"}]
             :catalogs [{:id 1}
                        {:id 2}]})

  ;; `system` will build it and calling `start` initiates it
  (def sys (-> (system topo cfg) start))

  ;; Check that the `:db` entry has started
  (:db sys)
  => (just {:status "started",
            :type :basic,
            :port 8080,
            :host "localhost"})

  ;; Check the first `:files` entry has started
  (-> sys :files first)
  => (just {:status "started",
            :path "/app/local/1"})

  ;; Check that the second `:store` entry has started
  (->> sys :catalogs second)
  => (contains {:id 2
                :status "started"
                :db {:status "started",
                        :type :basic,
                        :port 8080,
                        :host "localhost"}
                :fs {:path "/app/local/2", :status "started"}}))


^{:refer hara.component/system? :added "2.1"}
(fact "checks if object is a component system"

  (system? (system {} {}))
  => true)

(defrecord Camera []
    Object
    (toString [cam]
      (str "#cam" (into {} cam)))

    IComponent
    (-start [cam]
      (assoc cam :status "started"))
    (-stop [cam]
      (dissoc cam :status)))

  (defmethod print-method Camera
    [v ^java.io.Writer w]
    (.write w (str v)))

^{:refer hara.component/component? :added "2.2"}
(fact "checks if an instance extends IComponent"

  (component? (Database.))
  => true)

^{:refer hara.component/more-tests :added "2.1"}
(fact "creates a system of components"

  (def topology {:database   [{:constructor map->Database}]

                 :cameras    [{:constructor [map->Camera]
                               :initialiser #(map (fn [x] (assoc x :a 1)) %)}
                              :database]})

  (#'hara.component/system-constructors topology)
  => (contains {:cameras (contains [fn?])
                :database fn?})
  (#'hara.component/system-dependencies topology)
  => {:cameras #{:database}, :database #{}}
  (#'hara.component/system-augmentations topology)
  => {:cameras #{:database}, :database #{}}

  (start (system topology
                 {:watchmen [{:id 1} {:id 2}]
                  :cameras  ^{:hello "world"} [{:id 1} {:id 2 :hello "again"}]}))
  => (contains-in {:database {:status "started"}
                   :cameras [{:hello "world", :id 1,  :a 1 :status "started"}
                             {:hello "again", :id 2,  :a 1 :status "started"}]}))


^{:refer hara.component/expose-test :added "2.2"}
(fact "exposes sub-components within a system"

  (def topology {:database [map->Database]
                 :status   [{:expose [:status]} :database]})

  (start (system topology
                 {:database {:status "stopped"}}))
  => (contains {:database {:status "started"}
                :status   "started"}))
