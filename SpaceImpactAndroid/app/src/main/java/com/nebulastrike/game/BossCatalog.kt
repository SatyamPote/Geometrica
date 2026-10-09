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
    // 1..10 (Requires 750 - 900+ bullet hits)
    BossSpecInfo("prism", "PRISM", "MEDIUM", 26000f, 1, 1, 3), // refracts shots, 3 facets
    BossSpecInfo("cascade", "CASCADE", "EASY", 24000f, 0, 2, 2), // rhythm gaps
    BossSpecInfo("gauntlet", "GAUNTLET", "MEDIUM", 28000f, 9, 3, 3), // shifting barrier pairs
    BossSpecInfo("vortex", "VORTEX", "HARD", 32000f, 5, 4, 3), // gravity pull
    BossSpecInfo("beacon", "BEACON", "EASY", 25000f, 7, 5, 2), // rotating light beam
    BossSpecInfo("splitter", "SPLITTER", "HARD", 31000f, 1, 6, 3), // splits into two entities
    BossSpecInfo("orbit", "ORBIT", "MEDIUM", 29000f, 3, 7, 3), // 8 orbiting shards
    BossSpecInfo("sawtooth", "SAWTOOTH", "MEDIUM", 29500f, 2, 8, 3), // serrated deflect & pulse
    BossSpecInfo("tower", "TOWER", "EASY", 25500f, 0, 9, 2), // window cutouts
    BossSpecInfo("mesh", "MESH", "MEDIUM", 30000f, 4, 10, 3), // wireframe squares
    // 11..20 (Requires 850 - 1000+ bullet hits)
    BossSpecInfo("flare", "FLARE", "EASY", 27000f, 1, 11, 2), // swoop attacks
    BossSpecInfo("delta", "DELTA", "MEDIUM", 32000f, 2, 12, 3), // delta wing spread
    BossSpecInfo("web", "WEB", "HARD", 36000f, 5, 13, 3), // spiral web retract/extend
    BossSpecInfo("prismatic", "PRISMATIC", "MEDIUM", 33000f, 4, 14, 3), // swapping segment keys
    BossSpecInfo("helix", "HELIX", "MEDIUM", 34000f, 6, 15, 3), // corkscrew spiral
    BossSpecInfo("lockbox", "LOCKBOX", "EASY", 28000f, 0, 16, 2), // opening padlock shackle
    BossSpecInfo("flareon", "FLAREON", "MEDIUM", 35000f, 2, 17, 3), // shoot/shield mode
    BossSpecInfo("obsidian", "OBSIDIAN", "HARD", 39000f, 10, 18, 4), // jagged monolith break-off
    BossSpecInfo("glyph", "GLYPH", "MEDIUM", 35500f, 4, 19, 3), // angular rune matrix
    BossSpecInfo("siphon", "SIPHON", "MEDIUM", 36500f, 7, 20, 3), // hourglass alternating bulbs
    // 21..30 (Requires 950 - 1150+ bullet hits)
    BossSpecInfo("pulse", "PULSE", "EASY", 30000f, 3, 21, 2), // expanding pulse ring
    BossSpecInfo("spirebreak", "SPIREBREAK", "MEDIUM", 38000f, 7, 22, 3), // falling tower spires
    BossSpecInfo("facet", "FACET", "MEDIUM", 37500f, 4, 23, 3), // multi-sided rotating face
    BossSpecInfo("vortex2", "VORTEX II", "HARD", 42000f, 5, 24, 4), // dual counter-rotating vortices
    BossSpecInfo("cradle", "CRADLE", "EASY", 31000f, 0, 25, 2), // rocking cradle expose core
    BossSpecInfo("skyline", "SKYLINE", "MEDIUM", 39000f, 9, 26, 3), // skyscraper silhouette row
    BossSpecInfo("echo", "ECHO", "MEDIUM", 40000f, 1, 27, 3), // boomerang looping return
    BossSpecInfo("needle", "NEEDLE", "EASY", 32000f, 10, 28, 2), // rapid horizontal oscillation
    BossSpecInfo("coil", "COIL", "MEDIUM", 41000f, 2, 29, 3), // spring compress & blast
    BossSpecInfo("sentinel-guard", "SENTINEL-GUARD", "EASY", 33000f, 3, 30, 2), // rotating panels
    // 31..40 (Requires 1050 - 1300+ bullet hits)
    BossSpecInfo("rampart", "RAMPART", "MEDIUM", 42000f, 9, 31, 3), // fortified wall shifting crenellations
    BossSpecInfo("sentinel-core", "SENTINEL-CORE", "MEDIUM", 44000f, 4, 32, 3), // floating hub with accelerating rings
    BossSpecInfo("havoc", "HAVOC", "HARD", 48000f, 10, 33, 4), // jagged starburst random blasts
    BossSpecInfo("sentinel-wing", "SENTINEL-WING", "EASY", 34000f, 1, 34, 2), // wind gust projectiles
    BossSpecInfo("sentinel-tower", "SENTINEL-TOWER", "EASY", 35000f, 0, 35, 2), // command tower with rotating flag
    BossSpecInfo("cataclysm", "CATACLYSM", "VERY_HARD", 54000f, 3, 36, 4), // mushroom cloud spore burst
    BossSpecInfo("sentinel-spike", "SENTINEL-SPIKE", "EASY", 36000f, 7, 37, 2), // 360 turret base
    BossSpecInfo("sentinel-ring", "SENTINEL-RING", "MEDIUM", 46000f, 5, 38, 3), // dual rings & satellites
    BossSpecInfo("sentinel-cross", "SENTINEL-CROSS", "MEDIUM", 47000f, 4, 39, 3), // cruciform extending arms
    BossSpecInfo("sentinel-nest", "SENTINEL-NEST", "MEDIUM", 48000f, 8, 40, 3), // pod cluster drone hatch
    // 41..50 (Requires 1200 - 1600+ bullet hits)
    BossSpecInfo("dominion", "DOMINION", "VERY_HARD", 58000f, 9, 41, 4), // screen dividing death zones
    BossSpecInfo("sentinel-web", "SENTINEL-WEB", "HARD", 52000f, 5, 42, 4), // drone web electric lines
    BossSpecInfo("colossus-prime", "COLOSSUS-PRIME", "VERY_HARD", 65000f, 7, 43, 4), // fortress titan with hanging chains
    BossSpecInfo("spire-guard", "SPIRE-GUARD", "MEDIUM", 50000f, 4, 44, 3), // diamond spire node shields
    BossSpecInfo("ring-maiden", "RING-MAIDEN", "MEDIUM", 51000f, 6, 45, 3), // sine-wave whipping ribbons
    BossSpecInfo("cross-break", "CROSS-BREAK", "HARD", 56000f, 10, 46, 4), // separating & returning cross arms
    BossSpecInfo("nest-guard", "NEST-GUARD", "HARD", 60000f, 8, 47, 4), // peeling reinforced shell layers
    BossSpecInfo("ultimate", "ULTIMATE", "FINAL", 85000f, 6, 48, 5), // 3-phase composite sovereign
    BossSpecInfo("void-walker", "VOID-WALKER", "EASY", 42000f, 0, 49, 2), // intangible phasing entity
    BossSpecInfo("star-forge", "STAR-FORGE", "MEDIUM", 68000f, 4, 50, 4) // rotating pentagram with orbiting rune stones
)
