(ns lunch.models.session
  (:require [yesql.core :refer [defqueries]]
            [lunch.shared-specs]
            [clojure.spec :as s]))

(defqueries "lunch/models/sql/session.sql")

