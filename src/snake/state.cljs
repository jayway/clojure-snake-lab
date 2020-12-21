(ns snake.state)

(defonce initial {:alive true
                  :last-eaten nil
                  :last-moved nil
                  :width 15
                  :height 15
                  :score 0
                  :snake [[8 7] [7 7] [6 7]]
                  :fruit [10 10]})

(defonce game (atom initial))

(defn new-game []
  (-> initial
      (assoc :last-eaten (js/Date.now))
      (assoc :last-moved (js/Date.now))))
