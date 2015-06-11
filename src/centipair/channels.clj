(ns centipair.channels
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]]
            [centipair.core.contrib.mail :as mail]
            [centipair.movies.data :refer [init-movie-channel
                                           init-dvd-channel]]))


(def mail-channel (chan))


(defn process-email
  [mail]
  (mail/send-mail mail))

(defn init-mail-channel
  []
  (go 
    (while true
      (process-email (<! mail-channel)))))


(defn send-async-mail
  [mail]
  (go (>! mail-channel mail)))



(defn init-async-channels
  []
  (do 
    (init-mail-channel)))

