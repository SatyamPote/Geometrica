package com.nebulastrike.game

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random
import kotlin.math.PI

// AI archetypes (portrait: enemies fly downward)
const val AI_DRIFT = 0
const val AI_TRACK = 1
const val AI_DIVE = 2
const val AI_WEAVE = 3
const val AI_GUN = 4
const val AI_BURST = 5
const val AI_SPIRAL = 6
const val AI_SHIELD = 7
const val AI_KAMI = 8
const val AI_SNIPER = 9
const val AI_SPLIT = 10
const val AI_STRAFE = 11

/** One of exactly 50 unique enemy designs. Never displayed; internal only. */
data class EnemySpec(
    val name: String,
    val ai: Int,
    val hp: Float,
    val spd: Float,
    val r: Float,
    val score: Int,
    val fire: Float,
    val p1: Float = 0f,
    val p2: Float = 0f,
    val split: Int = -1,
    val skill: String = ""
)

/** Exactly 50 distinct enemy planes, ordered easy -> brutal. */
val ENEMY_ROSTER = listOf(
    EnemySpec("drift-dia01", AI_DRIFT, 16.0f, 1.0f, 16.0f, 100, 2.5f, 0.0f, 0.0f, -1, "sway-shot"),
    EnemySpec("drift-boom02", AI_DRIFT, 16.0f, 1.0f, 17.5f, 105, 2.5f, 0.0f, 0.0f, -1, "boomerang-weave"),
    EnemySpec("drift-pent03", AI_DRIFT, 28.8f, 1.15f, 19.0f, 210, 2.17f, 0.0f, 0.0f, -1, "rotating-edges"),
    EnemySpec("drift-hex04", AI_DRIFT, 28.8f, 1.15f, 20.5f, 215, 2.17f, 0.0f, 0.0f, -1, "side-shoot"),
    EnemySpec("drift-star05", AI_DRIFT, 44.8f, 1.3f, 22.0f, 370, 1.92f, 0.0f, 0.0f, -1, "point-spread"),
    EnemySpec("drift-blob06", AI_DRIFT, 44.8f, 1.3f, 23.5f, 375, 1.92f, 0.0f, 0.0f, -1, "random-drift"),
    EnemySpec("drift-ring07", AI_DRIFT, 64.0f, 1.45f, 16.0f, 580, 1.72f, 0.0f, 0.0f, -1, "ring-interlock"),
    EnemySpec("drift-wave08", AI_DRIFT, 104.0f, 1.6f, 17.5f, 935, 1.56f, 0.0f, 0.0f, -1, "wave-drift"),
    EnemySpec("drift-need09", AI_DRIFT, 16.0f, 1.0f, 19.0f, 140, 2.5f, 0.0f, 0.0f, -1, "needle-weave"),
    EnemySpec("drift-cres10", AI_DRIFT, 28.8f, 1.15f, 20.5f, 245, 2.17f, 0.0f, 0.0f, -1, "crescent-sweep"),
    EnemySpec("track-tri01", AI_TRACK, 28.8f, 1.15f, 22.0f, 250, 2.17f, 0.0f, 0.0f, -1, "track-lead"),
    EnemySpec("track-dia02", AI_TRACK, 44.8f, 1.3f, 23.5f, 405, 1.92f, 0.0f, 0.0f, -1, "edge-shoot"),
    EnemySpec("track-hex03", AI_TRACK, 44.8f, 1.3f, 16.0f, 410, 1.92f, 0.0f, 0.0f, -1, "hex-track"),
    EnemySpec("track-star04", AI_TRACK, 64.0f, 1.45f, 17.5f, 615, 1.72f, 0.0f, 0.0f, -1, "star-track"),
    EnemySpec("track-pred05", AI_TRACK, 104.0f, 1.6f, 19.0f, 970, 1.56f, 0.0f, 0.0f, -1, "predict-track"),
    EnemySpec("track-cir06", AI_TRACK, 16.0f, 1.0f, 20.5f, 175, 2.5f, 0.0f, 0.0f, -1, "circle-lead"),
    EnemySpec("track-pent07", AI_TRACK, 28.8f, 1.15f, 22.0f, 280, 2.17f, 0.0f, 0.0f, -1, "pent-lead"),
    EnemySpec("dive-tri01", AI_DIVE, 16.0f, 1.0f, 23.5f, 185, 2.5f, 0.0f, 0.0f, -1, "drop-shot"),
    EnemySpec("dive-dia02", AI_DIVE, 28.8f, 1.15f, 16.0f, 290, 2.17f, 0.0f, 0.0f, -1, "diamond-dive"),
    EnemySpec("dive-spir03", AI_DIVE, 44.8f, 1.3f, 17.5f, 445, 1.92f, 0.0f, 0.0f, -1, "spiral-dive"),
    EnemySpec("dive-zig04", AI_DIVE, 44.8f, 1.3f, 19.0f, 450, 1.92f, 0.0f, 0.0f, -1, "zigzag-dive"),
    EnemySpec("dive-star05", AI_DIVE, 64.0f, 1.45f, 20.5f, 655, 1.72f, 0.0f, 0.0f, -1, "star-dive"),
    EnemySpec("dive-cir06", AI_DIVE, 16.0f, 1.0f, 22.0f, 210, 2.5f, 0.0f, 0.0f, -1, "circle-dive"),
    EnemySpec("dive-dia07", AI_DIVE, 28.8f, 1.15f, 23.5f, 315, 2.17f, 0.0f, 0.0f, -1, "diamond-gap"),
    EnemySpec("weave-wave01", AI_WEAVE, 28.8f, 1.15f, 16.0f, 320, 2.17f, 0.0f, 0.0f, -1, "wave-weave"),
    EnemySpec("weave-serp02", AI_WEAVE, 44.8f, 1.3f, 17.5f, 475, 1.92f, 0.0f, 0.0f, -1, "serpent-weave"),
    EnemySpec("weave-spir03", AI_WEAVE, 64.0f, 1.45f, 19.0f, 680, 1.72f, 0.0f, 0.0f, -1, "spiral-weave"),
    EnemySpec("weave-simple04", AI_WEAVE, 16.0f, 1.0f, 20.5f, 235, 2.5f, 0.0f, 0.0f, -1, "back-and-forth"),
    EnemySpec("weave-805", AI_WEAVE, 28.8f, 1.15f, 22.0f, 340, 2.17f, 0.0f, 0.0f, -1, "figure-8"),
    EnemySpec("weave-tang06", AI_WEAVE, 44.8f, 1.3f, 23.5f, 495, 1.92f, 0.0f, 0.0f, -1, "tangle-weave"),
    EnemySpec("gun-rect01", AI_GUN, 28.8f, 1.15f, 16.0f, 350, 2.17f, 2.0f, 0.0f, -1, "forward-barrage"),
    EnemySpec("gun-pent02", AI_GUN, 44.8f, 1.3f, 17.5f, 505, 1.92f, 3.0f, 0.0f, -1, "spread-pent"),
    EnemySpec("gun-hex03", AI_GUN, 44.8f, 1.3f, 19.0f, 510, 1.92f, 3.0f, 0.0f, -1, "radial-hex"),
    EnemySpec("gun-star04", AI_GUN, 64.0f, 1.45f, 20.5f, 715, 1.72f, 3.0f, 0.0f, -1, "star-all"),
    EnemySpec("gun-cplx05", AI_GUN, 104.0f, 1.6f, 22.0f, 1070, 1.56f, 3.0f, 0.0f, -1, "complex-barrage"),
    EnemySpec("gun-tri06", AI_GUN, 16.0f, 1.0f, 23.5f, 275, 2.5f, 2.0f, 0.0f, -1, "tri-forward"),
    EnemySpec("gun-dia07", AI_GUN, 28.8f, 1.15f, 16.0f, 380, 2.17f, 2.0f, 0.0f, -1, "diamond-spread"),
    EnemySpec("burst-cir01", AI_BURST, 16.0f, 1.0f, 17.5f, 285, 2.5f, 4.0f, 0.0f, -1, "burst-small"),
    EnemySpec("burst-tri02", AI_BURST, 28.8f, 1.15f, 19.0f, 390, 2.17f, 4.0f, 0.0f, -1, "burst-tri"),
    EnemySpec("burst-pent03", AI_BURST, 44.8f, 1.3f, 20.5f, 545, 1.92f, 4.0f, 0.0f, -1, "burst-pent"),
    EnemySpec("burst-hex04", AI_BURST, 44.8f, 1.3f, 22.0f, 550, 1.92f, 4.0f, 0.0f, -1, "burst-hex"),
    EnemySpec("burst-star05", AI_BURST, 64.0f, 1.45f, 23.5f, 755, 1.72f, 4.0f, 0.0f, -1, "burst-star"),
    EnemySpec("burst-irr06", AI_BURST, 104.0f, 1.6f, 16.0f, 1110, 1.56f, 4.0f, 0.0f, -1, "burst-irr"),
    EnemySpec("burst-dia07", AI_BURST, 16.0f, 1.0f, 17.5f, 315, 2.5f, 4.0f, 0.0f, -1, "burst-dia"),
    EnemySpec("spiral-clock01", AI_SPIRAL, 28.8f, 1.15f, 19.0f, 420, 2.17f, 8.0f, 0.0f, -1, "spiral-out"),
    EnemySpec("spiral-ccw02", AI_SPIRAL, 44.8f, 1.3f, 20.5f, 575, 1.92f, 8.0f, 0.0f, -1, "spiral-ccw"),
    EnemySpec("spiral-dual03", AI_SPIRAL, 64.0f, 1.45f, 22.0f, 780, 1.72f, 8.0f, 0.0f, -1, "dual-spiral"),
    EnemySpec("spiral-coil04", AI_SPIRAL, 16.0f, 1.0f, 23.5f, 335, 2.5f, 8.0f, 0.0f, -1, "coil-simple"),
    EnemySpec("spiral-tight05", AI_SPIRAL, 28.8f, 1.15f, 16.0f, 440, 2.17f, 8.0f, 0.0f, -1, "tight-spiral"),
    EnemySpec("spiral-loose06", AI_SPIRAL, 44.8f, 1.3f, 17.5f, 595, 1.92f, 8.0f, 0.0f, -1, "loose-spiral"),
    EnemySpec("shield-circle01", AI_SHIELD, 28.8f, 1.15f, 19.0f, 450, 2.17f, 2.0f, 1.0f, -1, "shield-reg"),
    EnemySpec("shield-hex02", AI_SHIELD, 44.8f, 1.3f, 20.5f, 605, 1.92f, 2.0f, 1.0f, -1, "shield-hex"),
    EnemySpec("shield-multi03", AI_SHIELD, 64.0f, 1.45f, 22.0f, 810, 1.72f, 2.0f, 1.0f, -1, "multi-ring"),
    EnemySpec("shield-irr05", AI_SHIELD, 104.0f, 1.6f, 23.5f, 1165, 1.56f, 2.0f, 1.0f, -1, "irreg-shield"),
    EnemySpec("shield-small05", AI_SHIELD, 16.0f, 1.0f, 16.0f, 370, 2.5f, 2.0f, 1.0f, -1, "small-shield"),
    EnemySpec("shield-pent06", AI_SHIELD, 28.8f, 1.15f, 17.5f, 475, 2.17f, 2.0f, 1.0f, -1, "shield-pent"),
    EnemySpec("kami-tri01", AI_KAMI, 16.0f, 1.0f, 19.0f, 380, 2.5f, 0.0f, 0.0f, -1, "tri-crash"),
    EnemySpec("kami-dia02", AI_KAMI, 28.8f, 1.15f, 20.5f, 485, 2.17f, 0.0f, 0.0f, -1, "dia-crash"),
    EnemySpec("kami-pent03", AI_KAMI, 44.8f, 1.3f, 22.0f, 640, 1.92f, 0.0f, 0.0f, -1, "pent-crash"),
    EnemySpec("kami-hex04", AI_KAMI, 44.8f, 1.3f, 23.5f, 645, 1.92f, 0.0f, 0.0f, -1, "hex-crash"),
    EnemySpec("kami-star05", AI_KAMI, 64.0f, 1.45f, 16.0f, 850, 1.72f, 0.0f, 0.0f, -1, "star-crash"),
    EnemySpec("kami-irr06", AI_KAMI, 104.0f, 1.6f, 17.5f, 1205, 1.56f, 0.0f, 0.0f, -1, "irr-crash"),
    EnemySpec("kami-sml07", AI_KAMI, 16.0f, 1.0f, 19.0f, 410, 2.5f, 0.0f, 0.0f, -1, "small-kami"),
    EnemySpec("sniper-long01", AI_SNIPER, 28.8f, 1.15f, 20.5f, 515, 2.17f, 0.0f, 0.0f, -1, "sniper-long"),
    EnemySpec("sniper-pent02", AI_SNIPER, 44.8f, 1.3f, 22.0f, 670, 1.92f, 0.0f, 0.0f, -1, "sniper-pent"),
    EnemySpec("sniper-hex03", AI_SNIPER, 44.8f, 1.3f, 23.5f, 675, 1.92f, 0.0f, 0.0f, -1, "sniper-hex"),
    EnemySpec("sniper-star04", AI_SNIPER, 64.0f, 1.45f, 16.0f, 880, 1.72f, 0.0f, 0.0f, -1, "sniper-star"),
    EnemySpec("sniper-irr05", AI_SNIPER, 104.0f, 1.6f, 17.5f, 1235, 1.56f, 0.0f, 0.0f, -1, "sniper-irr"),
    EnemySpec("sniper-tri06", AI_SNIPER, 16.0f, 1.0f, 19.0f, 440, 2.5f, 0.0f, 0.0f, -1, "sniper-tri"),
    EnemySpec("sniper-dia07", AI_SNIPER, 28.8f, 1.15f, 20.5f, 545, 2.17f, 0.0f, 0.0f, -1, "sniper-dia"),
    EnemySpec("split-tri01", AI_SPLIT, 28.8f, 1.15f, 22.0f, 550, 2.17f, 2.0f, 0.0f, 71, "split-two"),
    EnemySpec("split-dia02", AI_SPLIT, 44.8f, 1.3f, 23.5f, 705, 1.92f, 2.0f, 0.0f, 72, "split-dia"),
    EnemySpec("split-star03", AI_SPLIT, 64.0f, 1.45f, 16.0f, 910, 1.72f, 2.0f, 0.0f, 73, "split-star"),
    EnemySpec("split-irr04", AI_SPLIT, 104.0f, 1.6f, 17.5f, 1265, 1.56f, 2.0f, 0.0f, 74, "split-irr"),
    EnemySpec("split-cir05", AI_SPLIT, 16.0f, 1.0f, 19.0f, 470, 2.5f, 2.0f, 0.0f, 75, "split-cir"),
    EnemySpec("split-pent06", AI_SPLIT, 28.8f, 1.15f, 20.5f, 575, 2.17f, 2.0f, 0.0f, 76, "split-pent"),
    EnemySpec("strafe-wide01", AI_STRAFE, 28.8f, 1.15f, 22.0f, 580, 2.17f, 0.0f, 0.0f, -1, "strafe-wide"),
    EnemySpec("strafe-dia02", AI_STRAFE, 44.8f, 1.3f, 23.5f, 735, 1.92f, 0.0f, 0.0f, -1, "strafe-dia"),
    EnemySpec("strafe-irr03", AI_STRAFE, 64.0f, 1.45f, 16.0f, 940, 1.72f, 0.0f, 0.0f, -1, "strafe-irr"),
    EnemySpec("strafe-cir04", AI_STRAFE, 16.0f, 1.0f, 17.5f, 495, 2.5f, 0.0f, 0.0f, -1, "strafe-cir"),
    EnemySpec("strafe-pent05", AI_STRAFE, 28.8f, 1.15f, 19.0f, 600, 2.17f, 0.0f, 0.0f, -1, "strafe-pent"),
    EnemySpec("strafe-hex06", AI_STRAFE, 44.8f, 1.3f, 20.5f, 755, 1.92f, 0.0f, 0.0f, -1, "strafe-hex"),
    EnemySpec("drift-rect11", AI_DRIFT, 28.8f, 1.15f, 22.0f, 610, 2.17f, 0.0f, 0.0f, -1, "rect-drift"),
    EnemySpec("drift-para12", AI_DRIFT, 44.8f, 1.3f, 23.5f, 765, 1.92f, 0.0f, 0.0f, -1, "para-drift"),
    EnemySpec("track-oct08", AI_TRACK, 28.8f, 1.15f, 16.0f, 620, 2.17f, 0.0f, 0.0f, -1, "oct-track"),
    EnemySpec("dive-oct08", AI_DIVE, 28.8f, 1.15f, 17.5f, 625, 2.17f, 0.0f, 0.0f, -1, "dive-oct"),
    EnemySpec("weave-8h08", AI_WEAVE, 28.8f, 1.15f, 19.0f, 630, 2.17f, 0.0f, 0.0f, -1, "weave-8h"),
    EnemySpec("gun-oct08", AI_GUN, 44.8f, 1.3f, 20.5f, 785, 1.92f, 3.0f, 0.0f, -1, "gun-oct"),
    EnemySpec("burst-oct08", AI_BURST, 28.8f, 1.15f, 22.0f, 640, 2.17f, 4.0f, 0.0f, -1, "burst-oct"),
    EnemySpec("spiral-tri07", AI_SPIRAL, 64.0f, 1.45f, 23.5f, 995, 1.72f, 8.0f, 0.0f, -1, "tri-spiral"),
    EnemySpec("shield-oct07", AI_SHIELD, 104.0f, 1.6f, 16.0f, 1350, 1.56f, 2.0f, 1.0f, -1, "oct-shield"),
    EnemySpec("kami-oct08", AI_KAMI, 64.0f, 1.45f, 17.5f, 1005, 1.72f, 0.0f, 0.0f, -1, "oct-kami"),
    EnemySpec("sniper-oct08", AI_SNIPER, 64.0f, 1.45f, 19.0f, 1010, 1.72f, 0.0f, 0.0f, -1, "sniper-oct"),
    EnemySpec("split-oct07", AI_SPLIT, 104.0f, 1.6f, 20.5f, 1365, 1.56f, 2.0f, 0.0f, 94, "split-oct"),
    EnemySpec("strafe-oct07", AI_STRAFE, 64.0f, 1.45f, 22.0f, 1020, 1.72f, 0.0f, 0.0f, -1, "strafe-oct"),
    EnemySpec("drift-arrow96", AI_DRIFT, 16.0f, 1.0f, 23.5f, 575, 2.5f, 0.0f, 0.0f, -1, "arrow-drift"),
    EnemySpec("track-star97", AI_TRACK, 28.8f, 1.15f, 16.0f, 680, 2.17f, 0.0f, 0.0f, -1, "star-track"),
    EnemySpec("dive-star98", AI_DIVE, 28.8f, 1.15f, 17.5f, 685, 2.17f, 0.0f, 0.0f, -1, "dive-star"),
    EnemySpec("weave-cx99", AI_WEAVE, 44.8f, 1.3f, 19.0f, 840, 1.92f, 0.0f, 0.0f, -1, "weave-complex"),
    EnemySpec("gun-irr100", AI_GUN, 104.0f, 1.6f, 20.5f, 1395, 1.56f, 3.0f, 0.0f, -1, "gun-irr"),
)

