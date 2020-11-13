(ns snake.welcome
  (:require [clojure.string :as str]))

(defn handle-welcome [req, res] 
  (.send res
    (str "<p>Welcome to clojure-snake-lab.</p>"
         "<a href=\""
         req.protocol
         "://"
         (.get req "Host")
         "/start"
         "\">Start Game!</a>")))
