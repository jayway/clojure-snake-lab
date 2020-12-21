(ns snake.handlers
  (:require [applied-science.js-interop :as j]
            [snake.state :as state]
            [snake.game :as game]))

(defn start-game [_req res]
  (let [new-game (reset! state/game (snake.state/new-game))]
    (.send res (clj->js new-game))))

(defn move [req res]
  (let [direction (j/get-in req [:query :direction])]
    (if (not (:alive @state/game))
      (.send res (clj->js @state/game))
      (do (reset! state/game (game/tick @state/game direction))
          (.send res (clj->js @state/game))))))

(defn start-training-game [req res]
  (when-let [nickname (j/get-in req [:params :nickname])]
    (let [new-game (snake.state/new-game)]
      (swap! state/games assoc-in [:players nickname] new-game)
      (.send res (clj->js new-game)))))

(defn training-move [req res]
  (when-let [nickname (j/get-in req [:params :nickname])]
    (let [direction (j/get-in req [:query :direction])
          game (get-in @state/games [:players nickname])
          new-game (game/tick game direction)
          _ (println @state/games)]
      (if (not (:alive game))
        (.send res (clj->js game))
        (do (swap! state/games assoc-in [:players nickname] new-game)
            (.send res (clj->js new-game)))))))