// Box obstacle kinds (portrait)
const val BOX_BLOCK_S = 0
const val BOX_BLOCK_L = 1
const val BOX_DRIFT = 2
const val BOX_WALL_V = 3 // horizontal wall, left-right gap
const val BOX_WALL_H = 4 // side bars, middle vertical lane
const val BOX_NARROW = 5
const val BOX_SPIN = 6
const val BOX_TANK = 7
const val BOX_FAST = 8
const val BOX_CLUSTER = 9

/** One of exactly 100 unique obstacle designs. Never displayed; internal only. */
data class BoxSpec(
    val kind: Int,
    val w: Float,
    val h: Float,
    val hp: Float,
    val speed: Float,
    val gap: Float,
    val drift: Float,
    val spin: Float,
    val cluster: Int = 0
)

/** Exactly 100 distinct obstacle boxes, ordered easy -> brutal. */
val BOX_ROSTER = listOf(
    BoxSpec(BOX_BLOCK_S, 60f, 60f, 20f, 1f, 0f, 0f, 0f),
    BoxSpec(BOX_BLOCK_S, 80f, 50f, 25f, 1f, 0f, 0f, 0f),
    BoxSpec(BOX_BLOCK_S, 50f, 90f, 25f, 1f, 0f, 0f, 0f),
    BoxSpec(BOX_BLOCK_S, 70f, 70f, 30f, 1f, 0f, 0f, 0f),
    BoxSpec(BOX_BLOCK_S, 90f, 60f, 35f, 1.05f, 0f, 0f, 0f),
    BoxSpec(BOX_BLOCK_S, 60f, 100f, 35f, 1.05f, 0f, 0f, 0f),
    BoxSpec(BOX_BLOCK_S, 100f, 80f, 40f, 1.1f, 0f, 0f, 0f),
    BoxSpec(BOX_BLOCK_S, 55f, 55f, 15f, 1.1f, 0f, 0f, 0f),
    BoxSpec(BOX_BLOCK_S, 85f, 85f, 45f, 1.15f, 0f, 0f, 0f),
    BoxSpec(BOX_BLOCK_S, 120f, 60f, 50f, 1.15f, 0f, 0f, 0f),
    BoxSpec(BOX_BLOCK_L, 120f, 120f, 0f, 1f, 0f, 0f, 0f),
    BoxSpec(BOX_BLOCK_L, 160f, 90f, 0f, 1f, 0f, 0f, 0f),
    BoxSpec(BOX_BLOCK_L, 90f, 160f, 0f, 1f, 0f, 0f, 0f),
    BoxSpec(BOX_BLOCK_L, 200f, 120f, 0f, 1.05f, 0f, 0f, 0f),
    BoxSpec(BOX_BLOCK_L, 140f, 140f, 0f, 1.05f, 0f, 0f, 0f),
    BoxSpec(BOX_BLOCK_L, 110f, 200f, 0f, 1.1f, 0f, 0f, 0f),
    BoxSpec(BOX_BLOCK_L, 220f, 100f, 0f, 1.1f, 0f, 0f, 0f),
    BoxSpec(BOX_BLOCK_L, 100f, 240f, 0f, 1.15f, 0f, 0f, 0f),
    BoxSpec(BOX_BLOCK_L, 180f, 180f, 0f, 1.15f, 0f, 0f, 0f),
    BoxSpec(BOX_BLOCK_L, 260f, 140f, 0f, 1.2f, 0f, 0f, 0f),
    BoxSpec(BOX_DRIFT, 70f, 70f, 0f, 1f, 0f, 120f, 0f),
    BoxSpec(BOX_DRIFT, 90f, 90f, 0f, 1f, 0f, -140f, 0f),
    BoxSpec(BOX_DRIFT, 80f, 80f, 0f, 1.05f, 0f, 180f, 0f),
    BoxSpec(BOX_DRIFT, 100f, 100f, 0f, 1.05f, 0f, -200f, 0f),
    BoxSpec(BOX_DRIFT, 60f, 120f, 0f, 1.1f, 0f, 150f, 0f),
    BoxSpec(BOX_DRIFT, 120f, 60f, 0f, 1.1f, 0f, -170f, 0f),
    BoxSpec(BOX_DRIFT, 85f, 85f, 0f, 1.15f, 0f, 220f, 0f),
    BoxSpec(BOX_DRIFT, 95f, 95f, 0f, 1.15f, 0f, -240f, 0f),
    BoxSpec(BOX_DRIFT, 75f, 140f, 0f, 1.2f, 0f, 160f, 0f),
    BoxSpec(BOX_DRIFT, 140f, 75f, 0f, 1.2f, 0f, -190f, 0f),
    BoxSpec(BOX_WALL_V, 46f, 0f, 0f, 1f, 340f, 0f, 0f),
    BoxSpec(BOX_WALL_V, 48f, 0f, 0f, 1f, 320f, 0f, 0f),
    BoxSpec(BOX_WALL_V, 50f, 0f, 0f, 1.05f, 300f, 0f, 0f),
    BoxSpec(BOX_WALL_V, 52f, 0f, 0f, 1.05f, 280f, 0f, 0f),
    BoxSpec(BOX_WALL_V, 54f, 0f, 0f, 1.1f, 260f, 0f, 0f),
    BoxSpec(BOX_WALL_V, 56f, 0f, 0f, 1.1f, 240f, 0f, 0f),
    BoxSpec(BOX_WALL_V, 58f, 0f, 0f, 1.15f, 220f, 0f, 0f),
    BoxSpec(BOX_WALL_V, 60f, 0f, 0f, 1.15f, 200f, 0f, 0f),
    BoxSpec(BOX_WALL_V, 62f, 0f, 0f, 1.2f, 190f, 0f, 0f),
    BoxSpec(BOX_WALL_V, 64f, 0f, 0f, 1.2f, 180f, 0f, 0f),
    BoxSpec(BOX_WALL_H, 200f, 44f, 0f, 1f, 360f, 0f, 0f),
    BoxSpec(BOX_WALL_H, 220f, 46f, 0f, 1f, 340f, 0f, 0f),
    BoxSpec(BOX_WALL_H, 240f, 48f, 0f, 1.05f, 320f, 0f, 0f),
    BoxSpec(BOX_WALL_H, 260f, 50f, 0f, 1.05f, 300f, 0f, 0f),
    BoxSpec(BOX_WALL_H, 280f, 52f, 0f, 1.1f, 280f, 0f, 0f),
    BoxSpec(BOX_WALL_H, 300f, 54f, 0f, 1.1f, 260f, 0f, 0f),
    BoxSpec(BOX_WALL_H, 320f, 56f, 0f, 1.15f, 240f, 0f, 0f),
    BoxSpec(BOX_WALL_H, 340f, 58f, 0f, 1.15f, 220f, 0f, 0f),
    BoxSpec(BOX_WALL_H, 360f, 60f, 0f, 1.2f, 200f, 0f, 0f),
    BoxSpec(BOX_WALL_H, 380f, 62f, 0f, 1.2f, 190f, 0f, 0f),
    BoxSpec(BOX_NARROW, 54f, 0f, 0f, 1.1f, 200f, 0f, 0f),
    BoxSpec(BOX_NARROW, 56f, 0f, 0f, 1.1f, 190f, 0f, 0f),
    BoxSpec(BOX_NARROW, 58f, 0f, 0f, 1.15f, 180f, 0f, 0f),
    BoxSpec(BOX_NARROW, 60f, 0f, 0f, 1.15f, 170f, 0f, 0f),
    BoxSpec(BOX_NARROW, 62f, 0f, 0f, 1.2f, 160f, 0f, 0f),
    BoxSpec(BOX_NARROW, 64f, 0f, 0f, 1.2f, 150f, 0f, 0f),
    BoxSpec(BOX_NARROW, 66f, 0f, 0f, 1.25f, 155f, 0f, 0f),
    BoxSpec(BOX_NARROW, 68f, 0f, 0f, 1.25f, 150f, 0f, 0f),
    BoxSpec(BOX_NARROW, 70f, 0f, 0f, 1.3f, 150f, 0f, 0f),
    BoxSpec(BOX_NARROW, 72f, 0f, 0f, 1.3f, 150f, 0f, 0f),
    BoxSpec(BOX_SPIN, 80f, 0f, 0f, 1f, 0f, 0f, 1.5f),
    BoxSpec(BOX_SPIN, 100f, 0f, 0f, 1f, 0f, 0f, -2f),
    BoxSpec(BOX_SPIN, 120f, 0f, 0f, 1.05f, 0f, 0f, 2.5f),
    BoxSpec(BOX_SPIN, 90f, 0f, 0f, 1.05f, 0f, 0f, -3f),
    BoxSpec(BOX_SPIN, 140f, 0f, 0f, 1.1f, 0f, 0f, 1.8f),
    BoxSpec(BOX_SPIN, 110f, 0f, 0f, 1.1f, 0f, 0f, -2.2f),
    BoxSpec(BOX_SPIN, 130f, 0f, 0f, 1.15f, 0f, 0f, 3.2f),
    BoxSpec(BOX_SPIN, 96f, 0f, 0f, 1.15f, 0f, 0f, -1.6f),
    BoxSpec(BOX_SPIN, 150f, 0f, 0f, 1.2f, 0f, 0f, 2f),
    BoxSpec(BOX_SPIN, 116f, 0f, 0f, 1.2f, 0f, 0f, -2.6f),
    BoxSpec(BOX_TANK, 90f, 90f, 120f, 1f, 0f, 0f, 0f),
    BoxSpec(BOX_TANK, 120f, 70f, 150f, 1f, 0f, 0f, 0f),
    BoxSpec(BOX_TANK, 70f, 120f, 150f, 1.05f, 0f, 0f, 0f),
    BoxSpec(BOX_TANK, 140f, 100f, 220f, 1.05f, 0f, 0f, 0f),
    BoxSpec(BOX_TANK, 100f, 140f, 220f, 1.1f, 0f, 0f, 0f),
    BoxSpec(BOX_TANK, 160f, 120f, 300f, 1.1f, 0f, 0f, 0f),
    BoxSpec(BOX_TANK, 120f, 160f, 300f, 1.15f, 0f, 0f, 0f),
    BoxSpec(BOX_TANK, 180f, 140f, 380f, 1.15f, 0f, 0f, 0f),
    BoxSpec(BOX_TANK, 140f, 180f, 380f, 1.2f, 0f, 0f, 0f),
    BoxSpec(BOX_TANK, 200f, 160f, 450f, 1.2f, 0f, 0f, 0f),
    BoxSpec(BOX_FAST, 50f, 50f, 0f, 2.2f, 0f, 0f, 0f),
    BoxSpec(BOX_FAST, 60f, 40f, 0f, 2.5f, 0f, 0f, 0f),
    BoxSpec(BOX_FAST, 40f, 60f, 0f, 2.8f, 0f, 0f, 0f),
    BoxSpec(BOX_FAST, 55f, 55f, 0f, 3f, 0f, 0f, 0f),
    BoxSpec(BOX_FAST, 45f, 45f, 0f, 3.2f, 0f, 0f, 0f),
    BoxSpec(BOX_FAST, 65f, 50f, 0f, 2.4f, 0f, 0f, 0f),
    BoxSpec(BOX_FAST, 50f, 65f, 0f, 2.6f, 0f, 0f, 0f),
    BoxSpec(BOX_FAST, 70f, 70f, 0f, 2.2f, 0f, 0f, 0f),
    BoxSpec(BOX_FAST, 42f, 42f, 0f, 3.4f, 0f, 0f, 0f),
    BoxSpec(BOX_FAST, 58f, 58f, 25f, 2f, 0f, 0f, 0f),
    BoxSpec(BOX_CLUSTER, 0f, 0f, 0f, 1f, 0f, 0f, 0f, cluster = 0),
    BoxSpec(BOX_CLUSTER, 0f, 0f, 0f, 1f, 0f, 0f, 0f, cluster = 1),
    BoxSpec(BOX_CLUSTER, 0f, 0f, 0f, 1.05f, 0f, 0f, 0f, cluster = 2),
    BoxSpec(BOX_CLUSTER, 0f, 0f, 0f, 1.05f, 0f, 0f, 0f, cluster = 3),
    BoxSpec(BOX_CLUSTER, 0f, 0f, 0f, 1.1f, 0f, 0f, 0f, cluster = 4),
    BoxSpec(BOX_CLUSTER, 0f, 0f, 0f, 1.1f, 0f, 0f, 0f, cluster = 5),
    BoxSpec(BOX_CLUSTER, 0f, 0f, 0f, 1.15f, 0f, 0f, 0f, cluster = 6),
    BoxSpec(BOX_CLUSTER, 0f, 0f, 0f, 1.15f, 0f, 0f, 0f, cluster = 7),
    BoxSpec(BOX_CLUSTER, 0f, 0f, 0f, 1.2f, 0f, 0f, 0f, cluster = 8),
    BoxSpec(BOX_CLUSTER, 0f, 0f, 0f, 1.2f, 0f, 0f, 0f, cluster = 9)
)

