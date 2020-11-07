(ns snake.game)

(defmulti move (fn [_ direction] direction))
(defmethod move "NORTH" [[x y] _] [x (inc y)])
(defmethod move "EAST" [[x y] _] [(inc x) y])
(defmethod move "SOUTH" [[x y] _] [x (dec y)])
(defmethod move "WEST" [[x y] _] [(dec x) y])

(defn update-game [game-state direction]
  (let [snake (game-state :snake)
        next-head-pos (move (first snake) direction)
        new-snake (cons next-head-pos (butlast snake))]
    (assoc game-state :snake new-snake)))

