(ns lunch.routes.menu
  (:import  [java.io IOException File]
            [java.sql SQLException])
  (:require [clojure.string :refer [blank? join]]
            [clojure.spec :as s]
            [clojure.tools.logging :as log]
            [ring.util.http-response :as res]
            [lunch.models.menu :as menu-model]
            [lunch.file-util :as futil]
            [lunch.exceptions :refer :all]
            [lunch.db :refer [get-conn]]
            [slingshot.slingshot :refer [throw+]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token* wrap-anti-forgery]]
            [compojure.core :refer :all]))

(def file-upload-error (ApplicationException 400 "File could not be uploaded, please try again"))

(s/def ::id (s/and string? (complement blank?)))
(s/def ::tempfile #(instance? File %))
(s/def ::file (s/keys :req-un [::tempfile]))
(s/def ::file-upload-params (s/keys :req-un [::id ::file]))

(defn menu-get [request]
  (res/ok request))

(defn menu-download
  "Handler for menu downloads, retrieves the filepath from the db and sends the file back"
  [id db] 
  (when (not (s/valid? ::id id))
    (log/warn (join " " [(s/explain ::id id) (str "Validation failed trying download file")]))
    (throw+ (ApplicationException 400 "Something went wrong when downloading the file")))
  (let [query-result (menu-model/find-by-id {:id id} (get-conn db))]
    (if (empty? query-result)
      (res/not-found)
      (let [filepath (:filepath query-result)]
          (res/file-response filepath)))))

(defn menu-upload 
  "Handler for menu uploads, saves the file if it does not already exist"
  [params db]
  (when (not (s/valid? ::file-upload-params params))
    (log/warn (join " " [(s/explain ::file-upload-params params) "Validation failed trying to upload file" params]))
    (throw+ (ApplicationException 400 "Something went wrong when uploading the file")))
  (if (s/valid? ::file-upload-params params)
    (let [file (:file params) place-id (:id params)]
      (try 
        (if (menu-model/insert-file-transactional file place-id db) 
          (res/created place-id)
          (res/conflict))
        (catch IOException e (do (log/warn (join " " ["Error writing file" file (.getMessage e)]))
                                 (throw+ file-upload-error)))
        (catch SQLException e (do (log/warn (join " " ["Error inserting entry into the databasee" file (.getMessage e)]))
                                  (throw+ file-upload-error)))))))


(defn handler
  [request db]
  (routes
    (GET "/:id" [request] (menu-get request))
    (GET "/:id/download" [id] (menu-download id db))
      (POST "/:id/upload" {params :params} (menu-upload params db))))
