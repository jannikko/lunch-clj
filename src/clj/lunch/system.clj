(ns lunch.system
  (:require [config.core :refer [env]]
            [lunch.db :refer [new-database]]
            [lunch.server :refer [new-server]]
            [com.stuartsierra.component :as component]))

(defn system-config
  [config]
  (component/system-map
    :database (new-database (:db config))
    :server (component/using
              (new-server (:server config))
              [:database])))

(def system (system-config env))

(defn -main [& args]
  (component/start system))
