(ns lunch.routes.session
  (:require [clojure.string :refer [blank? join]]
            [clojure.spec :as s]
            [clojure.edn :as edn]
            [clojure.tools.logging :as log]
            [clojure.core.async :as a :refer [<! go]]
            [manifold.stream :as stream]
            [manifold.deferred :as d]
            [aleph.http :as http]
            [lunch.views.session :as view]
            [lunch.exceptions :refer :all]
            [lunch.db :refer [get-connection get-datasource]]
            [lunch.specs]
            [lunch.models.session :as session-model]
            [ring.util.http-response :as res]
            [compojure.core :refer :all]))

(defn schedule-session-removal
  [session-id]
  (go
    (<! (a/timeout (-> 1000 (* 3600) (* 24))))
    (doseq [conn (-> session-id (session-model/get-connections) (vals))]
      (when-not (stream/closed? conn) (stream/close! conn)))
    (session-model/delete-session session-id)))

(defn watch-function
  [session-id _key _ref old-value new-value]
  (let [registered-conns (session-model/get-connections session-id)
        session (get new-value session-id)
        entries (view/render-entries session)]
    (doseq [conn (vals registered-conns)] (stream/put! conn (pr-str entries)))))

(s/def ::session-id string?)
(s/def ::place-id (s/get-spec :lunch.specs/non-blank-string))

(s/def :lunch.routes.session.generate/body (s/keys :req-un [::place-id]))
(s/def :lunch.routes.session.generate/request (s/keys :req-un [:lunch.routes.session.generate/body]))

(defn generate-session
  [request db]
  {:pre [(s/assert :lunch.routes.session.generate/request request)]}
  (let [place-id (get-in request [:body :place-id])
        session-id (session-model/register-session place-id watch-function)]
    (schedule-session-removal session-id)
    (res/created (str session-id))))

(defn get-session-metadata
  [session-id]
  {:pre [(s/assert ::session-id session-id)]}
  (if (session-model/registered? session-id)
    (let [session (session-model/read-cache session-id)]
      (res/ok (view/render-metadata session)))
    (res/not-found)))

;; Move these into view object / protocol
(s/def :lunch.routes.session.upload/lunch-order (s/conformer string?))
(s/def :lunch.routes.session.upload/name (s/conformer string?))
(s/def :lunch.routes.session.upload/entry-id (s/conformer number?))
(s/def :lunch.routes.session.upload/session-entry (s/keys :req-un [:lunch.routes.session.upload/lunch-order
                                                                   :lunch.routes.session.upload/name]
                                                          :opt-un [:lunch.routes.session.uploads/entry-id]))

(defn upload-session-entry
  [{:keys [session-id entry-id name lunch-order] :as request}]
  {:pre [(s/assert :lunch.routes.session.upload/session-entry request)]}
  (try
    (if-not (session-model/registered? session-id)
      (res/not-found)
      (if (nil? entry-id)
        (session-model/insert-cache {:name name :lunch-order lunch-order :session-id session-id})
        (session-model/update-cache {:session-id session-id :name name :lunch-order lunch-order :entry-id entry-id})))))


(s/def :lunch.routes.session.connect/params (s/keys :req-un [::session-id]))
(s/def :lunch.routes.session.connect/request (s/keys :req-un [:lunch.routes.session.upload/params]))

(defn connect
  [request]
  {:pre [(s/assert :lunch.routes.session.connect/request request)]}
  (let [session-id (-> request :params :session-id)]
    (if-not (session-model/registered? session-id)
      (res/not-found)
      (d/let-flow [conn (d/catch (http/websocket-connection request) (fn [_] (do (log/debug "catch handler called") nil)))]
                  (if-not conn
                    ;; if it wasn't a valid websocket handshake, return an error
                    (res/internal-server-error "Error establishing websocket connection.")
                    ;; otherwise, take the first two messages, which give us the chatroom and name
                    (do (stream/put! conn (-> session-id session-model/read-cache view/render-entries pr-str))
                        (session-model/register-connection session-id conn)
                        (stream/consume #(upload-session-entry (-> % (edn/read-string) (assoc :session-id session-id))) conn)
                        (res/ok))
                    )))))

(defn handler
  [db]
  (routes
    (POST "/generate" request (generate-session request db))
    (GET "/:session-id" [session-id] (get-session-metadata session-id))
    (GET "/:session-id/connect" request (connect request))
    (PUT "/:session-id" request (upload-session-entry request))))
