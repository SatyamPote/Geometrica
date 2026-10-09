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

private fun buildGenBoss(idx: Int, row: GenRow, mul: Float, w: Float, h: Float): Boss {
    val def = BossDef("boss$idx", row.name, 1200 + idx * 120, 0.44f, 0.22f, 3.2f, 0)
    val b = Boss(def, mul, w, h)
    b.wPx = w * def.wFrac
    b.hPx = h * def.hFrac
    b.x = w / 2f
    b.y = -b.hPx
    b.targetY = h * 0.2f
    b.maxHp = row.hp * mul
    b.hp = b.maxHp
    val pats = intArrayOf(row.p0, row.p1, row.p2)
    for (k in 0 until row.ph) {
        val primary = pats[k % 3]
        val until = if (k == row.ph - 1) 0f else 1f - (k + 1).toFloat() / row.ph
        val needParts = when {
            row.weak == 2 -> 3
            row.weak == 1 && k == 0 -> 2
            else -> 0
        }
        b.phases.add(
            Phase(
                "WAVE ${k + 1}", until,
                needsParts = needParts,
                partHp = row.hp * 0.07f,
                vuln = { if (needParts > 0) false else true },
                tick = { bb, ww, dt ->
                    bb.x += ((ww.w * 0.5f + sin(bb.tPhase * (0.4f + idx * 0.005f)) * (60f + idx)) - bb.x) * dt
                    every(bb, dt, 0, (1.8f - k * 0.15f).coerceAtLeast(0.9f)) { genPattern(primary, bb, ww) }
                    val secondary = if (primary == 0 || primary == 1 || primary == 10) 3 else 0
                    every(bb, dt, 1, 4f) { genPattern(secondary, bb, ww) }
                    if (row.weak == 3) {
                        every(bb, dt, 2, 8f) {
                            bb.weakUntil = ww.elapsed + 2.2f
                            ww.addText(bb.x - 120f, bb.y + 80f, "OVERHEAT")
                        }
                    }
                }
            )
        )
    }
    return b
}

