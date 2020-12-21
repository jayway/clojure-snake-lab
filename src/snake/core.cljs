(ns snake.core
  (:require ["express" :as express]
            ["socket.io" :as socket-io]
            [applied-science.js-interop :as j]
            [snake.state :as state]
            [snake.handlers :as handlers]))

;; currently broken in shadow-cljs
(set! *warn-on-infer* true)

(defonce server (atom nil))

(defn handle-new-connection [_socket]
  (println "New connection"))

(defn start-sockets [http]
  (let [io (socket-io http)
        on-game-state-update (fn [_key _atom _old-value new-value] (.emit io "update" (clj->js new-value)))]
    (.on io "connection" handle-new-connection)
    (add-watch state/game nil on-game-state-update)))


(defn start-server []
  (println "Starting server")
  (let [app (express)
        http (.listen app 3000 (fn [] (println "Clojuser-snake listening on port 3000!")))]
    (start-sockets http)
    (.use app (.static express "public"))
    (.get app "/test" (fn [req res] (.send res (clj->js (j/get req :query)))))
    (.get app "/start" handlers/start-game)
    (.get app "/start/:nickname/training" handlers/start-training-game)
    (.get app "/move" handlers/move)
    (.get app "/move/:nickname/training" handlers/training-move)))

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
