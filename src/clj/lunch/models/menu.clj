(ns lunch.models.menu
  (:require [yesql.core :refer [defqueries]]
            [clojure.spec :as s]))

(defqueries "lunch/models/sql/menu.sql")

(defn exists? [id conn]
  (let [menu (find-by-id {:id id} conn)]
    (not (empty? menu))))

(defn insert-link
  "Saves a file if it does not exist yet"
  ([link place-id conn]
   {:pre  [(s/valid? :lunch.routes.menu/link link)
           (s/valid? :lunch.routes.menu/id place-id)]
    :post [(s/valid? boolean? %)]}
   (if (exists? place-id conn)
     false
     (do (insert! {:id place-id :link link} conn)
         true))))
