(ns lunch.api.menu
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [secretary.core :refer [defroute]])
  (:require [lunch.api.handlers :refer [wrap-get-handler wrap-post-handler]]
            [re-frame.core :as re-frame :refer [dispatch]]
            [cljs.core.async :as async :refer [<! >! put! chan]]))

(defn menu [id] (str "api/menu/" id))

(defn get-one
  [_ place-id]
  {:url (str (menu place-id) "/download")})

(defn upload-menu
  [_ link place-id]
  {:url    (str (menu place-id) "/upload")
   :params {:json-params {"link" link}}})

(re-frame/register-handler
 :api-menu/get-one
 (wrap-get-handler get-one))

(re-frame/register-handler
 :api-menu/upload-menu
 (wrap-post-handler upload-menu))
