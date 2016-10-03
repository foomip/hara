(ns documentation.hara-zip
  (:use hara.test)
  (:require [hara.zip :as zip]))

[[:chapter {:title "Introduction"}]]

"
[hara.zip](https://github.com/zcaudate/hara/blob/master/src/hara/zip.clj) provides a customisable zipper implementation"

[[:section {:title "Installation"}]]

"
Add to `project.clj` dependencies:

    [im.chit/hara.zip \"{{PROJECT.version}}\"]
    
All functionality is found contained in the `hara.zip` namespace"

(comment (require '[hara.zip :as zip]))

[[:chapter {:title "API" :link "hara.zip"}]]

[[:api {:namespace "hara.zip" :title ""}]]
