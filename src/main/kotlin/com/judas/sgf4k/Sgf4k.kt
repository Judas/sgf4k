package com.judas.sgf4k

import com.judas.sgf4k.feature.exceptions.InvalidSgfException
import com.judas.sgf4k.feature.lexer.Lexer
import com.judas.sgf4k.feature.parser.Parser
import com.judas.sgf4k.feature.parser.model.GameCollection
import com.judas.sgf4k.feature.validator.Validator

/**
 * Parses an SGF string into a GameCollection object.
 * @throws InvalidSgfException if the SGF file is invalid.
 */
fun String.toGameCollection(): GameCollection {
    val tokens = Lexer().tokenize(this)
    val collection = Parser().parse(tokens)
    return Validator().validate(collection)
}
