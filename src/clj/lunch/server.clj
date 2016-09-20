(ns lunch.server
  (:require [lunch.handler :refer [handler]]
            [com.stuartsierra.component :as component]
            [aleph.http :as http]))

(defrecord Server [port database server]
  component/Lifecycle
  (start [component]
    (let [server (http/start-server (handler database) {:port port})]
      (assoc component :server server)))
  (stop [component]
    (.close server)
    (assoc component :server nil)))

(defn new-server
  [config]
  (map->Server {:port (:port config)}))
