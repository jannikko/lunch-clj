(ns lunch.exceptions)

(def application-exception-type ::application-exception)

(defn application-exception? [ex]
  (= (:type (ex-data ex)) application-exception-type))

(defn ApplicationException [code message]
  (ex-info message {:code code :type application-exception-type}))



