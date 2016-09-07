(ns lunch.models.menu
  (:require [yesql.core :refer [defqueries]]
            [lunch.file-util :as futil]
            [clojure.java.jdbc :as jdbc]
            [config.core :refer [env]]))

(def db-spec (:db-spec env))

(defqueries "lunch/models/sql/menu.sql"
   {:connection db-spec})

(defn exists? [id]
  (let [menu (find-by-id {:id id})]
         (not (empty? menu))))

(defn insert-file [file place-id]
  (let [filepath (futil/generate-file-path place-id)]
    (jdbc/with-db-transaction [conn db-spec]
      (insert! {:id place-id :filepath filepath} {:connection conn})
      (futil/save-file (:tempfile file) filepath))))
