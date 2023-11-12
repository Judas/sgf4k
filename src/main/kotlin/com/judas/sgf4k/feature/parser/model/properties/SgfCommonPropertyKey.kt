package com.judas.sgf4k.feature.parser.model.properties

/**
 * Those are the non-standard SGF properties but are commonly found inside a Go game SGF file.
 */
enum class SgfCommonPropertyKey(val description: String) {
    BC("Black Country"),
    BP("Black players (for multiplayer go)"),
    BS("Black species"),
    CH("Check markup"),
    DI("Difficulty (for tsumego)"),
    DTX("Extended date"),
    E("Stones captured (by the previous move) markup"),
    EVX("Event extended info"),
    GE("Problem type (for tsumego)"),
    ID("Game identifier"),
    JD("Japanese date"),
    KGSDE("KGS - Dead stones"),
    KGSSW("KGS - White score"),
    KGSSB("KGS - Black score"),
    KI("Integer komi"),
    L(" List of points, replaced by LB L[fg][es][jk] -> LB[fg:A][es:B][jk:C]"),
    LC("Number of byo-yomi periods"),
    LT("Length of byo-yomi periods"),
    LZ("Leela zero comment"),
    M("Mark"),
    MULTIGOGM("Multigo specific property"),
    MULTIGOBM("Multigo specific property"),
    OH("Old handicap"),
    OM("Number of moves per Canadian byoyomi period"),
    PBX("Player Black extended information"),
    PI("People involved (for multiplayer go)"),
    SI("Sigma markup"),
    SY("System, Go Editor version"),
    T("Time used (for the move)"),
    TC("Territory count"),
    TI("Tournament index"),
    WC("White country"),
    WP("White players (for multiplayer go)"),
    WS("White species"),
    WV("GoWrite version"),
    WX("GoWrite extension"),
    ZZ("Explanation (in chinese) of the next following property")
}
