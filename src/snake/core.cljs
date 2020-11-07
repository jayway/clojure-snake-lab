(ns snake.core
  (:require ["express" :as express]
            [snake.game :refer [update-game]]))

;; currently broken in shadow-cljs
(set! *warn-on-infer* true)

(defonce server (atom nil))

(defonce game-state (atom {:alive true
                           :width 15
                           :height 15
                           :snake [[0 1]]
                           :fruit [10 10]}))

(defn print-hello [] "hello")

;; direction = NORTH|EAST|SOUTH|WEST
(defn move-snake [direction]
  (swap! game-state update-game direction))

;; .send res (clj->js (move-snake "NORTH"))
(defn handle-move [req res] 
  (let [direction (.. req -query -direction)]
    (.send res (clj->js (move-snake direction)))))

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
