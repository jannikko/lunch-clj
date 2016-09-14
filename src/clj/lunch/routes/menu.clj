(ns lunch.routes.menu
  (:import  [java.io IOException]
            [java.sql SQLException])
  (:require [clojure.string :refer [blank? join]]
            [clojure.spec :as s]
            [clojure.tools.logging :as log]
            [ring.util.http-response :as res]
            [lunch.models.menu :as menu-model]
            [lunch.exceptions :refer :all]
            [lunch.db :refer [get-connection get-datasource]]
            [slingshot.slingshot :refer [throw+]]
            [compojure.core :refer :all]))

(def link-upload-error (ApplicationException 500 "Link could not be uploaded, please try again"))

(s/def ::id (s/and string? (complement blank?)))
(s/def ::link (s/and string? (complement blank?)))
(s/def ::menu-upload-params (s/keys :req-un [::id]))
(s/def ::menu-upload-body (s/keys :req-un [::link]))
(s/def ::menu-upload-request (s/keys :req-un [::params ::body]))

(defn menu-get [request]
  (res/ok request))

(defn menu-download
  "Handler for menu downloads, retrieves the link from the db and sends it back"
  [id db]
  {:pre [(s/valid? ::id id)]}
  (let [query-result (first (menu-model/find-by-id {:id id} (get-connection db)))]
    (if (empty? query-result)
      (res/not-found)
      (res/ok {:link (:link query-result)}))))

(defn menu-upload 
  "Handler for menu uploads, saves the link if it does not already exist"
  [request db]
  {:pre [(s/valid? ::menu-upload-request request)]}
  (let [id (-> request :params :id) link (-> request :body :link)]
    (try
      (if (menu-model/insert-link link id (get-connection db))
        (res/created)
        (res/conflict))
      (catch IOException e (do (log/warn (join " " ["Error writing link" id link (.getMessage e)]))
                               (throw+ link-upload-error)))
      (catch SQLException e (do (log/warn (join " " ["Error inserting entry into the databasee" id link (.getMessage e)]))
                                (throw+ link-upload-error))))))


(defn handler
  [db]
  (routes
    (GET "/:id" [request] (menu-get request))
    (GET "/:id/download" [id] (menu-download id db))
      (POST "/:id/upload" request (menu-upload request db))))
