(ns scripts.doc-gen
  (:require [lucid.unit :as lucid]
            [lucid.publish :as publish]
            [lucid.publish.theme :as theme]
            [hara.io.project :as project]))

(def project-file "/Users/chris/Development/chit/hara/project.clj")

(def PROJECT (project/project project-file))

(comment
  (lucid/scaffold 'hara.zip PROJECT)
  (lucid/import 'hara.zip PROJECT)
  (lucid/import :all PROJECT)
  
  (publish/copy-assets (publish/load-settings {:refresh true} PROJECT) PROJECT)

  (doseq [key (->> PROJECT :publish :files keys sort)] 
    (println "PUBLISHING:" key)
    (publish/publish key {} PROJECT))
  
  (publish/publish "hara-reflect" {:refresh true} PROJECT)
  (publish/publish "hara-zip" {} PROJECT)
  (publish/publish "hara-data" {} PROJECT)
  (publish/publish "index" {} PROJECT)
  
  (doseq [m (map second (-> PROJECT
                            :publish
                            :files
                            seq
                            sort))]
    (println (format "- [hara.%s](hara-%s.html) - %s"
                     (:title m)
                     (:title m)
                     (:subtitle m)))))
