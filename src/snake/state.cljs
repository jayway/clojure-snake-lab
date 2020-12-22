(ns snake.state)

(defonce initail-server-state {:players {}})

(defonce initial-game {:alive true
                       :training false
                       :last-eaten nil
                       :last-moved nil
                       :width 15
                       :height 15
                       :score 0
                       :snake [[8 7] [7 7] [6 7]]
                       :fruit [10 10]})

(defn new-game []
  (-> initial-game
      (assoc :last-eaten (js/Date.now))
      (assoc :last-moved (js/Date.now))))

(defonce games (atom initail-server-state))

(defonce highscores (atom {}))
