(ns user
  (:require [com.stuartsierra.component :as component]
            [clojure.tools.namespace.repl :refer [refresh]]
            [clojure.spec :refer [check-asserts]]
            [lunch.system :as app]))

(def system nil)

(defn init []
  (alter-var-root #'system
    (constantly app/system)))

(defn start []
  (check-asserts true)
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system
    (fn [s] (when s
              (check-asserts true)
              (component/stop s)))))

(defn go []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go))