class Input {
    var mx = 0f
    var my = 0f
    var firing = false
    fun clearMove() {
        mx = 0f; my = 0f
    }
}

interface WorldListener {
    fun gameOver(score: Int)
    fun runComplete()
}

class Player {
    var x = 0f
    var y = 0f
    var hits = 15
    var maxHits = 15
    var energy = 100f
    var maxEnergy = 100f
    var invuln = 0f
    var fireCd = 0f
    var rapidT = 0f
    var doubleT = 0f
    var spreadT = 0f
    var pierceT = 0f
    var skillActive = false
    var dead = false
    var deathT = 0f
    var missileTimer = 10f
    var laserFiring = false
}

class Missile {
    var x = 0f; var y = 0f; var vx = 0f; var vy = -400f
    var r = 9f; var dmg = 180f; var life = 5.0f
    var speed = 620f
    var trailT = 0f
}

class Bullet {
    var x = 0f; var y = 0f; var vx = 0f; var vy = 0f
    var r = 7f; var dmg = 12f; var foe = false
    var big = false; var pierce = false; var life = 3f
}

class Enemy {
    var spec = 0
    var ai = 0
    var x = 0f; var y = 0f; var vx = 0f; var vy = 0f
    var hp = 10f; var maxHp = 10f
    var spd = 1f; var r = 18f; var score = 50; var fire = 99f
    var p1 = 0f; var p2 = 0f; var split = -1
    var t = 0f; var fireT = 1f
    var flash = 0f
    var seed = 0f
    var state = 0
    var stateT = 0f
    var tx = 0f; var ty = 0f
    var shieldUp = true
    var gone = false
}

