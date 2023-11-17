package com.judas.sgf4k.feature.exceptions

class InvalidGameException(reason: String) : Sgf4kRuntimeException("Invalid game: $reason")
