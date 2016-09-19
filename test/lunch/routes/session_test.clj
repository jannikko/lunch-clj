(ns lunch.routes.session-test
  (:import [clojure.lang ExceptionInfo])
  (:require [lunch.routes.session :refer :all]
            [lunch.db :refer [new-database]]
            [lunch.exceptions :refer :all]
            [lunch.test-fixtures :refer [general-fixtures]]
            [clojure.test :refer :all]))

(general-fixtures)

(defn response? [code res]
  (= (:status res) code))

(def db (new-database {:datasource "whatever"}))
(def good-request {:body {:id "1237192ASDLAS"}})
(def bad-request1 {:body {}})
(def bad-request2 {:body {:id nil}})
(def fake-uuid "6EB91B1B-702E-494F-AEE3-4C1F046E452E")

(deftest menu-routes
  (testing "session-routes"
    (testing "generate-session"
      ;; Test input validation
      (is (thrown? ExceptionInfo (generate-session bad-request1 db)))
      (is (thrown? ExceptionInfo (generate-session bad-request2 db)))
      (is (thrown? ExceptionInfo (generate-session nil db)))
      ;; Good request should return 201 and return new session non-blank-string
      (with-redefs-fn {#'lunch.routes.session/random-uuid        (constantly fake-uuid)
                       #'lunch.models.session/insert-session-id! (constantly nil)}
        #(do (is (response? 201 (generate-session good-request db)))
             (is (= fake-uuid (get-in (generate-session good-request db) [:headers "Location"]))))
        ))))



