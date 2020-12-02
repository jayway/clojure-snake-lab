(ns snake.core
  (:require ["express" :as express]
            ["socket.io" :as socket-io]
            [snake.game :refer [update-game]]))

;; currently broken in shadow-cljs
(set! *warn-on-infer* true)

(defonce server (atom nil))

(def new-game-state {:alive true
                     :width 15
                     :height 15
                     :snake [[0 1] [1 1] [2 1] [3 1]]
                     :fruit [10 10]})

(defonce game-state (atom new-game-state))

(defn print-hello [] "hello")

;; direction = NORTH|EAST|SOUTH|WEST
(defn move-snake [direction]
  (swap! game-state update-game direction))

;; .send res (clj->js (move-snake "NORTH"))
(defn handle-move [req res]
  (let [direction (.. req -query -direction)]
    (.send res (clj->js (move-snake direction)))))

(defn handle-new-connection [socket]
  (println "New connection"))

(defn start-sockets [http]
  (let [io (socket-io http)
        update-fn (fn [key atom old-value new-value] (.emit io "update" (clj->js new-value)))]
    (.on io "connection" handle-new-connection)
    (add-watch game-state nil update-fn)))

(defn start-server []
  (println "Starting server")
  (let [app (express)
        http (.listen app 3000 (fn [] (println "Example app listening on port 3000!")))]
    (start-sockets http)
    (.use app (.static express "public"))
    (.get app "/start"
          (fn [req res]
            (let [state (reset! game-state new-game-state)]
              (.send res (clj->js state)))))
    (.get app "/move" handle-move)))

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
