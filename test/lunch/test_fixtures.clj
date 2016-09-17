(ns lunch.test-fixtures
  (:require [clojure.test :refer [use-fixtures]]
            [clojure.spec :as s]))

(defn one-time-setup []
  (do (s/check-asserts true)))

(defn one-time-teardown []
  (s/check-asserts false))

(defn once-fixture [f]
  (one-time-setup)
  (f)
  (one-time-teardown))

(defn general-fixtures [] (use-fixtures :once once-fixture))