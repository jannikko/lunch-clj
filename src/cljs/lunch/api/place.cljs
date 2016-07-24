(ns lunch.api.place
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [secretary.core :refer [defroute]])
  (:require [lunch.util :refer [GET]]
            [cljs-http.client :as http]
            [cljs.core.async :as async :refer [<! >! put! chan]]))

(defn place [id] (str "api/place/" id))

(defn get-one
  [id]
  (GET (place id)))

(defn post-one
  [id file]
  (http/post (place id) {:multipart-params [["file" file]]}))