class Item {
    var kind = 0
    var x = 0f; var y = 0f; var t = 0f
}

class Particle {
    var x = 0f; var y = 0f; var vx = 0f; var vy = 0f
    var life = 0f; var maxLife = 1f; var size = 5f
}

class FloatText {
    var x = 0f; var y = 0f; var text = ""
    var life = 0f
}

class Rock {
    var x = 0f; var y = 0f; var vx = 0f; var vy = 0f
    var r = 30f; var hp = 30f; var rot = 0f; var spin = 1f
}

class Box {
    var spec = 0
    var kind = 0
    var x = 0f; var y = 0f
    var baseX = 0f
    var w = 60f; var h = 60f; var r = 30f
    var hp = 0f; var maxHp = 0f
    var rot = 0f; var spin = 0f
    var gapX = 0f; var gapW = 260f // WALL_V / NARROW: horizontal gap
    var gapY = 0f; var gapH = 260f // WALL_H: vertical lane
    var hot = false; var t = 0f
    var passed = false
    var flash = 0f
    var gone = false
}

class Beam {
    var x1 = 0f; var y1 = 0f; var x2 = 0f; var y2 = 0f
    var w = 22f; var tele = 0.9f; var active = 0.8f
    var hitCd = 0f
}

// ================================================================
class World(val save: Save, val sound: Sound) {

    init {
        require(ENEMY_ROSTER.size == 100) { "enemy roster must be exactly 100" }
        require(BOX_ROSTER.size == 100) { "box roster must be exactly 100" }
    }

    lateinit var listener: WorldListener
    val rnd = Random(System.nanoTime())
    val input = Input()

    var w = 720f
    var h = 1280f

    val player = Player()
    val shots = mutableListOf<Bullet>()
    val missiles = mutableListOf<Missile>()
    val foeShots = mutableListOf<Bullet>()
    val enemies = mutableListOf<Enemy>()
    val items = mutableListOf<Item>()
    val parts = mutableListOf<Particle>()
    val texts = mutableListOf<FloatText>()
    val rocks = mutableListOf<Rock>()
    val boxes = mutableListOf<Box>()
    val beams = mutableListOf<Beam>()
    var boss: Boss? = null

    var score = 0
    var streak = 0
    var streakT = 0f
    var maxStreak = 0
    var runCoins = 0
    var bossKills = 0
    var progress = 0f // hidden 0..1 journey gauge (never displayed)
    var elapsed = 0f
    var shake = 0f
    var over = false
    var kills = 0

    var spawnT = 2f
    var boxT = 6f
    var lullT = 0f
    var calmClock = 0f
    var bossAt = bossSchedule().first
    var bossIds = bossSchedule().second
    var warn: String? = null
    var warnT = 0f
    var bossPending: String? = null
    var bossDelay = 0f
    var bossGap = 0f // minimum wave-time between boss fights (waves last >=30s)
    var shieldMsgT = 0f

    var hpM = 1f
    var spdM = 1f
    var fireM = 1f
    var shotSpd = 1f
    var scrollM = 1f
    var gapM = 1.15f

    /** 50 unique bosses across the journey with custom skills and designs. */
    private fun bossSchedule(): Pair<MutableList<Float>, MutableList<String>> {
        val marks = mutableListOf<Float>()
        val ids = mutableListOf<String>()
        for (i in 0 until 50) {
            marks.add(0.03f + i * (0.92f / 49f))
            ids.add(ALL_50_BOSS_SPECS[i].id)
        }
        return Pair(marks, ids)
    }

    private fun sstep(p: Float): Float {
        val c = p.coerceIn(0f, 1f)
        return c * c * (3f - 2f * c)
    }

    fun reset() {
        shots.clear(); missiles.clear(); foeShots.clear(); enemies.clear(); items.clear()
        parts.clear(); texts.clear(); rocks.clear(); boxes.clear(); beams.clear()
        boss = null
        score = 0; streak = 0; streakT = 0f; maxStreak = 0
        runCoins = 0; bossKills = 0
        progress = 0f; elapsed = 0f; shake = 0f
        over = false; kills = 0
        spawnT = 2f; boxT = 6f; lullT = 0f; calmClock = 0f
        val sched = bossSchedule()
        bossAt = sched.first
        bossIds = sched.second
        warn = null; warnT = 0f; bossPending = null; shieldMsgT = 0f
        bossGap = 0f
        val p = player
        val sIdx = save.shipIndex()
        val ship = ALL_100_PLAYER_SHIPS[sIdx]
        p.x = w / 2f; p.y = h * 0.78f
        val effectiveHp = ship.health + save.upHp(sIdx) + save.upElite(sIdx) * 20
        p.maxHits = (effectiveHp / 5).coerceIn(6, 40)
        p.hits = p.maxHits
        val effectiveEnergy = (ship.energy * 25f) + save.upDur(sIdx) * 10f + save.upElite(sIdx) * 15f
        p.maxEnergy = effectiveEnergy
        p.energy = p.maxEnergy
        p.invuln = 1f; p.fireCd = 0f
        p.rapidT = 0f; p.doubleT = 0f; p.spreadT = 0f; p.pierceT = 0f
        p.dead = false; p.deathT = 0f
        input.firing = false
    }

    // ---------------- helpers ----------------
    fun foeShot(x: Float, y: Float, vx: Float, vy: Float, r: Float = 8f) {
        if (foeShots.size > 420) foeShots.removeAt(0)
        val b = Bullet()
        b.x = x; b.y = y; b.vx = vx; b.vy = vy; b.r = r
        b.foe = true
        foeShots.add(b)
    }

    fun aimed(x: Float, y: Float, speed: Float, r: Float = 8f) {
        val dx = player.x - x
        val dy = player.y - y
        val d = hypot(dx, dy).coerceAtLeast(1f)
        foeShot(x, y, dx / d * (speed * 0.55f), dy / d * (speed * 0.55f), r)
    }

    fun radial(x: Float, y: Float, n: Int, speed: Float, off: Double) {
        for (i in 0 until n) {
            val a = off + i * 2 * PI / n
            foeShot(x, y, (cos(a) * speed).toFloat(), (sin(a) * speed).toFloat())
        }
    }

    fun spreadAt(x: Float, y: Float, n: Int, speed: Float) {
        val base = atan2(player.y - y, player.x - x)
        for (i in 0 until n) {
            val a = base - 0.22f * (n - 1) / 2f + 0.22f * i
            foeShot(x, y, cos(a) * speed, sin(a) * speed)
        }
    }

    // boss ordnance flies ~20% slower: hard but dodgeable
    fun bShot(x: Float, y: Float, vx: Float, vy: Float, r: Float = 8f) = foeShot(x, y, vx * 0.8f, vy * 0.8f, r)
    fun bAimed(x: Float, y: Float, speed: Float, r: Float = 8f) = aimed(x, y, speed * 0.8f, r)
    fun bRadial(x: Float, y: Float, n: Int, speed: Float, off: Double) = radial(x, y, n, speed * 0.8f, off)

    fun explode(x: Float, y: Float, n: Int, big: Boolean) {
        for (i in 0 until n) {
            if (parts.size > 380) return
            val p = Particle()
            val a = rnd.nextFloat() * 2 * PI
            val s = (0.3f + rnd.nextFloat() * 0.7f) * (if (big) 520f else 340f)
            p.x = x; p.y = y
            p.vx = (cos(a) * s).toFloat()
            p.vy = (sin(a) * s).toFloat() + 120f
            p.maxLife = 0.35f + rnd.nextFloat() * 0.45f; p.life = p.maxLife
            p.size = (if (big) 7f else 5f) * (0.5f + rnd.nextFloat())
            parts.add(p)
        }
    }

    fun addText(x: Float, y: Float, s: String) {
        val t = FloatText()
        t.x = x; t.y = y; t.text = s; t.life = 1f
        texts.add(t)
    }

    fun shieldedMsg(x: Float, y: Float) {
        if (shieldMsgT > 0) return
        shieldMsgT = 2f
        addText(x, y, "SHIELDED")
    }

    fun addScore(base: Int, x: Float, y: Float) {
        val mult = (1 + streak / 10).coerceAtMost(5)
        val pts = base * mult
        score += pts
        streak++
        if (streak > maxStreak) maxStreak = streak
        streakT = 3.2f
        addText(x, y, "+$pts")
        // shop-combo-system: combo streak multiplier increases coin drops
        val earnedCoins = (mult * (base / 10).coerceAtLeast(1))
        save.addCoins(earnedCoins)
        runCoins += earnedCoins
    }

