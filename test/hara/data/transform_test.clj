(ns hara.data.transform-test
  (:use hara.test)
  (:require [hara.data.transform :refer :all])
  (:refer-clojure :exclude [apply]))

^{:refer hara.data.transform/template-rel :added "2.4"}
(fact "creates the id for a relation"

  (template-rel [:authority :username])
  = :authority/username)

^{:refer hara.data.transform/forward-rel :added "2.4"}
(fact "returns the template for a forward relation"

  (forward-rel {:authority {:username [:user]
                            :password [:pass]}})
  
  = {:authority {:username :authority/username,
                 :password :authority/password}})

^{:refer hara.data.transform/backward-rel :added "2.4"}
(fact "returns the template for a back relation"
  
  (backward-rel {:authority {:username [:user]
                             :password [:pass]}})
  = {:user :authority/username, :pass :authority/password})

^{:refer hara.data.transform/collect :added "2.4"}
(fact "collects nested keys for transform"
  
  (collect {:authority {:username :authority/username,
                        :password :authority/password}})
  = {:authority/username [:authority :username],
      :authority/password [:authority :password]})

^{:refer hara.data.transform/relation :added "2.4"}
(fact "creates template for the transform relationship"

  (relation {:authority {:username [:user]
                         :password [:pass]}})
  = {:authority/username [[:authority :username] [:user]],
      :authority/password [[:authority :password] [:pass]]})

^{:refer hara.data.transform/apply :added "2.4"}
(fact "applies the relation to a map"

  (apply {:user "chris" :pass "hello"}
         {:authority/username [[:authority :username] [:user]],
          :authority/password [[:authority :password] [:pass]]})
  = {:authority {:username "chris", :password "hello"}})

^{:refer hara.data.transform/retract :added "2.4"}
(fact "retracts the relation from the map"

  (retract {:authority {:username "chris", :password "hello"}}
           {:authority/username [[:authority :username] [:user]],
            :authority/password [[:authority :password] [:pass]]})
  = {:user "chris" :pass "hello"})
