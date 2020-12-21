(ns snake.state)

(defonce initail-server-state {:players {}})

(defonce initial {:alive true
                  :last-eaten nil
                  :last-moved nil
                  :width 15
                  :height 15
                  :score 0
                  :snake [[8 7] [7 7] [6 7]]
                  :fruit [10 10]})

(defonce game (atom initial))

(defonce games (atom initail-server-state))

(defn new-game []
  (-> initial
      (assoc :last-eaten (js/Date.now))
      (assoc :last-moved (js/Date.now))))
