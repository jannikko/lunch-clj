(ns lunch.repl-util
  (:require [config.core :refer [env]]
            [lunch.db :refer [new-database]]))


(defn get-db []
  (.start (new-database (:db env))))
