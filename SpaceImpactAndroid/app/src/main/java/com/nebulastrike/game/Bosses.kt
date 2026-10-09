package com.nebulastrike.game

import android.graphics.Canvas
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.math.PI

// Reusable boss framework: ordered phases, parts, telegraphs, death sequences.
class BossDef(val id: String, val title: String, val reward: Int, val wFrac: Float, val hFrac: Float, val deathTime: Float, val deathStyle: Int)

class BossPart(val ox: Float, val oy: Float, val r: Float, var hp: Float, val name: String) {
    var maxHp = hp
    var alive = true
}

class Phase(
    val name: String,
    val until: Float,
    val timed: Float = -1f,
    val needsParts: Int = 0,
    val partHp: Float = 100f,
    val vuln: (Boss) -> Boolean = { true },
    val enter: (Boss, World) -> Unit = { _, _ -> },
    val tick: (Boss, World, Float) -> Unit
)

class Boss(val def: BossDef, val mul: Float, val scrW: Float, val scrH: Float) {
    var maxHp = 1000f
    var hp = 1000f
    var x = 0f
    var y = -200f
    var wPx = 300f
    var hPx = 200f
    var targetY = 0f
    var entered = false
    val phases = mutableListOf<Phase>()
    var phaseIdx = 0
    var tPhase = 0f
    var timers = FloatArray(8)
    var flash = 0f
    var dying = false
    var deathT = 0f
    var gone = false
    var spinA = 0f
    var weakUntil = 0f
    var mercy = false
    var parts = mutableListOf<BossPart>()
    val segs = mutableListOf<Pair<Float, Float>>()
    private var boomT = 0f
    private var seqIdx = 0

    fun phase(): Phase = phases[phaseIdx.coerceIn(0, phases.size - 1)]

    fun weakMul(t: Float): Float = if (t < weakUntil) 2f else 1f

    fun vulnerable(): Boolean {
        if (dying || gone || !entered) return false
        return try {
            phase().vuln(this) || mercy
        } catch (_: Exception) {
            true
        }
    }

    fun hitTest(px: Float, py: Float): Boolean {
        if (def.id == "worm") {
            for (s in segs) if (hypot(px - s.first, py - s.second) < scrW * 0.05f + 10f) return true
            return false
        }
        val dx = (px - x) / (wPx * 0.55f)
        val dy = (py - y) / (hPx * 0.6f)
        return dx * dx + dy * dy <= 1f
    }

    private fun setupParts(world: World) {
        parts.clear()
        val ph = phase()
        if (ph.needsParts <= 0) return
        for (i in 0 until ph.needsParts) {
            val (ox, oy) = partSlot(ph.needsParts, i)
            parts.add(BossPart(ox, oy, wPx * 0.09f, ph.partHp * mul, "NODE"))
        }
        world.addText(x - wPx * 0.3f, y + hPx * 0.6f, "BREAK THE NODES")
    }

    private fun partSlot(n: Int, i: Int): Pair<Float, Float> {
        return when (def.id) {
            "fortress" -> Pair((i - 1) * wPx * 0.32f, hPx * 0.18f)
            "colossus" -> Pair(-wPx * 0.25f + i * wPx * 0.25f, hPx * 0.28f)
            else -> Pair((i - (n - 1) / 2f) * wPx * 0.28f, hPx * 0.12f)
        }
    }

    fun tryHit(px: Float, py: Float, dmg: Float, world: World, t: Float): Boolean {
        if (dying || gone || !entered) return false
        for (pt in parts) {
            if (!pt.alive) continue
            if (hypot(px - (x + pt.ox), py - (y + pt.oy)) < pt.r + 24f) {
                pt.hp -= dmg
                flash = 0.06f
                if (pt.hp <= 0) {
                    pt.alive = false
                    world.explode(x + pt.ox, y + pt.oy, 16, true)
                    world.sound.boom(true)
                    world.addScore(500, x + pt.ox, y + pt.oy)
                    if (parts.none { it.alive } && phase().needsParts > 0) advanceOrDie(world)
                }
                return true
            }
        }
        if (!vulnerable()) {
            world.explode(px, py, 6, false)
            world.shieldedMsg(px, py - 40f)
            return true
        }
        if (!hitTest(px, py)) return false
        hp -= dmg * weakMul(t)
        flash = 0.07f
        if (hp <= phase().until * maxHp) advanceOrDie(world)
        return true
    }