    // ---------------- firing ----------------
    private fun firePlayer() {
        val p = player
        val sIdx = save.shipIndex()
        val ship = ALL_100_PLAYER_SHIPS[sIdx]
        val effectiveSpd = ship.speed + save.upSpd(sIdx) + save.upElite(sIdx)
        val baseInterval = (0.16f - effectiveSpd * 0.012f).coerceAtLeast(0.05f)
        val interval = if (p.rapidT > 0 || ship.skill.contains("rapid")) baseInterval * 0.7f else baseInterval
        if (p.fireCd > 0) return
        p.fireCd = interval

        // Firing consumes a small amount of ship energy (1.5 units)
        // Energy recharges automatically when not firing or between shots
        p.energy = (p.energy - 1.5f).coerceAtLeast(0f)
        val energyBoost = if (p.energy > p.maxEnergy * 0.25f) 1.25f else 1.0f

        sound.shoot()
        val sx = p.x
        val sy = p.y - 34f
        fun shot(x: Float, y: Float, vx: Float, vy: Float, dmg: Float, big: Boolean) {
            if (shots.size > 300) shots.removeAt(0)
            val b = Bullet()
            b.x = x; b.y = y; b.vx = vx; b.vy = vy
            b.dmg = dmg; b.big = big; b.pierce = p.pierceT > 0 || ship.skill.contains("pierce")
            shots.add(b)
        }
        val effectivePwr = ship.power + save.upPwr(sIdx) + save.upElite(sIdx)
        // Noticeable, punchy damage scaling: 28 base + 12 per power level * energy boost
        val dmg = (28f + effectivePwr * 12f) * energyBoost

        val hasSpread = p.spreadT > 0 || ship.skill.contains("spread") || ship.skill.contains("quad")
        val hasDouble = p.doubleT > 0 || ship.skill.contains("dual")

        if (hasSpread) {
            shot(sx, sy, 0f, -980f, dmg, p.pierceT > 0)
            shot(sx, sy, -240f, -920f, dmg * 0.85f, false)
            shot(sx, sy, 240f, -920f, dmg * 0.85f, false)
            if (ship.skill.contains("quad")) {
                shot(sx, sy, -440f, -860f, dmg * 0.75f, false)
                shot(sx, sy, 440f, -860f, dmg * 0.75f, false)
            }
        } else if (hasDouble) {
            shot(sx - 14f, sy, 0f, -980f, dmg, p.pierceT > 0)
            shot(sx + 14f, sy, 0f, -980f, dmg, p.pierceT > 0)
        } else {
            shot(sx, sy, 0f, -980f, dmg, p.pierceT > 0)
        }

        // Dual Companion Drones ("Two bought Spaceship side by side Attachments")
        if (ship.hasDrones) {
            shot(sx - 48f, sy + 10f, -30f, -960f, dmg * 0.75f, false)
            shot(sx + 48f, sy + 10f, 30f, -960f, dmg * 0.75f, false)
        }
    }

    /** Launch guided auto-homing missiles towards nearest enemy or boss */
    fun launchMissiles(n: Int = 2, dmg: Float = 250f) {
        val p = player
        sound.pickup()
        for (i in 0 until n) {
            val m = Missile()
            val off = if (n == 1) 0f else ((i.toFloat() / (n - 1).coerceAtLeast(1) - 0.5f) * 64f)
            m.x = p.x + off
            m.y = p.y - 28f
            m.vx = (if (i % 2 == 0) -180f else 180f) * (0.85f + rnd.nextFloat() * 0.3f)
            m.vy = -380f
            m.dmg = dmg
            missiles.add(m)
        }
    }

    // ---------------- damage ----------------
    fun damageEnemy(e: Enemy, dmg: Float) {
        if (e.gone) return
        e.hp -= dmg
        e.flash = 0.07f
        if (e.hp <= 0) killEnemy(e)
    }

    private fun killEnemy(e: Enemy) {
        if (e.gone) return
        e.gone = true
        kills++
        progress += 0.00045f
        sound.boom(e.r > 40f)
        explode(e.x, e.y, if (e.r > 40f) 26 else 12, e.r > 40f)
        shake = max(shake, if (e.r > 40f) 5f else 2f)
        val spec = ENEMY_ROSTER[e.spec]
        addScore((spec.score * (1f + progress)).toInt(), e.x, e.y)
        if (spec.split >= 0) {
            val n = spec.p1.toInt().coerceIn(1, 4)
            repeat(n) {
                spawnSpec(spec.split, e.x + (rnd.nextFloat() - 0.5f) * 60f, e.y + (rnd.nextFloat() - 0.5f) * 60f)
            }
        }
        if (e.ai == AI_KAMI) {
            if (hypot(player.x - e.x, player.y - e.y) < e.p1) hurtPlayer()
            explode(e.x, e.y, 14, true)
        }
        if (rnd.nextFloat() < 0.15f) dropItem(e.x, e.y)
        if (rnd.nextFloat() < 0.06f) dropHeart(e.x + 30f, e.y)
        // Coin collection system: basic coins + rare golden coins from special enemies + collectible parts
        if (rnd.nextFloat() < 0.28f) dropCoin(e.x - 20f, e.y, false)
        if (e.r > 20f && rnd.nextFloat() < 0.12f) dropCoin(e.x + 20f, e.y, true)
        if (e.r > 30f && rnd.nextFloat() < 0.08f) dropPart(e.x, e.y - 20f)
    }

    fun dropCoin(x: Float, y: Float, rare: Boolean) {
        val it = Item()
        it.kind = if (rare) P_COIN_RARE else P_COIN
        it.x = x.coerceIn(40f, w - 40f); it.y = y.coerceIn(80f, h - 80f)
        items.add(it)
    }

    fun dropPart(x: Float, y: Float) {
        val it = Item()
        it.kind = P_PART
        it.x = x.coerceIn(40f, w - 40f); it.y = y.coerceIn(80f, h - 80f)
        items.add(it)
    }

    fun dropHeart(x: Float, y: Float) {
        val it = Item()
        it.kind = P_HEART
        it.x = x.coerceIn(40f, w - 40f); it.y = y.coerceIn(80f, h - 80f)
        items.add(it)
    }

    fun dropItem(x: Float, y: Float) {
        val it = Item()
        val r = rnd.nextFloat()
        it.kind = when {
            r < 0.3f -> P_RAPID
            r < 0.55f -> P_DOUBLE
            r < 0.8f -> P_SPREAD
            else -> P_PIERCE
        }
        it.x = x.coerceIn(40f, w - 40f); it.y = y.coerceIn(80f, h - 80f)
        items.add(it)
    }

    fun hurtPlayer() {
        val p = player
        if (p.dead || p.invuln > 0 || over) return
        p.hits--
        streak = 0
        sound.hurt()
        shake = max(shake, 8f)
        explode(p.x, p.y, 16, false)
        if (p.hits <= 0) {
            p.dead = true
            p.deathT = 1.4f
            sound.boom(false)
            explode(p.x, p.y, 46, true)
            shake = 14f
        } else {
            p.invuln = 3.0f
        }
    }

    // ---------------- spawning ----------------
    fun spawnSpec(idx: Int, x: Float, y: Float): Enemy? {
        if (enemies.size > 42) return null
        val s = ENEMY_ROSTER[idx]
        val e = Enemy()
        e.spec = idx; e.ai = s.ai
        e.x = x.coerceIn(40f, w - 40f); e.y = y
        e.hp = s.hp * hpM; e.maxHp = e.hp
        e.spd = s.spd; e.r = s.r; e.score = s.score; e.fire = s.fire
        e.p1 = s.p1; e.p2 = s.p2; e.split = s.split
        e.fireT = s.fire / fireM * (0.7f + rnd.nextFloat() * 0.6f)
        e.seed = rnd.nextFloat() * 10f
        e.tx = e.x; e.ty = e.y
        if (e.ai == AI_SHIELD) {
            e.shieldUp = true
            e.stateT = s.p1
        }
        enemies.add(e)
        return e
    }

    private fun unlockedSpec(): Int {
        val top = (progress / 0.85f * 100f).toInt().coerceIn(0, 99)
        return if (rnd.nextFloat() < 0.6f) {
            val lo = max(0, top - 16)
            lo + rnd.nextInt(top - lo + 1)
        } else {
            rnd.nextInt(top + 1)
        }
    }

    fun formation() {
        when (rnd.nextInt(6)) {
            0 -> { // line
                val n = 3 + rnd.nextInt(3)
                val s = unlockedSpec()
                val x = w * (0.2f + rnd.nextFloat() * 0.6f)
                for (i in 0 until n) spawnSpec(s, x, -60f - i * 80f)
            }
            1 -> { // vee
                val a = unlockedSpec()
                val b = unlockedSpec()
                val x = w * (0.25f + rnd.nextFloat() * 0.5f)
                for (i in -2..2) spawnSpec(if (i == 0) b else a, x + i * 75f, -60f - kotlin.math.abs(i) * 90f)
            }
            2 -> { // column
                val s = unlockedSpec()
                val x = w * (0.15f + rnd.nextFloat() * 0.7f)
                for (i in 0 until 4) spawnSpec(s, x, -80f - i * 110f)
            }
            3 -> { // wave pair
                val s = unlockedSpec()
                val x = w * (0.25f + rnd.nextFloat() * 0.5f)
                for (i in 0 until 4) {
                    val e = spawnSpec(s, x + (i % 2) * 90f - 45f, -60f - i * 80f)
                    if (e != null) e.seed = i * 1.7f
                }
            }
            4 -> { // spearhead
                val tough = (unlockedSpec() + (10 + progress * 40f).toInt()).coerceAtMost(99)
                val x = w * (0.3f + rnd.nextFloat() * 0.4f)
                spawnSpec(tough, x, -80f)
                val s = unlockedSpec()
                spawnSpec(s, x - 100f, -200f)
                spawnSpec(s, x + 100f, -200f)
            }
            else -> { // scattered pack
                for (i in 0 until 4) spawnSpec(unlockedSpec(), w * (0.12f + rnd.nextFloat() * 0.76f), -60f - rnd.nextFloat() * 200f)
            }
        }
    }

    /** 2 to 3 types of larger elite minion escorts that only appear during boss battles */
    fun spawnBossEscorts(boss: Boss) {
        if (enemies.size >= 6) return // screen stays clean & readable
        val type = rnd.nextInt(3)
        when (type) {
            0 -> {
                // Type 1: Dread-Guard Tank (Heavy flanking escort duo)
                val e1 = Enemy().apply {
                    spec = 94
                    ai = AI_GUN
                    x = (boss.x - 140f).coerceIn(40f, w - 40f)
                    y = (boss.y + 40f).coerceIn(60f, h * 0.45f)
                    r = 38f
                    hp = (350f + progress * 250f) * hpM
                    maxHp = hp
                    spd = 0.75f
                    score = 400
                    fire = 2.0f
                    p1 = 2f
                    fireT = 1.0f
                }
                val e2 = Enemy().apply {
                    spec = 95
                    ai = AI_GUN
                    x = (boss.x + 140f).coerceIn(40f, w - 40f)
                    y = (boss.y + 40f).coerceIn(60f, h * 0.45f)
                    r = 38f
                    hp = (350f + progress * 250f) * hpM
                    maxHp = hp
                    spd = 0.75f
                    score = 400
                    fire = 2.0f
                    p1 = 2f
                    fireT = 1.6f
                }
                enemies.add(e1)
                enemies.add(e2)
            }
            1 -> {
                // Type 2: Aegis Wing Interceptor (Fast agile escort weaving below boss)
                val e = Enemy().apply {
                    spec = 97
                    ai = AI_WEAVE
                    x = boss.x + (rnd.nextFloat() - 0.5f) * 160f
                    y = (boss.y + 90f).coerceIn(60f, h * 0.45f)
                    r = 34f
                    hp = (260f + progress * 180f) * hpM
                    maxHp = hp
                    spd = 1.35f
                    score = 350
                    fire = 1.8f
                    p1 = 1f
                    fireT = 0.8f
                }
                enemies.add(e)
            }
            2 -> {
                // Type 3: Plasma Destroyer (Heavy capital escort firing bursts)
                val e = Enemy().apply {
                    spec = 98
                    ai = AI_BURST
                    x = (boss.x + (if (rnd.nextBoolean()) -100f else 100f)).coerceIn(40f, w - 40f)
                    y = (boss.y + 60f).coerceIn(60f, h * 0.45f)
                    r = 44f
                    hp = (520f + progress * 350f) * hpM
                    maxHp = hp
                    spd = 0.85f
                    score = 550
                    fire = 2.4f
                    p1 = 3f
                    fireT = 1.2f
                }
                enemies.add(e)
            }
        }
    }

