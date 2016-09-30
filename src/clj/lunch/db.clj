(ns lunch.db
  (:import [java.sql Connection])
  (:require [hikari-cp.core :refer :all]
            [config.core :refer [env]]
            [clojure.spec :as s]
            [com.stuartsierra.component :as component]
            [clojure.java.jdbc :as jdbc])
  (:gen-class))

(s/def ::connection #(instance? Connection %))
(s/def ::valid-connection (s/keys :req-un [::connection]))

(defrecord Database [spec datasource]
  component/Lifecycle
  (start [this]
    (let [datasource (make-datasource spec)]
      (assoc this :datasource datasource)))
  (stop [this]
    (close-datasource datasource)
    (assoc this :datasource nil)))

(defn new-database
  [config]
  (map->Database {:spec {:maximum-pool-size (:db-maximum-pool-size config)
                         :adapter           (:db-adapter config)
                         :username          (:db-username config)
                         :password          (:db-password config)
                         :database-name     (:db-name config)
                         :server-name       (:db-server-name config)
                         :port-number       (:db-port-number config)}}))

(defn get-datasource
  [db]
  {:datasource (:datasource db)})

(defn get-connection
  "Returns a jdbc connection object"
  [db]
  {:connection (jdbc/get-connection {:connection (get-datasource db)})})