    fun onSpawn(world: World) {
        setupParts(world)
        try {
            phase().enter(this, world)
        } catch (_: Exception) {
        }
    }

    private fun advanceOrDie(world: World) {
        if (phaseIdx >= phases.size - 1) {
            startDying(world)
            return
        }
        mercy = false
        phaseIdx++
        tPhase = 0f
        timers = FloatArray(8)
        setupParts(world)
        phase().enter(this, world)
        world.warn = "WARNING"
        world.warnT = 1.6f
        world.sound.warn()
    }

    fun startDying(world: World) {
        if (dying) return
        dying = true
        deathT = def.deathTime
        boomT = 0f
        seqIdx = 0
        world.foeShots.clear()
        world.beams.clear()
        world.addText(x - wPx * 0.3f, y, "HOSTILE DOWN")
        world.sound.boom(false)
    }

    fun update(dt: Float, world: World, t: Float) {
        if (gone) return
        if (flash > 0) flash -= dt
        if (dying) {
            deathT -= dt
            boomT -= dt
            world.shake = 5f
            if (boomT <= 0) {
                boomT = if (def.id == "core") 0.12f else 0.18f
                if (def.deathStyle == 1 && def.id == "worm" && segs.isNotEmpty()) {
                    val s = segs[seqIdx % segs.size]
                    world.explode(s.first, s.second, 30, true)
                    seqIdx++
                } else {
                    val bx = x + (world.rnd.nextFloat() - 0.5f) * wPx
                    val by = y + (world.rnd.nextFloat() - 0.5f) * hPx
                    world.explode(bx, by, 26, true)
                }
                world.sound.boom(true)
            }
            if (deathT <= 0) {
                gone = true
                world.shake = 16f
                if (def.id == "worm") {
                    for (s in segs) world.explode(s.first, s.second, 20, true)
                } else {
                    world.explode(x, y, 60, true)
                }
                world.sound.boom(false)
            }
            return
        }
        if (!entered) {
            y += 320f * dt
            if (y >= targetY) {
                y = targetY
                entered = true
            }
            return
        }
        tPhase += dt
        val phm = phase()
        if (!mercy && phm.needsParts > 0 && tPhase > 30f && parts.any { it.alive }) {
            mercy = true
            world.addText(x - 120f, y + 140f, "SHIELD DOWN")
            world.sound.pickup()
        }
        try {
            phm.tick(this, world, dt)
        } catch (_: Exception) {
        }
        if (phm.timed > 0 && tPhase >= phm.timed) advanceOrDie(world)
        else if (phm.needsParts > 0 && parts.isNotEmpty() && parts.none { it.alive }) advanceOrDie(world)
        if (!world.player.dead && hitTest(world.player.x, world.player.y)) world.hurtPlayer()
    }

