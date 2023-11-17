package com.judas.sgf4k.feature.exceptions

open class Sgf4kRuntimeException(reason: String) : Exception("Error while processing: $reason")
