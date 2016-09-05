(ns lunch.models.menu
  (:require [yesql.core :refer [defqueries]]
            [config.core :refer [env]]))

(defqueries "lunch/models/sql/menu.sql"
   {:connection (:db-spec env)})

(defn exists? [id]
  (let [menu (find-by-id {:id id})]
         (not (empty? menu))))
