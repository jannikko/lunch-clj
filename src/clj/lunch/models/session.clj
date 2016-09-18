(ns lunch.models.session
  (:require [yesql.core :refer [defqueries]]
            [lunch.specs]
            [clojure.spec :as s]))

(defqueries "lunch/models/sql/session.sql")
