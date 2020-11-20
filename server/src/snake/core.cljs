(ns snake.core
  (:require ["express" :as express]))

;; currently broken in shadow-cljs
(set! *warn-on-infer* true)

(defonce server (atom nil))

(defonce game-state (atom {:alive true
                           :width 15
                           :height 15
                           :snake [[0 0]]
                           :fruit [10 10]}))

(defn generate-fruit []
  (let [new-fruit [(rand-int (+ 1 (@game-state :width)))
                   (rand-int (+ 1 (@game-state :height)))]]
    (swap! game-state assoc :fruit new-fruit)))

(defn is-inside-bounds [position]
  (let [x (first position)
        y (last position)]
    (and
     (>= x 0)
     (<= x (@game-state :width))
     (>= y 0)
     (<= y (@game-state :height)))))

(defn move-head [head direction]
  (let [x (first head) y (last head)]
   (case direction
    "NORTH" [x (inc y)]
    "EAST" [(inc x) y]
    "SOUTH" [x (dec y)]
    "WEST" [(dec x) y])))

;; add a new head on new position (if not on fruit)
;; remove the last position
(defn move-snake [new-head snake]
  (let [new-snake (cons new-head snake)
        eats (= new-head (@game-state :fruit))]
    (when eats (generate-fruit))
    (if eats
      new-snake
      (drop-last new-snake))))

(defn move [direction snake]
  (let [new-head (move-head (first snake) direction)]
    (if (is-inside-bounds new-head)
      (move-snake new-head snake)
      snake)))


;; direction = NORTH|EAST|SOUTH|WEST
(defn run-game [direction]
  (let [snake (@game-state :snake)]
    (swap! game-state assoc :snake (move direction snake))))

(defn handle-move [req res]
  (let [direction (.-direction (.-query req))]
    (.send res (clj->js (run-game direction)))))

(defn start-server []
  (println "Starting server")
  (let [app (express)]
    (.get app "/" (fn [_ res] (.send res "Hello, world")))
    (.get app "/start" (fn [_ res] (.send res (clj->js (deref game-state)))))
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

;; TODO:
;; enforce input direction
;; lose when going into wall? or end up on other side?
;; hunger?
;; points?