// ================================================================
fun makeBoss(id: String, mul: Float, w: Float, h: Float): Boss {
    if (id.startsWith("gen")) {
        val idx = id.removePrefix("gen").toIntOrNull()?.coerceIn(0, GEN_TABLE.size - 1) ?: 0
        return buildGenBoss(idx, GEN_TABLE[idx], mul, w, h)
    }
    val def = when (id) {
        "sentinel" -> BossDef(id, "SENTINEL", 3000, 0.60f, 0.16f, 3.0f, 0)
        "worm" -> BossDef(id, "WORM", 4500, 0.50f, 0.20f, 3.6f, 1)
        "fortress" -> BossDef(id, "FORTRESS", 6000, 0.70f, 0.16f, 4.0f, 0)
        "hunter" -> BossDef(id, "HUNTER", 3400, 0.24f, 0.15f, 4.0f, 0)
        "colossus" -> BossDef(id, "COLOSSUS", 5600, 0.86f, 0.30f, 4.6f, 0)
        else -> BossDef("core", "VOID CORE", 8000, 0.55f, 0.26f, 5.5f, 0)
    }
    val b = Boss(def, mul, w, h)
    b.wPx = w * def.wFrac
    b.hPx = h * def.hFrac
    b.x = w / 2f
    b.y = -b.hPx
    b.targetY = when (id) {
        "colossus" -> h * 0.13f
        else -> h * 0.20f
    }

    when (id) {
        // ---------- BOSS 1: SENTINEL ----------
        "sentinel" -> {
            b.maxHp = 2600f * mul; b.hp = b.maxHp
            b.phases.add(
                Phase("PERIMETER", 0.5f,
                    tick = { bb, ww, dt ->
                        bb.x += ((ww.w * 0.5f + sin(bb.tPhase * 0.8f) * ww.w * 0.2f) - bb.x) * dt
                        every(bb, dt, 0, 1.1f) { ww.bAimed(bb.x, bb.y + bb.hPx * 0.4f, 380f) }
                        every(bb, dt, 1, 4f) {
                            ww.addBeam(0f, ww.player.y, ww.w, ww.player.y, 26f, 0.9f, 0.7f)
                        }
                        every(bb, dt, 2, 7f) {
                            ww.spawnSpec(0, bb.x - 80f, bb.y + 40f)
                            ww.spawnSpec(0, bb.x + 80f, bb.y + 40f)
                        }
                    })
            )
            b.phases.add(
                Phase("OVERDRIVE", 0f,
                    tick = { bb, ww, dt ->
                        bb.x += ((ww.w * 0.5f + sin(bb.tPhase * 1.3f) * ww.w * 0.28f) - bb.x) * dt
                        every(bb, dt, 0, 0.8f) { ww.bAimed(bb.x, bb.y + bb.hPx * 0.4f, 420f) }
                        every(bb, dt, 1, 2.8f) {
                            ww.addBeam(0f, ww.player.y, ww.w, ww.player.y, 30f, 0.8f, 0.7f)
                        }
                        every(bb, dt, 2, 6f) {
                            repeat(3) { ww.spawnSpec(0, bb.x + (it - 1) * 90f, bb.y + 60f) }
                        }
                    })
            )
        }
        // ---------- BOSS 2: WORM ----------
        "worm" -> {
            b.maxHp = 3400f * mul; b.hp = b.maxHp
            fun layout(bb: Boss, ww: World, cx: Float, cy: Float) {
                bb.segs.clear()
                for (i in 0 until 7) {
                    val sx = cx + sin(bb.tPhase * 2f + i * 0.8f) * ww.w * 0.06f + i * ww.w * 0.055f - ww.w * 0.16f
                    val sy = cy + sin(bb.tPhase * 2.4f + i * 0.9f) * ww.h * 0.05f
                    bb.segs.add(Pair(sx, sy))
                }
            }
            b.phases.add(
                Phase("BURROW", 0.5f,
                    enter = { bb, ww -> layout(bb, ww, ww.w * 0.5f, ww.h * 0.18f) },
                    tick = { bb, ww, dt ->
                        val charging = bb.timers[7] > 0
                        if (!charging) {
                            val cx = ww.w * 0.5f + sin(bb.tPhase * 0.5f) * ww.w * 0.08f
                            val cy = ww.h * 0.18f + sin(bb.tPhase * 0.7f) * ww.h * 0.05f
                            layout(bb, ww, cx, cy)
                            val head = bb.segs[0]
                            bb.x = head.first
                            bb.y = head.second
                        }
                        every(bb, dt, 0, 2.2f) {
                            val s = bb.segs.firstOrNull() ?: return@every
                            val a = atan2(ww.player.y - s.second, ww.player.x - s.first)
                            for (k in -1..1) ww.bShot(s.first, s.second, cos(a + k * 0.3f) * 360f, sin(a + k * 0.3f) * 360f)
                        }
                        every(bb, dt, 1, 7f) {
                            bb.timers[7] = 1.6f
                            bb.timers[6] = ww.player.x
                            ww.addBeam(ww.player.x, 0f, ww.player.x, ww.h, 10f, 0.7f, 0.01f)
                        }
                        if (charging) {
                            bb.timers[7] -= dt
                            val tx = bb.timers[6]
                            if (bb.timers[7] < 0.9f) {
                                val ph = 1f - bb.timers[7] / 0.9f
                                val hy = if (ph < 0.5f) ww.h * 0.18f + ph * 2f * ww.h * 0.8f else ww.h * 0.98f - (ph - 0.5f) * 2f * ww.h * 0.8f
                                layout(bb, ww, tx, hy)
                                bb.x = tx
                                bb.y = hy
                            }
                        }
                    })
            )
            b.phases.add(
                Phase("FRENZY", 0f,
                    tick = { bb, ww, dt ->
                        val cx = ww.w * 0.5f + sin(bb.tPhase * 0.9f) * ww.w * 0.1f
                        val cy = ww.h * 0.2f + sin(bb.tPhase * 1.2f) * ww.h * 0.06f
                        layout(bb, ww, cx, cy)
                        val head = bb.segs[0]
                        bb.x = head.first
                        bb.y = head.second
                        every(bb, dt, 0, 1.6f) {
                            val s = bb.segs.firstOrNull() ?: return@every
                            val a = atan2(ww.player.y - s.second, ww.player.x - s.first)
                            for (k in -2..2) ww.bShot(s.first, s.second, cos(a + k * 0.28f) * 380f, sin(a + k * 0.28f) * 380f)
                        }
                        every(bb, dt, 1, 5f) { ww.bRadial(bb.x, bb.y, 10, 280f, bb.tPhase.toDouble()) }
                        every(bb, dt, 2, 6f) {
                            bb.timers[7] = 1.4f
                            bb.timers[6] = ww.player.x
                            ww.addBeam(ww.player.x, 0f, ww.player.x, ww.h, 10f, 0.6f, 0.01f)
                        }
                        if (bb.timers[7] > 0) {
                            bb.timers[7] -= dt
                            if (bb.timers[7] < 0.8f) {
                                val ph = 1f - bb.timers[7] / 0.8f
                                val tx = bb.timers[6]
                                val hy = if (ph < 0.5f) ww.h * 0.2f + ph * 2f * ww.h * 0.8f else ww.h - (ph - 0.5f) * 2f * ww.h * 0.8f
                                layout(bb, ww, tx, hy)
                                bb.x = tx
                                bb.y = hy
                            }
                        }
                    })
            )
        }
        // ---------- BOSS 3: FORTRESS ----------
        "fortress" -> {
            b.maxHp = 4600f * mul; b.hp = b.maxHp
            b.phases.add(
                Phase("BATTERIES", 0.62f, needsParts = 3, partHp = 220f, vuln = { false },
                    tick = { bb, ww, dt ->
                        bb.x += ((ww.w * 0.5f + sin(bb.tPhase * 0.4f) * 40f) - bb.x) * dt
                        every(bb, dt, 0, 1.7f) {
                            for (pt in bb.parts) {
                                if (!pt.alive) continue
                                ww.bAimed(bb.x + pt.ox, bb.y + pt.oy, 380f)
                            }
                        }
                    })
            )
            b.phases.add(
                Phase("CORE EXPOSED", 0.3f,
                    tick = { bb, ww, dt ->
                        bb.x += ((ww.w * 0.5f + sin(bb.tPhase * 0.6f) * ww.w * 0.1f) - bb.x) * dt
                        every(bb, dt, 0, 2.2f) { ww.bRadial(bb.x, bb.y + bb.hPx * 0.3f, 10, 280f, bb.tPhase.toDouble()) }
                        every(bb, dt, 1, 5f) {
                            ww.spawnSpec(37, bb.x - 100f, bb.y + 60f)
                            ww.spawnSpec(37, bb.x + 100f, bb.y + 60f)
                        }
                    })
            )
            b.phases.add(
                Phase("MELTDOWN", 0f,
                    tick = { bb, ww, dt ->
                        bb.x += ((ww.player.x) - bb.x) * 0.6f * dt
                        every(bb, dt, 0, 1.4f) { ww.bRadial(bb.x, bb.y + bb.hPx * 0.3f, 12, 300f, (-bb.tPhase).toDouble()) }
                        every(bb, dt, 1, 3.4f) {
                            ww.addBeam(0f, bb.y + 60f, ww.w, bb.y + 60f, 26f, 0.8f, 0.7f)
                        }
                    })
            )
        }
        // ---------- BOSS 4: HUNTER ----------
        "hunter" -> {
            b.maxHp = 3400f * mul; b.hp = b.maxHp
            b.phases.add(
                Phase("DUEL", 0.5f,
                    tick = { bb, ww, dt ->
                        bb.x += ((ww.player.x) - bb.x) * 2.2f * dt
                        bb.y += ((ww.h * 0.24f + sin(bb.tPhase * 0.9f) * 60f) - bb.y) * dt
                        every(bb, dt, 0, 1.4f) {
                            repeat(3) { ww.bAimed(bb.x, bb.y + 50f, 440f) }
                        }
                        every(bb, dt, 1, 3.2f) {
                            bb.timers[7] = 0.9f
                            bb.timers[5] = ww.player.x
                            bb.timers[6] = ww.player.y - 260f
                            bb.flash = 0.5f
                        }
                        if (bb.timers[7] > 0) {
                            bb.timers[7] -= dt
                            if (bb.timers[7] < 0.4f) {
                                bb.x += ((bb.timers[5]) - bb.x) * 8f * dt
                                bb.y += ((bb.timers[6]) - bb.y) * 8f * dt
                            }
                        }
                        every(bb, dt, 2, 9f) {
                            bb.weakUntil = ww.elapsed + 2.2f
                            ww.addText(bb.x - 120f, bb.y + 80f, "OVERHEAT")
                        }
                    })
            )
            b.phases.add(
                Phase("BLOODLUST", 0f,
                    tick = { bb, ww, dt ->
                        bb.x += ((ww.player.x) - bb.x) * 2.8f * dt
                        bb.y += ((ww.h * 0.22f + sin(bb.tPhase * 1.4f) * 90f) - bb.y) * dt
                        every(bb, dt, 0, 1.1f) {
                            val a = atan2(ww.player.y - bb.y, ww.player.x - bb.x)
                            for (k in -2..2) ww.bShot(bb.x, bb.y + 50f, cos(a + k * 0.22f) * 440f, sin(a + k * 0.22f) * 440f)
                        }
                        every(bb, dt, 1, 2.4f) {
                            bb.timers[7] = 0.8f
                            bb.timers[5] = ww.player.x
                            bb.timers[6] = ww.player.y - 240f
                            bb.flash = 0.5f
                        }
                        if (bb.timers[7] > 0) {
                            bb.timers[7] -= dt
                            if (bb.timers[7] < 0.35f) {
                                bb.x += ((bb.timers[5]) - bb.x) * 9f * dt
                                bb.y += ((bb.timers[6]) - bb.y) * 9f * dt
                            }
                        }
                        every(bb, dt, 2, 8f) {
                            bb.weakUntil = ww.elapsed + 2.2f
                            ww.addText(bb.x - 120f, bb.y + 80f, "OVERHEAT")
                        }
                    })
            )
        }
        // ---------- BOSS 5: COLOSSUS ----------
        "colossus" -> {
            b.maxHp = 5600f * mul; b.hp = b.maxHp
            b.phases.add(
                Phase("PLATING", 0.66f, needsParts = 3, partHp = 380f, vuln = { false },
                    tick = { bb, ww, dt ->
                        every(bb, dt, 0, 1.8f) {
                            for (pt in bb.parts) {
                                if (!pt.alive) continue
                                ww.bAimed(bb.x + pt.ox, bb.y + pt.oy, 380f)
                            }
                        }
                        every(bb, dt, 1, 5f) { ww.spawnSpec(1, bb.x - bb.wPx * 0.3f, bb.y + 60f) }
                    })
            )
            b.phases.add(
                Phase("FURNACE", 0.25f,
                    tick = { bb, ww, dt ->
                        every(bb, dt, 0, 2.6f) { ww.bRadial(bb.x, bb.y + 60f, 12, 290f, bb.tPhase.toDouble()) }
                        every(bb, dt, 1, 3.8f) {
                            ww.addBeam(0f, bb.y + 100f, ww.w, bb.y + 100f, 30f, 0.9f, 0.8f)
                            ww.addBeam(0f, bb.y + 200f, ww.w, bb.y + 200f, 30f, 1.1f, 0.8f)
                        }
                        every(bb, dt, 2, 3f) {
                            ww.bAimed(bb.x - 80f, bb.y + 80f, 300f, 12f)
                            ww.bAimed(bb.x + 80f, bb.y + 80f, 300f, 12f)
                        }
                    })
            )
            b.phases.add(
                Phase("COLLAPSE", 0f,
                    tick = { bb, ww, dt ->
                        every(bb, dt, 0, 1.6f) { ww.bRadial(bb.x, bb.y + 60f, 16, 310f, (-bb.tPhase * 1.4f).toDouble()) }
                        every(bb, dt, 1, 2.8f) {
                            ww.addBeam(ww.player.x - 120f, 0f, ww.player.x - 120f, ww.h, 30f, 0.8f, 0.7f)
                            ww.addBeam(ww.player.x + 120f, 0f, ww.player.x + 120f, ww.h, 30f, 0.8f, 0.7f)
                        }
                        every(bb, dt, 2, 4f) { ww.spawnSpec(39, bb.x, bb.y + 100f) }
                    })
            )
        }
        // ---------- FINAL BOSS: VOID CORE ----------
        "core" -> {
            b.maxHp = 8000f * mul; b.hp = b.maxHp
            b.phases.add(
                Phase("EYE OPENS", 0.8f,
                    tick = { bb, ww, dt ->
                        bb.x += ((ww.w * 0.5f + sin(bb.tPhase * 0.5f) * ww.w * 0.1f) - bb.x) * dt
                        every(bb, dt, 0, 1.5f) { ww.bAimed(bb.x, bb.y + 80f, 400f) }
                        every(bb, dt, 1, 2.6f) { ww.bRadial(bb.x, bb.y + 80f, 8, 280f, bb.tPhase.toDouble()) }
                    })
            )
            b.phases.add(
                Phase("SPIRAL", 0.6f,
                    tick = { bb, ww, dt ->
                        bb.x += ((ww.w * 0.5f + sin(bb.tPhase * 0.7f) * ww.w * 0.15f) - bb.x) * dt
                        bb.spinA += dt * 2.2f
                        every(bb, dt, 0, 0.3f) {
                            for (k in 0 until 3) {
                                val a = bb.spinA + k * 2f * PI.toFloat() / 3f
                                ww.bShot(bb.x, bb.y + 60f, cos(a) * 300f, sin(a) * 300f)
                            }
                        }
                        every(bb, dt, 1, 4f) { ww.spawnSpec(37, bb.x, bb.y + 80f) }
                    })
            )
            b.phases.add(
                Phase("EVENT WALL", 0.4f, needsParts = 2, partHp = 340f, vuln = { false },
                    tick = { bb, ww, dt ->
                        every(bb, dt, 0, 1.8f) { ww.bRadial(bb.x, bb.y + 80f, 12, 290f, (-bb.tPhase).toDouble()) }
                        every(bb, dt, 1, 4.5f) { ww.spawnWallBox(false) }
                        every(bb, dt, 2, 3f) {
                            ww.bAimed(bb.x - 60f, bb.y + 80f, 400f)
                            ww.bAimed(bb.x + 60f, bb.y + 80f, 400f)
                        }
                    })
            )
            b.phases.add(
                Phase("SINGULARITY", 0.18f,
                    tick = { bb, ww, dt ->
                        bb.x += ((ww.player.x) - bb.x) * 1.2f * dt
                        every(bb, dt, 0, 1.1f) { ww.bRadial(bb.x, bb.y + 80f, 14, 300f, (bb.tPhase * 1.5f).toDouble()) }
                        every(bb, dt, 1, 2.6f) {
                            ww.addBeam(ww.player.x, 0f, ww.player.x, ww.h, 34f, 0.8f, 0.7f)
                        }
                        every(bb, dt, 2, 3.4f) { ww.spawnSpec(26, bb.x - 60f, bb.y + 80f); ww.spawnSpec(26, bb.x + 60f, bb.y + 80f) }
                    })
            )
            b.phases.add(
                Phase("UNSTABLE", 0f,
                    tick = { bb, ww, dt ->
                        bb.x += ((ww.w * 0.5f + sin(bb.tPhase * 2.2f) * ww.w * 0.3f) - bb.x) * 2f * dt
                        ww.shake = 3f
                        every(bb, dt, 0, 0.85f) { ww.bRadial(bb.x, bb.y + 80f, 16, 320f, bb.spinA.toDouble()) }
                        bb.spinA += dt * 1.5f
                        every(bb, dt, 1, 1.3f) {
                            val a = atan2(ww.player.y - bb.y, ww.player.x - bb.x)
                            for (k in -2..2) ww.bShot(bb.x, bb.y + 80f, cos(a + k * 0.2f) * 420f, sin(a + k * 0.2f) * 420f)
                        }
                        every(bb, dt, 2, 5f) { ww.spawnWallBox(true) }
                    })
            )
        }
    }
    return b
}
