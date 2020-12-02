(ns client.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(def canvas (.getElementById js/document "canvas"))
(def ctx (.getContext canvas "2d"))
(defonce canvas-info (atom {:width (.-width canvas)
                            :height (.-height canvas)
                            :tile-size 30
                            :horizontal-tiles 15
                            :vertical-tiles 15}))

(defn draw-board []
  (set! (.-fillStyle ctx) "bisque")
  (let [board (deref canvas-info)
        width (:width board)
        height (:height board)]
    (.fillRect ctx 0 0 width height)))

(defn map-coordinates-to-canvas [coordinates]
  (let [board (deref canvas-info)
        segment-width (int (/ (:width board) (:horizontal-tiles board)))
        segment-height (int (/ (:height board) (:vertical-tiles board)))]
    [(* (first coordinates) segment-width)
     (- (:height board) (* (+ (second coordinates) 1) segment-height))]))

(defn draw-block [coordinates]
  (let [mapped-coordinates (map-coordinates-to-canvas coordinates)
        x (first mapped-coordinates)
        y (second mapped-coordinates)
        tile-size (:tile-size (deref canvas-info))]
    (.fillRect ctx x y tile-size tile-size)))

(defn draw-snake [snake]
  (set! (.-fillStyle ctx) "green")
  (mapv draw-block snake))

(defn draw-fruit [fruit]
  (set! (.-fillStyle ctx) "red")
  (draw-block fruit))

(defn update-frame [snake fruit]
  (draw-board)
  (draw-fruit fruit)
  (draw-snake snake))

(defn init-game []
  (go (let [response (<! (http/get "http://localhost:3000/init" {:with-credentials? false}))]
        (:body response))))

(defn handle-state-update [game-state]
  (let [snake (:snake game-state)
        fruit (:fruit game-state)
        alive (:alive game-state)]
    (update-frame snake fruit)
    (prn alive (not alive) snake fruit)
    (if-not alive (let [response-body (init-game)]
                    (println (clj->js response-body))))))

(defn update-position [direction]
  (go (let [response (<! (http/get "http://localhost:3000/move"
                                   {:with-credentials? false
                                    :query-params {"direction" direction}}))]
        (handle-state-update (:body response)))))

(defn start-new-game []
  (let [response-body (init-game)]
    (prn response-body)
    (handle-state-update response-body)))

(defn get-direction-from-key [keycode]
  (case keycode
    "ArrowUp" "NORTH"
    "ArrowRight" "EAST"
    "ArrowDown" "SOUTH"
    "ArrowLeft" "WEST"
    nil))

(defn keydown-handler [event]
  (let [keycode (.-code event) direction (get-direction-from-key keycode)]
    (when (not (nil? direction)) (update-position direction))))

(.addEventListener js/window "keydown" keydown-handler)

(defn main []
  (println "howdy!")
  (start-new-game))
