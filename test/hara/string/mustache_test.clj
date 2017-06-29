(ns hara.string.mustache-test
  (:use hara.test)
  (:require [hara.string.mustache :refer :all]))

^{:refer hara.string.mustache/render :added "2.5"}
(fact "converts a template with mustache data"

  (render "{{user.name}}" {:user {:name "zcaudate"}})
  => "zcaudate"

  (render "{{# user.account}}{{name}} {{/user.account}}"
          {:user {:account [{:name "admin"}
                            {:name "user"}]}})
  => "admin user "
  
  (render "{{? user}}hello{{/user}}" {:user true})
  => "hello"

  (render "{{^ user.name}}hello{{/user.name}}" {:user nil})
  => "hello")