(ns snake.handlers
  (:require [applied-science.js-interop :as j]
            [snake.state :as state]
            [snake.game :as game]))

(defn start-game [_req res]
  (let [new-game (reset! state/game state/initial)]
    (.send res (clj->js new-game))))

(defn move [req res]
  (let [direction (j/get-in req [:query :direction])]
    (if (not (:alive @state/game))
      (.send res (clj->js @state/game))
      (do (reset! state/game (game/tick @state/game direction))
          (.send res (clj->js @state/game))))))
