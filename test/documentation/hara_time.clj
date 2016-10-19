(ns documentation.hara-time
  (:use hara.test)
  (:require [hara.time :as t]
            [hara.time.data
             [common :as common]
             [map :as map]])
  (:import [java.util Date TimeZone Calendar]))

[[:chapter {:title "Introduction"}]]

"[hara.time](https://github.com/zcaudate/hara/blob/master/src/hara/time.clj) is a unified framework for representating time on the JVM."

[[:section {:title "Installation"}]]

"Add to `project.clj` dependencies:

    [im.chit/hara.time \"{{PROJECT.version}}\"]

All functionality is contained in the `hara.time` namespace."

(comment
  (require '[hara.time :as t]))

[[:section {:title "Motivation"}]]

"`hara.time` provides a compact interface for dealing with the different representation of time available on the jvm. The library sticks to the following principles of how an interface around dates should be exposed:

- it should be consistent, so that there may be a common language between all time implementions.
- it should be extensible, so that new implemention can be added easily
- it should be simple and clear, to have easy to use functions and for interfactions between time objects to be seamless

Currently there are a couple of implementions for time on the JVM:

- the < jdk1.8 options for time: `java.util.Date`, `java.util.Calendar`, `java.sql.Timestamp`
- the < jdk1.8 defacto standard: the [joda-time](http://www.joda.org/joda-time/) package
- the new jdk1.8 `java.time` library

Clojure libraries for time are:

- [clj-time](https://github.com/clj-time/clj-time) wraps joda and is the standard for dealing with time in clojure
- [clojure.joda-time](https://github.com/dm3/clojure.joda-time) is another wrapper around joda time
- [clojure.java-time](https://github.com/dm3/clojure.java-time) is a wrapper around the jdk1.8 `java.time` package.
- [duckling](https://github.com/wit-ai/duckling) is a super amazing library for temporal expressions

`hara.time` comes at the problem by providing a core set of operations and representations of time, allowing for many different implementions of time to speak the same language."
[[:chapter {:title "Index"}]]

[[:api {:title ""
        :namespace "hara.time"
        :exclude ["duration?"
                  "instant?"
                  "representation"
                  "representation?"
                  "time-meta"
                  "to-length"
                  "wrap-proxy"]
        :display #{:tags}}]]

[[:chapter {:title "API"}]]

[[:section {:title "Representation"}]]

[[:api {:namespace "hara.time"
        :title ""
        :only ["now" "epoch" "default-type" "default-timezone"]}]]

"We can start off with the easiest call:"

(t/now)
;;=> {:day 4, :hour 14, :timezone "Asia/Kolkata",
;;    :long 1457081866919, :second 46, :month 3,
;;    :type java.util.Date, :year 2016, :millisecond 919, :minute 27}

"Note that `now` returns a clojure map representing the current time. This is the default type, but we can also specify that we want a `java.util.Date` object"

(t/now {:type java.util.Date})
;;=> #inst "2016-03-04T08:57:46.919-00:00"

"If on Java version 1.8, the use of `:type` can set the returned object to be of type `java.time.Instant`."

(t/now {:type java.time.Instant})
;;=> #<Instant 2016-03-04T08:58:11.678Z>

"The default timezone can also be accessed and modified through `default-timezone`"

(t/default-timezone)
;;=> "Asia/Kolkata"

"The default type can be accessed and modified through `default-type`:"

(t/default-type)
;;=> clojure.lang.PersistentArrayMap

[[:section {:title "Supported Types"}]]

"Currently `hara.time` supports the following time representations

- `java.lang.Long`
- `java.util.Date`
- `java.util.Calendar`
- `java.sql.Timestamp`
- `java.time.Instant`
- `java.time.Clock`
- `org.joda.time.DateTime` (when required)
"
"Changing the `default-type` to Calendar will immediately affect the `now` function to return a `java.util.Calendar` object"

(t/default-type java.util.Calendar)

(t/now)
;;=> #inst "2016-03-04T14:28:39.481+05:30"

(type (t/now))
;;=> java.util.GregorianCalendar

"And again, a change of type will result in another representation"

(t/default-type java.time.ZonedDateTime)

(t/now)
;;=> #<ZonedDateTime 2016-03-04T15:41:17.901+05:30[Asia/Kolkata]>

(type (t/now))
;;=> java.time.ZonedDateTime

[[:section {:title "Date as Data"}]]

"`hara.time` has two basic concepts of time:

- time as an absolute value (long)
- time as a representation in a given context (map)"

"These concepts can also be set as the default type, for example, we now set `Long` as the default type:"

(t/default-type Long)

(t/now)
;;=> 1457086323250

"As well as a map as the default type:"

(t/default-type clojure.lang.PersistentArrayMap)

(t/now)
;;=> {:day 4, :hour 14, :timezone "Asia/Kolkata",
;;    :second 0, :day-of-week 6, :month 3,
;;    :year 2016, :millisecond 611, :minute 33}

"A specific timezone can be passed in and this is the same for all supported time objects:"

(t/now {:timezone "GMT"})
;;=> {:day 4, :hour 9, :timezone "GMT",
;;    :second 13, :day-of-week 6, :month 3,
;;    :year 2016, :millisecond 585, :minute 4}

[[:section {:title "Coercion"}]]

"Any of the dates can be coerced to and from each other. This is made possible by `coerce`. map and long representations of time provide the two most basic forms. `from-map`, `to-map`, `from-long` and `to-long` can be used to convert any datetime instance to a map/long as well as back again."

[[:api {:namespace "hara.time"
        :title ""
        :only ["coerce" "to-long" "to-map" "from-map" "from-long"]}]]

[[:section {:title "Timezones"}]]

"There are additional methods for dealing with timezones, as some datatime objects support timezones but others do not."

[[:api {:namespace "hara.time"
        :title ""
        :only ["has-timezone?" "get-timezone" "with-timezone"]}]]

[[:section {:title "Format and Parsing"}]]

"Dates can be formatted and parsed using the following methods:"

[[:api {:namespace "hara.time"
        :title ""
        :only ["format" "parse"]}]]

[[:section {:title "Extensiblity"}]]

"Because the API is based on protocols, it is very easy to extend. For an example of how other date libraries can be added to the framework, please see [hara.time.joda](https://github.com/zcaudate/hara/blob/master/src/hara/time/joda/) for how [joda-time](http://www.joda.org/joda-time/) was added."

[[:section {:title "Accessors"}]]

"Date accessors are provided to access singular values of time, as well vector representation for selected fields"

[[:api {:namespace "hara.time"
        :title ""
        :only ["year" "month" "day" "day-of-week" "hour" "minute" "second" "millisecond" "to-vector"]}]]

[[:section {:title "Operations"}]]

"Date can be compared and manipulated according to the following functions:"

[[:api {:namespace "hara.time"
        :title ""
        :only ["plus" "minus" "equal" "before" "after" "latest" "earliest" "adjust" "truncate"]}]]
