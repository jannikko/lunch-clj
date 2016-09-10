(ns lunch.models.db
  (:require [hikari-cp.core :refer :all]
            [config.core :refer [env]]
            [clojure.java.jdbc :as jdbc]))

(def db-spec (:db-spec env))

(def datasource (make-datasource db-spec))

(defn with-transaction 
  "Executes the provided function in a transactional context"
  [func] 
  (jdbc/with-db-transaction [conn {:datasource datasource}]
    (func {:connection conn})))
