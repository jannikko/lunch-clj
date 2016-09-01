(ns lunch.file-util (:require [clojure.java.io :as io]))

;; TODO move to config 
(def file-storage-path "/Users/Jannik/lunchfiles/")

(defn generate-file-path [id]
  (str file-storage-path id))

(defn save-file [file-path file-id]
    (io/copy file-path (io/file (generate-file-path file-id))))
