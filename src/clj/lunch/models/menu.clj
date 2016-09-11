(ns lunch.models.menu
  (:require [yesql.core :refer [defqueries]]
            [lunch.file-util :as futil]
            [lunch.db :as db]))

(defqueries "lunch/models/sql/menu.sql")

(defn exists? [id conn]
  (let [menu (find-by-id {:id id} conn)]
    (not (empty? menu))))

(defn insert-file
  "Saves a file if it does not exist yet"
  ([file place-id conn]
  (let [filepath (futil/generate-file-path place-id)]
    (if (exists? place-id conn)
      false
      (do (insert! {:id place-id :filepath filepath} conn)
          (futil/save-file (:tempfile file) filepath)
          true)))))

(defn insert-file-transactional
  "Saves a file transactional if it does not exist yet"
  [file place-id connection]
  (db/with-transaction (partial insert-file file place-id) connection))
