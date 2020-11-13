(ns snake.core
  (:require ["express" :as express]
            [snake.game :as game]))

;; currently broken in shadow-cljs
(set! *warn-on-infer* true)

(defonce server (atom nil))

;; .send res (clj->js (move-snake "NORTH"))
(defn handle-move [_req res] (.send res (clj->js (game/move-snake "NORTH"))))

(defn start-server []
  (println "Starting server")
  (let [app (express)]
    (.get app "/" (fn [_req res] (.send res "Hello, world")))
    (.get app "/start" (fn [_req res] (.send res (clj->js (deref game/state)))))
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
