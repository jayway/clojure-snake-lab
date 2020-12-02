(ns snake.core
  (:require ["express" :as express])
  (:require ["cors" :as cors]))

;; currently broken in shadow-cljs
(set! *warn-on-infer* true)

(defonce server (atom nil))
(defonce initial-game-state (atom {:alive true
                                   :width 15
                                   :height 15
                                   :snake [[0 1] [1 1] [2 1]]
                                   :fruit [10 10]}))
(defonce game-state (atom (deref initial-game-state)))

(defn update-head-position [direction position]
  (case direction
    "NORTH" (mapv + [0 1] position)
    "EAST" (mapv + [1 0] position)
    "SOUTH" (mapv + [0 -1] position)
    "WEST" (mapv + [-1 0] position)
    position))

(defn check-intersection [position positions]
  (> (count (filter #(= % position) positions)) 0))

(defn is-within-bounds [position]
  (let [state (deref game-state)
        max-width (:width state)
        max-height (:height state)]
    (and
     (>= (first position) 0)
     (<= (first position) (- max-width 1))
     (>= (second position) 0)
     (<= (second position) (- max-height 1)))))

(defn get-snake-position [direction snake-coordinates]
  (let [head (first snake-coordinates)
        body (subvec snake-coordinates 1)
        new-head-pos (update-head-position direction head)]
    (cond
      (not (is-within-bounds new-head-pos)) []
      (check-intersection new-head-pos body) []
      :else (vec (concat [new-head-pos head] (pop body))))))

(defonce possible-spots (for [x (range (- (@game-state :width) 1)) y (range (- (@game-state :height) 1))] [x y]))
(defn generate-new-fruit [snake]
  (let [remaining-spots (filter #(not (check-intersection % snake)) possible-spots)]
    (rand-nth remaining-spots)))

(defn move-snake [direction]
  (let [snake (@game-state :snake)
        fruit (@game-state :fruit)
        updated-snake (get-snake-position direction snake)
        alive (> (count updated-snake) 0)
        reached-fruit (= true alive (check-intersection fruit updated-snake))
        longer-snake (conj updated-snake (last snake))]
    (cond
      (and alive reached-fruit) (swap! game-state assoc :snake longer-snake)
      alive (swap! game-state assoc :snake updated-snake)
      :else (swap! game-state assoc :alive false))
    (when reached-fruit (swap! game-state assoc :fruit (generate-new-fruit updated-snake)))))

(defn handle-init [req res]
  (prn "init called")
  (reset! game-state (deref initial-game-state))
  (.send res (clj->js (deref initial-game-state))))

(defn handle-move [req res]
  (let [query-params (.-query req) direction (.-direction query-params)]
    (move-snake direction)
    (prn (:snake (deref game-state)))
    (.send res (clj->js (deref game-state)))))

(defn start-server []
  (println "Starting server")
  (let [app (express)]
    (.use app (cors))
    (.get app "/" (fn [req res] (.send res "Hello, world")))
    (.get app "/init" handle-init)
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
