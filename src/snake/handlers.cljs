(ns snake.handlers
  (:require [applied-science.js-interop :as j]
            [snake.state :as state]
            [snake.game :as game]))

(defn start-game [mode]
  (fn [req res]
    (when-let [nickname (j/get-in req [:params :nickname])]
      (let [new-game (-> (snake.state/new-game)
                         (assoc :training (= mode :training)))]
        (swap! state/games assoc-in [:players nickname] new-game)
        (.send res (clj->js new-game))))))

(defn move [req res]
  (when-let [nickname (j/get-in req [:params :nickname])]
    (let [direction (j/get-in req [:query :direction])
          game (get-in @state/games [:players nickname])]
      (if (not (:alive game))
        (.send res (clj->js game))
        (let [new-game (game/tick game direction)]
          (when (and (not (:training new-game)) (not (:alive new-game)))
            (swap! state/highscores assoc nickname (max (get @state/highscores nickname) (:score new-game))))
          (swap! state/games assoc-in [:players nickname] new-game)
          (.send res (clj->js new-game)))))))

(defn training-view [req res]
  (when-let [nickname (j/get-in req [:params :nickname])]
    (.render res "training/index" (clj->js {:nickname nickname}))))

(defn competition [_req res]
  (.render res "competition" (clj->js {:players (:players @state/games)})))
