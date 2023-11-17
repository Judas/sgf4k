package com.judas.sgf4k.feature.exceptions

class InvalidSgfException(reason: String) : Sgf4kRuntimeException("Invalid SGF file: $reason")
