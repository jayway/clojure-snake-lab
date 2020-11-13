(ns snake.game
  (:require [clojure.string :as str]))


(defonce state (atom {:alive true
                      :width 15
                      :height 15
                      :snake [[8 7] [7 7] [6 7]]
                      :fruit [10 10]}))

(defn within-positions? [positions position]
  (boolean (some #{position} positions)))

(defn not-within? [positions position]
  (not (within-positions? positions position)))

(defn within-world? [width height position]
  (let [[x y] position]
    (and (>= x 0) (<= x width) (>= y 0) (<= y height))))

(defn legal-position? [world-width world-height snake position]
  ((every-pred
    (partial within-world? world-width world-height)
    (partial not-within? snake))
   position))

(defn calculate-new-head-position [current direction]
  (condp = (str/upper-case direction)
    "NORTH" (map + [0 1] current)
    "EAST"  (map + [1 0] current)
    "SOUTH" (map + [0 -1] current)
    "WEST"  (map + [-1 0] current)))

(defn reposition-snake [snake new-head]
  (into [new-head] (pop snake)))

;; direction = NORTH|EAST|SOUTH|WEST
(defn move-snake [direction]
  (let [{width :width height :height snake :snake} @state
        current-head (first snake)
        new-head (calculate-new-head-position current-head direction)]
    (if (legal-position? width height snake new-head)
      (swap! state assoc :snake (reposition-snake snake new-head))
      (swap! state assoc :alive false))))
