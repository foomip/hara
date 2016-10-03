(defproject im.chit/hara "2.4.5"
  :description "patterns and utilities"
  :url "https://github.com/zcaudate/hara"
  :license {:name "The MIT License"
            :url "http://http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :aliases {"test" ["run" "-m" "hara.test" "exit"]}
  :profiles {:dev {:dependencies [[compojure "1.4.0"]
                                  [ring "1.4.0"]
                                  [clj-http "2.1.0"]
                                  [org.eclipse.jgit "4.0.1.201506240215-r"]]
                   :plugins [[lein-repack "0.2.10"]
                             [lein-hydrox "0.1.17"]]}}
  :publish {:theme  "bolton"
  
            :template {:author "Chris Zheng"
                       :email  "z@caudate.me"
                       :site   "hara"
                       :tracking "UA-31320512-2"}
            
            :files {"index"
                    {:input "test/documentation/home_hara.clj"
                     :title "hara"
                     :subtitle "patterns and utilities"}
                    "hara-class"
                    {:input "test/documentation/hara_class.clj"
                     :title "hara.class"
                     :subtitle "functions for reasoning about classes"}
                    "hara-common"
                    {:input "test/documentation/hara_common.clj"
                     :title "hara.common"
                     :subtitle "primitives declarations and functions"}
                    "hara-component"
                    {:input "test/documentation/hara_component.clj"
                     :title "hara.component"
                     :subtitle "constructing composable systems"}
                    "hara-concurrent"
                    {:input "test/documentation/hara_concurrent.clj"
                     :title "hara.concurrent"
                     :subtitle "methods and datastructures for concurrency"}
                    "hara-concurrent-ova"
                    {:input "test/documentation/hara_concurrent_ova.clj"
                     :title "hara.concurrent.ova"
                     :subtitle "shared mutable state for multi-threaded applications"}
                    "hara-concurrent-procedure"
                    {:input "test/documentation/hara_concurrent_procedure.clj"
                     :title "hara.concurrent.procedure"
                     :subtitle "model for controllable execution"}
                    "hara-data"
                    {:input "test/documentation/hara_data.clj"
                     :title "hara.data"
                     :subtitle "manipulation of maps and representations of data"}
                    "hara-event"
                    {:input "test/documentation/hara_event.clj"
                     :title "hara.event"
                     :subtitle "event signalling and conditional restart"}
                    "hara-expression"
                    {:input "test/documentation/hara_expression.clj"
                     :title "hara.expression"
                     :subtitle "interchange between code and data"}
                    "hara-extend"
                    {:input "test/documentation/hara_extend.clj"
                     :title "hara.extend"
                     :subtitle "macros for extensible objects"}
                    "hara-function"
                    {:input "test/documentation/hara_function.clj"
                     :title "hara.function"
                     :subtitle "functions for reasoning about functions"}
                    "hara-group"
                    {:input "test/documentation/hara_group.clj"
                     :title "hara.group"
                     :subtitle "generic typed collections"}
                    "hara-io-file"
                    {:input "test/documentation/hara_io_file.clj"
                     :title "hara.io.file"
                     :subtitle "tools for the file system"}
                    "hara-io"
                    {:input "test/documentation/hara_io.clj"
                     :title "hara.io"
                     :subtitle "tools for files and io operations"}
                    "hara-io-scheduler"
                    {:input "test/documentation/hara_io_scheduler.clj"
                     :title "hara.io.scheduler"
                     :subtitle "easy and intuitive task scheduling"}
                    "hara-io-watch"
                    {:input "test/documentation/hara_io_watch.clj"
                     :title "hara.io.watch"
                     :subtitle "watch for filesystem changes"}
                    "hara-namespace"
                    {:input "test/documentation/hara_namespace.clj"
                     :title "hara.namespace"
                     :subtitle "utilities for manipulation of namespaces"}
                    "hara-object"
                    {:input "test/documentation/hara_object.clj"
                     :title "hara.object"
                     :subtitle "think data, escape encapsulation"}
                    "hara-reflect"
                    {:input "test/documentation/hara_reflect.clj"
                     :title "hara.reflect"
                     :subtitle "java reflection made easy"}
                    "hara-sort"
                    {:input "test/documentation/hara_sort.clj"
                     :title "hara.sort"
                     :subtitle "micellaneous sorting functions"}
                    "hara-string"
                    {:input "test/documentation/hara_string.clj"
                     :title "hara.string"
                     :subtitle "methods for string manipulation"}
                    "hara-test"
                    {:input "test/documentation/hara_test.clj"
                     :title "hara.test"
                     :subtitle "easy to use test framework"}
                    "hara-time"
                    {:input "test/documentation/hara_time.clj"
                     :title "hara.time"
                     :subtitle "time as a clojure map"}
                    "hara-zip"
                    {:input "test/documentation/hara_zip.clj"
                     :title "hara.zip"
                     :subtitle "data traversal in style"}}}
  :jvm-opts []
  :global-vars {*warn-on-reflection* true}
  :java-source-paths ["java"]
  :jar-exclusions [#"^test\..+\.class"]
  :repack [{:type :clojure
            :levels 2
            :path "src"
            :standalone #{"reflect" "time" "event" "object" "test"}}])
