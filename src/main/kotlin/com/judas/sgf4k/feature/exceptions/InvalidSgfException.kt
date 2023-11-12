package com.judas.sgf4k.feature.exceptions

class InvalidSgfException(reason: String) : Exception("Invalid SGF file: $reason")
