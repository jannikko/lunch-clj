(ns lunch.routes.menu
  (:import  [java.io IOException]
            [org.apache.commons.validator.routines UrlValidator]
            [java.sql SQLException])
  (:require [clojure.string :refer [blank? join]]
            [clojure.spec :as s]
            [clojure.tools.logging :as log]
            [ring.util.http-response :as res]
            [lunch.specs]
            [lunch.models.menu :as menu-model]
            [lunch.exceptions :refer :all]
            [lunch.db :refer [get-connection get-datasource]]
            [compojure.core :refer :all]))

(def url-validator (UrlValidator. (into-array ["http" "https"])))
(def link-upload-error (ApplicationException 500 "Link could not be uploaded, please try again"))

(s/def ::link (s/and string? #(.isValid url-validator %)))
(s/def ::id (s/get-spec :lunch.specs/non-blank-string))
(s/def ::params (s/keys :req-un [::id]))
(s/def ::body (s/keys :req-un [::link]))
(s/def ::menu-upload-request (s/keys :req-un [::params ::body]))

(defn menu-download
  "Handler for menu downloads, retrieves the link from the db and sends it back"
  [id db]
  {:pre [(s/assert :lunch.specs/non-blank-string id)]}
  (let [query-result (first (menu-model/find-by-id {:id id} (get-connection db)))]
    (if (empty? query-result)
      (res/not-found)
      (res/ok {:link (:link query-result)}))))

(defn menu-upload 
  "Handler for menu uploads, saves the link if it does not already exist"
  [request db]
  {:pre [(s/assert ::menu-upload-request request)]}
  (let [id (-> request :params :id) link (-> request :body :link)]
    (try
      (if (menu-model/insert-link link id (get-connection db))
        (res/created id)
        (res/conflict))
      (catch IOException e (do (log/warn (join " " ["Error writing link" id link (.getMessage e)]))
                               (throw link-upload-error)))
      (catch SQLException e (do (log/warn (join " " ["Error inserting entry into the databasee" id link (.getMessage e)]))
                                (throw link-upload-error))))))

(defn handler
  [db]
  (routes
    (GET "/:id/download" [id] (menu-download id db))
      (POST "/:id/upload" request (menu-upload request db))))
