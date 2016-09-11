(ns lunch.models.menu-test
  (:import [java.io File]
           [java.sql Connection])
  (:require [lunch.routes.menu :refer :all]
            [lunch.models.menu :as menu-model]
            [ring.mock.request :as mock]
            [lunch.file-util :as futil]
            [clojure.test :refer :all]))

;; Mock SQL with in-memory store
(def mock-db (atom []))
(def mock-exists (fn [id conn] (->> @mock-db (filter #(= (:id %) id)) empty? not)))
(def mock-insert! (fn [object conn] (swap! mock-db conj object)))
(def mock-save-file (fn [file filepath] ()))

(def connection {:connection (reify Connection)})
(def test-file (File. "/tmp"))

(def valid-file {:tempfile test-file})

(deftest menu-routes
  (testing "menu-model"
    (testing "insert-file"
      (with-redefs-fn {#'lunch.models.menu/exists? mock-exists
                       #'lunch.models.menu/insert! mock-insert!
                       #'lunch.file-util/save-file mock-save-file}
      #(do
         (reset! mock-db [])
         (is (= true (menu-model/insert-file valid-file "12345" connection)))
         (is (= false (menu-model/insert-file valid-file "12345" connection)))
         (is (= true (menu-model/insert-file valid-file "54321" connection)))
        )))))




