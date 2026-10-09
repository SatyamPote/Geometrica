package com.nebulastrike.game

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.math.PI

/** 
 * Specification for the 50 unique bosses requested by the user,
 * each with a distinct ID, title, difficulty, base HP, mechanics, and attack routines.
 */
data class BossSpecInfo(
    val id: String,
    val title: String,
    val difficulty: String,
    val baseHp: Float,
    val patternCode: Int, // primary attack pattern
    val skillCode: Int,   // unique boss ability / gimmick
    val phasesCount: Int
)

val ALL_50_BOSS_SPECS = listOf(
    // 1..10 (Boss 1 = 1,000 hits, Boss 2 = 2,000 hits, then +500 to +1,000 hits each)
    BossSpecInfo("prism", "PRISM", "MEDIUM", 32000f, 1, 1, 3),            // #1  ~1,000 hits
    BossSpecInfo("cascade", "CASCADE", "EASY", 60000f, 0, 2, 2),          // #2  ~2,000 hits
    BossSpecInfo("gauntlet", "GAUNTLET", "MEDIUM", 75000f, 9, 3, 3),      // #3  ~2,500 hits
    BossSpecInfo("vortex", "VORTEX", "HARD", 90000f, 5, 4, 3),           // #4  ~3,000 hits
    BossSpecInfo("beacon", "BEACON", "EASY", 105000f, 7, 5, 2),          // #5  ~3,500 hits
    BossSpecInfo("splitter", "SPLITTER", "HARD", 120000f, 1, 6, 3),       // #6  ~4,000 hits
    BossSpecInfo("orbit", "ORBIT", "MEDIUM", 135000f, 3, 7, 3),          // #7  ~4,500 hits
    BossSpecInfo("sawtooth", "SAWTOOTH", "MEDIUM", 150000f, 2, 8, 3),     // #8  ~5,000 hits
    BossSpecInfo("tower", "TOWER", "EASY", 165000f, 0, 9, 2),             // #9  ~5,500 hits
    BossSpecInfo("mesh", "MESH", "MEDIUM", 180000f, 4, 10, 3),           // #10 ~6,000 hits
    // 11..20
    BossSpecInfo("flare", "FLARE", "EASY", 195000f, 1, 11, 2),           // #11 ~6,500 hits
    BossSpecInfo("delta", "DELTA", "MEDIUM", 210000f, 2, 12, 3),         // #12 ~7,000 hits
    BossSpecInfo("web", "WEB", "HARD", 225000f, 5, 13, 3),               // #13 ~7,500 hits
    BossSpecInfo("prismatic", "PRISMATIC", "MEDIUM", 240000f, 4, 14, 3), // #14 ~8,000 hits
    BossSpecInfo("helix", "HELIX", "MEDIUM", 255000f, 6, 15, 3),         // #15 ~8,500 hits
    BossSpecInfo("lockbox", "LOCKBOX", "EASY", 270000f, 0, 16, 2),       // #16 ~9,000 hits
    BossSpecInfo("flareon", "FLAREON", "MEDIUM", 285000f, 2, 17, 3),     // #17 ~9,500 hits
    BossSpecInfo("obsidian", "OBSIDIAN", "HARD", 300000f, 10, 18, 4),    // #18 ~10,000 hits
    BossSpecInfo("glyph", "GLYPH", "MEDIUM", 315000f, 4, 19, 3),         // #19 ~10,500 hits
    BossSpecInfo("siphon", "SIPHON", "MEDIUM", 330000f, 7, 20, 3),       // #20 ~11,000 hits
    // 21..30
    BossSpecInfo("pulse", "PULSE", "EASY", 345000f, 3, 21, 2),           // #21 ~11,500 hits
    BossSpecInfo("spirebreak", "SPIREBREAK", "MEDIUM", 360000f, 7, 22, 3),// #22 ~12,000 hits
    BossSpecInfo("facet", "FACET", "MEDIUM", 375000f, 4, 23, 3),         // #23 ~12,500 hits
    BossSpecInfo("vortex2", "VORTEX II", "HARD", 390000f, 5, 24, 4),     // #24 ~13,000 hits
    BossSpecInfo("cradle", "CRADLE", "EASY", 405000f, 0, 25, 2),         // #25 ~13,500 hits
    BossSpecInfo("skyline", "SKYLINE", "MEDIUM", 420000f, 9, 26, 3),     // #26 ~14,000 hits
    BossSpecInfo("echo", "ECHO", "MEDIUM", 435000f, 1, 27, 3),           // #27 ~14,500 hits
    BossSpecInfo("needle", "NEEDLE", "EASY", 450000f, 10, 28, 2),        // #28 ~15,000 hits
    BossSpecInfo("coil", "COIL", "MEDIUM", 465000f, 2, 29, 3),           // #29 ~15,500 hits
    BossSpecInfo("sentinel-guard", "SENTINEL-GUARD", "EASY", 480000f, 3, 30, 2), // #30 ~16,000 hits
    // 31..40
    BossSpecInfo("rampart", "RAMPART", "MEDIUM", 495000f, 9, 31, 3),     // #31 ~16,500 hits
    BossSpecInfo("sentinel-core", "SENTINEL-CORE", "MEDIUM", 510000f, 4, 32, 3), // #32 ~17,000 hits
    BossSpecInfo("havoc", "HAVOC", "HARD", 525000f, 10, 33, 4),          // #33 ~17,500 hits
    BossSpecInfo("sentinel-wing", "SENTINEL-WING", "EASY", 540000f, 1, 34, 2), // #34 ~18,000 hits
    BossSpecInfo("sentinel-tower", "SENTINEL-TOWER", "EASY", 555000f, 0, 35, 2), // #35 ~18,500 hits
    BossSpecInfo("cataclysm", "CATACLYSM", "VERY_HARD", 570000f, 3, 36, 4), // #36 ~19,000 hits
    BossSpecInfo("sentinel-spike", "SENTINEL-SPIKE", "EASY", 585000f, 7, 37, 2), // #37 ~19,500 hits
    BossSpecInfo("sentinel-ring", "SENTINEL-RING", "MEDIUM", 600000f, 5, 38, 3), // #38 ~20,000 hits
    BossSpecInfo("sentinel-cross", "SENTINEL-CROSS", "MEDIUM", 615000f, 4, 39, 3), // #39 ~20,500 hits
    BossSpecInfo("sentinel-nest", "SENTINEL-NEST", "MEDIUM", 630000f, 8, 40, 3), // #40 ~21,000 hits
    // 41..50
    BossSpecInfo("dominion", "DOMINION", "VERY_HARD", 645000f, 9, 41, 4), // #41 ~21,500 hits
    BossSpecInfo("sentinel-web", "SENTINEL-WEB", "HARD", 660000f, 5, 42, 4), // #42 ~22,000 hits
    BossSpecInfo("colossus-prime", "COLOSSUS-PRIME", "VERY_HARD", 675000f, 7, 43, 4), // #43 ~22,500 hits
    BossSpecInfo("spire-guard", "SPIRE-GUARD", "MEDIUM", 690000f, 4, 44, 3), // #44 ~23,000 hits
    BossSpecInfo("ring-maiden", "RING-MAIDEN", "MEDIUM", 705000f, 6, 45, 3), // #45 ~23,500 hits
    BossSpecInfo("cross-break", "CROSS-BREAK", "HARD", 720000f, 10, 46, 4), // #46 ~24,000 hits
    BossSpecInfo("nest-guard", "NEST-GUARD", "HARD", 735000f, 8, 47, 4), // #47 ~24,500 hits
    BossSpecInfo("ultimate", "ULTIMATE", "FINAL", 750000f, 6, 48, 5),   // #48 ~25,000 hits
    BossSpecInfo("void-walker", "VOID-WALKER", "EASY", 765000f, 0, 49, 2),// #49 ~25,500 hits
    BossSpecInfo("star-forge", "STAR-FORGE", "MEDIUM", 780000f, 4, 50, 4) // #50 ~26,000 hits
)
