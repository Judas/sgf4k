package com.judas.sgf4k.feature.parser

import com.judas.sgf4k.feature.lexer.Token
import com.judas.sgf4k.feature.lexer.Token.Separator.*
import com.judas.sgf4k.feature.lexer.Token.Type.*
import com.judas.sgf4k.feature.exceptions.InvalidSgfException
import com.judas.sgf4k.feature.parser.model.Game
import com.judas.sgf4k.feature.parser.model.GameCollection
import com.judas.sgf4k.feature.parser.model.GameNode
import com.judas.sgf4k.feature.parser.model.properties.SgfProperty
import com.judas.sgf4k.feature.parser.model.properties.SgfPropertyKey
import com.judas.sgf4k.feature.parser.model.properties.toSgfPropertyKey

/**
 * The syntactic parser in charge of turning the tokenized
 * output of the lexer into a syntax into a game object.
 *
 * See https://www.red-bean.com/sgf/sgf4.html
 */
internal class Parser {
    /**
     * Analyzes the given tokens to create the corresponding
     * GameCollection object.
     */
    fun parse(tokens: List<Token>): GameCollection {
        val collection = GameCollection()

        var index = 0
        var tmpGameTokens = mutableListOf<Token>()
        while (index < tokens.size) {
            val token = tokens[index]

            // Skip non separator characters for speedup
            if (token.type != SEPARATOR) {
                index++
                continue
            }

            when (token.value) {
                // '(' means a new game => buffer content and skip to its end
                LEFT_PARENTHESIS.char.toString() -> {
                    tmpGameTokens = extractGameTokens(tokens, index + 1)
                    index += tmpGameTokens.size
                }

                // ')' means end of a game => add buffered content to collection
                RIGHT_PARENTHESIS.char.toString() ->
                    if (tmpGameTokens.isNotEmpty()) {
                        parseGame(tmpGameTokens)?.let { collection.games.add(it) }
                        tmpGameTokens.clear()
                    }
            }

            index++
        }

        return collection
    }

    /**
     * Extracts all the game tokens.
     * This will inspect the given token list from the given index
     * until it finds the corresponding closing parenthesis.
     *
     * @param tokens The game tokens.
     *
     * @param fromIndex The index of the first character
     * of the game (right after the opening parenthesis)
     * inside the whole SGF file.
     */
    private fun extractGameTokens(tokens: List<Token>, fromIndex: Int): MutableList<Token> {
        val result = mutableListOf<Token>()

        // We parse all the SGF until we found a ')', corresponding to the end of the game
        // We keep track of inside '(' & ')' encountered for that
        var parenthesisCount = 0
        run tokenLoop@{
            tokens.subList(fromIndex, tokens.size).forEach { token ->
                // Skip non separator characters for speedup
                if (token.type != SEPARATOR) {
                    result.add(token)
                    return@forEach
                }

                when (token.value) {
                    LEFT_PARENTHESIS.char.toString() -> parenthesisCount++

                    RIGHT_PARENTHESIS.char.toString() ->
                        if (parenthesisCount > 0) parenthesisCount--
                        else return@tokenLoop
                }
                result.add(token)
            }
        }
        return result
    }

    /**
     * Parses the given tokens and returns the corresponding
     * Game object.
     *
     * A game is represented as a tree, so we use a LIFO stack
     * to browse through all nodes one by one and keeping the
     * parent node associations.
     *
     * @param tokens The game list of lexical tokens.
     */
    private fun parseGame(tokens: List<Token>): Game? {
        val stack = ArrayDeque<GameNode>() // LIFO Stack
        var rootNode: GameNode? = null
        var parentNode: GameNode? = null

        var index = 0
        while (index < tokens.size) {
            val token = tokens[index]

            // Skip non separator characters for speedup
            if (token.type != SEPARATOR) {
                index++
                continue
            }

            when (token.value) {
                // '(' means this is the start of a new variation branch
                // Put the parent node at the top the stack
                LEFT_PARENTHESIS.char.toString() ->
                    parentNode?.let { stack.addLast(it) }

                // ')' means this is the end of a variation branch
                // Get back to the last parent node at the top of the stack
                RIGHT_PARENTHESIS.char.toString() ->
                    if (stack.isNotEmpty()) parentNode = stack.removeLast()

                // ';' means the start of a node => extract it and add it to game
                SEMI_COLON.char.toString() -> {
                    val nodeTokens = extractNodeTokens(tokens, index + 1)
                    index += nodeTokens.size

                    val node = parseNode(nodeTokens, parentNode)

                    // Filter empty nodes
                    if (node.properties.isNotEmpty()) {
                        parentNode?.children?.add(node) ?: run { rootNode = node }
                        parentNode = node
                    }
                }
            }

            index++
        }

        return rootNode?.let { Game(it) }
    }

    /**
     * Extracts all the tokens corresponding to a node starting at
     * position fromIndex. This will inspect the given token list from the given index
     * until it finds the end of the node.
     *
     * @param tokens The node tokens.
     *
     * @param fromIndex The index of the first character of the node
     * (right after the semicolon) inside the whole SGF file.
     */
    private fun extractNodeTokens(tokens: List<Token>, fromIndex: Int): MutableList<Token> {
        val result = mutableListOf<Token>()
        run tokenLoop@{
            tokens.subList(fromIndex, tokens.size).forEach { token ->
                // Skip non separator characters for speedup
                if (token.type != SEPARATOR) {
                    result.add(token)
                    return@forEach
                }

                when (token.value) {
                    LEFT_PARENTHESIS.char.toString(),
                    RIGHT_PARENTHESIS.char.toString(),
                    SEMI_COLON.char.toString() -> return@tokenLoop
                }
                result.add(token)
            }
        }
        return result
    }

    /**
     * Parses the given tokens and returns the corresponding
     * GameNode object.
     *
     * A node is represented as a map of properties (order does not matter),
     * one property key can have multiple property values.
     *
     * @param tokens The node list of lexical tokens.
     * @param parentNode The parent node, can be null if this is the root node
     */
    private fun parseNode(tokens: List<Token>, parentNode: GameNode?): GameNode {
        val properties = mutableListOf<SgfProperty>()

        var tmpKey: SgfPropertyKey? = null
        val tmpValues = mutableListOf<String>()
        tokens
            .forEach { token ->
                when (token.type) {
                    IDENTIFIER -> {
                        // Add previous buffered key/values if needed
                        tmpKey?.let { properties.add(SgfProperty(it, tmpValues.toMutableList())) }

                        // Check for duplicates
                        if (properties.any { it.key.toString() == token.value })
                            throw InvalidSgfException("Property ${token.value} appears multiple times in the same node.")

                        // Prepare key/value buffer
                        tmpKey = token.value.toSgfPropertyKey()
                        tmpValues.clear()
                    }

                    LITERAL -> tmpValues.add(token.value) // Add value to buffer

                    SEPARATOR -> {} // Skip separators
                }
            }

        // Add last buffered key/value
        tmpKey?.let { properties.add(SgfProperty(it, tmpValues.toMutableList())) }

        return GameNode(parentNode, properties)
    }
}