    fun spawnRock() {
        if (rocks.size > 10) return
        val r = Rock()
        r.x = w * rnd.nextFloat()
        r.y = -80f
        r.r = 22f + rnd.nextFloat() * 26f
        r.hp = r.r * 1.1f * hpM
        r.vx = (rnd.nextFloat() - 0.5f) * 60f
        r.vy = (110f + rnd.nextFloat() * 120f) * spdM
        r.spin = (rnd.nextFloat() - 0.5f) * 3f
        rocks.add(r)
    }

    fun spawnBox(idx: Int) {
        val s = BOX_ROSTER[idx]
        if (s.kind == BOX_CLUSTER) {
            spawnCluster(s)
            return
        }
        // never stack a fresh wall right above another one
        if (s.kind == BOX_WALL_V || s.kind == BOX_NARROW || s.kind == BOX_WALL_H) {
            for (o in boxes) {
                if (o.y < h * 0.3f && (o.kind == BOX_WALL_V || o.kind == BOX_NARROW || o.kind == BOX_WALL_H)) return
            }
        }
        val b = Box()
        b.spec = idx; b.kind = s.kind
        b.w = s.w; b.h = s.h; b.r = s.w / 2f
        b.hp = s.hp * (if (s.hp > 0) hpM else 1f); b.maxHp = b.hp
        b.x = w * (0.15f + rnd.nextFloat() * 0.7f)
        b.y = -max(s.h, 120f)
        b.baseX = b.x
        if (s.kind == BOX_WALL_V || s.kind == BOX_NARROW) {
            b.gapW = s.gap * gapM
            b.gapX = (b.gapW / 2 + 40f + rnd.nextFloat() * (w - b.gapW - 80f).coerceAtLeast(1f))
                .coerceIn(b.gapW / 2 + 40f, w - b.gapW / 2 - 40f)
        }
        if (s.kind == BOX_WALL_H) {
            b.w = s.w / 4f // side-bar width (gap between bars is the lane)
        }
        b.spin = s.spin
        boxes.add(b)
    }

    private fun spawnCluster(s: BoxSpec) {
        val cx = w * (0.25f + rnd.nextFloat() * 0.5f)
        val cy = -120f
        fun child(dx: Float, dy: Float) {
            val b = Box()
            b.spec = BOX_ROSTER.indexOf(s)
            b.kind = BOX_BLOCK_S
            b.w = 58f; b.h = 58f; b.r = 29f
            b.hp = 25f * hpM; b.maxHp = b.hp
            b.x = (cx + dx).coerceIn(60f, w - 60f); b.y = cy + dy
            b.baseX = b.x
            boxes.add(b)
        }
        when (s.cluster % 10) {
            0 -> repeat(3) { child((it - 1) * 70f, 0f) }
            1 -> repeat(3) { child(0f, (it - 1) * 70f) }
            2 -> {
                child(-45f, -45f); child(45f, -45f)
                child(-45f, 45f); child(45f, 45f)
            }
            3 -> {
                child(0f, -70f); child(-60f, 40f)
                child(0f, 40f); child(60f, 40f)
            }
            4 -> repeat(5) { child((it - 2) * 65f, kotlin.math.abs(it - 2) * -40f) }
            5 -> {
                child(0f, 0f); child(-70f, -60f); child(70f, -60f)
            }
            6 -> repeat(4) { child(0f, (it - 1.5f) * 70f) }
            7 -> {
                child(0f, 0f); child(-70f, 0f); child(70f, 0f)
                child(0f, -70f); child(0f, 70f)
            }
            8 -> repeat(4) { child((it - 1.5f) * 70f, 0f) }
            else -> {
                for (i in 0 until 5) {
                    val a = i * 2 * PI / 5
                    child((cos(a) * 80f).toFloat(), (sin(a) * 80f).toFloat())
                }
            }
        }
    }

    private fun unlockedBox(): Int {
        val top = (progress / 0.92f * 100f).toInt().coerceIn(0, 99)
        return if (rnd.nextFloat() < 0.6f) {
            val lo = max(0, top - 24)
            lo + rnd.nextInt(top - lo + 1)
        } else {
            rnd.nextInt(top + 1)
        }
    }

    fun spawnWallBox(narrow: Boolean) {
        val idx = if (narrow) (50 + (progress * 9f).toInt()).coerceIn(50, 59)
        else (30 + (progress * 9f).toInt()).coerceIn(30, 39)
        spawnBox(idx)
    }

    // ---------------- update ----------------
    fun update(dt: Float) {
        if (over) {
            updateFx(dt)
            return
        }
        elapsed += dt
        progress = min(1f, progress + dt / 2400f)
        val s = sstep(progress)
        hpM = 1f + 2.5f * progress
        spdM = 1f + 0.85f * s
        fireM = 1f + 1.7f * s
        shotSpd = 1f + 0.9f * s
        scrollM = 1f + 0.8f * s
        gapM = 1.15f - 0.45f * s
        if (shake > 0) shake = max(0f, shake - dt * 34f)
        if (streakT > 0) {
            streakT -= dt
            if (streakT <= 0) streak = 0
        }
        warn?.let {
            warnT -= dt
            if (warnT <= 0) warn = null
        }
        if (shieldMsgT > 0) shieldMsgT -= dt

        updatePlayer(dt)
        updateDirector(dt)
        updateEnemies(dt)
        updateRocks(dt)
        updateBoxes(dt)
        boss?.update(dt, this, elapsed)
        updateShots(dt)
        updateMissiles(dt)
        updateContinuousLaser(dt)
        updateBeams(dt)
        updateItems(dt)
        updateFx(dt)
        checkFlow()
    }

    private fun updateFx(dt: Float) {
        for (p in parts.toList()) {
            p.life -= dt
            if (p.life <= 0) {
                parts.remove(p); continue
            }
            p.x += p.vx * dt
            p.y += p.vy * dt
            p.vx *= (1f - 1.8f * dt)
            p.vy *= (1f - 1.8f * dt)
        }
        for (t in texts.toList()) {
            t.life -= dt
            t.y -= 60f * dt
            if (t.life <= 0) texts.remove(t)
        }
    }

    private fun updatePlayer(dt: Float) {
        val p = player
        if (p.dead) {
            p.deathT -= dt
            if (p.deathT <= 0 && !over) {
                over = true
                sound.over()
                sound.music.stop()
                save.saveHi(score)
                listener.gameOver(score)
            }
            return
        }
        val sIdx = save.shipIndex()
        val ship = ALL_100_PLAYER_SHIPS[sIdx]
        val effectiveSpd = ship.speed + save.upSpd(sIdx) + save.upElite(sIdx)
        val moveSpd = 500f + effectiveSpd * 50f
        p.x = (p.x + input.mx * moveSpd * dt).coerceIn(30f, w - 30f)
        p.y = (p.y + input.my * moveSpd * dt).coerceIn(h * 0.30f, h - 46f)
        if (p.fireCd > 0) p.fireCd -= dt
        if (p.invuln > 0) p.invuln -= dt
        if (p.rapidT > 0) p.rapidT -= dt
        if (p.doubleT > 0) p.doubleT -= dt
        if (p.spreadT > 0) p.spreadT -= dt
        if (p.pierceT > 0) p.pierceT -= dt

        // Energy passive recharge (faster when not actively firing)
        val rechargeRate = if (input.firing) 8f else (18f + ship.durability * 4f)
        p.energy = min(p.maxEnergy, p.energy + rechargeRate * dt)

        if (input.firing) firePlayer()

        // Automatic Guided Missile Salvo every 8-10 seconds
        if (ship.hasMissiles && !p.dead) {
            p.missileTimer -= dt
            if (p.missileTimer <= 0f) {
                val cooldown = if (ship.num == 4 || ship.num == 7) 8f else 10f
                p.missileTimer = cooldown
                val missileCount = if (ship.num == 4 || ship.num == 7) 4 else 2
                val missileDmg = 180f + (ship.power + save.upPwr(sIdx) + save.upElite(sIdx)) * 35f
                launchMissiles(missileCount, missileDmg)
                addText(p.x, p.y - 50f, "MISSILES LAUNCHED")
            }
        }
    }

    private fun updateDirector(dt: Float) {
        if (boss != null || bossPending != null) return
        if (bossGap > 0) bossGap -= dt
        if (bossAt.isNotEmpty() && bossGap <= 0f && progress >= bossAt[0]) {
            bossAt.removeAt(0)
            val id = bossIds.removeAt(0)
            bossPending = id
            bossDelay = 2.5f
            warn = "WARNING"
            warnT = 2.5f
            sound.warn()
            return
        }
        if (lullT > 0) {
            lullT -= dt
            spawnT -= dt
            if (spawnT <= 0) {
                spawnT = 2.6f
                spawnSpec((progress * 8f).toInt().coerceIn(0, 7), w * (0.15f + rnd.nextFloat() * 0.7f), -60f)
            }
            return
        }
        calmClock += dt
        if (calmClock > 55f) {
            calmClock = 0f
            lullT = 6f
            return
        }
        val s = sstep(progress)
        spawnT -= dt
        if (spawnT <= 0) {
            spawnT = (2.4f - 1.85f * s).coerceAtLeast(0.55f) * (0.7f + rnd.nextFloat() * 0.6f)
            if (rnd.nextFloat() < 0.68f) formation()
            else if (progress > 0.06f && rnd.nextFloat() < 0.5f) {
                spawnRock()
                if (progress > 0.3f && rnd.nextFloat() < 0.35f) spawnRock()
            } else formation()
        }
        boxT -= dt
        if (boxT <= 0 && progress > 0.03f) {
            boxT = (9f - 5.8f * s).coerceAtLeast(3.2f) * (0.8f + rnd.nextFloat() * 0.5f)
            spawnBox(unlockedBox())
        }
    }

    // ----- enemies -----
    private fun updateEnemies(dt: Float) {
        for (e in enemies.toList()) {
            if (e.gone) {
                enemies.remove(e); continue
            }
            e.t += dt
            if (e.flash > 0) e.flash -= dt
            updateEnemy(e, dt)
            if (e.gone) {
                enemies.remove(e); continue
            }
            if (e.y > h + 200f || e.x < -200f || e.x > w + 200f) enemies.remove(e)
        }
    }

