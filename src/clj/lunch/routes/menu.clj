(ns lunch.routes.menu
  (:import  [java.io IOException File]
            [java.sql SQLException])
  (:require [clojure.string :refer [blank? join]]
            [clojure.spec :as s]
            [lunch.models.menu :as menu-model]
            [clojure.tools.logging :as log]
            [ring.util.response :as res]
            [lunch.file-util :as futil]
            [lunch.exceptions :refer :all]
            [slingshot.slingshot :refer [throw+]]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token* wrap-anti-forgery]]
            [compojure.core :refer :all]))


(def file-upload-error (ApplicationException 400 "File could not be uploaded, please try again"))

(defn menu-download [id]
  (-> (res/response (str "You are viewing article: " id))
      (res/content-type "text/plain")))

(s/def ::id (s/and string? (complement blank?)))
(s/def ::tempfile #(instance? File %))
(s/def ::file (s/keys :req-un [::tempfile]))
(s/def ::file-upload-params (s/keys :req-un [::id ::file]))

(defn menu-upload 
  "Handler for menu uploads, saves the file if it does not already exist"
  [params]
  (if (s/valid? ::file-upload-params params)
    (let [file (:file params) place-id (:id params)]
      (try 
        (if (menu-model/exists? place-id)
          (res/response "Already uploaded")
          (let [filepath (futil/save-file (:tempfile file) place-id)]
            (menu-model/insert! {:id place-id :filepath filepath})
            (res/created place-id)))
        (catch IOException e (do (log/error (str "Error writing file " file " " (.getMessage e)))
                                 (throw+ file-upload-error)))
        (catch SQLException e (do (log/warn (str "Error inserting entry into the database " file " " (.getMessage e)))
                                  (throw+ file-upload-error)))))
    (do (log/warn (join " " [(s/explain ::file-upload-params params) (str "Validation failed trying to upload file " params)]))
        (throw+ (ApplicationException 400 "Something went wrong when uploading the file")))))


(defn handler [request]
  (routes
    (GET "/:id" [id] (menu-download id))
    (GET "/:id/download" request (menu-upload request))
      (POST "/:id/upload" {params :params} (menu-upload params))))
