(ns snake.screen
  (:require [clojure.string :as str]))

(def EMPTY "██")
(def SNAKE "▒▒")
(def FRUIT "░░")

(defn code-block [code]
  (str "<pre>" code "</pre>"))

(defn generate-blank-grid [x y val]
  (vec (repeat y (vec (repeat x val)))))

(defn render-grid [grid]
  (str/join "\n" (map str/join grid)))

(defn impose-block [grid fill position]
  (assoc-in grid position fill))

(defn impose-fruit [grid state]
  (impose-block grid FRUIT (:fruit state)))

(defn impose-snake-segment [grid positions]
  (if (peek positions)
    (impose-snake-segment
      (impose-block grid SNAKE (last positions))
      (pop positions))
    grid))

(defn impose-snake [grid state]
  (impose-snake-segment grid (:snake state)))

(defn render [state]
  (code-block
    (render-grid
      (impose-snake
        (impose-fruit
          (generate-blank-grid (:width state) (:height state) EMPTY)
          state)
        state))))
