(ns hara.io.ansii-test
  (:use hara.test)
  (:require [hara.io.ansii :refer :all]))

^{:refer hara.io.ansii/encode :added "2.4"}
(fact "encodes the ansii characters for modifiers"
  (encode :bold)
  => "[1m"
  
  (encode :red)
  => "[31m")

^{:refer hara.io.ansii/style :added "2.4"}
(fact "styles the text according to the modifiers"

  (style "hello" [:bold :red])
  => "[1m[31mhello[0m")

^{:refer hara.io.ansii/define-ansii-forms :added "2.4"}
(fact "defines ansii forms given by the lookups"

  ;; Text:
  ;; [blue cyan green grey magenta red white yellow]

  (blue "hello")
  => "[34mhello[0m"

  ;; Background:
  ;; [on-blue on-cyan on-green on-grey
  ;;  on-magenta on-red on-white on-yellow]

  (on-white "hello")
  => "[47mhello[0m"

  ;; Attributes:
  ;; [blink bold concealed dark reverse-color underline]

  (blink "hello")
  => "[5mhello[0m")
