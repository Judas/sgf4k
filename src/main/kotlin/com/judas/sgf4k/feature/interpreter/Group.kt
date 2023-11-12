package com.judas.sgf4k.feature.interpreter

/**
 * A group of linked stones.
 */
internal class Group(val initialIntersection: Intersection) {
    var intersections = mutableListOf<Intersection>()

    /**
     * Expand the initial intersection to its corresponding group
     * of intersections.
     */
    fun expand(goban: MutableGoban) {
        expand(listOf(initialIntersection), goban)
    }

    private fun expand(intersectionsToCheck: List<Intersection>, goban: MutableGoban) {
        if (intersectionsToCheck.isEmpty()) return

        val remainingIntersections = intersectionsToCheck.toMutableList()
        val tmpIntersection = remainingIntersections.removeFirst()
        intersections.add(tmpIntersection) // Add intersection to "checked" intersections

        tmpIntersection.adjacentIntersections()
            .asSequence()
            .filter { it.first >= 0 && it.first < goban.size } // Ensure valid column
            .filter { it.second >= 0 && it.second < goban.size } // Ensure valid row
            .map { goban.intersections[it.first][it.second] } // Get corresponding intersection
            .filter { it.state == tmpIntersection.state } // Ensure valid state (same color as ref intersection)
            .filterNot { intersections.contains(it) } // Ensure it is not already checked
            .toList().forEach { remainingIntersections.add(it) } // Add to "to be checked" intersections

        expand(remainingIntersections, goban)
    }

    /**
     * Check if the group is dead. Group is dead if none of the intersections
     * composing it has a liberty (adjacent intersection is empty).
     */
    fun isDead(goban: MutableGoban): Boolean = intersections
        .flatMap { it.adjacentIntersections() }
        .filter { it.first >= 0 && it.first < goban.size } // Ensure valid column
        .filter { it.second >= 0 && it.second < goban.size } // Ensure valid row
        .map { goban.intersections[it.first][it.second] } // Get corresponding intersection
        .none { it.state == IntersectionState.EMPTY } // Check liberties

    /**
     * Update the goban to remove the dead group.
     */
    fun removeFrom(goban: MutableGoban) {
        intersections.forEach { deadStone ->
            if (deadStone.state == IntersectionState.BLACK) goban.blackDeadStones++
            else goban.whiteDeadStones++

            goban.intersections[deadStone.column][deadStone.row] =
                Intersection(deadStone.column, deadStone.row, IntersectionState.EMPTY)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Group

        if (initialIntersection != other.initialIntersection) return false
        if (intersections != other.intersections) return false

        return true
    }

    override fun hashCode(): Int {
        var result = initialIntersection.hashCode()
        result = 31 * result + intersections.hashCode()
        return result
    }
}
