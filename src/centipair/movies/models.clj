(ns centipair.movies.models
  (:use korma.core
        korma.db
        centipair.core.db.connection)
  (:require [centipair.core.contrib.time :as time]
            [centipair.core.utilities.pagination :as pagination]))


(defentity movie)



(defn get-movie-rt [rt-id]
  (select movie (fields :movie_rt_id) (where {:movie_rt_id (Integer. rt-id)})))


(defn get-movie [id]
  (first (select movie (where {:movie_id (Integer. id)}))))


(defn get-movie-url [id url-slug]
  (let [movie-obj (get-movie id)]
    (if (= (:movie_url_slug movie-obj) url-slug)
      movie-obj
      nil)))


(defn create-movie
  "Inserts movie data into db"
  [params]
  (println params)
  (let [db-movie (get-movie-rt (:movie_rt_id params))]
    (if (empty? db-movie)
      (insert movie (values params))
      db-movie)))


(defn format-date
  "format 2015-04-02"
  [date]
  (if (nil? date)
    nil
    (time/to-sql-date (time/parse-date date))))


(defn update-release-dates
  [rt-id dates]
  (update movie 
          (set-fields {:movie_release_date_theater (format-date (:theater dates))
                       :movie_release_date_dvd (format-date (:dvd dates))})
          (where {:movie_rt_id (Integer. rt-id)})))


(defn get-movies [page page-limit]
  (let [offset-limit-params (pagination/offset-limit page page-limit)]
    (select movie
            (fields :movie_id
                    :movie_title
                    :movie_poster_thumbnail
                    :movie_tomato_rating
                    :movie_url_slug)
            (order [:movie_release_date_dvd :movie_tomato_rating] :DESC)
            (offset (:offset offset-limit-params))
            (limit (:limit offset-limit-params)))))


(defn search-movies
  [query]
  (exec-raw ["SELECT movie_id,movie_title,movie_poster_thumbnail,movie_tomato_rating,movie_url_slug FROM movie WHERE movie_title ILIKE ? ORDER BY movie_release_date_dvd,movie_tomato_rating ;" [(str "%" query "%")]] :results))
