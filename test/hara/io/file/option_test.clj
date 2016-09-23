(ns hara.io.file.option-test
  (:use hara.test)
  (:require [hara.io.file.option :refer :all]
            [hara.class.enum :as enum])
  (:import (java.nio.file AccessMode)))

^{:refer hara.io.file.option/enum-lookup :added "2.4"}
(fact "creates lookup table for enumerations"
  (enum-lookup (enum/enum-values AccessMode))
  => {:read    AccessMode/READ
      :write   AccessMode/WRITE
      :execute AccessMode/EXECUTE})

^{:refer hara.io.file.option/to-mode-string :added "2.4"}
(fact "transforms mode numbers to mode strings"

  (to-mode-string "455")
  => "r--r-xr-x"

  (to-mode-string "777")
  => "rwxrwxrwx")

^{:refer hara.io.file.option/to-mode-number :added "2.4"}
(fact "transforms mode numbers to mode strings"

  (to-mode-number "r--r-xr-x")
  => "455"

  (to-mode-number "rwxrwxrwx")
  => "777")

^{:refer hara.io.file.option/to-permissions :added "2.4"}
(fact "transforms mode to permissions"

  (to-permissions "455")
  => (contains [:owner-read
                :group-read
                :group-execute
                :others-read
                :others-execute] :in-any-order))
  
^{:refer hara.io.file.option/from-permissions :added "2.4"}
(fact "transforms permissions to mode"

  (from-permissions [:owner-read
                     :group-read
                     :group-execute
                     :others-read
                     :others-execute])
  => "455")
