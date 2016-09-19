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
            [lunch.specs]
            [lunch.models.session :as session-model]
            [ring.util.http-response :as res]
            [compojure.core :refer :all]))


(defn random-uuid [] (UUID/randomUUID))
(def generate-session-error (ApplicationException 500 "Error trying to generate-session a new session, please try again later"))

(s/def :lunch.routes.session.generate/id (s/get-spec :lunch.specs/non-blank-string))
(s/def :lunch.routes.session.generate/body (s/keys :req-un [:lunch.routes.session.generate/id]))
(s/def :lunch.routes.session.generate/request (s/keys :req-un [:lunch.routes.session.generate/body]))

(defn generate-session
  [request db]
  {:pre [(s/assert :lunch.routes.session.generate/request request)]}
  (let [place-id (get-in request [:body :id]) uuid (random-uuid)]
    (try (let [conn (get-connection db)]
           (session-model/insert-session-id! {:id uuid :place_id place-id} conn)
           (res/created (str uuid)))
         (catch IOException e (do (log/error (join " " ["Connection error writing session non-blank-string into db for" place-id (.getMessage e)]))
                                  (throw generate-session-error)))
         (catch SQLException e (do (log/error (join " " ["Error inserting session into the databasee" place-id (.getMessage e)]))
                                   (throw generate-session-error))))))

(defn get-session
  [id db]
  (try
    (let [session-id (UUID/fromString id)
          sessions (session-model/find-session-entries {:session_id session-id} (get-connection db))]
      (if (some? sessions)
        (res/ok {:id id :session_entries (->> sessions (map #(select-keys % [:name :lunch_order])))})
        (res/not-found)))
    (catch IllegalArgumentException _ (throw (ex-info "Not a valid uuid" {:id id})))
    (catch IOException e (do (log/error (join " " ["Connection error selecting uuid from table" id (.getMessage e)]))
                             (throw generate-session-error)))
    (catch SQLException e (do (log/error (join " " ["Error selecting uuid from table" id (.getMessage e)]))
                              (throw generate-session-error)))))

;; Move these into view object / protocol
(s/def :lunch.routes.session.upload/lunch_order (s/conformer string?))
(s/def :lunch.routes.session.upload/name (s/conformer string?))
(s/def :lunch.routes.session.upload/id (s/get-spec number?))
(s/def :lunch.routes.session.upload/body (s/keys :req-un [:lunch.routes.session.upload/lunch_order
                                                         :lunch.routes.session.upload/name]
                                                :opt-un [:lunch.routes.session.uploads/id]))
(s/def :lunch.routes.session.upload/params (s/keys :req-un [:lunch.routes.session.upload/id]))
(s/def :lunch.routes.session.upload/request (s/keys :req-un [:lunch.routes.session.upload/body :lunch.routes.session.upload/params]))

(defn upload-session-entry
  [{:keys [body params] :as request} db]
  {:pre [(s/assert :lunch.routes.session.upload/request request)]}
  (try
    (let [session-id (UUID/fromString (:id params))
          {:keys [id name lunch_order]} body]
      (if-not (nil? id)
        ;; Update the existing entry
        (do (session-model/update-session-entry! {:id id :name name :lunch_order lunch_order} (get-connection db))
            (res/ok))
        ;; Create a new entry
        (let [entry-id (session-model/insert-session-entry<! {:name name :lunch_order lunch_order :session_id session-id} (get-connection db))]
          (res/created (:id entry-id)))))
    (catch IllegalArgumentException e (do (log/error (.getMessage e))
                                          (throw (ex-info "Invalid UUID" {}))))
    (catch IOException e (do (log/error (join " " ["Connection error selecting uuid from table" (.getMessage e)]))
                             (throw generate-session-error)))
    (catch SQLException e (do (log/error (join " " ["Error selecting uuid from table" (.getMessage e)]))
                              (throw generate-session-error)))))

(defn handler
  [db]
  (routes
    (POST "/generate" request (generate-session request db))
    (GET "/get-session" [id] (get-session id db))
    (PUT "/upload-session-entry" request (upload-session-entry request db))))