    fun draw(c: Canvas, t: Long, gt: Float) {
        if (gone) return
        if (def.id == "worm") {
            if (segs.isEmpty()) {
                Art.drawSegment(c, x, y, scrW * 0.05f, true, flash > 0)
                return
            }
            for ((i, s) in segs.withIndex()) {
                val r = scrW * 0.05f * (1f - i * 0.06f)
                Art.drawSegment(c, s.first, s.second, r, i == 0, flash > 0)
            }
            return
        }
        var alpha = 255
        if (dying && (t / 90) % 2L == 0L) alpha = 150
        c.saveLayerAlpha(0f, 0f, scrW, scrH, alpha)
        Art.drawBoss(c, def.id, x, y, wPx, hPx, t, vulnerable(), flash > 0)
        for (pt in parts) {
            if (!pt.alive) continue
            val px = x + pt.ox
            val py = y + pt.oy
            Art.drawWeak(c, px, py, pt.r, t)
            c.drawRect(px - pt.r * 0.4f, py - pt.r * 0.4f, px + pt.r * 0.4f, py + pt.r * 0.4f, Art.fill.apply { color = WHITE })
            if (def.id == "fortress") {
                Art.fill.color = WHITE
                c.drawCircle(px, py, pt.r * 0.9f, Art.fill)
                Art.fill.color = BLACK
                c.drawCircle(px, py, pt.r * 0.35f, Art.fill)
            }
        }
        if (weakMul(gt) > 1f) Art.drawWeak(c, x, y, wPx * 0.3f, t)
        if (!vulnerable() && entered && !dying && (t / 240) % 2L == 0L) {
            Art.stroke.color = WHITE
            Art.stroke.strokeWidth = 6f
            val rx = wPx * 0.62f
            val ry = hPx * 0.68f
            for (i in 0 until 6) {
                val a0 = i * PI / 3
                val a1 = (i + 1) * PI / 3
                c.drawLine(
                    x + (cos(a0) * rx).toFloat(), y + (sin(a0) * ry).toFloat(),
                    x + (cos(a1) * rx).toFloat(), y + (sin(a1) * ry).toFloat(), Art.stroke
                )
            }
        }
        c.restore()
    }
}

private fun every(b: Boss, dt: Float, slot: Int, cd: Float, fn: () -> Unit) {
    b.timers[slot] -= dt
    if (b.timers[slot] <= 0) {
        b.timers[slot] = cd
        fn()
    }
}

// pattern codes: 0 aimed, 1 spread3, 2 spread5, 3 radial8, 4 radial12,
// 5 spiral2, 6 spiral3, 7 beams, 8 summon, 9 walls, 10 aimed-burst
// weak: 0 always open, 1 shielded entry (2 nodes), 2 regenerating shields (3 nodes), 3 overheat windows
data class GenRow(
    val name: String, val vis: String, val hp: Float, val ph: Int,
    val p0: Int, val p1: Int, val p2: Int, val weak: Int
)

