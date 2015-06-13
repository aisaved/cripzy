(ns centipair.routes.home
  (:require [centipair.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [clojure.java.io :as io]
            [centipair.core.contrib.response :as response]
            [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
            [centipair.movies.models :as movie-models]
            ))

(defn home-page []
  (layout/render
    "home.html" ))


(defn movie-page [id]
  (let [movie-info (movie-models/get-movie id)]
    (layout/render
    "movie.html" movie-info)))


(defn csrf-token []
  (response/json-response {:token *anti-forgery-token*}))


(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/movie/:id" [id] (movie-page id)))
