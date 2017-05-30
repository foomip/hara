(ns hara.component-test
  (:use hara.test)
  (:require [hara.component :refer :all]))

(defrecord Database []
  IComponent
  (-start [db]
    (assoc db :status "started"))
  (-stop [db]
    (dissoc db :status)))

(defmethod print-method Database [v w]
  (.write w (str "#db" (into {} v))))

(defrecord Filesystem []
  IComponent
  (-start [sys]
    (assoc sys :status "started"))
  (-stop [sys]
    (dissoc sys :status))
  (-properties [sys]
    {:hello "world"}))

(defmethod print-method Filesystem [v w]
  (.write w (str "#fs" (into {} v))))

(defrecord Catalog []
  IComponent
  (-start [store]
    (assoc store :status "started"))
  (-stop [store]
    (dissoc store :status)))

(defmethod print-method Catalog [v w]
  (.write w (str "#cat" (into {} v))))

^{:refer hara.component/primitive? :added "2.5"}
(fact "checks if a component is a primitive type"

  (primitive? 1) => true
  
  (primitive? {}) => false)

^{:refer hara.component/started? :added "2.1"}
(fact "checks if a component has been started"

  (started? 1)
  => true

  (started? {})
  => false
  
  (started? (start {}))
  => true
  
  (started? (Database.))
  => false

  (started? (start (Database.)))
  => true

  (started? (stop (start (Database.))))
  => false)

^{:refer hara.component/stopped? :added "2.1"}
(fact "checks if a component has been stopped"

  (stopped? 1)
  => false

  (stopped? {})
  => true
  
  (stopped? (start {}))
  => false
  
  (stopped? (Database.))
  => true

  (stopped? (start (Database.)))
  => false

  (stopped? (stop (start (Database.))))
  => true)

^{:refer hara.component/start :added "2.1"}
(fact "starts a component/array/system"

  (start (Database.))
  => {:status "started"})

^{:refer hara.component/stop :added "2.1"}
(fact "stops a component/array/system"

  (stop (start (Database.))) => {})

^{:refer hara.component/properties :added "2.1"}
(fact "returns properties of the system"

  (properties (Database.)) => {}

  (properties (Filesystem.)) => {:hello "world"})

^{:refer hara.component/array :added "2.1"}
(fact "creates an array of components"

  (def recs (start (array {:constructor map->Database} [{:id 1} {:id 2}])))
  (count (seq recs)) => 2
  (first recs) => (just {:id 1 :status "started"}))

^{:refer hara.component/array? :added "2.1"}
(fact "checks if object is a component array"

  (array? (array map->Database []))
  => true)

^{:refer hara.component/start-array :added "2.1"}
(fact "starts an array of components")

^{:refer hara.component/stop-array :added "2.1"}
(fact "stops an array of components")

^{:refer hara.component/constructor :added "2.5"}
(fact "returns the constructor from topology")

