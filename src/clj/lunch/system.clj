(ns lunch.system
  (:require [config.core :refer [env]]
            [lunch.db :refer [new-database]]
            [lunch.server :refer [new-server]]
            [clojure.spec :refer [check-asserts]]
            [com.stuartsierra.component :as component])
  (:gen-class))

(defn system-config
  [config]
  (component/system-map
    :database (new-database config)
    :server (component/using
              (new-server config)
              [:database])))

(def system (system-config env))

(defn -main [& args]
  (do (check-asserts true)
      (component/start system)
      @(promise)))
