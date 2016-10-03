(ns scripts.doc-gen
  (:require [lucid.test :as lucid]
            [lucid.publish :as publish]
            [lucid.publish.theme :as theme]
            [hara.io.project :as project]))

(def project-file "/Users/chris/Development/chit/hara/project.clj")

(def PROJECT (project/project project-file))

(comment
  (lucid/scaffold 'hara.zip PROJECT)
  (lucid/import 'hara.zip PROJECT)
  (lucid/import :all PROJECT)
  
  (publish/copy-assets  PROJECT)
  
  (publish/publish "hara-zip" {:theme "stark"} PROJECT)
  
  )
