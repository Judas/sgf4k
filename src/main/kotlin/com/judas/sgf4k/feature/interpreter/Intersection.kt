package com.judas.sgf4k.feature.interpreter

/**
 * Represents a goban intersection.
 */
data class Intersection(
    val column: Int,
    val row: Int,
    val state: IntersectionState
) {
    internal fun adjacentIntersections() = mutableListOf(
        (column - 1) to row,
        (column + 1) to row,
        column to (row - 1),
        column to (row + 1)
    )
}

/**
 * State of a stone intersection, can either be a black stone, a white stone, or empty.
 */
enum class IntersectionState {
    EMPTY, BLACK, WHITE
}

