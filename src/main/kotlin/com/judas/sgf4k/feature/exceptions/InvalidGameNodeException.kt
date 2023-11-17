package com.judas.sgf4k.feature.exceptions

class InvalidGameNodeException(reason: String) : Sgf4kRuntimeException("Invalid game node: $reason")
