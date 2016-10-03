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

  (doseq [key (->> PROJECT :publish :files keys sort)] 
    (println "PUBLISHING:" key)
    (publish/publish key {:theme "bolton"} PROJECT))
  
  (publish/publish "hara-zip" {} PROJECT)
  (publish/publish "hara-data" {} PROJECT)
  (publish/publish "index" {:refresh true} PROJECT)
  
  )
