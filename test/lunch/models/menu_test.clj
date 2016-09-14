(ns lunch.models.menu-test
  (:import [java.io File]
           [java.sql Connection])
  (:require [lunch.routes.menu :refer :all]
            [lunch.models.menu :as menu-model]
            [clojure.test :refer :all]))

;; Mock SQL with in-memory store
(def mock-db (atom []))
(def mock-exists (fn [id conn] (->> @mock-db (filter #(= (:id %) id)) empty? not)))
(def mock-insert! (fn [object conn] (swap! mock-db conj object)))

(def connection {:connection (proxy [Connection] [] (constantly nil))})

(def valid-link "www.example.com")
(def invalid-file {})

(deftest menu-routes
  (testing "menu-model"
    (testing "insert-file"
      (with-redefs-fn {#'lunch.models.menu/exists? mock-exists
                       #'lunch.models.menu/insert! mock-insert!}
        #(do
          (reset! mock-db [])
          (is (= true (menu-model/insert-link valid-link "12345" connection)))
          ;; Does not create the resource again if the id is the same
          (is (= false (menu-model/insert-link valid-link "12345" connection)))
          (is (= true (menu-model/insert-link valid-link "54321" connection)))
          (is (thrown? AssertionError (menu-model/insert-link invalid-file "54321" connection)))
          (is (thrown? AssertionError (menu-model/insert-link invalid-file nil connection)))
          (is (thrown? AssertionError (menu-model/insert-link invalid-file nil "not a connection")))
          )))))




