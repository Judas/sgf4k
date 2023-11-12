package com.judas.sgf4k.utility

import com.judas.sgf4k.feature.parser.model.GameNode
import com.judas.sgf4k.feature.parser.model.properties.SgfProperty
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

fun loadTestFile(filename: String): String =
    File("src/test/resources/$filename").readText(Charsets.UTF_8)

fun assertSgfPropertyEquals(property: SgfProperty?, value: String) {
    assertNotNull(property)
    assertEquals(1, property.values.size)
    assertEquals(value, property.values.first())
}

// For test only, no nullity check, will crash if it happens
fun GameNode.nthChild(n: Int): GameNode {
    var node = this
    for (i in 1..n)
        node = node.children.first()
    return node
}
