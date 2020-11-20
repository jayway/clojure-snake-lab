(ns client.core)

(def canvas (.getElementById js/document "canvas"))
(def ctx (.getContext canvas "2d"))

;; canvas is 600x600, and has 15x15 squares
;; each square is 40x40 px
;; but maybe make it 38x38px

(defn calculate-part-position [position]
  (let [x (first position)
        y (last position)
        canvas-x (+ 1 (* x 40)) ;; x * 40 + 1
        canvas-y (- 560 (- (* y 40) 1))] ;; 560 - (y * 40 - 1)
    (println [canvas-x canvas-y])
    [canvas-x canvas-y]))


(defn draw-part [position color]
  (let [canvas-position (calculate-part-position position)]
    (set! (.-fillStyle ctx) color)
    (.beginPath ctx)
    (println canvas-position)
    (.rect ctx (first canvas-position) (last canvas-position) 38 38)
    (.fill ctx)))

(defn draw-snake [snake]
  (let [head (first snake) tail (rest snake)]
    (draw-part head "rgb(150,0,0)")
    (mapv (fn [pos] (draw-part pos "rgb(150,150,150)")) tail)))

;; (defn fetch-state []
;;   (-> (js/fetch "http://localhost:3000/start" )
;;    (.then (fn [res] (println res)))))

(defn main []
  ;; (fetch-state)
  (draw-snake [[0 3] [0 2] [0 1] [0 0]]))

(main)