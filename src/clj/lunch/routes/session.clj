(ns lunch.routes.session
  (:import [java.util UUID]
           [java.io IOException]
           [java.sql SQLException])
  (:require [clojure.string :refer [blank? join]]
            [clojure.java.jdbc :as jdbc]
            [clojure.spec :as s]
            [clojure.tools.logging :as log]
            [lunch.exceptions :refer :all]
            [lunch.db :refer [get-connection get-datasource]]
            [lunch.shared-specs]
            [lunch.models.session :as session-model]
            [ring.util.http-response :as res]
            [slingshot.slingshot :refer [throw+]]
            [compojure.core :refer :all]))


(defn generate-uuid []
  (UUID/randomUUID))

(s/def ::body (s/keys :req-un [:lunch.shared-specs/place-id]))
(s/def ::generate-session-request (s/keys :req-un [::body]))

(def generate-session-error (ApplicationException 500 "Error trying to generate a new session, please try again later"))

(defn generate
  [request db]
  {:pre [(s/valid? ::generate-session-request request)]}
  (let [place-id (get-in request [:body :place-id])
        uuid (generate-uuid)
        conn (get-connection db)]
    (try (do (session-model/insert-session-id! {:id uuid :place_id place-id} conn)
             (res/created (str uuid)))
         (catch IOException e (do (log/error (join " " ["Connection error writing session id into db for" place-id (.getMessage e)]))
                                  (throw+ generate-session-error)))
         (catch SQLException e (do (log/error (join " " ["Error inserting session into the databasee" place-id (.getMessage e)]))
                                   (throw+ generate-session-error))))))


(defn handler
  [db]
  (routes
    (POST "/generate" request (generate request db))))
