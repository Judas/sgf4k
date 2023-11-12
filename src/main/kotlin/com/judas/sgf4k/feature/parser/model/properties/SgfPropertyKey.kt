package com.judas.sgf4k.feature.parser.model.properties

/**
 * An SGF property key. As the SGF format can be extended by using custom properties, it can either be :
 * - a language standard reserved key
 * - a non-standard but commonly found key
 * - a custom unknown key
 */
sealed class SgfPropertyKey {
    /**
     * SGF languages standard reserved key.
     * https://www.red-bean.com/sgf/proplist.html
     */
    class Standard(val label: SgfStandardPropertyKey) : SgfPropertyKey()

    /**
     * Common keys found in go game SGF.
     */
    class Common(val label: SgfCommonPropertyKey) : SgfPropertyKey()

    /**
     * Custom unknown key.
     */
    class Custom(val label: String) : SgfPropertyKey()

    override fun toString(): String = when (this) {
        is Standard -> label.name
        is Common -> label.name
        is Custom -> label
    }
}

fun String.toSgfPropertyKey(): SgfPropertyKey =
    SgfStandardPropertyKey.entries.find { it.name == this }?.let { SgfPropertyKey.Standard(it) }
        ?: SgfCommonPropertyKey.entries.find { it.name == this }?.let { SgfPropertyKey.Common(it) }
        ?: SgfPropertyKey.Custom(this)
