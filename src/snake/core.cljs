(ns snake.core
  (:require ["express" :as express]
            [snake.game :as game]
            [snake.welcome :as welcome]
            [clojure.string :as str]))

;; currently broken in shadow-cljs
(set! *warn-on-infer* true)

(defonce server (atom nil))

(defn start-server []
  (println "Starting server")
  (let [app (express)]
    (.get app "/" welcome/handle-welcome)
    (.get app "/start" game/handle-start)
    (.get app "/move" game/handle-move)
    (.listen app 3000 (fn [] (println "Example app listening on port 3000!")))))

(defn start! []
  (reset! server (start-server)))

(defn stop! []
  (.close @server)
  (reset! server nil))

(defn main []
  (start!))
