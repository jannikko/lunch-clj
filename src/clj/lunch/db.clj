(ns lunch.db
  (:require [hikari-cp.core :refer :all]
            [config.core :refer [env]]
            [clojure.spec :as s]
            [com.stuartsierra.component :as component]
            [clojure.java.jdbc :as jdbc]))

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

(defn with-transaction 
  "Executes the provided function in a transactional context"
  [func db] 
  (jdbc/with-db-transaction [conn {:datasource (:datasource db)}]
    (func {:connection conn})))

(defn get-conn
  [db]
  {:connection {:datasource (:datasource db)}})
