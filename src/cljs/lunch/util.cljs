(ns lunch.util
  (:require
   [cljs.core.async :as async :refer [<! >! put! chan]]
   [cljs-http.client :as http]))

(defn GET
  "Makes a GET request to an endpoint"
  ([endpoint] (GET (chan) endpoint {}))
  ([endpoint params] (GET (chan) endpoint params))
  ([c endpoint params]
   (http/get endpoint {:query-params params
                       :channel c})))
