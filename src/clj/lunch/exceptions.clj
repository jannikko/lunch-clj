(ns lunch.exceptions)

(def application-exception-key ::application-exception)

(defn application-exception? [ex]
  (= (:type ex) application-exception-key))

(defn ApplicationException [code message]
  {:type application-exception-key :code code :message message})
