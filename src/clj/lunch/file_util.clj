(ns lunch.file-util (:require [clojure.java.io :as io]))

;; TODO move to config 
(def file-storage-path "/var/lib/lunch/")

(defn generate-file-path [id]
  (str file-storage-path id))

(defn save-file [temp-file file-id]
    (io/copy temp-file (io/file (generate-file-path file-id))))
