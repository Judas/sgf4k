package com.judas.sgf4k.feature.parser.model.properties

import com.judas.sgf4k.feature.parser.model.properties.SgfPropertyType.*

/**
 * Those are the standard SGF properties.
 *
 * https://www.red-bean.com/sgf/proplist.html
 * https://www.red-bean.com/sgf/properties.html
 * https://www.red-bean.com/sgf/loa.html
 * https://www.red-bean.com/sgf/go.html
 */
enum class SgfStandardPropertyKey(val type: SgfPropertyType, val description: String) {
    AB(SETUP, "Add Black"),
    AE(SETUP, "Add Empty (remove)"),
    AN(GAME_INFO, "Annotation"),
    AP(ROOT, "Application"),
    AR(OTHER, "Arrow markup"),
    AS(OTHER, "Who add stones"),
    AW(SETUP, "Add White"),
    B(MOVE, "Black move"),
    BL(MOVE, "Black time left"),
    BM(MOVE, "Bad move"),
    BR(GAME_INFO, "Black rank"),
    BT(GAME_INFO, "Black team"),
    C(OTHER, "Comment"),
    CA(ROOT, "Charset"),
    CP(OTHER, "Copyright"),
    CR(OTHER, "Circle markup"),
    DD(OTHER, "Dim points markup"),
    DM(OTHER, "Even position"),
    DO(MOVE, "Doubtful"),
    DT(GAME_INFO, "Date"),
    EV(GAME_INFO, "Event"),
    FF(ROOT, "File format"),
    FG(OTHER, "Figure"),
    GB(OTHER, "Good for Black"),
    GC(GAME_INFO, "Game Comment"),
    GM(ROOT, "Game (Go = 1, Othello = 2, Chess = 3...)"),
    GN(GAME_INFO, "Game Name"),
    GW(OTHER, "Good for White"),
    HA(GAME_INFO, "Handicap (Go)"),
    HO(OTHER, "Hotspot"),
    IP(GAME_INFO, "Initial position (Lines of Action)"),
    IT(MOVE, "Interesting"),
    IY(GAME_INFO, "Invert Y Axis (Lines of Action)"),
    KM(GAME_INFO, "Komi (Go)"),
    KO(MOVE, "Ko"),
    LB(OTHER, "Label"),
    LN(OTHER, "Line"),
    MA(OTHER, "X markup"),
    MN(MOVE, "Move number"),
    N(OTHER, "Node name"),
    OB(OTHER, "Number of Black moves left (in Canadian byo-yomi)"),
    ON(OTHER, "Opening"),
    OT(OTHER, "Overtime"),
    OW(OTHER, "Number of White moves left (in Canadian byo-yomi)"),
    PB(GAME_INFO, "Player Black"),
    PC(GAME_INFO, "Place"),
    PL(SETUP, "Player to play"),
    PM(OTHER, "Print move mode"),
    PW(GAME_INFO, "Player White"),
    RE(GAME_INFO, "Result"),
    RO(GAME_INFO, "Round"),
    RU(GAME_INFO, "Rules"),
    SE(OTHER, "Legal moves markup (Lines of Action)"),
    SL(OTHER, "Selected"),
    SO(GAME_INFO, "Source"),
    SQ(OTHER, "Square markup"),
    ST(ROOT, "Style"),
    SU(GAME_INFO, "Setup type (Lines of Action)"),
    SZ(ROOT, "Size"),
    TB(OTHER, "Territory Black (Go)"),
    TE(MOVE, "Tesuji"),
    TM(GAME_INFO, "Time limit"),
    TR(OTHER, "Triangle markup"),
    TW(OTHER, "Territory White (Go)"),
    UC(OTHER, "Unclear position"),
    US(GAME_INFO, "User"),
    V(OTHER, "Value of the move"),
    VW(OTHER, "View (to restrict display)"),
    W(MOVE, "White move"),
    WL(MOVE, "White time left"),
    WR(GAME_INFO, "White rank"),
    WT(GAME_INFO, "White team")
}