    private fun updateEnemy(e: Enemy, dt: Float) {
        val p = player
        val sp = spdM * e.spd
        fun fireAimed(speed: Float) {
            aimed(e.x, e.y + e.r, speed * shotSpd)
        }
        when (e.ai) {
            AI_DRIFT -> {
                e.y += 150f * sp * dt
                e.x += sin(e.t * 2f + e.seed) * 60f * dt
                e.fireT -= dt
                if (e.fireT <= 0 && e.fire < 90f && e.y > 0 && e.y < h * 0.7f) {
                    e.fireT = e.fire / fireM
                    fireAimed(340f)
                }
            }
            AI_TRACK -> {
                e.y += (200f + e.spd * 90f) * spdM * dt
                e.x += (p.x - e.x).coerceIn(-1f, 1f) * 260f * spdM * dt
                e.fireT -= dt
                if (e.fireT <= 0 && e.fire < 90f && e.y > 0 && e.y < h) {
                    e.fireT = e.fire / fireM
                    fireAimed(400f)
                }
            }
            AI_DIVE -> {
                when (e.state) {
                    0 -> {
                        e.y += 260f * sp * dt
                        if (e.y > h * 0.25f) {
                            e.state = 1; e.stateT = 0.55f
                            e.tx = p.x; e.ty = p.y
                            if (e.p1 > 0) fireAimed(420f)
                        }
                    }
                    1 -> {
                        e.stateT -= dt
                        e.y += 60f * dt
                        if (e.stateT <= 0) {
                            e.state = 2
                            val dx = e.tx - e.x
                            val dy = e.ty - e.y
                            val d = hypot(dx, dy).coerceAtLeast(1f)
                            e.vx = dx / d * 720f * sp
                            e.vy = dy / d * 720f * sp
                        }
                    }
                    else -> {
                        e.x += e.vx * dt
                        e.y += e.vy * dt
                    }
                }
            }
            AI_WEAVE -> {
                e.y += (170f + e.spd * 90f) * spdM * dt
                e.x += sin(e.t * 3.2f + e.seed) * 200f * spdM * dt
                e.fireT -= dt
                if (e.fireT <= 0 && e.fire < 90f && e.y > 0 && e.y < h) {
                    e.fireT = e.fire / fireM
                    fireAimed(380f)
                }
            }
            AI_GUN -> {
                e.y += 90f * sp * dt
                if (e.y > h * 0.28f) e.y = h * 0.28f
                e.x += sin(e.t + e.seed) * 40f * dt
                e.fireT -= dt
                if (e.fireT <= 0 && e.y > 0) {
                    e.fireT = e.fire / fireM
                    val n = e.p1.toInt().coerceIn(1, 6)
                    repeat(n) { fireAimed(360f) }
                }
            }
            AI_BURST -> {
                e.y += 200f * sp * dt
                e.x += sin(e.t * 1.8f + e.seed) * 170f * spdM * dt
                e.fireT -= dt
                if (e.fireT <= 0 && e.y > 0 && e.y < h) {
                    e.fireT = e.fire / fireM
                    val n = e.p1.toInt().coerceIn(1, 6)
                    repeat(n) { fireAimed(400f) }
                }
            }
            AI_SPIRAL -> {
                e.y += 180f * sp * dt
                e.x += sin(e.t * 2.6f + e.seed) * 150f * spdM * dt
                e.fireT -= dt
                if (e.fireT <= 0 && e.y > 0 && e.y < h) {
                    e.fireT = e.fire / fireM
                    radial(e.x, e.y + e.r, e.p1.toInt().coerceIn(4, 16), 280f * shotSpd, e.t.toDouble())
                }
            }
            AI_SHIELD -> {
                e.y += 135f * sp * dt
                if (e.y > h * 0.4f) e.y = h * 0.4f
                e.stateT -= dt
                if (e.stateT <= 0) {
                    e.shieldUp = !e.shieldUp
                    e.stateT = if (e.shieldUp) e.p1 else e.p2
                }
                e.fireT -= dt
                if (e.fireT <= 0 && e.y > 0) {
                    e.fireT = e.fire / fireM
                    fireAimed(380f)
                }
            }
            AI_KAMI -> {
                val dx = p.x - e.x
                val dy = p.y - e.y
                val d = hypot(dx, dy).coerceAtLeast(1f)
                e.vx += dx / d * 800f * dt
                e.vy += dy / d * 800f * dt
                val vd = hypot(e.vx, e.vy).coerceAtLeast(1f)
                val vmax = 460f * sp
                if (vd > vmax) {
                    e.vx = e.vx / vd * vmax
                    e.vy = e.vy / vd * vmax
                }
                e.x += e.vx * dt
                e.y += (e.vy + 140f) * dt
                if (!p.dead && d < 44f) {
                    hurtPlayer()
                    killEnemy(e)
                }
            }
            AI_SNIPER -> {
                val wantY = h * 0.2f
                e.y += ((wantY - e.y).coerceIn(-1f, 1f)) * 200f * spdM * dt + 60f * dt
                e.x += sin(e.t * 1.2f + e.seed) * 90f * dt
                e.fireT -= dt
                if (e.fireT <= 0 && e.y > 0 && e.y < h * 0.6f) {
                    e.fireT = e.fire / fireM
                    fireAimed(480f)
                    if (e.p1 > 0) fireAimed(480f)
                }
            }
            AI_SPLIT -> {
                e.y += 130f * sp * dt
                e.x += sin(e.t * 1.4f + e.seed) * 80f * dt
                e.fireT -= dt
                if (e.fireT <= 0 && e.fire < 90f && e.y > 0 && e.y < h * 0.6f) {
                    e.fireT = e.fire / fireM
                    fireAimed(360f)
                }
            }
            AI_STRAFE -> {
                e.y += 210f * sp * dt
                e.x += sin(e.t * 2.2f + e.seed) * 230f * spdM * dt
                e.fireT -= dt
                if (e.fireT <= 0 && e.y > 0 && e.y < h) {
                    e.fireT = e.fire / fireM
                    spreadAt(e.x, e.y + e.r, e.p1.toInt().coerceIn(3, 7), 360f * shotSpd)
                }
            }
        }
        if (!e.gone && !p.dead && e.ai != AI_KAMI) {
            if (hypot(p.x - e.x, p.y - e.y) < e.r + 13f) {
                hurtPlayer()
                damageEnemy(e, 150f)
            }
        }
    }

    // ----- rocks -----
    private fun updateRocks(dt: Float) {
        for (r in rocks.toList()) {
            r.x += r.vx * dt
            r.y += r.vy * dt
            r.rot += r.spin * dt
            if (r.hp <= 0) {
                rocks.remove(r)
                explode(r.x, r.y, 14, false)
                sound.boom(true)
                addScore(30, r.x, r.y)
                continue
            }
            if (r.y > h + 120f) {
                rocks.remove(r); continue
            }
            if (!player.dead && hypot(player.x - r.x, player.y - r.y) < r.r * 0.8f + 13f) {
                hurtPlayer()
                r.hp -= 80f
            }
        }
    }

    // ----- boxes -----
    private fun scrollPx(spec: BoxSpec): Float = (200f + progress * 160f) * spec.speed

    private fun updateBoxes(dt: Float) {
        for (b in boxes.toList()) {
            if (b.gone) {
                boxes.remove(b); continue
            }
            if (b.flash > 0) b.flash -= dt
            val spec = BOX_ROSTER[b.spec]
            b.y += scrollPx(spec) * dt
            when (b.kind) {
                BOX_DRIFT -> {
                    // sway around the anchor: never seals the screen
                    b.t += dt
                    b.x = (b.baseX + sin(b.t * 1.1f) * 130f).coerceIn(70f, w - 70f)
                }
                BOX_SPIN -> b.rot += b.spin * dt
                BOX_WALL_V, BOX_NARROW, BOX_WALL_H -> {
                    b.t += dt
                    if (!b.hot && b.t > 0.4f) b.hot = true
                }
            }
            if (b.y > h + 300f) {
                boxes.remove(b); continue
            }
            if (!b.passed && b.y - (if (b.kind == BOX_WALL_H) 190f else b.h / 2) > player.y &&
                (b.kind == BOX_WALL_V || b.kind == BOX_NARROW || b.kind == BOX_WALL_H)
            ) {
                b.passed = true
                addScore(100, player.x + 60f, player.y - 60f)
            }
            if (!player.dead && boxHitsPlayer(b)) {
                hurtPlayer()
                if (b.hp > 0) damageBox(b, 100f)
            }
        }
    }

    private fun boxHitsPlayer(b: Box): Boolean {
        val px = player.x
        val py = player.y
        return when (b.kind) {
            BOX_SPIN -> hypot(px - b.x, py - b.y) < b.r + 13f
            BOX_WALL_V, BOX_NARROW -> {
                if (hypot(0f, py - b.y) > 34f) return false
                px < b.gapX - b.gapW / 2 || px > b.gapX + b.gapW / 2
            }
            BOX_WALL_H -> {
                // side bars: hurt only when overlapping a bar rect
                val inY = py > b.y - 190f - 13f && py < b.y + 190f + 13f
                if (!inY) return false
                px < b.w + 13f || px > w - b.w - 13f
            }
            else -> {
                val cx = px.coerceIn(b.x - b.w / 2, b.x + b.w / 2)
                val cy = py.coerceIn(b.y - b.h / 2, b.y + b.h / 2)
                hypot(px - cx, py - cy) < 13f
            }
        }
    }

    fun damageBox(b: Box, dmg: Float) {
        if (b.gone || b.hp <= 0) return
        b.hp -= dmg
        b.flash = 0.07f
        if (b.hp <= 0) {
            b.gone = true
            explode(b.x, b.y, 12, false)
            sound.boom(true)
            addScore(if (b.kind == BOX_TANK) 150 else 40, b.x, b.y)
        }
    }

    // ----- shots -----
    private fun updateShots(dt: Float) {
        for (b in shots.toList()) {
            b.x += b.vx * dt
            b.y += b.vy * dt
            b.life -= dt
            var dead = b.life <= 0 || b.y < -60f || b.x < -40f || b.x > w + 40f
            if (!dead) {
                for (r in rocks) {
                    if (hypot(b.x - r.x, b.y - r.y) < r.r + 8f) {
                        r.hp -= b.dmg
                        explode(b.x, b.y, 3, false)
                        dead = !b.pierce
                        break
                    }
                }
            }
            if (!dead) {
                for (bx in boxes.toList()) {
                    if (bx.gone || bx.hp <= 0) continue
                    val cx = b.x.coerceIn(bx.x - bx.w / 2, bx.x + bx.w / 2)
                    val cy = b.y.coerceIn(bx.y - bx.h / 2, bx.y + bx.h / 2)
                    if (hypot(b.x - cx, b.y - cy) < 9f) {
                        damageBox(bx, b.dmg)
                        explode(b.x, b.y, 3, false)
                        dead = !b.pierce
                        break
                    }
                }
            }
            if (!dead) {
                for (e in enemies.toList()) {
                    if (e.gone) continue
                    if (hypot(b.x - e.x, b.y - e.y) < e.r + 9f) {
                        // belly plate blocks shots rising into it
                        if (e.ai == AI_SHIELD && e.shieldUp && b.y > e.y - e.r * 0.4f) {
                            explode(b.x, b.y, 5, false)
                            shieldedMsg(b.x, b.y - 40f)
                            dead = !b.pierce
                            break
                        }
                        damageEnemy(e, b.dmg)
                        explode(b.x, b.y, 3, false)
                        dead = !b.pierce
                        break
                    }
                }
            }
            if (!dead) {
                val bo = boss
                if (bo != null && !bo.dying && bo.tryHit(b.x, b.y, b.dmg, this, elapsed)) {
                    explode(b.x, b.y, 3, false)
                    sound.bossHit()
                    dead = !b.pierce
                }
            }
            if (dead) shots.remove(b)
        }
        for (b in foeShots.toList()) {
            b.x += b.vx * dt
            b.y += b.vy * dt
            b.life -= dt
            var dead = b.life <= 0 || b.y > h + 40f || b.y < -80f || b.x < -60f || b.x > w + 60f
            if (!dead && !player.dead) {
                if (hypot(b.x - player.x, b.y - player.y) < b.r + 12f) {
                    hurtPlayer()
                    dead = true
                }
            }
            if (dead) foeShots.remove(b)
        }
    }

