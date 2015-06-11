(ns centipair.movies.data
 (:require [clj-http.client :as client]
           [cheshire.core :refer [parse-string]]
           [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]))



;; rotten tomatoes url sample : "http://www.rottentomatoes.com/api/private/v1.0/m/list/find?page=1&limit=30&type=dvd-all&services=amazon%3Bamazon_prime%3Bflixster%3Bhbo_go%3Bitunes%3Bnetflix_iw%3Bvudu&sortBy=release"

;;omdb samplel url : "http://www.omdbapi.com/?t=KINGSMAN%3A+THE+SECRET+SERVICE&y=&plot=short&r=json&tomatoes=true"

(def movie-channel (chan))
(def dvd-channel (chan))

(def omdb-base-url "http://www.omdbapi.com/")
(def rt-direct-base-url "http://www.rottentomatoes.com/api/private/v1.0/m/list/find")


(defn fetch-data
  [url query-params]
  (let [response-rt (client/get url {:accept :json
                                     :query-params query-params})
        response-body (:body response-rt)
        response-json (parse-string response-body true)]
    response-json))




(defn fetch-omdb [title]
  (fetch-data omdb-base-url
              {:t title
               :r "json"
               :plot "full"
               :tomatoes true
               }))


(defn fetch-rt-dvd [page-limit]
  (fetch-data rt-direct-base-url
              {:page (:page page-limit)
               :limit (:limit page-limit)
               :type "dvd-all"
               :services "amazon;amazon_prime;flixster;hbo_go;itunes;netflix_iw;vudu"
               :sortBy "release"}))



(defn save-movie [title-rtid]
  (let [movie-data (fetch-omdb (:title title-rtid))]
    (println "####################---saving movie---#####################")
    (println (:Title movie-data))))


(defn process-dvd
  [page-limit]
  (let [dvd-data (:results (fetch-rt-dvd page-limit))]
    (doseq [each dvd-data]
      (go
        (>! movie-channel {:title (:title each) :rtid (:id each)})))))


(defn init-movie-channel
  []
  (go 
    (while true
      (save-movie (<! movie-channel)))))


(defn init-dvd-channel
  []
  (go 
    (while true
      (process-dvd (<! dvd-channel)))))
