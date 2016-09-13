(ns lunch.file-util
  (:require [clojure.java.io :as io]))

;; TODO move to config 
(def file-storage-path "/Users/Jannik/lunchfiles/")

(defn generate-file-path
  "Generates a file-path for the given id"
  [id]
  (str file-storage-path id))

(defn save-file
  "Copies a file from a temporary path to a specified destination path"
  [tmp-path dest-path]
  (io/copy tmp-path (io/file dest-path)))

(defn file-exists? [path]
  (.exists (io/as-file path)))