^{:refer hara.component/system :added "2.1"}
(fact "creates a system of components"
  
  ;; The topology specifies how the system is linked
  (def topo {:db        [map->Database]
             :files     [[map->Filesystem]]
             :catalogs  [[map->Catalog] [:files {:type :element :as :fs}] :db]})
  
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
  => (contains-in {:id 2
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

^{:refer hara.component/system-string :added "2.5"}
(fact "returns the system for display"

  (system-string (system {:a [identity]
                          :b [identity]}
                         {:a 1 :b 2}
                         {:tag "happy"}))
  => "#happy {:a 1, :b 2}")

^{:refer hara.component/long-form-imports :added "2.5"}
(fact "converts short form imports to long form"
  
  (long-form-imports [:db [:file {:as :fs}]])
  => {:db   {:type :single, :as :db},
      :file {:type :single, :as :fs}}
  
  (long-form-imports [[:ids {:type :element :as :id}]])
  => {:ids {:type :element, :as :id}})

^{:refer hara.component/long-form-entry :added "2.5"}
(fact "converts short form entry into long form"

  (long-form-entry [{:constructor :identity
                     :initialiser :identity}])
  => {:type :build
      :compile :single
      :constructor :identity
      :initialiser :identity
      :import {}, :dependencies ()}

  (long-form-entry [[identity]])
  => (contains {:compile :array,
                :type :build,
                :constructor fn?
                :import {},
                :dependencies ()})

  (long-form-entry [[identity] [:model {:as :raw}] [:ids {:type :element :as :id}]])
  => (contains {:compile :array,
                :type :build
                :constructor fn?
                :import {:model {:type :single, :as :raw},
                         :ids {:type :element, :as :id}},
                :dependencies [:model :ids]}))

^{:refer hara.component/long-form :added "2.5"}
(fact "converts entire topology to long form"

  (long-form {:db [identity]
              :count [{:expose :count} :db]})
  => (contains-in {:db {:compile :single,
                        :type :build,
                        :constructor fn?,
                        :import {},
                        :dependencies ()},
                   :count {:compile :single,
                           :type :expose,
                           :in :db,
                           :function :count,
                           :dependencies [:db]}}))

^{:refer hara.component/get-dependencies :added "2.5"}
(fact "get dependencies for long form"
  (-> (long-form {:model   [identity]
                  :ids     [[identity]]
                  :traps   [[identity] [:model {:as :raw}] [:ids {:type :element :as :id}]]
                  :entry   [identity :model :ids]
                  :nums    [[{:expose :id}] :traps]
                  :model-tag  [{:expose :tag
                                :setup identity}  :model]})
      get-dependencies)
  => {:model #{},
      :ids #{},
      :traps #{:ids :model},
      :entry #{:ids :model},
      :nums #{:traps},
      :model-tag #{:model}})

^{:refer hara.component/get-exposed :added "2.5"}
(fact "get exposed keys for long form"
  (-> (long-form {:model   [identity]
                  :ids     [[identity]]
                  :traps   [[identity] [:model {:as :raw}] [:ids {:type :element :as :id}]]
                  :entry   [identity :model :ids]
                  :nums    [[{:expose :id}] :traps]
                  :model-tag  [{:expose :tag
                                :setup identity}  :model]})
      get-exposed)
  => [:nums :model-tag])

^{:refer hara.component/all-dependencies :added "2.5"}
(fact "gets all dependencies for long form"

  (all-dependencies
   {1 #{4 2}
    2 #{3}
    3 #{5}
    4 #{}
    5 #{6}
    6 #{}})
  => {1 #{2 3 4 5 6}
      2 #{3 5 6}
      3 #{5 6}
      4 #{}
      5 #{6}
      6 #{}}
  
  (-> (long-form {:model   [identity]
                  :ids     [[identity]]
                  :traps   [[identity] [:model {:as :raw}] [:ids {:type :element :as :id}]]
                  :entry   [identity :model :ids]
                  :nums    [[{:expose :id}] :traps]
                  :model-tag  [{:expose :tag
                                :setup identity}  :model]})
      get-dependencies
      all-dependencies)
  => {:model #{},
      :ids #{},
      :traps #{:ids :model},
      :entry #{:ids :model},
      :nums #{:ids :traps :model},
      :model-tag #{:model}})

^{:refer hara.component/valid-subcomponents :added "2.5"}
(fact "returns only the components that will work (for partial systems)"
  
  (valid-subcomponents
   (long-form {:model  [identity]
               :tag    [{:expose :tag} :model]
               :kramer [identity :tag]})
   [:model])
  => [:model :tag])

^{:refer hara.component/start-system :added "2.5"}
(comment "starts a system"
  (->> (system {:models [[identity] [:files {:type :element :as :fs}]]
                :files  [[identity]]}
               {:models [{:m 1} {:m 2}]
                :files  [{:id 1} {:id 2}]})
       start-system
       (into {}))
  => (contains-in {:models [{:m 1,
                             :fs {:id 1}}
                            {:m 2,
                             :fs {:id 2}}],
                   :files [{:id 1} {:id 2}]}))

^{:refer hara.component/stop-system :added "2.5"}
(comment "stops a system"
  (stop-system
   (start-system
    (system {:model   [identity]
             :ids     [[identity]]
             :traps   [[identity] [:model {:as :raw}] [:ids {:type :element :as :id}]]
             :entry   [identity :model :ids]
             :nums    [[{:expose :id}] :traps]
             :model-tag  [{:expose :tag
                           :setup identity}  :model]}
            {:model {:tag :barbie}
             :ids   [1 2 3 4 5]
             :traps [{} {} {} {} {}]
             :entry {}})))
  =>  {:model {:tag :barbie}, :ids [1 2 3 4 5], :traps [{} {} {} {} {}], :entry {}})


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

^{:refer hara.component/system-import :added "2.5"}
(fact "imports a component into the system")

^{:refer hara.component/system-expose :added "2.5"}
(fact "exposes a component into the system")

^{:refer hara.component/system-deport :added "2.5"}
(fact "deports a component from the system")

^{:refer hara.component/more-tests :added "2.1"}
(fact "creates a system of components"

  (def topology {:database   [{:constructor map->Database}]

                 :cameras    [[{:constructor map->Camera
                                 :setup #(map (fn [x] (assoc x :a 1)) %)}]
                              :database]})
  
  (start (system topology
                 {:database {}
                  :watchmen [{:id 1} {:id 2}]
                  :cameras  ^{:hello "world"} [{:id 1} {:id 2 :hello "again"}]}))
  => (contains-in {:database {:status "started"}
                   :cameras [{:hello "world", :id 1,  :a 1 :status "started"}
                             {:hello "again", :id 2,  :a 1 :status "started"}]}))

^{:refer hara.component/expose-test :added "2.2"}
(fact "exposes sub-components within a system"

  (def topology {:database [map->Database]
                 :status   [{:expose :status} :database]})
  
  (start (system topology
                 {:database {:status "stopped"}}))
  => (contains {:database {:status "started"}
                :status   "started"}))

(comment
  
  (require '[lucid.unit :as unit])
  (unit/import *ns*)
  
  (start {:functions {:dissoc-setup #(dissoc % :setup)
                      :print #(doto % prn)}}
         {:setup #(assoc % :setup true)
          :hooks {:pre-start [:print :dissoc-setup]
                  :post-start [:print :dissoc-setup]}})

  )