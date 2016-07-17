(ns user
  (:require [ragtime.jdbc :as jdbc]
	    [ragtime.repl :as repl]))

(defn load-config []
  {:datastore  (jdbc/sql-database "postgresql://lunch:lunch@localhost/lunch")
   :migrations (jdbc/load-resources "migrations")})

(defn migrate []
  (repl/migrate (load-config)))

(defn rollback []
  (repl/rollback (load-config)))
