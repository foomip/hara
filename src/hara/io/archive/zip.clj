(ns hara.io.archive.zip
  (:require [hara.protocol.archive :as archive]
            [hara.io.environment :as env]
            [hara.io.file :as fs])
  (:import (java.nio.file Files
                          FileSystem
                          FileSystems
                          Paths)))

(env/init [{:java {:major 9 :minor 0}}]
          (:import [jdk.nio.zipfs ZipFileSystem]))

(env/init [{}
           {:java {:major 8}}]
          (:import [com.sun.nio.zipfs ZipFileSystem]))

(extend-protocol archive/IArchive
  ZipFileSystem
  (-url    [archive]
    (str archive))
  
  (-path    [archive entry]
    (.getPath archive (str entry) (make-array String 0)))
  
  (-list    [archive]
    (-> (archive/-path archive "/")
        (fs/select)))
  
  (-has?    [archive entry]
    (fs/exists? (archive/-path archive entry)))
  
  (-archive [archive root inputs]
    (->> inputs
         (map (juxt #(str (fs/relativize root %))
                    identity))
         (mapv (fn [[entry input]]
                 (archive/-insert archive (str entry) input)))))

  (-extract [archive output entries]
    (keep (fn [entry]
            (let [zip-path (archive/-path archive entry)
                  out-path (fs/path (str output) entry)]
              (when-not (fs/directory? zip-path)
                (fs/create-directory (fs/parent out-path))
                (fs/copy-single (archive/-path archive entry)
                                out-path
                                {:options [:replace-existing]}))))
          entries))

  (-insert  [archive entry input]
    (fs/copy-single (fs/path input)
                    (archive/-path archive entry)
                    {:options [:replace-existing]}))
  
  (-remove  [archive entry]
    (fs/delete (archive/-path archive entry)))
  
  (-stream  [archive entry]
    (fs/input-stream (archive/-path archive entry))))
