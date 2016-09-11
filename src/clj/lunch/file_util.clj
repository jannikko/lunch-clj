(ns lunch.file-util 
  (:require [clojure.java.io :as io]))

;; TODO move to config 
(def file-storage-path "/Users/Jannik/lunchfiles/")

(defn generate-file-path [id]
  (str file-storage-path id))

(defn save-file [tmp-path dest-path]
      (io/copy tmp-path (io/file dest-path)))

(defn file-exists? [path]
  (.exists (io/as-file path)))
