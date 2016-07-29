(ns lunch.api.place
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [secretary.core :refer [defroute]])
  (:require [lunch.util :refer [GET]]
            [cljs-http.client :as http]
            [lunch.api.handlers :refer [GET-handler]]
            [re-frame.core :as re-frame :refer [dispatch]]
            [cljs.core.async :as async :refer [<! >! put! chan]]))

(defn place [id] (str "api/place/" id))

(defn get-one
  [db [_ handler]]
  (GET (place (get-in db [:view :place-id]))))

(re-frame/register-handler
 :api-place/get-one
 (GET-handler get-one))

(defn upload
  [id file]
  (http/post (str (place id) "/upload") {:multipart-params [["file" file]]}))
