(ns snake.core
  (:require ["express" :as express]
            ["socket.io" :as socket-io]
            ["ejs" :as ejs]
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
        on-game-state-update (fn [_key _atom _old-state new-state] (.emit io "update" (clj->js new-state)))
        on-highscore-updates (fn [_key _atom _old-scores highscore] (.emit io "highscores" (clj->js highscore)))]
    (.on io "connection" handle-new-connection)
    (add-watch state/games nil on-game-state-update)
    (add-watch state/highscores nil on-highscore-updates)))


(defn start-server []
  (println "Starting server")
  (let [app (express)
        http (.listen app 3000 (fn [] (println "Clojuser-snake listening on port 3000!")))]
    (start-sockets http)
    (.set app "view engine" "ejs")
    (.use app (.static express "public"))
    (.get app "/" (fn [_ res] (.render res "index")))
    (.get app "/competition" handlers/competition)
    (.get app "/training/:nickname" handlers/training-view)
    (.get app "/training/start/:nickname" (handlers/start-game :training))
    (.get app "/training/move/:nickname" handlers/move)
    (.get app "/start/:nickname" (handlers/start-game :main))
    (.get app "/move/:nickname" handlers/move)))

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
