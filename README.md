# clojure-snake-lab
Competence development lab for creating a snake game in ClojureScript

# API
/start -> GAME_STATE
/move?direction=[NORTH|EAST|SOUTH|WEST] -> GAME_STATE 

# Game State
```
GAME_STATE =
{
#2  "gameid": number,
    "alive": boolean,
    "width": number,
    "height": number,
    "snake": [{x, y}, ...] head -> tail
    "fruit": {x, y},
#2  "score": number,
#2  "hunger": 0 - 100
}
```