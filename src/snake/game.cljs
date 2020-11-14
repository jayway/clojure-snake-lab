(ns snake.game
  (:require [clojure.string :as str]))

(defn within-positions? [positions position]
  (boolean (some #{position} positions)))

(def not-within? (complement within-positions?))

(defn within-world? [width height position]
  (let [[x y] position]
    (and (>= x 0) (<= x width) (>= y 0) (<= y height))))

(defn legal-position? [state position]
  ((every-pred
    (partial within-world? (:width state) (:height state))
    (partial not-within? (:snake state)))
   position))

(def illeagal-position? (complement legal-position?))

(defn snake-head-ok? [state position]
  ((every-pred
    (partial within-world? (:width state) (:height state))
    (partial not-within? (rest (:snake state))))
   position))

(defn at-fruit? [head fruit] (= head fruit))

(defn eaten-fruit? [state]
  (at-fruit? (first (:snake state)) (:fruit state)))

(defn new-fruit [state]
  (let [new-fruit (first (drop-while #(illeagal-position? state %)
                                     (repeatedly #(vec [(rand-int (:width state)) (rand-int (:height state))]))))]
    (assoc state :fruit new-fruit)))

(defn calculate-new-head-position [current direction]
  (condp = (str/upper-case direction)
    "NORTH" (map + [0 1] current)
    "EAST"  (map + [1 0] current)
    "SOUTH" (map + [0 -1] current)
    "WEST"  (map + [-1 0] current)
    current))

(defn reposition-snake [state new-head]
  (into [new-head] (if (at-fruit? new-head (:fruit state))
                     (:snake state)
                     (pop (:snake state)))))

(defn move-snake
  "Possible directions: NORTH/EAST/SOUTH/WEST"
  [state direction]
  (let [current-head (first (:snake state))
        new-head (calculate-new-head-position current-head direction)]
    (if (snake-head-ok? state new-head)
      (assoc state :snake (reposition-snake state new-head))
      (assoc state :alive false))))

(defn tick [state direction]
  (-> state
      (move-snake direction)
      (cond-> (eaten-fruit? state) (update :score inc))
      (cond-> (eaten-fruit? state) (new-fruit))))
