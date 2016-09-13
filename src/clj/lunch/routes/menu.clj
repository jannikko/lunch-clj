(ns lunch.routes.menu
  (:import  [java.io IOException File]
            [java.sql SQLException])
  (:require [clojure.string :refer [blank? join]]
            [clojure.spec :as s]
            [clojure.tools.logging :as log]
            [ring.util.http-response :as res]
            [lunch.models.menu :as menu-model]
            [lunch.exceptions :refer :all]
            [lunch.db :refer [get-connection]]
            [slingshot.slingshot :refer [throw+]]
            [compojure.core :refer :all]))

(def file-upload-error (ApplicationException 500 "File could not be uploaded, please try again"))

(s/def ::id (s/and string? (complement blank?)))
(s/def ::tempfile #(instance? File %))
(s/def ::file (s/keys :req-un [::tempfile]))
(s/def ::file-upload-params (s/keys :req-un [::id ::file]))

(defn menu-get [request]
  (res/ok request))

(defn menu-download
  "Handler for menu downloads, retrieves the filepath from the db and sends the file back"
  [id db]
  {:pre [(s/valid? ::id id)]}
  (let [query-result (menu-model/find-by-id {:id id} (get-connection db))]
    (if (empty? query-result)
      (res/not-found)
      (let [filepath (:filepath query-result)]
          (res/file-response filepath)))))

(defn menu-upload 
  "Handler for menu uploads, saves the file if it does not already exist"
  [params db]
  {:pre [(s/valid? ::file-upload-params params)]}
  (let [file (:file params) place-id (:id params)]
    (try
      (if (menu-model/insert-file-transactional file place-id (get-connection db))
        (res/created place-id)
        (res/conflict))
      (catch IOException e (do (log/warn (join " " ["Error writing file" file (.getMessage e)]))
                               (throw+ file-upload-error)))
      (catch SQLException e (do (log/warn (join " " ["Error inserting entry into the databasee" file (.getMessage e)]))
                                (throw+ file-upload-error))))))


(defn handler
  [db]
  (routes
    (GET "/:id" [request] (menu-get request))
    (GET "/:id/download" [id] (menu-download id db))
      (POST "/:id/upload" {params :params} (menu-upload params db))))
