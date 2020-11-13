(ns snake.game
  (:require [snake.screen :as screen]))

(defonce state (atom nil))

(defn start-game []
  (reset! state {:alive true
                  :width 15
                  :height 15
                  :snake [[0 0] [1 0] [1 1] [1 2]]
                  :fruit [10 10]}))

;; direction = NORTH|EAST|SOUTH|WEST
(defn move-snake [direction]
  (let [position (first (@state :snake))]
    (swap! state assoc :snake [[5 6]])))

(defn handle-start [req, res]
  (start-game)
  (.send res (screen/render @state)))

(defn handle-move [req res] (.send res (clj->js (move-snake "NORTH"))))
