# SGF4K

Full Kotlin SGF tool that parses SGF strings into game objects.
It also includes an itnerpreter to get the goban state at each node of the parsed game.

This project is heavily inspired by the [SGF4J](https://github.com/toomasr/sgf4j) library.

## Download


## Usage

Parse your SGF string using:

```
import com.judas.sgf4k.toGameCollection

val coolection = "(;FF[4]GM[1]SZ[19];B[ab];W[cd])".toGameCollection()
```

Get goban state using the interpreter:

```
import com.judas.sgf4k.toGameCollection

val game = collection.games.first()
val interpreter = Interpreter(game)
val goban1 = interpreter.gobanFor(game.rootNode) // State of root node
val goban2 = interpreter.gobanFor(game.rootNode.children.first()) // State of first child
```
