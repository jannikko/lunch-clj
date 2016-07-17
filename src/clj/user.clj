(ns user
  (:require [ragtime.jdbc :as jdbc]
            [config.core :refer [env]]
	    [ragtime.repl :as repl]))

(defn load-config []
  ;; Move this to config file
  {:datastore  (jdbc/sql-database (:db-uri env))
   :migrations (jdbc/load-resources "migrations")})

(defn migrate []
  (repl/migrate (load-config)))

(defn rollback []
  (repl/rollback (load-config)))
