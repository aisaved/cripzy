(ns centipair.movies.models
  (:use korma.core
        korma.db
        centipair.core.db.connection
        centipair.core.contrib.time
        centipair.core.contrib.mail
        centipair.core.utilities.pagination))


(defentity movie)



(defn get-movie-rt [rt-id]
  (select movie (fields :movie_rt_id) (where {:movie_rt_id (Integer. rt-id)})))

(defn create-movie
  "Inserts movie data into db"
  [params]
  (println params)
  (let [db-movie (get-movie-rt (:movie_rt_id params))]
    (if (empty? db-movie)
      (insert movie (values params))
      db-movie)))
