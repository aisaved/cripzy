(ns centipair.movies.dvd
  (:require [reagent.core :as reagent]
            [centipair.core.ui :as ui]
            [centipair.core.utilities.ajax :as ajax]))



(def movie-list-data (reagent/atom {:page 1
                                    :limit 30
                                    :data []}))

(def row-count (atom 0))


(defn prepare-movie-data
  [data-list]
  (let [partition-data (partition-all 6 data-list)
        added-data (concat (:data @movie-list-data) partition-data)]
    (swap! movie-list-data assoc :data added-data)))

(defn fetch-movie-list
  []
  (ajax/get-json 
   "/api/1/movies/dvd"
   {:page (:page @movie-list-data)
    :limit (:limit @movie-list-data)}
   (fn [response]
     (prepare-movie-data response))))

(defn load-more
  []
  (let [next-page (inc (:page @movie-list-data))]
    (swap! movie-list-data assoc :page next-page))
  (fetch-movie-list))


(defn movie-block
  [movie]
  [:div {:class "col-md-2 text-center"
         :id (str "movie-cell-" (:movie_id movie))
         :key (str "movie-cell-" (:movie_id movie))}
    [:div {:class "text-center"
           :id (str "movie-poster-container" (:movie_id movie))
           :key (str "movie-poster-container" (:movie_id movie))}
     [:a {:href (str "/movie/" (:movie_id movie))
          :id (str "movie-link-" (:movie_id movie))
          :key (str "movie-link-" (:movie_id movie))
          }
      [:img {:src (:movie_poster_thumbnail movie)
             ;;:width "130px"
             ;;:height "190px"
             :class "img-responsive"
             :id (str "movie-poster-" (:movie_id movie))
             :key (str "movie-poster-" (:movie_id movie))}]]]
   [:div {:class "text-center"
          :id (str "movie-info-container-" (:movie_id movie))
          :key (str "movie-infor-container-" (:movie_id movie))}
    [:span {:id (str "movie-title-" (:movie_id movie))
            :key (str "movie-title-" (:movie_id movie))} 
     (:movie_title movie)]]])


(defn movie-row
  [row]
  (swap! row-count inc)
  [:div {:class "row"
         :id (str "dvd-row-" @row-count)
         :key (str "dvd-row-" @row-count)}
   (doall (map movie-block row))])


(defn movie-list
  []
  [:div
   (doall (map movie-row (:data @movie-list-data)))
   [:button {:class "btn btn-lg btn-block"
             :type "button"
             :on-click #(load-more)} "Load more"]])





(defn render-movie-list []
  (fetch-movie-list)
  (ui/render movie-list "movie-list"))