val GEN_TABLE = listOf(
    GenRow("GNAW", "nest", 1000f, 2, 1, 3, 0, 0),
    GenRow("THORN", "spire", 1100f, 2, 2, 3, 0, 0),
    GenRow("RASP", "ring", 1200f, 2, 0, 3, 8, 0),
    GenRow("MAW", "nest", 1300f, 2, 2, 5, 0, 1),
    GenRow("SPIRE", "spire", 1400f, 2, 3, 7, 0, 0),
    GenRow("GRIT", "cross", 1500f, 2, 0, 2, 8, 0),
    GenRow("VEX", "ring", 1600f, 3, 2, 3, 5, 0),
    GenRow("HOUND", "hunter", 1700f, 3, 0, 10, 3, 3),
    GenRow("CROWN", "ring", 1800f, 3, 3, 7, 2, 0),
    GenRow("FANG", "nest", 1900f, 3, 2, 5, 8, 1),
    GenRow("LOOM", "spire", 2000f, 3, 3, 9, 0, 0),
    GenRow("DREG", "cross", 2100f, 3, 0, 2, 4, 0),
    GenRow("TALON", "hunter", 2200f, 3, 10, 5, 3, 3),
    GenRow("ORBIT", "ring", 2300f, 3, 4, 7, 8, 1),
    GenRow("GULLET", "nest", 2400f, 3, 2, 6, 0, 0),
    GenRow("PILLAR", "spire", 2500f, 3, 7, 4, 9, 2),
    GenRow("WRETCH", "cross", 2600f, 3, 0, 4, 2, 0),
    GenRow("STALK", "hunter", 2700f, 3, 10, 7, 3, 3),
    GenRow("HALO", "ring", 2800f, 3, 3, 5, 7, 0),
    GenRow("REAP", "nest", 2900f, 3, 2, 8, 6, 1),
    GenRow("MONOLITH", "spire", 3000f, 3, 7, 9, 4, 2),
    GenRow("HUSK", "cross", 3100f, 3, 0, 4, 2, 0),
    GenRow("LANCE", "hunter", 3200f, 3, 10, 6, 4, 3),
    GenRow("VORTEX", "ring", 3300f, 3, 5, 7, 4, 0),
    GenRow("JAWS", "nest", 3400f, 3, 2, 6, 8, 1),
    GenRow("OBELISK", "spire", 3500f, 3, 7, 9, 4, 2),
    GenRow("BLIGHT", "cross", 3600f, 3, 4, 2, 10, 0),
    GenRow("SWIFT", "hunter", 3700f, 3, 10, 6, 7, 3),
    GenRow("CORONA", "ring", 3800f, 4, 6, 7, 9, 0),
    GenRow("CRUSH", "nest", 3900f, 4, 2, 4, 8, 1),
    GenRow("TOWER", "spire", 4000f, 4, 7, 9, 6, 2),
    GenRow("ROT", "cross", 4100f, 4, 4, 10, 2, 0),
    GenRow("GALE", "hunter", 4200f, 4, 10, 6, 4, 3),
    GenRow("ECLIPSE", "ring", 4300f, 4, 6, 7, 8, 1),
    GenRow("GORGE", "nest", 4400f, 4, 2, 6, 9, 0),
    GenRow("KEEP", "spire", 4500f, 4, 7, 4, 9, 2),
    GenRow("PLAGUE", "cross", 4600f, 4, 4, 2, 8, 0),
    GenRow("FURY", "hunter", 4700f, 4, 10, 6, 7, 3),
    GenRow("NOVA", "ring", 4800f, 4, 6, 7, 4, 1),
    GenRow("TITAN", "nest", 4900f, 4, 2, 9, 8, 0),
    GenRow("PARAGON", "spire", 5000f, 4, 7, 9, 6, 2),
    GenRow("SPECTRE", "cross", 5100f, 4, 4, 6, 8, 0),
    GenRow("TEMPEST", "hunter", 5200f, 4, 10, 7, 6, 3),
    GenRow("ABYSS", "ring", 5400f, 4, 6, 4, 9, 1)
)

private fun genPattern(code: Int, bb: Boss, ww: World) {
    val bx = bb.x
    val by = bb.y + 60f
    when (code) {
        0 -> ww.bAimed(bx, by, 400f)
        1 -> {
            val a = atan2(ww.player.y - by, ww.player.x - bx)
            for (k in -1..1) ww.bShot(bx, by, cos(a + k * 0.22f) * 380f, sin(a + k * 0.22f) * 380f)
        }
        2 -> {
            val a = atan2(ww.player.y - by, ww.player.x - bx)
            for (k in -2..2) ww.bShot(bx, by, cos(a + k * 0.2f) * 400f, sin(a + k * 0.2f) * 400f)
        }
        3 -> ww.bRadial(bx, by, 8, 280f, bb.tPhase.toDouble())
        4 -> ww.bRadial(bx, by, 12, 290f, (-bb.tPhase).toDouble())
        5 -> {
            bb.spinA += 0.6f
            for (k in 0 until 2) {
                val a = bb.spinA + k * PI.toFloat()
                ww.bShot(bx, by, cos(a) * 300f, sin(a) * 300f)
            }
        }
        6 -> {
            bb.spinA += 0.6f
            for (k in 0 until 3) {
                val a = bb.spinA + k * 2f * PI.toFloat() / 3f
                ww.bShot(bx, by, cos(a) * 300f, sin(a) * 300f)
            }
        }
        7 -> ww.addBeam(0f, ww.player.y, ww.w, ww.player.y, 26f, 0.9f, 0.7f)
        8 -> {
            ww.spawnSpec(0, bx - 60f, by)
            ww.spawnSpec(7, bx + 60f, by)
        }
        9 -> ww.spawnWallBox(false)
        else -> repeat(3) { ww.bAimed(bx, by, 420f) }
    }
}


