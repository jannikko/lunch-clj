(ns lunch.models.place
  (:require [clojure.java.jdbc :as sql]))

(def spec (or (System/getenv "DATABASE_URL")
              "postgresql://localhost:5432/lunch"))

(defn all []
  (into [] (sql/query spec ["select filepath from lunch"])))
