(ns snake.core
  (:require ["express" :as express]))

;; currently broken in shadow-cljs
(set! *warn-on-infer* true)

(defonce server (atom nil))

(defonce game-state (atom {:alive true
                           :width 15
                           :height 15
                           :snake [[0 0] [1 0] [2 0]]
                           :fruit [10 10]}))

(defn update-head-position [direction position]
  (case direction
    "NORTH" (map + [0 1] position)
    "EAST" (map + [1 0] position)
    "SOUTH" (map + [0 -1] position)
    "WEST" (map + [-1 0] position)
    position))

(defn check-intersection [position positions]
  (> (count (filter #(= % position) positions)) 0))

(defn get-snake-position [direction snake-coordinates]
  (let [head (first snake-coordinates)
        body (rest snake-coordinates)
        new-head-pos (update-head-position direction head)]
    (cond
      (check-intersection new-head-pos body) []
      :else (concat [new-head-pos head] (butlast body)))))

(defonce possible-spots (for [x (range (@game-state :width)) y (range (@game-state :height))] [x y]))
(defn generate-new-fruit [snake]
  (let [remaining-spots (filter #(not (check-intersection % snake)) possible-spots)]
    (rand-nth remaining-spots)))

(defn move-snake [direction]
  (let [snake (@game-state :snake)
        fruit (@game-state :fruit)
        updated-snake (get-snake-position direction snake)
        alive (> (count updated-snake) 0)
        reached-fruit (= true alive (check-intersection fruit updated-snake))]
    (when alive (swap! game-state assoc :snake updated-snake))
    (when reached-fruit (swap! game-state assoc :fruit (generate-new-fruit updated-snake)))))

(defn handle-move [req res]
  (let [query-params (.-query req) direction (.-direction query-params)]
    (move-snake direction)
    (.send res (clj->js (deref game-state)))))

(defn start-server []
  (println "Starting server")
  (let [app (express)]
    (.get app "/" (fn [req res] (.send res "Hello, world")))
    (.get app "/start" (fn [req res] (.send res (clj->js (deref game-state)))))
    (.get app "/move" handle-move)
    (.listen app 3000 (fn [] (println "Example app listening on port 3000!")))))

(defn start! []
  ;; called by main and after reloading code
  (reset! server (start-server)))

(defn stop! []
  ;; called before reloading code
  (.close @server)
  (reset! server nil))

(defn main []
  ;; executed once, on startup, can do one time setup here
  (start!))
