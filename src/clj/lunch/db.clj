(ns lunch.db
  (:import [java.sql Connection])
  (:require [hikari-cp.core :refer :all]
            [config.core :refer [env]]
            [clojure.spec :as s]
            [com.stuartsierra.component :as component]
            [clojure.java.jdbc :as jdbc]))

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
  (map->Database {:spec (:spec config)}))

(defn get-connection
  "Returns a jdbc connection object"
  [db]
  {:connection (jdbc/get-connection {:connection {:datasource (:datasource db)}})})

(defn with-transaction
  "Executes the provided function in a transactional context"
  [func conn]
  (jdbc/with-db-transaction [trans-conn conn]
                            (func {:connection trans-conn})))

