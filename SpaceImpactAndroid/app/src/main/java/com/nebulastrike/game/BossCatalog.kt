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
    // 1..10
    BossSpecInfo("prism", "PRISM", "MEDIUM", 1100f, 1, 1, 3), // refracts shots, 3 facets
    BossSpecInfo("cascade", "CASCADE", "EASY", 950f, 0, 2, 2), // rhythm gaps
    BossSpecInfo("gauntlet", "GAUNTLET", "MEDIUM", 1250f, 9, 3, 3), // shifting barrier pairs
    BossSpecInfo("vortex", "VORTEX", "HARD", 1600f, 5, 4, 3), // gravity pull
    BossSpecInfo("beacon", "BEACON", "EASY", 1000f, 7, 5, 2), // rotating light beam
    BossSpecInfo("splitter", "SPLITTER", "HARD", 1500f, 1, 6, 3), // splits into two entities
    BossSpecInfo("orbit", "ORBIT", "MEDIUM", 1300f, 3, 7, 3), // 8 orbiting shards
    BossSpecInfo("sawtooth", "SAWTOOTH", "MEDIUM", 1350f, 2, 8, 3), // serrated deflect & pulse
    BossSpecInfo("tower", "TOWER", "EASY", 1050f, 0, 9, 2), // window cutouts
    BossSpecInfo("mesh", "MESH", "MEDIUM", 1400f, 4, 10, 3), // wireframe squares
    // 11..20
    BossSpecInfo("flare", "FLARE", "EASY", 1100f, 1, 11, 2), // swoop attacks
    BossSpecInfo("delta", "DELTA", "MEDIUM", 1450f, 2, 12, 3), // delta wing spread
    BossSpecInfo("web", "WEB", "HARD", 1750f, 5, 13, 3), // spiral web retract/extend
    BossSpecInfo("prismatic", "PRISMATIC", "MEDIUM", 1500f, 4, 14, 3), // swapping segment keys
    BossSpecInfo("helix", "HELIX", "MEDIUM", 1550f, 6, 15, 3), // corkscrew spiral
    BossSpecInfo("lockbox", "LOCKBOX", "EASY", 1150f, 0, 16, 2), // opening padlock shackle
    BossSpecInfo("flareon", "FLAREON", "MEDIUM", 1600f, 2, 17, 3), // shoot/shield mode
    BossSpecInfo("obsidian", "OBSIDIAN", "HARD", 1900f, 10, 18, 4), // jagged monolith break-off
    BossSpecInfo("glyph", "GLYPH", "MEDIUM", 1650f, 4, 19, 3), // angular rune matrix
    BossSpecInfo("siphon", "SIPHON", "MEDIUM", 1700f, 7, 20, 3), // hourglass alternating bulbs
    // 21..30
    BossSpecInfo("pulse", "PULSE", "EASY", 1200f, 3, 21, 2), // expanding pulse ring
    BossSpecInfo("spirebreak", "SPIREBREAK", "MEDIUM", 1800f, 7, 22, 3), // falling tower spires
    BossSpecInfo("facet", "FACET", "MEDIUM", 1750f, 4, 23, 3), // multi-sided rotating face
    BossSpecInfo("vortex2", "VORTEX II", "HARD", 2100f, 5, 24, 4), // dual counter-rotating vortices
    BossSpecInfo("cradle", "CRADLE", "EASY", 1300f, 0, 25, 2), // rocking cradle expose core
    BossSpecInfo("skyline", "SKYLINE", "MEDIUM", 1850f, 9, 26, 3), // skyscraper silhouette row
    BossSpecInfo("echo", "ECHO", "MEDIUM", 1900f, 1, 27, 3), // boomerang looping return
    BossSpecInfo("needle", "NEEDLE", "EASY", 1350f, 10, 28, 2), // rapid horizontal oscillation
    BossSpecInfo("coil", "COIL", "MEDIUM", 1950f, 2, 29, 3), // spring compress & blast
    BossSpecInfo("sentinel-guard", "SENTINEL-GUARD", "EASY", 1400f, 3, 30, 2), // rotating panels
    // 31..40
    BossSpecInfo("rampart", "RAMPART", "MEDIUM", 2000f, 9, 31, 3), // fortified wall shifting crenellations
    BossSpecInfo("sentinel-core", "SENTINEL-CORE", "MEDIUM", 2100f, 4, 32, 3), // floating hub with accelerating rings
    BossSpecInfo("havoc", "HAVOC", "HARD", 2400f, 10, 33, 4), // jagged starburst random blasts
    BossSpecInfo("sentinel-wing", "SENTINEL-WING", "EASY", 1500f, 1, 34, 2), // wind gust projectiles
    BossSpecInfo("sentinel-tower", "SENTINEL-TOWER", "EASY", 1550f, 0, 35, 2), // command tower with rotating flag
    BossSpecInfo("cataclysm", "CATACLYSM", "VERY_HARD", 2800f, 3, 36, 4), // mushroom cloud spore burst
    BossSpecInfo("sentinel-spike", "SENTINEL-SPIKE", "EASY", 1600f, 7, 37, 2), // 360 turret base
    BossSpecInfo("sentinel-ring", "SENTINEL-RING", "MEDIUM", 2250f, 5, 38, 3), // dual rings & satellites
    BossSpecInfo("sentinel-cross", "SENTINEL-CROSS", "MEDIUM", 2300f, 4, 39, 3), // cruciform extending arms
    BossSpecInfo("sentinel-nest", "SENTINEL-NEST", "MEDIUM", 2350f, 8, 40, 3), // pod cluster drone hatch
    // 41..50
    BossSpecInfo("dominion", "DOMINION", "VERY_HARD", 3100f, 9, 41, 4), // screen dividing death zones
    BossSpecInfo("sentinel-web", "SENTINEL-WEB", "HARD", 2700f, 5, 42, 4), // drone web electric lines
    BossSpecInfo("colossus-prime", "COLOSSUS-PRIME", "VERY_HARD", 3400f, 7, 43, 4), // fortress titan with hanging chains
    BossSpecInfo("spire-guard", "SPIRE-GUARD", "MEDIUM", 2500f, 4, 44, 3), // diamond spire node shields
    BossSpecInfo("ring-maiden", "RING-MAIDEN", "MEDIUM", 2600f, 6, 45, 3), // sine-wave whipping ribbons
    BossSpecInfo("cross-break", "CROSS-BREAK", "HARD", 3000f, 10, 46, 4), // separating & returning cross arms
    BossSpecInfo("nest-guard", "NEST-GUARD", "HARD", 3200f, 8, 47, 4), // peeling reinforced shell layers
    BossSpecInfo("ultimate", "ULTIMATE", "FINAL", 5000f, 6, 48, 5), // 3-phase composite sovereign
    BossSpecInfo("custom-49", "DEVEL RED EYE", "EASY", 2000f, 0, 49, 3), // demonic ocular gaze
    BossSpecInfo("custom-50", "UNCENCED AI", "MEDIUM", 3500f, 4, 50, 4) // rogue cybernetic AI hyper-matrix
)
