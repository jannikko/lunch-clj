(ns lunch.server
  (:require [lunch.handler :refer [handler]]
            [com.stuartsierra.component :as component]
            [ring.adapter.jetty :refer [run-jetty]]))

(defrecord Server [port database server]
  component/Lifecycle
  (start [component]
    (let [server (run-jetty (handler database) {:port port :join? false})]
      (assoc component :server server)))
  (stop [component]
    (.stop server)
    (assoc component :server nil)))

(defn new-server
  [config]
  (map->Server {:port (:port config)}))
