# Snake game server in ClojureScript

Competence development lab for creating a snake game server in ClojureScript.

The server provides an API that lets a client start a game and then send input to guide the snake.


## API

There are two endpoints, both of will return a game state object (see below):

* `GET /start` initiated a new game and will return the game object
* `GET /move?direction=[NORTH|EAST|SOUTH|WEST]` will take one step in the indicated direction.


### Game State

Game state is a json object. Properties marked with `#2` are stretch goals and will probably not be implemented in the first version.

```
{
#2  "gameid": number,
    // Used to handle several simultaneous games

    "alive": boolean,
    // Indicates if the snake is still alive.
    
    "width": number, 
    // Width of the playing field
    
    "height": number,
    // Height of the playing field

    "snake": [[x, y], ...]
    // Positions of the snake segements order from the head element to the tail

    "fruit": [x, y],
    // Position of a fruit to catch

#2  "score": number,
    // The score

#2  "hunger": 0 - 100
    // How hungry the snake is.
}
```

## TBD

There are a bunch of things that has been discussed but not decided:

* How to handle bad requests (wrong direction or somethign)
* Calculating the score.
* What the hunger really is. Maybe number of moves before you need to eat a fruit to avoid starvation.
* Visualization of the game. Maybe console drawing, maybe something else.