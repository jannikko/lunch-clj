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


(defn get-connections
  [session-id]
  (get @connections session-id))

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

(defn register-connection
  [session-id conn]
  (let [registered-conns (get @connections session-id)
        updated-conns (conj registered-conns conn)]
    (swap! connections assoc session-id updated-conns)))


(defn register-session
  [place-id watch-function]
  (let [session-id (str (UUID/randomUUID))]
    (do
      (swap! connections assoc session-id [])
      (add-watch session-cache session-id (partial watch-function session-id))
      (send session-cache #(assoc % session-id {:place-id place-id :session-entries {}}))
      session-id)))

(defn registered?
  [session-id]
  (contains? @session-cache session-id))
