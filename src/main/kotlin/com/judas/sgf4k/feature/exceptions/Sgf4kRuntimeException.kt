package com.judas.sgf4k.feature.exceptions

class Sgf4kRuntimeException(reason: String) : Exception("Error while processing: $reason")
