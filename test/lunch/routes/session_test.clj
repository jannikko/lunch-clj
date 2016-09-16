(ns lunch.routes.session-test
  (:require [lunch.routes.session :refer :all]
            [lunch.db :refer [new-database]]
            [lunch.exceptions :refer :all]
            [slingshot.test]
            [clojure.test :refer :all]))

(defn response? [code res]
  (= (:status res) code))

(def db (new-database {:datasource "whatever"}))
(def good-request {:body {:place-id "1237192ASDLAS"}})
(def bad-request1 {:body {}})
(def bad-request2 {:body {:place-id nil}})
(def fake-uuid "6EB91B1B-702E-494F-AEE3-4C1F046E452E")

(deftest menu-routes
  (testing "session-routes"
    (testing "generate"
      ;; Test input validation
      (is (thrown? AssertionError (generate bad-request1 db)))
      (is (thrown? AssertionError (generate bad-request2 db)))
      (is (thrown? AssertionError (generate nil db)))
      ;; Good request should return 201 and return new session id
      (with-redefs-fn {#'lunch.routes.session/generate-uuid (constantly fake-uuid)
                       #'lunch.models.session/insert-session-id! (constantly nil)}
        #(do (is (response? 201 (generate good-request db)))
          (is (= fake-uuid (get-in (generate good-request db) [:headers "Location"]))))
        ))))



