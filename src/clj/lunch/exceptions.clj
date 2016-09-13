(ns lunch.exceptions)

(def application-exception-type ::application-exception)

(defn application-exception? [ex]
  (= (:type ex) application-exception-type))

(defn ApplicationException [code message]
  {:type application-exception-type :code code :message message})