    private fun updateMissiles(dt: Float) {
        for (m in missiles.toList()) {
            m.life -= dt
            if (m.life <= 0) {
                missiles.remove(m)
                continue
            }
            // Spawn spark / smoke particle trail
            m.trailT -= dt
            if (m.trailT <= 0f) {
                m.trailT = 0.035f
                if (parts.size < 380) {
                    val p = Particle()
                    p.x = m.x + (rnd.nextFloat() - 0.5f) * 6f
                    p.y = m.y + 12f
                    p.vx = (rnd.nextFloat() - 0.5f) * 50f
                    p.vy = 90f + rnd.nextFloat() * 60f
                    p.maxLife = 0.28f; p.life = p.maxLife
                    p.size = 4.5f
                    parts.add(p)
                }
            }

            // Find target (closest active boss or enemy)
            val b = boss
            var tx = m.x
            var ty = -200f
            if (b != null && b.entered && !b.gone && b.vulnerable()) {
                tx = b.x
                ty = b.y
            } else {
                val target = enemies.filter { !it.gone && it.y > 0 }.minByOrNull { hypot(it.x - m.x, it.y - m.y) }
                if (target != null) {
                    tx = target.x
                    ty = target.y
                }
            }

            // Steer towards target
            val targetAngle = atan2(ty - m.y, tx - m.x)
            val curAngle = atan2(m.vy, m.vx)
            var diff = targetAngle - curAngle
            while (diff < -PI) diff += (2 * PI).toFloat()
            while (diff > PI) diff -= (2 * PI).toFloat()
            val newAngle = curAngle + diff.coerceIn(-5.5f * dt, 5.5f * dt)
            m.speed = min(860f, m.speed + 360f * dt)
            m.vx = (cos(newAngle) * m.speed).toFloat()
            m.vy = (sin(newAngle) * m.speed).toFloat()
            m.x += m.vx * dt
            m.y += m.vy * dt

            // Collision test with rocks and boxes
            var hit = false
            for (r in rocks) {
                if (hypot(m.x - r.x, m.y - r.y) < r.r + m.r) {
                    r.hp -= m.dmg
                    hit = true
                    break
                }
            }
            if (!hit) {
                for (bx in boxes.toList()) {
                    if (bx.gone || bx.hp <= 0) continue
                    val cx = m.x.coerceIn(bx.x - bx.w / 2, bx.x + bx.w / 2)
                    val cy = m.y.coerceIn(bx.y - bx.h / 2, bx.y + bx.h / 2)
                    if (hypot(m.x - cx, m.y - cy) < m.r + 10f) {
                        damageBox(bx, m.dmg)
                        hit = true
                        break
                    }
                }
            }
            if (!hit) {
                for (e in enemies.toList()) {
                    if (!e.gone && hypot(e.x - m.x, e.y - m.y) < e.r + m.r + 6f) {
                        damageEnemy(e, m.dmg)
                        hit = true
                        break
                    }
                }
            }
            if (!hit && b != null && !b.dying && b.tryHit(m.x, m.y, m.dmg, this, elapsed)) {
                hit = true
            }
            if (hit) {
                explode(m.x, m.y, 22, true)
                sound.boom(false)
                shake = max(shake, 6f)
                missiles.remove(m)
            } else if (m.y < -120f || m.y > h + 120f || m.x < -120f || m.x > w + 120f) {
                missiles.remove(m)
            }
        }
    }

    private fun updateContinuousLaser(dt: Float) {
        val p = player
        if (p.dead || !input.firing) return
        val sIdx = save.shipIndex()
        val ship = ALL_100_PLAYER_SHIPS[sIdx]
        if (!ship.hasLaser) return

        val effectivePwr = ship.power + save.upPwr(sIdx) + save.upElite(sIdx)
        val beamWidth = 24f + effectivePwr * 2f
        val beamDmg = (16f + effectivePwr * 6f) * (dt / 0.08f)

        // Damage enemies in vertical laser column
        for (e in enemies.toList()) {
            if (!e.gone && e.y < p.y && kotlin.math.abs(e.x - p.x) < beamWidth / 2f + e.r) {
                damageEnemy(e, beamDmg)
                if (rnd.nextFloat() < 0.25f) explode(e.x, e.y, 2, false)
            }
        }
        // Damage rocks & boxes in beam
        for (r in rocks) {
            if (r.y < p.y && kotlin.math.abs(r.x - p.x) < beamWidth / 2f + r.r) {
                r.hp -= beamDmg
            }
        }
        // Damage boss in laser column
        val b = boss
        if (b != null && !b.dying && b.y < p.y && kotlin.math.abs(b.x - p.x) < beamWidth / 2f + b.wPx * 0.55f) {
            b.tryHit(b.x, b.y, beamDmg, this, elapsed)
            if (rnd.nextFloat() < 0.35f) explode(b.x, b.y + 40f, 3, false)
        }
    }

    private fun updateBeams(dt: Float) {
        for (b in beams.toList()) {
            if (b.hitCd > 0) b.hitCd -= dt
            if (b.tele > 0) b.tele -= dt
            else {
                b.active -= dt
                if (!player.dead && b.active > 0 && b.hitCd <= 0) {
                    if (distToSeg(player.x, player.y, b.x1, b.y1, b.x2, b.y2) < b.w / 2 + 11f) {
                        hurtPlayer()
                        b.hitCd = 0.6f
                    }
                }
                if (b.active <= 0) beams.remove(b)
            }
        }
    }

    private fun distToSeg(px: Float, py: Float, x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x2 - x1
        val dy = y2 - y1
        val l2 = dx * dx + dy * dy
        if (l2 == 0f) return hypot(px - x1, py - y1)
        val t = (((px - x1) * dx + (py - y1) * dy) / l2).coerceIn(0f, 1f)
        return hypot(px - (x1 + t * dx), py - (y1 + t * dy))
    }

    fun addBeam(x1: Float, y1: Float, x2: Float, y2: Float, w: Float, tele: Float, active: Float) {
        val b = Beam()
        b.x1 = x1; b.y1 = y1; b.x2 = x2; b.y2 = y2; b.w = w
        b.tele = tele; b.active = active
        beams.add(b)
    }

    // ----- items -----
    private fun updateItems(dt: Float) {
        for (m in items.toList()) {
            if (!items.contains(m)) continue
            m.t += dt
            m.y += 115f * dt
            if (!player.dead) {
                val d = hypot(player.x - m.x, player.y - m.y)
                // Magnetic vacuum attraction: up to 240px range
                if (d < 240f && d > 1f) {
                    val magnetSpd = (540f * (1f - d / 260f)).coerceAtLeast(280f)
                    m.x += (player.x - m.x) / d * magnetSpd * dt
                    m.y += (player.y - m.y) / d * magnetSpd * dt
                }
                if (d < 46f) {
                    applyItem(m.kind)
                    items.remove(m)
                    continue
                }
            }
            if (m.y > h + 60f) items.remove(m)
        }
    }

    fun applyItem(kind: Int) {
        val p = player
        sound.pickup()
        explode(p.x, p.y, 8, false)
        when (kind) {
            P_RAPID -> {
                p.rapidT = 12f
                addText(p.x + 70f, p.y - 50f, "RAPID")
            }
            P_DOUBLE -> {
                p.doubleT = 12f
                addText(p.x + 70f, p.y - 50f, "DOUBLE")
            }
            P_SPREAD -> {
                p.spreadT = 12f
                addText(p.x + 70f, p.y - 50f, "SPREAD")
            }
            P_PIERCE -> {
                p.pierceT = 10f
                addText(p.x + 70f, p.y - 50f, "PIERCE")
            }
            4 -> {
                // Use p.maxHits which is already calculated in reset() from ship.health + upgrades
                if (p.hits < p.maxHits) {
                    p.hits++
                    addText(p.x + 70f, p.y - 50f, "+HULL")
                } else {
                    addScore(500, p.x + 70f, p.y - 50f)
                }
            }
            P_COIN -> {
                save.addCoins(100)
                addText(p.x + 70f, p.y - 50f, "+100 C")
            }
            P_COIN_RARE -> {
                save.addCoins(500)
                addText(p.x + 70f, p.y - 50f, "+500 C GOLD")
            }
            P_PART -> {
                save.addShipPart(1)
                addText(p.x + 70f, p.y - 50f, "+1 SHIP PART")
            }
        }
    }

    // ----- flow: finite journey, bosses appear automatically -----
    private fun checkFlow() {
        if (bossPending != null) {
            bossDelay -= 1f / 60f
            if (bossDelay <= 0) {
                val id = bossPending!!
                bossPending = null
                boss = makeBoss(id, 1f + progress * 0.5f, w, h)
                boss?.onSpawn(this)
                lullT = 0f
                calmClock = 0f
                sound.music.start(true)
            }
        }
        val b = boss
        if (b != null && b.gone) {
            boss = null
            bossKills++
            progress += 0.008f
            bossGap = 30f // waves breathe at least 30s between bosses
            addScore(b.def.reward, w * 0.5f, h * 0.35f)
            addText(w * 0.5f, h * 0.35f - 50f, "HOSTILE DOWN")
            sound.win()
            sound.music.start(false)
            player.invuln = max(player.invuln, 2f)
            dropItem(w * 0.4f, h * 0.4f)
            dropItem(w * 0.6f, h * 0.5f)
            dropHeart(w * 0.5f, h * 0.45f)
            val top = (progress * 99f).toInt().coerceIn(12, 99)
            spawnSpec(top, w * 0.3f, -80f)
            spawnSpec((top - 4).coerceAtLeast(8), w * 0.7f, -200f)
            formation()
            addText(w * 0.5f, h * 0.5f, "ELITE SURGE")
            lullT = 10f
            if (b.def.id == "core") listener.runComplete()
        }
    }
}
