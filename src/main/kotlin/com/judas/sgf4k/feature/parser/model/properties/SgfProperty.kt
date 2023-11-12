package com.judas.sgf4k.feature.parser.model.properties

/**
 * Represents an SGF property, consisting of a
 * key and a list of String values.
 */
data class SgfProperty(
    val key: SgfPropertyKey,
    val values: List<String>
) {
    val isMove: Boolean = key is SgfPropertyKey.Standard &&
            (key.label == SgfStandardPropertyKey.W || key.label == SgfStandardPropertyKey.B)
    val isPassMove: Boolean = isMove && values.firstOrNull()?.isBlank() == true
    val isSetup: Boolean = key is SgfPropertyKey.Standard && key.label.type == SgfPropertyType.SETUP
}
