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

;; Helpful for debugging in REPL
(defonce NORTH "NORTH")
(defonce EAST "EAST")
(defonce SOUTH "SOUTH")
(defonce WEST "WEST")

(defn update-head-position [direction position]
  (case direction
    "NORTH" [(first position) (+ 1 (second position))]
    "EAST" [(+ 1 (first position)) (second position)]
    "SOUTH" [(first position) (- 1 (second position))]
    "WEST" [(- 1 (first position)) (second position)]
    position))

(defn get-snake-position [direction snake-coordinates]
  (let [head (first snake-coordinates)
        body (rest snake-coordinates)
        new-head-pos (update-head-position direction head)]
    (concat [new-head-pos head] (butlast body))))

(defn move-snake [direction]
  (let [position (@game-state :snake)]
    (swap! game-state assoc :snake (get-snake-position direction position))))

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