private fun buildCustomSkillBoss(spec: BossSpecInfo, idx: Int, mul: Float, w: Float, h: Float): Boss {
    val def = BossDef(spec.id, spec.title, 1200 + idx * 150, 0.52f, 0.22f, 3.5f, 0)
    val b = Boss(def, mul, w, h)
    b.wPx = w * def.wFrac
    b.hPx = h * def.hFrac
    b.x = w / 2f
    b.y = -b.hPx
    b.targetY = h * 0.18f
    b.maxHp = spec.baseHp * mul
    b.hp = b.maxHp

    val totalPhases = spec.phasesCount
    for (k in 0 until totalPhases) {
        val until = if (k == totalPhases - 1) 0f else 1f - (k + 1).toFloat() / totalPhases
        val needParts = when (spec.skillCode) {
            14 -> if (k == 0) 2 else 0 // prismatic key nodes
            16 -> if (k == 0) 1 else 0 // lockbox shackle
            44 -> 2 // spire-guard shields
            47 -> if (k == 0) 3 else 0 // nest-guard shell
            else -> 0
        }
        b.phases.add(
            Phase(
                "PHASE ${k + 1}", until,
                needsParts = needParts,
                partHp = spec.baseHp * 0.12f,
                vuln = { bb ->
                    when (spec.skillCode) {
                        16 -> if (k == 0 && bb.parts.any { it.alive }) false else true
                        17 -> (bb.timers[3] > 0f) // flareon shoot/shield alternating
                        else -> if (needParts > 0) bb.parts.none { it.alive } else true
                    }
                },
                tick = { bb, ww, dt ->
                    // Unique boss movement based on archetype
                    when (spec.skillCode) {
                        4, 24 -> { // vortex gravity pull
                            bb.x += ((ww.w * 0.5f + sin(bb.tPhase * 1.5f) * 60f) - bb.x) * dt
                            // pull player slightly towards center
                            if (ww.player.y < ww.h * 0.75f) {
                                ww.player.y += 45f * dt
                            }
                        }
                        11 -> { // flare horizontal swoop
                            bb.x += ((ww.w * 0.5f + sin(bb.tPhase * 2.2f) * (ww.w * 0.35f)) - bb.x) * 1.5f * dt
                        }
                        12 -> { // delta aircraft banking
                            bb.x += ((ww.w * 0.5f + sin(bb.tPhase * 1.2f) * (ww.w * 0.28f)) - bb.x) * dt
                        }
                        28 -> { // needle rapid oscillation
                            bb.x += ((ww.w * 0.5f + sin(bb.tPhase * 3.5f) * (ww.w * 0.38f)) - bb.x) * 2.5f * dt
                        }
                        else -> {
                            bb.x += ((ww.w * 0.5f + sin(bb.tPhase * 0.7f) * (ww.w * 0.22f)) - bb.x) * dt
                        }
                    }

                    // Primary attack routine
                    every(bb, dt, 0, (1.8f - k * 0.2f).coerceAtLeast(0.7f)) {
                        genPattern(spec.patternCode, bb, ww)
                    }

                    // Unique secondary skill behavior
                    every(bb, dt, 1, 3.2f) {
                        when (spec.skillCode) {
                            1 -> { // prism refract spread
                                val a = atan2(ww.player.y - bb.y, ww.player.x - bb.x)
                                ww.bShot(bb.x, bb.y + 40f, cos(a - 0.3f) * 350f, sin(a - 0.3f) * 350f)
                                ww.bShot(bb.x, bb.y + 40f, cos(a) * 350f, sin(a) * 350f)
                                ww.bShot(bb.x, bb.y + 40f, cos(a + 0.3f) * 350f, sin(a + 0.3f) * 350f)
                            }
                            2 -> { // cascade music rhythm gaps
                                ww.spawnWallBox(false)
                            }
                            3 -> { // gauntlet narrowing passage
                                ww.spawnWallBox(true)
                            }
                            5, 43 -> { // beacon / colossus beam
                                ww.addBeam(bb.x, 0f, bb.x, ww.h, 30f, 0.8f, 0.6f)
                            }
                            6 -> { // splitter dual attack
                                ww.bAimed(bb.x - 60f, bb.y + 40f, 380f)
                                ww.bAimed(bb.x + 60f, bb.y + 40f, 380f)
                            }
                            7 -> { // orbit fragments attack
                                ww.bRadial(bb.x, bb.y + 40f, 8, 300f, bb.tPhase.toDouble())
                            }
                            8 -> { // sawtooth deflect burst
                                ww.bRadial(bb.x, bb.y + 40f, 6, 320f, (-bb.tPhase).toDouble())
                            }
                            13, 42 -> { // web drone spawn & strands
                                ww.spawnSpec(12, bb.x - 40f, bb.y + 40f)
                                ww.spawnSpec(12, bb.x + 40f, bb.y + 40f)
                            }
                            15, 29 -> { // helix / coil spring release
                                val a = atan2(ww.player.y - bb.y, ww.player.x - bb.x)
                                repeat(5) { sIdx ->
                                    ww.bShot(bb.x, bb.y + 40f, cos(a + (sIdx - 2) * 0.15f) * 440f, sin(a + (sIdx - 2) * 0.15f) * 440f)
                                }
                            }
                            17 -> { // flareon shield toggle
                                bb.timers[3] = if (bb.timers[3] > 0f) 0f else 2.5f
                                ww.addText(bb.x - 60f, bb.y + 60f, if (bb.timers[3] > 0f) "VULNERABLE" else "SHIELDED")
                            }
                            21 -> { // pulse expanding ring
                                ww.bRadial(bb.x, bb.y + 40f, 10, 260f + k * 40f, 0.0)
                            }
                            34 -> { // sentinel-wing wind gust
                                for (wIdx in -2..2) {
                                    ww.bShot(bb.x + wIdx * 40f, bb.y + 40f, wIdx * 40f, 360f)
                                }
                            }
                            36 -> { // cataclysm spore cloud
                                ww.bRadial(bb.x, bb.y + 40f, 12, 280f, (bb.tPhase * 2.0).toDouble())
                            }
                            41 -> { // dominion zone execution
                                val zx = if (ww.player.x < ww.w * 0.5f) ww.w * 0.25f else ww.w * 0.75f
                                ww.addBeam(zx, 0f, zx, ww.h, 45f, 0.9f, 0.7f)
                            }
                            48 -> { // ultimate planetary sovereign
                                ww.bRadial(bb.x, bb.y + 60f, 14, 320f, bb.spinA.toDouble())
                                bb.spinA += 0.8f
                                ww.bAimed(bb.x, bb.y + 60f, 420f)
                            }
                            49 -> { // the devel red eye
                                ww.bAimed(bb.x, bb.y + 40f, 460f)
                                ww.addBeam(bb.x, 0f, bb.x, ww.h, 24f, 0.7f, 0.5f)
                            }
                            50 -> { // uncenced ai matrix
                                ww.bRadial(bb.x, bb.y + 40f, 8, 300f, 0.0)
                                ww.bRadial(bb.x, bb.y + 40f, 8, 300f, PI / 8.0)
                            }
                            else -> {
                                ww.bRadial(bb.x, bb.y + 40f, 8, 280f, bb.tPhase.toDouble())
                            }
                        }
                    }
                }
            )
        )
    }
    return b
}

fun makeBoss(id: String, mul: Float, w: Float, h: Float): Boss {
    // Check if ID matches any of our 50 custom skill bosses
    val spec = ALL_50_BOSS_SPECS.find { it.id.equals(id, ignoreCase = true) }
        ?: run {
            val idx = (id.replace("boss", "").replace("gen", "").toIntOrNull() ?: 0).coerceIn(0, 49)
            ALL_50_BOSS_SPECS[idx]
        }
    val idx = ALL_50_BOSS_SPECS.indexOf(spec)
    return buildCustomSkillBoss(spec, idx, mul, w, h)
}
