(ns snake.game)

(defmulti move (fn [_ direction] direction))
(defmethod move "NORTH" [[x y] _] [x (inc y)])
(defmethod move "EAST" [[x y] _] [(inc x) y])
(defmethod move "SOUTH" [[x y] _] [x (dec y)])
(defmethod move "WEST" [[x y] _] [(dec x) y])

(defn new-fruit [game-state]
  (let [fruit (:fruit game-state)
        snake (:snake game-state)
        width (:width game-state)
        height (:height game-state)
        empty? (fn [p] (and (not= p fruit) (every? #(not= % p) snake)))]
    (first
     (filter
      empty?
      (repeatedly
       (fn [] [(rand-int width) (rand-int height)]))))))

(defn valid-pos? [pos state]
  (let [[x y] pos]
  (and
   (<= 0 x (dec (:width state)))
   (<= 0 y (dec (:height state)))
   (every? #(not= % pos) (:snake state)))))

(defn update-game [game-state direction]
  (let [snake (game-state :snake)
        next-head-pos (move (first snake) direction)
        is-fruit (= next-head-pos (game-state :fruit))
        new-score (+ (game-state :score) 1 (if is-fruit 15 0))
        new-fruit (if is-fruit (new-fruit game-state) (game-state :fruit))
        snake-body (if is-fruit snake (butlast snake))
        new-snake (cons next-head-pos snake-body)]
    (if (valid-pos? next-head-pos game-state)
      (assoc game-state :snake new-snake :fruit new-fruit :score new-score)
      (assoc game-state :alive false))))

