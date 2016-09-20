(ns lunch.models.session
  (:import [java.util UUID]
           [java.util.concurrent.atomic AtomicInteger])
  (:require [yesql.core :refer [defqueries]]
            [lunch.specs]
            [manifold.stream :as stream]
            [clojure.tools.logging :as log]
            [lunch.db :refer [get-connection]]))

(defqueries "lunch/models/sql/session.sql")

(def session-cache (agent {}))
(def connections (atom {}))
(def counter (AtomicInteger.))

(defn register-connection
  [session-id conn]
  (let [registered-conns (get @connections session-id)
        updated-conns (conj registered-conns conn)]
    (swap! connections assoc session-id updated-conns)))

(defn watch-function
  [session-id _key _ref old-value new-value]
  (let [registered-conns (get @connections session-id)]
    (log/debug "new value" new-value)
    (doseq [conn registered-conns] (stream/put! conn (pr-str new-value)))))

(defn read-cache
  [session-id]
  (get @session-cache session-id))

(defn update-cache
  [{:keys [session-id entry-id name lunch-order]}]
  (when (contains? @session-cache session-id)
    (send session-cache #(assoc-in % [session-id :session-entries entry-id] {:name name :lunch-order lunch-order}))))

(defn insert-cache
  [{:keys [session-id name lunch-order]}]
  (when (contains? @session-cache session-id)
    (let [count (.incrementAndGet counter)]
      (send session-cache #(assoc-in % [session-id :session-entries count] {:name name :lunch-order lunch-order}))
      count)))

(defn register-session
  [place-id db]
  (let [session-id (str (UUID/randomUUID))]
    (do
      (send session-cache #(assoc % session-id {:place-id place-id :session-entries {}}))
      (add-watch session-cache session-id (partial watch-function session-id))
      (swap! connections assoc session-id [])
      session-id)))

(defn registered?
  [session-id]
  (contains? @session-cache session-id))
