(ns centipair.movies.api
  (:use compojure.core)
   (:require [liberator.core :refer [resource defresource]]
             [centipair.core.contrib.response :as response]
             [centipair.core.auth.user.models :as user-models]))



(defresource api-dvd-releases [& [source]]
  :available-media-types ["application/json"]
  :allowed-methods [:get]
  :handle-ok (fn [context] ))


