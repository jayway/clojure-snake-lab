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

(defn print-hello [] "hello")

(defn start-server []
  (println "Starting server")
  (let [app (express)]
    (.get app "/" (fn [req res] (.send res "Hello, world")))
    (.get app "/start" (fn [req res] (.send res (clj->js (deref game-state)))))
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
