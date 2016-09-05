(ns user
  (:require [ragtime.jdbc :as jdbc]
            [figwheel-sidecar.repl-api :as ra]
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

(defn start  []  (ra/start-figwheel!))

(defn stop  []  (ra/stop-figwheel!))

(defn cljs  []  (ra/cljs-repl "dev"))
