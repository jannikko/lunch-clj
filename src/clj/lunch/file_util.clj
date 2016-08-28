(ns lunch.file-util (:require [clojure.java.io :as io]))

;; TODO move to config 
(def file-storage-path "/var/lib/lunch/")

(defn generate-file-path [id]
  (str file-storage-path id))

(defn save-file [file file-id]
  (let [file-path (generate-file-path file-id)
        tempfile (:tempfile file)]
    (io/copy tempfile (io/file (generate-file-path file-id))))
  file-path)
