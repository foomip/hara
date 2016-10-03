(ns documentation.hara-concurrent)

[[:chapter {:title "concurrent.latch" :link "hara.concurrent.latch"}]]

"Add to `project.clj` dependencies:

    [im.chit/hara.concurrent.latch \"{{PROJECT.version}}\"]

**hara.concurrent.latch** supplies a simple primary/follower latch mechanism for atoms and ref such that if the primary is updated, then the followers will update as well"

[[:api {:namespace "hara.concurrent.latch" :title ""}]]

[[:chapter {:title "concurrent.notification" :link "hara.concurrent.notification"}]]

"Add to `project.clj` dependencies:

    [im.chit/hara.concurrent.notification \"{{PROJECT.version}}\"]

**hara.concurrent.notification** introduces a way to be notified of changes, based on this [post](http://stackoverflow.com/questions/13717161/are-there-any-good-libraries-or-strategies-for-testing-multithreaded-application)"

[[:api {:namespace "hara.concurrent.notification" :title ""}]]

[[:chapter {:title "concurrent.pipe" :link "hara.concurrent.pipe"}]]

"Add to `project.clj` dependencies:

    [im.chit/hara.concurrent.pipe \"{{PROJECT.version}}\"]

**hara.concurrent.pipe** provides a simple asynchronous pipe that can be sent tasks that are queued until previous tasks are complete"

[[:api {:namespace "hara.concurrent.pipe" :title ""}]]

[[:chapter {:title "concurrent.propagate"
            :link "hara.concurrent.propagate"
            :only ["cell" "link" "unlink"]}]]

"Add to `project.clj` dependencies:

    [im.chit/hara.concurrent.propagate \"{{PROJECT.version}}\"]

**hara.concurrent.propagate** is an implemention around the concept of [propagators](http://web.mit.edu/~axch/www/art.pdf), introduced by [Sussman](https://vimeo.com/12184930)"

[[:api {:namespace "hara.concurrent.propagate"
        :title ""
        :only ["cell" "link" "unlink"]}]]
