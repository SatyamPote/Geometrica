package com.nebulastrike.game

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

const val WHITE = 0xFFFFFFFF.toInt()
const val GRAY = 0xFF9AA0A6.toInt()
const val DIM = 0xFF555555.toInt()
const val DARK = 0xFF222222.toInt()
const val BLACK = 0xFF000000.toInt()

// AI family ids live in World.kt (shared); powerup ids below

// powerup ids
const val P_RAPID = 0
const val P_DOUBLE = 1
const val P_SPREAD = 2
const val P_PIERCE = 3
const val P_HEART = 4

/** Strict black/white pixel sprites, portrait orientation. No color, no gradients. */
object Art {
    val fill = Paint().apply { isAntiAlias = false }
    val stroke = Paint().apply {
        isAntiAlias = false
        style = Paint.Style.STROKE
    }
    private val path = Path()

    private fun rect(c: Canvas, x: Float, y: Float, w: Float, h: Float, color: Int) {
        fill.color = color
        c.drawRect(x, y, x + w, y + h, fill)
    }

    private fun circle(c: Canvas, x: Float, y: Float, r: Float, color: Int) {
        fill.color = color
        c.drawCircle(x, y, r, fill)
    }

    private fun ring(c: Canvas, x: Float, y: Float, r: Float, w: Float, color: Int) {
        stroke.color = color
        stroke.strokeWidth = w
        c.drawCircle(x, y, r, stroke)
    }

    // ---------------- player (faces up) ----------------
    fun drawPlayer(c: Canvas, x: Float, y: Float, s: Float, t: Long, blink: Boolean) {
        if (blink && (t / 80) % 2L == 0L) return
        // engine flame (2-frame)
        val f = if ((t / 90) % 2L == 0L) 1f else 0.6f
        rect(c, x - s * 0.16f, y + s * 0.75f, s * 0.32f, s * 0.6f * f, WHITE)
        rect(c, x - s * 0.08f, y + s * 1.05f, s * 0.16f, s * 0.4f * f, GRAY)
        // side fins
        path.reset()
        path.moveTo(x - s * 0.2f, y + s * 0.5f)
        path.lineTo(x - s * 0.75f, y + s * 1.0f)
        path.lineTo(x - s * 0.15f, y + s * 0.8f)
        path.close()
        fill.color = GRAY
        c.drawPath(path, fill)
        path.reset()
        path.moveTo(x + s * 0.2f, y + s * 0.5f)
        path.lineTo(x + s * 0.75f, y + s * 1.0f)
        path.lineTo(x + s * 0.15f, y + s * 0.8f)
        path.close()
        c.drawPath(path, fill)
        // fuselage (nose up)
        path.reset()
        path.moveTo(x, y - s * 1.1f)
        path.lineTo(x + s * 0.42f, y + s * 0.55f)
        path.lineTo(x - s * 0.42f, y + s * 0.55f)
        path.close()
        fill.color = WHITE
        c.drawPath(path, fill)
        // cockpit
        rect(c, x - s * 0.12f, y - s * 0.15f, s * 0.24f, s * 0.35f, BLACK)
    }

    // ---------------- enemies (face down), one silhouette per AI family ----------------
            fun drawEnemy(c: Canvas, ai: Int, spec: Int, x: Float, y: Float, r: Float, t: Long, shieldUp: Boolean, flash: Boolean) {
        val col = if (flash) BLACK else WHITE
        val inv = if (flash) WHITE else BLACK
        val bg = if (flash) WHITE else DARK
        val idx = spec.coerceIn(0, 99)
        val tier = idx / 25
        val family = (idx % 25) % 8
        val s = r * 0.95f

        when (family) {
            0 -> {
                // V-Wing Faceted Interceptor
                path.reset()
                path.moveTo(x, y + s * 1.3f)
                path.lineTo(x - s * 0.9f, y - s * 0.8f)
                path.lineTo(x - s * 0.35f, y - s * 0.4f)
                path.lineTo(x, y - s * 0.6f)
                path.lineTo(x + s * 0.35f, y - s * 0.4f)
                path.lineTo(x + s * 0.9f, y - s * 0.8f)
                path.close()
                fill.color = col
                c.drawPath(path, fill)
                // cockpit facet
                path.reset()
                path.moveTo(x, y + s * 0.5f)
                path.lineTo(x - s * 0.25f, y - s * 0.3f)
                path.lineTo(x + s * 0.25f, y - s * 0.3f)
                path.close()
                fill.color = inv
                c.drawPath(path, fill)
                if (tier >= 2) {
                    stroke.color = col; stroke.strokeWidth = 2f
                    c.drawLine(x - s * 0.9f, y - s * 0.8f, x - s * 1.1f, y - s * 0.2f, stroke)
                    c.drawLine(x + s * 0.9f, y - s * 0.8f, x + s * 1.1f, y - s * 0.2f, stroke)
                }
            }
            1 -> {
                // Swept Diamond Razorback
                path.reset()
                path.moveTo(x, y + s * 1.2f)
                path.lineTo(x + s * 0.75f, y)
                path.lineTo(x, y - s * 1.1f)
                path.lineTo(x - s * 0.75f, y)
                path.close()
                fill.color = bg
                c.drawPath(path, fill)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawPath(path, stroke)
                c.drawLine(x, y - s * 1.1f, x, y + s * 1.2f, stroke)
                path.reset()
                path.moveTo(x, y + s * 0.4f)
                path.lineTo(x + s * 0.3f, y)
                path.lineTo(x, y - s * 0.4f)
                path.lineTo(x - s * 0.3f, y)
                path.close()
                fill.color = col
                c.drawPath(path, fill)
                circle(c, x, y, 3f, inv)
            }
            2 -> {
                // Twin Forward Sponsons Gunship
                rect(c, x - s * 0.45f, y - s * 0.7f, s * 0.9f, s * 1.3f, col)
                rect(c, x - s * 0.25f, y - s * 0.4f, s * 0.5f, s * 0.7f, inv)
                rect(c, x - s * 0.85f, y - s * 0.5f, s * 0.35f, s * 1.6f, col)
                rect(c, x + s * 0.5f, y - s * 0.5f, s * 0.35f, s * 1.6f, col)
                if (tier >= 1) {
                    rect(c, x - s * 0.15f, y + s * 0.6f, s * 0.3f, s * 0.7f, col)
                }
            }
            3 -> {
                // Needle Lance Void Dart
                path.reset()
                path.moveTo(x, y + s * 1.5f)
                path.lineTo(x - s * 0.4f, y - s * 0.9f)
                path.lineTo(x, y - s * 0.6f)
                path.lineTo(x + s * 0.4f, y - s * 0.9f)
                path.close()
                fill.color = col
                c.drawPath(path, fill)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine(x - s * 0.75f, y - s * 0.2f, x + s * 0.75f, y - s * 0.2f, stroke)
                rect(c, x - 2f, y - s * 0.3f, 4f, s * 0.7f, inv)
            }
            4 -> {
                // Armored Hex-Corsair
                path.reset()
                for (i in 0 until 6) {
                    val a = i * PI / 3.0
                    val px = x + (cos(a) * s * 0.9f).toFloat()
                    val py = y + (sin(a) * s * 0.9f).toFloat()
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close()
                fill.color = bg
                c.drawPath(path, fill)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawPath(path, stroke)
                circle(c, x, y, s * 0.35f, col)
                circle(c, x, y, 4f, inv)
                if (tier >= 1) {
                    c.drawLine(x - s * 0.9f, y, x - s * 1.2f, y + s * 0.5f, stroke)
                    c.drawLine(x + s * 0.9f, y, x + s * 1.2f, y + s * 0.5f, stroke)
                }
            }
            5 -> {
                // Sacred Prism
                path.reset()
                path.moveTo(x, y + s * 1.1f)
                path.lineTo(x - s * 0.85f, y - s * 0.9f)
                path.lineTo(x + s * 0.85f, y - s * 0.9f)
                path.close()
                fill.color = col
                c.drawPath(path, fill)
                path.reset()
                path.moveTo(x, y + s * 0.4f)
                path.lineTo(x - s * 0.4f, y - s * 0.6f)
                path.lineTo(x + s * 0.4f, y - s * 0.6f)
                path.close()
                fill.color = inv
                c.drawPath(path, fill)
                circle(c, x, y, 3f, col)
            }
            6 -> {
                // Dual Nacelle Cruiser
                for (k in floatArrayOf(-s * 0.45f, s * 0.45f)) {
                    path.reset()
                    path.moveTo(x + k, y + s * 1.2f)
                    path.lineTo(x + k - s * 0.3f, y - s * 0.8f)
                    path.lineTo(x + k + s * 0.3f, y - s * 0.8f)
                    path.close()
                    fill.color = col
                    c.drawPath(path, fill)
                    circle(c, x + k, y, 3f, inv)
                }
                rect(c, x - s * 0.3f, y - s * 0.3f, s * 0.6f, s * 0.4f, col)
            }
            else -> {
                // Cruciform Dread Drone
                rect(c, x - s * 0.8f, y - s * 0.2f, s * 1.6f, s * 0.4f, col)
                rect(c, x - s * 0.2f, y - s * 0.9f, s * 0.4f, s * 2.0f, col)
                circle(c, x, y, s * 0.3f, bg)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawCircle(x, y, s * 0.3f, stroke)
                circle(c, x, y, 3f, col)
            }
        }

        if (shieldUp) {
            rect(c, x - s * 0.9f, y + s * 0.75f, s * 1.8f, s * 0.3f, col)
        }
    }

    fun drawShot(c: Canvas, x: Float, y: Float, big: Boolean) {
        if (big) rect(c, x - 4f, y - 14f, 8f, 28f, WHITE)
        else rect(c, x - 2.5f, y - 9f, 5f, 18f, WHITE)
    }

    fun drawFoeShot(c: Canvas, x: Float, y: Float, r: Float) {
        stroke.color = WHITE
        stroke.strokeWidth = 3f
        c.drawRect(x - r, y - r, x + r, y + r, stroke)
    }

    // ---------------- pickups ----------------
    private val pLetters = arrayOf("R", "2", "S", "P")

    fun drawItem(c: Canvas, kind: Int, x: Float, y: Float, r: Float, t: Long, txt: Paint) {
        if (kind == 4) {
            // heart pickup: pixel heart + blinking box
            if ((t / 150) % 2L == 0L) {
                stroke.color = WHITE
                stroke.strokeWidth = 4f
                c.drawRect(x - r, y - r, x + r, y + r, stroke)
            }
            fill.color = WHITE
            val u = r / 3f
            c.drawRect(x - 1.5f * u, y - 0.5f * u, x + 1.5f * u, y + u, fill)
            c.drawCircle(x - 0.75f * u, y - 0.5f * u, 0.78f * u, fill)
            c.drawCircle(x + 0.75f * u, y - 0.5f * u, 0.78f * u, fill)
            path.reset()
            path.moveTo(x - 1.5f * u, y + 0.2f * u)
            path.lineTo(x, y + 1.5f * u)
            path.lineTo(x + 1.5f * u, y + 0.2f * u)
            path.close()
            c.drawPath(path, fill)
            return
        }
        if ((t / 150) % 2L == 0L) {
            stroke.color = WHITE
            stroke.strokeWidth = 4f
            c.drawRect(x - r, y - r, x + r, y + r, stroke)
        } else {
            stroke.color = GRAY
            stroke.strokeWidth = 4f
            c.drawRect(x - r, y - r, x + r, y + r, stroke)
        }
        txt.color = WHITE
        txt.textSize = r * 1.2f
        c.drawText(pLetters[kind % pLetters.size], x, y + r * 0.42f, txt)
    }

    // ---------------- rocks ----------------
    fun drawRock(c: Canvas, x: Float, y: Float, r: Float, rot: Float) {
        fill.color = DIM
        poly(c, x, y, r, 8, rot, 0.3f)
        fill.color = GRAY
        poly(c, x, y, r * 0.65f, 8, rot + 0.5f, 0.3f)
        fill.color = BLACK
        c.drawCircle(x - r * 0.2f, y + r * 0.15f, r * 0.16f, fill)
        c.drawCircle(x + r * 0.25f, y - r * 0.2f, r * 0.12f, fill)
    }

    private fun poly(c: Canvas, x: Float, y: Float, r: Float, n: Int, rot: Float, jag: Float) {
        path.reset()
        for (i in 0 until n) {
            val a = rot + i * 2 * PI / n
            val rr = r * (1f + jag * sin(i * 12.9898).toFloat())
            val px = x + (cos(a) * rr).toFloat()
            val py = y + (sin(a) * rr).toFloat()
            if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
        }
        path.close()
        c.drawPath(path, fill)
    }

    // ---------------- obstacle boxes (portrait: walls span width, gaps left-right) ----------------
    fun drawBox(c: Canvas, b: Box, w: Float, t: Long) {
        val flash = b.flash > 0
        val edge = if (flash) BLACK else WHITE
        val body = if (flash) WHITE else DARK
        when (b.kind) {
            BOX_WALL_V, BOX_NARROW -> {
                // horizontal wall with a left-right gap
                val blink = !b.hot && (t / 180) % 2L == 0L
                stroke.color = if (blink) GRAY else edge
                stroke.strokeWidth = 7f
                val gl = b.gapX - b.gapW / 2
                val gr = b.gapX + b.gapW / 2
                var xx = 0f
                while (xx < gl) {
                    c.drawLine(xx, b.y, minOf(xx + 30f, gl), b.y, stroke)
                    xx += 52f
                }
                xx = gr
                while (xx < w) {
                    c.drawLine(xx, b.y, minOf(xx + 30f, w), b.y, stroke)
                    xx += 52f
                }
                fill.color = edge
                c.drawRect(0f, b.y - 26f, 30f, b.y + 26f, fill)
                c.drawRect(w - 30f, b.y - 26f, w, b.y + 26f, fill)
            }
            BOX_WALL_H -> {
                // left+right bars leaving a middle vertical lane
                val blink = !b.hot && (t / 180) % 2L == 0L
                fill.color = if (blink) GRAY else body
                val bar = b.w
                c.drawRect(0f, b.y - 190f, bar, b.y + 190f, fill)
                c.drawRect(w - bar, b.y - 190f, w, b.y + 190f, fill)
                stroke.color = if (blink) GRAY else edge
                stroke.strokeWidth = 5f
                c.drawRect(0f, b.y - 190f, bar, b.y + 190f, stroke)
                c.drawRect(w - bar, b.y - 190f, w, b.y + 190f, stroke)
            }
            BOX_SPIN -> {
                c.save()
                c.translate(b.x, b.y)
                c.rotate(Math.toDegrees(b.rot.toDouble()).toFloat())
                stroke.color = edge
                stroke.strokeWidth = 6f
                c.drawRect(-b.r, -b.r, b.r, b.r, stroke)
                c.restore()
                fill.color = edge
                c.drawCircle(b.x, b.y, 7f, fill)
            }
            else -> {
                fill.color = body
                c.drawRect(b.x - b.w / 2, b.y - b.h / 2, b.x + b.w / 2, b.y + b.h / 2, fill)
                stroke.color = edge
                stroke.strokeWidth = 5f
                c.drawRect(b.x - b.w / 2, b.y - b.h / 2, b.x + b.w / 2, b.y + b.h / 2, stroke)
                if (b.maxHp > 0) {
                    fill.color = edge
                    val frac = (b.hp / b.maxHp).coerceIn(0f, 1f)
                    c.drawRect(b.x - b.w / 2 + 8f, b.y + b.h / 2 - 12f, b.x - b.w / 2 + 8f + (b.w - 16f) * frac, b.y + b.h / 2 - 5f, fill)
                }
            }
        }
    }

    // ---------------- bosses (top-anchored) ----------------
                    fun drawBoss(c: Canvas, id: String, x: Float, y: Float, w: Float, h: Float, t: Long, vuln: Boolean, flash: Boolean) {
        val col = if (flash) BLACK else WHITE
        val inv = if (flash) WHITE else BLACK
        val bg = if (flash) WHITE else DIM
        val bIdx = (id.replace("boss", "").toIntOrNull() ?: (id.hashCode() and 0x7FFFFFFF)) % 50
        val bw = w * 0.68f
        val bh = h * 0.52f

        when (bIdx) {
            0 -> { // COBRA WYRM: Coiled snake with flared hood, fangs, and segmented tail
                // Hood
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.35).toFloat())
                path.lineTo((x - bw * 0.45).toFloat(), (y - bw * 0.1).toFloat())
                path.lineTo((x - bw * 0.2).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x + bw * 0.2).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x + bw * 0.45).toFloat(), (y - bw * 0.1).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
                // Head & Fangs
                c.drawRect((x-bw*0.12).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.12).toFloat(), (y-bw*0.15).toFloat(), fill.apply { color = col })
                path.reset()
                path.moveTo((x - bw * 0.1).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x - bw * 0.04).toFloat(), (y + bw * 0.05).toFloat())
                path.lineTo((x).toFloat(), (y - bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                path.reset()
                path.moveTo((x + bw * 0.1).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.04).toFloat(), (y + bw * 0.05).toFloat())
                path.lineTo((x).toFloat(), (y - bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                // Eyes
                c.drawOval((x-bw*0.08).toFloat(), (y-bw*0.32).toFloat(), (x-bw*0.02).toFloat(), (y-bw*0.24).toFloat(), fill.apply { color = inv })
                c.drawOval((x+bw*0.02).toFloat(), (y-bw*0.32).toFloat(), (x+bw*0.08).toFloat(), (y-bw*0.24).toFloat(), fill.apply { color = inv })
                // Tail coils
                for (i in 0 until 4) {
                val ty = (y + bw*0.2 + i * bw*0.1).toFloat()
                val tx = (x + sin(i.toDouble()*1.5)*bw*0.22).toFloat()
                c.drawOval((tx-bw*0.08).toFloat(), (ty-bw*0.08).toFloat(), (tx+bw*0.08).toFloat(), (ty+bw*0.08).toFloat(), fill.apply { color = col })
            }
            }
            1 -> { // CYCLOPS SPIDER: Round hairy thorax, glowing central eye, 8 jointed legs
                c.drawOval((x-bw*0.25).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.25).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.25).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.25).toFloat(), stroke)
                c.drawOval((x-bw*0.12).toFloat(), (y-bw*0.12).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.12).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.04).toFloat(), (y-bw*0.04).toFloat(), (x+bw*0.04).toFloat(), (y+bw*0.04).toFloat(), fill.apply { color = inv })
                for (l in 0 until 4) {
                for (side in floatArrayOf(-1f, 1f)) {
                val x1 = (x + side * bw*0.22).toFloat()
                val y1 = (y - bw*0.18 + l * bw*0.12).toFloat()
                val x2 = (x + side * bw*0.42).toFloat()
                val y2 = (y1 - bw*0.15).toFloat()
                val x3 = (x + side * bw*0.48).toFloat()
                val y3 = (y1 + bw*0.2).toFloat()
                path.reset()
                path.moveTo((x1).toFloat(), (y1).toFloat())
                path.lineTo((x2).toFloat(), (y2).toFloat())
                path.lineTo((x3).toFloat(), (y3).toFloat())
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
            }
            }
            }
            2 -> { // EMPEROR SCORPION: Massive pincers, plated carapace, overhead venom stinger
                // Carapace
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.3).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.3).toFloat(), stroke)
                // Curved tail arching up
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawArc((x-bw*0.25).toFloat(), (y-bw*0.48).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.1).toFloat(), (160).toFloat(), ((380).toFloat() - (160).toFloat()), false, stroke)
                // Stinger bulb
                path.reset()
                path.moveTo((x + bw * 0.15).toFloat(), (y - bw * 0.28).toFloat())
                path.lineTo((x + bw * 0.28).toFloat(), (y - bw * 0.42).toFloat())
                path.lineTo((x + bw * 0.05).toFloat(), (y - bw * 0.4).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                // Dual front claws
                for (side in floatArrayOf(-1f, 1f)) {
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x + side * bw * 0.15).toFloat(), (y).toFloat(), (x + side * bw * 0.35).toFloat(), (y - bw * 0.15).toFloat(), stroke)
                path.reset()
                path.moveTo((x + side * bw * 0.35).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + side * bw * 0.46).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + side * bw * 0.25).toFloat(), (y - bw * 0.32).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            }
            3 -> { // TITAN CENTIPEDE: 7 overlapping body rings with crawling side spurs
                for (i in 0 until 7) {
                val segY = (y - bw*0.4 + i * bw*0.13).toFloat()
                c.drawOval((x-bw*0.22).toFloat(), (y-bw*0.08).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.08).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.22).toFloat(), (y-bw*0.08).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.08).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x - bw * 0.22).toFloat(), (y).toFloat(), (x - bw * 0.4).toFloat(), (y - bw * 0.06).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x + bw * 0.22).toFloat(), (y).toFloat(), (x + bw * 0.4).toFloat(), (y - bw * 0.06).toFloat(), stroke)
                // Head mandibles
                path.reset()
                path.moveTo((x - bw * 0.12).toFloat(), (y - bw * 0.42).toFloat())
                path.lineTo((x - bw * 0.2).toFloat(), (y - bw * 0.52).toFloat())
                path.lineTo((x - bw * 0.04).toFloat(), (y - bw * 0.48).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                path.reset()
                path.moveTo((x + bw * 0.12).toFloat(), (y - bw * 0.42).toFloat())
                path.lineTo((x + bw * 0.2).toFloat(), (y - bw * 0.52).toFloat())
                path.lineTo((x + bw * 0.04).toFloat(), (y - bw * 0.48).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            }
            4 -> { // GIANT CRAB: Broad armored shell, twin eye stalks, serrated crushing claws
                c.drawOval((x-bw*0.35).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.25).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.35).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.25).toFloat(), stroke)
                // Eye stalks
                for (side in floatArrayOf(-1f, 1f)) {
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x + side * bw * 0.1).toFloat(), (y - bw * 0.18).toFloat(), (x + side * bw * 0.14).toFloat(), (y - bw * 0.35).toFloat(), stroke)
                c.drawOval((x+side*bw*0.14-4).toFloat(), (y-bw*0.35-4).toFloat(), (x+side*bw*0.14+4).toFloat(), (y-bw*0.35+4).toFloat(), fill.apply { color = col })
                // Claws
                for (side in floatArrayOf(-1f, 1f)) {
                c.drawRect((x+side*bw*0.38-bw*0.08).toFloat(), (y-bw*0.25).toFloat(), (x+side*bw*0.38+bw*0.08).toFloat(), (y+bw*0.05).toFloat(), fill.apply { color = col })
                path.reset()
                path.moveTo((x + side * bw * 0.38).toFloat(), (y - bw * 0.25).toFloat())
                path.lineTo((x + side * bw * 0.48).toFloat(), (y - bw * 0.42).toFloat())
                path.lineTo((x + side * bw * 0.3).toFloat(), (y - bw * 0.35).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            }
            }
            5 -> { // ABYSSAL KRAKEN: Central bulbous mantle with 6 undulating tentacles
                c.drawOval((x-bw*0.28).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.28).toFloat(), (y).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.28).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.28).toFloat(), (y).toFloat(), stroke)
                c.drawOval((x-bw*0.1).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.1).toFloat(), (y-bw*0.1).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.04).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.04).toFloat(), (y-bw*0.15).toFloat(), fill.apply { color = inv })
                // Tentacles hanging below
                for (i in 0 until 6) {
                val tx = (x - bw*0.22 + i * bw*0.09).toFloat()
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawArc((tx-bw*0.08).toFloat(), (y).toFloat(), (tx+bw*0.08).toFloat(), (y+bw*0.45).toFloat(), (0).toFloat(), ((180).toFloat() - (0).toFloat()), false, stroke)
            }
            }
            6 -> { // DRAGON FLY / MANTIS: Triangular head, razor forearms, double cross wings
                // Head & Body
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.38).toFloat())
                path.lineTo((x - bw * 0.12).toFloat(), (y - bw * 0.18).toFloat())
                path.lineTo((x + bw * 0.12).toFloat(), (y - bw * 0.18).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawRect((x-bw*0.06).toFloat(), (y-bw*0.18).toFloat(), (x+bw*0.06).toFloat(), (y+bw*0.4).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.06).toFloat(), (y-bw*0.18).toFloat(), (x+bw*0.06).toFloat(), (y+bw*0.4).toFloat(), stroke)
                // Forearms (scythes)
                for (side in floatArrayOf(-1f, 1f)) {
                path.reset()
                path.moveTo((x + side * bw * 0.1).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + side * bw * 0.35).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + side * bw * 0.22).toFloat(), (y).toFloat())
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                // Wings
                for (side in floatArrayOf(-1f, 1f)) {
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.1).toFloat())
                path.lineTo((x + side * bw * 0.46).toFloat(), (y - bw * 0.22).toFloat())
                path.lineTo((x + side * bw * 0.4).toFloat(), (y - bw * 0.05).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.05).toFloat())
                path.lineTo((x + side * bw * 0.42).toFloat(), (y + bw * 0.02).toFloat())
                path.lineTo((x + side * bw * 0.36).toFloat(), (y + bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            }
            }
            7 -> { // BIO-HYDRA: 3 distinct dragon/serpent heads emerging from one trunk
                // Base trunk
                c.drawRect((x-bw*0.2f), (y+bw*0.15f), (x+bw*0.2f), (y+bw*0.42f), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.2f), (y+bw*0.15f), (x+bw*0.2f), (y+bw*0.42f), stroke)
                // 3 necks & heads
                val angles = floatArrayOf(-0.5f, 0f, 0.5f)
                for (hIdx in 0 until 3) {
                    val ang = angles[hIdx]
                    val hx = (x + sin(ang.toDouble()) * bw * 0.32f).toFloat()
                    val hy = (y - bw * 0.15f - (if (hIdx == 1) bw * 0.1f else 0f))
                    stroke.color = col; stroke.strokeWidth = 3f
                    c.drawLine(x, y + bw * 0.2f, hx, hy, stroke)
                    path.reset()
                    path.moveTo(hx, hy - bw * 0.15f)
                    path.lineTo(hx - bw * 0.1f, hy + bw * 0.05f)
                    path.lineTo(hx + bw * 0.1f, hy + bw * 0.05f)
                    path.close()
                    c.drawPath(path, fill.apply { color = col })
                    c.drawOval(hx - 3f, hy - bw * 0.05f - 3f, hx + 3f, hy - bw * 0.05f + 3f, fill.apply { color = inv })
                }
            }
            8 -> { // MECHA-BEETLE (Scarab): Heavy domed shell, segmented pincer horns, shell lines
                c.drawOval((x-bw*0.3).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.3).toFloat(), (y+bw*0.35).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.3).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.3).toFloat(), (y+bw*0.35).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x).toFloat(), (y - bw * 0.15).toFloat(), (x).toFloat(), (y + bw * 0.35).toFloat(), stroke)
                // Heavy front horn
                c.drawRect((x-bw*0.08).toFloat(), (y-bw*0.35).toFloat(), (x+bw*0.08).toFloat(), (y-bw*0.15).toFloat(), fill.apply { color = col })
                path.reset()
                path.moveTo((x - bw * 0.15).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x - bw * 0.05).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + bw * 0.05).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + bw * 0.15).toFloat(), (y - bw * 0.45).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            9 -> { // JELLYFISH LEVIATHAN: Glowing bell dome with trailing shock tentacle tendrils
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawArc((x - bw * 0.35f), (y - bw * 0.4f), (x + bw * 0.35f), (y + bw * 0.1f), 180f, 180f, true, fill.apply { color = bg })
                c.drawArc((x - bw * 0.35f), (y - bw * 0.4f), (x + bw * 0.35f), (y + bw * 0.1f), 180f, 180f, false, stroke)
                c.drawOval((x - bw * 0.15f), (y - bw * 0.3f), (x + bw * 0.15f), (y - bw * 0.1f), fill.apply { color = col })
                // Tendrils
                for (i in 0 until 5) {
                    val tx = (x - bw * 0.25f + i * bw * 0.125f)
                    stroke.color = col; stroke.strokeWidth = 2f
                    c.drawLine(tx, y - bw * 0.15f, (tx + sin(i.toDouble()) * 10f).toFloat(), y + bw * 0.4f, stroke)
            }
            }
            10 -> { // SHADOW BAT: Giant horned ears, spread jagged wings, glowing fangs
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.25).toFloat())
                path.lineTo((x - bw * 0.48).toFloat(), (y - bw * 0.3).toFloat())
                path.lineTo((x - bw * 0.3).toFloat(), (y - bw * 0.05).toFloat())
                path.lineTo((x - bw * 0.15).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.15).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + bw * 0.3).toFloat(), (y - bw * 0.05).toFloat())
                path.lineTo((x + bw * 0.48).toFloat(), (y - bw * 0.3).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
                // Ears
                path.reset()
                path.moveTo((x - bw * 0.12).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x - bw * 0.08).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x - bw * 0.02).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                path.reset()
                path.moveTo((x + bw * 0.12).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x + bw * 0.08).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x + bw * 0.02).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawOval((x-4).toFloat(), (y-bw*0.05-4).toFloat(), (x+4).toFloat(), (y-bw*0.05+4).toFloat(), fill.apply { color = col })
            }
            11 -> { // OROBOROS (Ring Snake swallowing tail): Circular segmented serpent
                val r = (bw * 0.35).toFloat()
                stroke.color = col; stroke.strokeWidth = 8f
                c.drawOval((x-r).toFloat(), (y-r).toFloat(), (x+r).toFloat(), (y+r).toFloat(), stroke)
                for (i in 0 until 8) {
                val a = (i * PI / 4).toFloat()
                c.drawOval((x+cos(a)*r-6).toFloat(), (y+sin(a)*r-6).toFloat(), (x+cos(a)*r+6).toFloat(), (y+sin(a)*r+6).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x+cos(a)*r-6).toFloat(), (y+sin(a)*r-6).toFloat(), (x+cos(a)*r+6).toFloat(), (y+sin(a)*r+6).toFloat(), stroke)
                // Dragon head biting tail
                path.reset()
                path.moveTo((x + r).toFloat(), (y - bw * 0.1).toFloat())
                path.lineTo((x + r + bw * 0.15).toFloat(), (y).toFloat())
                path.lineTo((x + r - bw * 0.05).toFloat(), (y + bw * 0.12).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            }
            12 -> { // GARGOYLE GOLEM: Broad shoulder blocks, horned crest, heavy center chest eye
                c.drawRect((x-bw*0.35).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.25).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.35).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.25).toFloat(), stroke)
                // Horns
                path.reset()
                path.moveTo((x - bw * 0.25).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x - bw * 0.35).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x - bw * 0.15).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                path.reset()
                path.moveTo((x + bw * 0.25).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x + bw * 0.35).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x + bw * 0.15).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                // Core
                c.drawOval((x-bw*0.12).toFloat(), (y-bw*0.05).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.19).toFloat(), fill.apply { color = col })
                c.drawRect((x-3).toFloat(), (y+bw*0.02).toFloat(), (x+3).toFloat(), (y+bw*0.12).toFloat(), fill.apply { color = inv })
            }
            13 -> { // ANGLER LEVIATHAN: Huge gaping jaw, luminous dorsal lure hanging over mouth
                c.drawOval((x-bw*0.32).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.32).toFloat(), (y+bw*0.3).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.32).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.32).toFloat(), (y+bw*0.3).toFloat(), stroke)
                // Gaping mouth
                path.reset()
                path.moveTo((x - bw * 0.2).toFloat(), (y + bw * 0.05).toFloat())
                path.lineTo((x + bw * 0.2).toFloat(), (y + bw * 0.05).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.28).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = inv })
                // Teeth
                for (i in floatArrayOf(-bw*0.12f, -bw*0.04f, bw*0.04f, bw*0.12f)) {
                path.reset()
                path.moveTo((x + i - 2).toFloat(), (y + bw * 0.05).toFloat())
                path.lineTo((x + i).toFloat(), (y + bw * 0.12).toFloat())
                path.lineTo((x + i + 2).toFloat(), (y + bw * 0.05).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                // Lure stalk
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawArc((x-bw*0.15).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.15).toFloat(), (y-bw*0.1).toFloat(), (180).toFloat(), ((360).toFloat() - (180).toFloat()), false, stroke)
                c.drawOval((x+bw*0.15-6).toFloat(), (y-bw*0.2-6).toFloat(), (x+bw*0.15+6).toFloat(), (y-bw*0.2+6).toFloat(), fill.apply { color = col })
            }
            }
            14 -> { // CENTAUR BEAST: Forearm blade armor with quadruped lower mechanical body
                c.drawRect((x-bw*0.15).toFloat(), (y-bw*0.38).toFloat(), (x+bw*0.15).toFloat(), (y).toFloat(), fill.apply { color = col })
                c.drawRect((x-bw*0.35).toFloat(), (y).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.25).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.35).toFloat(), (y).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.25).toFloat(), stroke)
                // Legs
                for (side in floatArrayOf(-1f, 1f)) {
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x + side * bw * 0.25).toFloat(), (y + bw * 0.25).toFloat(), (x + side * bw * 0.35).toFloat(), (y + bw * 0.45).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x + side * bw * 0.1).toFloat(), (y + bw * 0.25).toFloat(), (x + side * bw * 0.15).toFloat(), (y + bw * 0.45).toFloat(), stroke)
            }
            }
            15 -> { // CHIMERA WYRM: Dual serpent bodies joined at a central armored skull
                c.drawOval((x-bw*0.18).toFloat(), (y-bw*0.18).toFloat(), (x+bw*0.18).toFloat(), (y+bw*0.18).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.06).toFloat(), (y-bw*0.06).toFloat(), (x+bw*0.06).toFloat(), (y+bw*0.06).toFloat(), fill.apply { color = inv })
                for (side in floatArrayOf(-1f, 1f)) {
                for (i in 0 until 4) {
                val sx = (x + side * (bw*0.15 + i * bw*0.09)).toFloat()
                val sy = (y + sin(i.toDouble()*1.2)*bw*0.18).toFloat()
                c.drawOval((sx-7).toFloat(), (sy-7).toFloat(), (sx+7).toFloat(), (sy+7).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((sx-7).toFloat(), (sy-7).toFloat(), (sx+7).toFloat(), (sy+7).toFloat(), stroke)
            }
            }
            }
            16 -> { // ARMORED WASP: Narrow waist, heavy stinger needle, angled insectoid wings
                c.drawOval((x-bw*0.15).toFloat(), (y-bw*0.35).toFloat(), (x+bw*0.15).toFloat(), (y-bw*0.1).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.15).toFloat(), (y-bw*0.35).toFloat(), (x+bw*0.15).toFloat(), (y-bw*0.1).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawLine((x).toFloat(), (y - bw * 0.1).toFloat(), (x).toFloat(), (y + bw * 0.05).toFloat(), stroke)
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.42).toFloat())
                path.lineTo((x - bw * 0.18).toFloat(), (y + bw * 0.05).toFloat())
                path.lineTo((x + bw * 0.18).toFloat(), (y + bw * 0.05).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                for (side in floatArrayOf(-1f, 1f)) {
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.12).toFloat())
                path.lineTo((x + side * bw * 0.44).toFloat(), (y - bw * 0.3).toFloat())
                path.lineTo((x + side * bw * 0.35).toFloat(), (y - bw * 0.05).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
            }
            }
            17 -> { // CYBER GHOUL: Skull-shaped mask with glowing eye sockets and ribcage frame
                c.drawOval((x-bw*0.25).toFloat(), (y-bw*0.35).toFloat(), (x+bw*0.25).toFloat(), (y).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.14).toFloat(), (y-bw*0.22).toFloat(), (x-bw*0.04).toFloat(), (y-bw*0.12).toFloat(), fill.apply { color = inv })
                c.drawOval((x+bw*0.04).toFloat(), (y-bw*0.22).toFloat(), (x+bw*0.14).toFloat(), (y-bw*0.12).toFloat(), fill.apply { color = inv })
                // Ribcage
                for (i in 0 until 3) {
                val ry = (y + bw*0.08 + i * bw*0.1).toFloat()
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x - bw * 0.22).toFloat(), (ry).toFloat(), (x + bw * 0.22).toFloat(), (ry).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x).toFloat(), (y).toFloat(), (x).toFloat(), (y + bw * 0.38).toFloat(), stroke)
            }
            }
            18 -> { // TRICERATOPS RAM: Heavy triangular frill shield with 3 massive forward horns
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x - bw * 0.4).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x + bw * 0.4).toFloat(), (y - bw * 0.4).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawRect((x-bw*0.18).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.18).toFloat(), (y+bw*0.25).toFloat(), fill.apply { color = col })
                // 3 horns pointing down
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.25).toFloat())
                path.lineTo((x - bw * 0.05).toFloat(), (y + bw * 0.45).toFloat())
                path.lineTo((x + bw * 0.05).toFloat(), (y + bw * 0.45).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                path.reset()
                path.moveTo((x - bw * 0.28).toFloat(), (y - bw * 0.3).toFloat())
                path.lineTo((x - bw * 0.38).toFloat(), (y).toFloat())
                path.lineTo((x - bw * 0.2).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                path.reset()
                path.moveTo((x + bw * 0.28).toFloat(), (y - bw * 0.3).toFloat())
                path.lineTo((x + bw * 0.38).toFloat(), (y).toFloat())
                path.lineTo((x + bw * 0.2).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            19 -> { // VOID BASILISK: Multi-eyed lizard head with crown crest and coiled serpent tail
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.15).toFloat())
                path.lineTo((x - bw * 0.25).toFloat(), (y - bw * 0.25).toFloat())
                path.lineTo((x + bw * 0.25).toFloat(), (y - bw * 0.25).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                // Crown spikes
                for (i in floatArrayOf(-0.2f, 0f, 0.2f)) {
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x + i * bw).toFloat(), (y - bw * 0.25).toFloat(), (x + i * bw * 1.3).toFloat(), (y - bw * 0.45).toFloat(), stroke)
                // Snake tail
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawArc((x-bw*0.25).toFloat(), (y+bw*0.1).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.45).toFloat(), (0).toFloat(), ((180).toFloat() - (0).toFloat()), false, stroke)
                c.drawOval((x-4).toFloat(), (y-4).toFloat(), (x+4).toFloat(), (y+4).toFloat(), fill.apply { color = col })
            }
            }
            20 -> { // EYE CLUSTER OVERMIND: 1 primary eye surrounded by 6 orbiting mini-eyes
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.2).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.08).toFloat(), (y-bw*0.08).toFloat(), (x+bw*0.08).toFloat(), (y+bw*0.08).toFloat(), fill.apply { color = inv })
                for (i in 0 until 6) {
                val a = (i * PI / 3).toFloat()
                val ox = (x + cos(a)*bw*0.35).toFloat()
                val oy = (y + sin(a)*bw*0.35).toFloat()
                c.drawOval((ox-8).toFloat(), (oy-8).toFloat(), (ox+8).toFloat(), (oy+8).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((ox-8).toFloat(), (oy-8).toFloat(), (ox+8).toFloat(), (oy+8).toFloat(), stroke)
                c.drawOval((ox-2).toFloat(), (oy-2).toFloat(), (ox+2).toFloat(), (oy+2).toFloat(), fill.apply { color = col })
            }
            }
            21 -> { // GARGANTUAN BEHEMOTH: Mammoth armor shoulders with hanging tusk spikes
                c.drawRect((x-bw*0.45).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.45).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.45).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.45).toFloat(), (y+bw*0.1).toFloat(), stroke)
                // Center visor slit
                c.drawRect((x-bw*0.25).toFloat(), (y-bw*0.05).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.05).toFloat(), fill.apply { color = col })
                // Massive downward curved tusks
                path.reset()
                path.moveTo((x - bw * 0.35).toFloat(), (y + bw * 0.1).toFloat())
                path.lineTo((x - bw * 0.45).toFloat(), (y + bw * 0.45).toFloat())
                path.lineTo((x - bw * 0.25).toFloat(), (y + bw * 0.25).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                path.reset()
                path.moveTo((x + bw * 0.35).toFloat(), (y + bw * 0.1).toFloat())
                path.lineTo((x + bw * 0.45).toFloat(), (y + bw * 0.45).toFloat())
                path.lineTo((x + bw * 0.25).toFloat(), (y + bw * 0.25).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            22 -> { // BIO-NAUTILUS: Spiral shell with trailing tentacles
                c.drawOval((x-bw*0.35).toFloat(), (y-bw*0.38).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.15).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.35).toFloat(), (y-bw*0.38).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.15).toFloat(), stroke)
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.05).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.08).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.08).toFloat(), (y).toFloat(), fill.apply { color = inv })
                for (i in 0 until 5) {
                val tx = (x - bw*0.2 + i * bw*0.1).toFloat()
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((tx).toFloat(), (y + bw * 0.1).toFloat(), (tx + sin(i.toDouble()) * 8).toFloat(), (y + bw * 0.45).toFloat(), stroke)
            }
            }
            23 -> { // PHOENIX (Firebird): Upward flame wings with long feathered tail streamers
                // Head & beak
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x - bw * 0.08).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x + bw * 0.08).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                // Wings
                for (side in floatArrayOf(-1f, 1f)) {
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x + side * bw * 0.45).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + side * bw * 0.35).toFloat(), (y - bw * 0.05).toFloat())
                path.lineTo((x + side * bw * 0.48).toFloat(), (y + bw * 0.15).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.1).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
                // Tail streamers
                for (side in floatArrayOf(-0.08f, 0f, 0.08f)) {
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x + side * bw).toFloat(), (y + bw * 0.1).toFloat(), (x + side * bw * 2).toFloat(), (y + bw * 0.48).toFloat(), stroke)
            }
            }
            }
            24 -> { // CYBER-LEECH: Tube mouth with concentric teeth rings and pulsing bulb tail
                c.drawOval((x-bw*0.25).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.25).toFloat(), (y-bw*0.15).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.14).toFloat(), (y-bw*0.36).toFloat(), (x+bw*0.14).toFloat(), (y-bw*0.21).toFloat(), fill.apply { color = inv })
                c.drawOval((x-bw*0.05).toFloat(), (y-bw*0.31).toFloat(), (x+bw*0.05).toFloat(), (y-bw*0.26).toFloat(), fill.apply { color = col })
                // Body sacks
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.18).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.15).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.18).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.15).toFloat(), stroke)
                c.drawOval((x-bw*0.15).toFloat(), (y+bw*0.12).toFloat(), (x+bw*0.15).toFloat(), (y+bw*0.42).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.15).toFloat(), (y+bw*0.12).toFloat(), (x+bw*0.15).toFloat(), (y+bw*0.42).toFloat(), stroke)
            }
            25 -> { // ANUBIS MECH: Jackal head with upright pointed ears and glowing collar
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.1).toFloat())
                path.lineTo((x - bw * 0.15).toFloat(), (y - bw * 0.18).toFloat())
                path.lineTo((x + bw * 0.15).toFloat(), (y - bw * 0.18).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                // Tall ears
                path.reset()
                path.moveTo((x - bw * 0.15).toFloat(), (y - bw * 0.18).toFloat())
                path.lineTo((x - bw * 0.18).toFloat(), (y - bw * 0.48).toFloat())
                path.lineTo((x - bw * 0.06).toFloat(), (y - bw * 0.18).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                path.reset()
                path.moveTo((x + bw * 0.15).toFloat(), (y - bw * 0.18).toFloat())
                path.lineTo((x + bw * 0.18).toFloat(), (y - bw * 0.48).toFloat())
                path.lineTo((x + bw * 0.06).toFloat(), (y - bw * 0.18).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                // Collar mantle
                path.reset()
                path.moveTo((x - bw * 0.35).toFloat(), (y + bw * 0.1).toFloat())
                path.lineTo((x + bw * 0.35).toFloat(), (y + bw * 0.1).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.38).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
            }
            26 -> { // ARMORED RAY: Diamond ray wings with long whip stinger tail
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + bw * 0.46).toFloat(), (y).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.25).toFloat())
                path.lineTo((x - bw * 0.46).toFloat(), (y).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x).toFloat(), (y + bw * 0.25).toFloat(), (x).toFloat(), (y + bw * 0.48).toFloat(), stroke)
                c.drawOval((x-6).toFloat(), (y-bw*0.1-6).toFloat(), (x+6).toFloat(), (y-bw*0.1+6).toFloat(), fill.apply { color = col })
            }
            27 -> { // GORGON (Medusa): Face core with 6 slithering snake hair tendrils
                c.drawOval((x-bw*0.18).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.18).toFloat(), (y+bw*0.22).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.08).toFloat(), (y).toFloat(), (x-bw*0.02).toFloat(), (y+bw*0.06).toFloat(), fill.apply { color = inv })
                c.drawOval((x+bw*0.02).toFloat(), (y).toFloat(), (x+bw*0.08).toFloat(), (y+bw*0.06).toFloat(), fill.apply { color = inv })
                // Snake hair
                for (a in 0 until 6) {
                val ang = (a * PI / 5 - PI).toFloat()
                val x1 = (x + cos(ang) * bw*0.18).toFloat()
                val y1 = (y - bw*0.15 + sin(ang) * bw*0.15).toFloat()
                val x2 = (x + cos(ang) * bw*0.42).toFloat()
                val y2 = (y - bw*0.25 + sin(ang) * bw*0.25).toFloat()
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x1).toFloat(), (y1).toFloat(), (x2).toFloat(), (y2).toFloat(), stroke)
                c.drawOval((x2-4).toFloat(), (y2-4).toFloat(), (x2+4).toFloat(), (y2+4).toFloat(), fill.apply { color = col })
            }
            }
            28 -> { // CARRIER BEETLE: Heavy split wing covers exposing glowing pulsating engine
                // Exposed inner core
                c.drawOval((x-bw*0.15).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.15).toFloat(), (y+bw*0.2).toFloat(), fill.apply { color = col })
                // Left and right wing shells open
                path.reset()
                path.moveTo((x - bw * 0.05).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x - bw * 0.42).toFloat(), (y - bw * 0.05).toFloat())
                path.lineTo((x - bw * 0.35).toFloat(), (y + bw * 0.35).toFloat())
                path.lineTo((x - bw * 0.05).toFloat(), (y + bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
                path.reset()
                path.moveTo((x + bw * 0.05).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x + bw * 0.42).toFloat(), (y - bw * 0.05).toFloat())
                path.lineTo((x + bw * 0.35).toFloat(), (y + bw * 0.35).toFloat())
                path.lineTo((x + bw * 0.05).toFloat(), (y + bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
            }
            29 -> { // TITAN CRUSTACEAN: Multi-segmented carapace with front crusher claws
                for (i in 0 until 4) {
                val w = (bw * (0.35 - i * 0.05)).toFloat()
                val segY = (y - bw*0.15 + i * bw*0.12).toFloat()
                c.drawRect((x-w).toFloat(), (y).toFloat(), (x+w).toFloat(), (y+bw*0.09).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-w).toFloat(), (y).toFloat(), (x+w).toFloat(), (y+bw*0.09).toFloat(), stroke)
                // Claws
                for (side in floatArrayOf(-1f, 1f)) {
                path.reset()
                path.moveTo((x + side * bw * 0.2).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + side * bw * 0.42).toFloat(), (y - bw * 0.38).toFloat())
                path.lineTo((x + side * bw * 0.3).toFloat(), (y - bw * 0.42).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            }
            }
            30 -> { // MECHANICAL FLY: Giant faceted compound eyes with high-speed vibrating wings
                c.drawOval((x-bw*0.12).toFloat(), (y-bw*0.05).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.3).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.12).toFloat(), (y-bw*0.05).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.3).toFloat(), stroke)
                // Giant twin compound eyes
                c.drawOval((x-bw*0.28).toFloat(), (y-bw*0.3).toFloat(), (x-bw*0.04).toFloat(), (y-bw*0.06).toFloat(), fill.apply { color = col })
                c.drawOval((x+bw*0.04).toFloat(), (y-bw*0.3).toFloat(), (x+bw*0.28).toFloat(), (y-bw*0.06).toFloat(), fill.apply { color = col })
                for (side in floatArrayOf(-1f, 1f)) {
                c.drawOval((x+side*bw*0.28-bw*0.15).toFloat(), (y-bw*0.1).toFloat(), (x+side*bw*0.28+bw*0.15).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x+side*bw*0.28-bw*0.15).toFloat(), (y-bw*0.1).toFloat(), (x+side*bw*0.28+bw*0.15).toFloat(), (y+bw*0.1).toFloat(), stroke)
            }
            }
            31 -> { // DEVOURER MOUTH: Giant circular toothy vortex with outer armor ring
                c.drawOval((x-bw*0.38).toFloat(), (y-bw*0.38).toFloat(), (x+bw*0.38).toFloat(), (y+bw*0.38).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawOval((x-bw*0.38).toFloat(), (y-bw*0.38).toFloat(), (x+bw*0.38).toFloat(), (y+bw*0.38).toFloat(), stroke)
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.2).toFloat(), fill.apply { color = inv })
                for (i in 0 until 8) {
                val a = (i * PI / 4).toFloat()
                val tx = (x + cos(a)*bw*0.2).toFloat()
                val ty = (y + sin(a)*bw*0.2).toFloat()
                path.reset()
                path.moveTo((tx).toFloat(), (ty).toFloat())
                path.lineTo((tx + cos(a + 0.3) * 12).toFloat(), (ty + sin(a + 0.3) * 12).toFloat())
                path.lineTo((x).toFloat(), (y).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            }
            32 -> { // GHOST WYRM: Skeletal floating serpent ribs with glowing skull
                // Skull
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x - bw * 0.15).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x + bw * 0.15).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawOval((x-4).toFloat(), (y-bw*0.28-4).toFloat(), (x+4).toFloat(), (y-bw*0.28+4).toFloat(), fill.apply { color = inv })
                // Rib cages
                for (i in 0 until 5) {
                val segY = (y - bw*0.1 + i * bw*0.1).toFloat()
                val w = (bw * (0.35 - i * 0.04)).toFloat()
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawArc((x-w).toFloat(), (y-bw*0.05).toFloat(), (x+w).toFloat(), (y+bw*0.08).toFloat(), (0).toFloat(), ((180).toFloat() - (0).toFloat()), false, stroke)
            }
            }
            33 -> { // DRAGON CHIEF: Horned draconian skull, wide scaled crest, fire organ
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.25).toFloat())
                path.lineTo((x - bw * 0.2).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.2).toFloat(), (y - bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                // Long swept backward horns
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawLine((x - bw * 0.15).toFloat(), (y - bw * 0.15).toFloat(), (x - bw * 0.4).toFloat(), (y - bw * 0.45).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawLine((x + bw * 0.15).toFloat(), (y - bw * 0.15).toFloat(), (x + bw * 0.4).toFloat(), (y - bw * 0.45).toFloat(), stroke)
                // Glowing mouth
                c.drawOval((x-bw*0.08).toFloat(), (y+bw*0.05).toFloat(), (x+bw*0.08).toFloat(), (y+bw*0.2).toFloat(), fill.apply { color = col })
            }
            34 -> { // TRIPLE CANNON CRAB: Broad carapace with 3 massive forward railgun barrels
                c.drawRect((x-bw*0.4).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.4).toFloat(), (y+bw*0.25).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.4).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.4).toFloat(), (y+bw*0.25).toFloat(), stroke)
                // 3 cannon barrels pointing down
                for (k in floatArrayOf(-bw*0.25f, 0f, bw*0.25f)) {
                c.drawRect((x+k-bw*0.05).toFloat(), (y+bw*0.25).toFloat(), (x+k+bw*0.05).toFloat(), (y+bw*0.45).toFloat(), fill.apply { color = col })
                // Eyes
                c.drawOval((x-bw*0.15).toFloat(), (y).toFloat(), (x-bw*0.05).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = col })
                c.drawOval((x+bw*0.05).toFloat(), (y).toFloat(), (x+bw*0.15).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = col })
            }
            }
            35 -> { // SEA SERPENT: Undulating S-curve snake with dorsal fin spines
                var lastX = x
                var lastY = y - bw * 0.4f
                val headX = (x + sin(0.0) * bw * 0.3f).toFloat()
                val headY = (y - bw * 0.4f)
                c.drawOval(headX - 8f, headY - 8f, headX + 8f, headY + 8f, fill.apply { color = col })
                for (i in 0 until 12) {
                    val t = i / 11.0
                    val curX = (x + sin(t * PI * 2.0) * bw * 0.3f).toFloat()
                    val curY = (y - bw * 0.4f + t * bw * 0.85f).toFloat()
                    if (i > 0) {
                        stroke.color = col; stroke.strokeWidth = 6f
                        c.drawLine(lastX, lastY, curX, curY, stroke)
                    }
                    if (i % 2 == 0) {
                        stroke.color = col; stroke.strokeWidth = 2f
                        c.drawLine(curX, curY, curX + 15f, curY - 10f, stroke)
                    }
                    lastX = curX
                    lastY = curY
            }
            }
            36 -> { // QUEEN WASP: Giant egg sack abdomen with razor wing blades
                // Giant egg sack
                c.drawOval((x-bw*0.22).toFloat(), (y).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.45).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.22).toFloat(), (y).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.45).toFloat(), stroke)
                // Thorax & head
                c.drawOval((x-bw*0.15).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.15).toFloat(), (y).toFloat(), fill.apply { color = col })
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.42).toFloat())
                path.lineTo((x - bw * 0.1).toFloat(), (y - bw * 0.25).toFloat())
                path.lineTo((x + bw * 0.1).toFloat(), (y - bw * 0.25).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                for (side in floatArrayOf(-1f, 1f)) {
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x).toFloat(), (y - bw * 0.15).toFloat(), (x + side * bw * 0.45).toFloat(), (y - bw * 0.3).toFloat(), stroke)
            }
            }
            37 -> { // COCKATRICE: Avian razor beak, bat wings, long coiled serpent tail
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x - bw * 0.15).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.15).toFloat(), (y - bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawRect((x-bw*0.2).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.15).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.2).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.15).toFloat(), stroke)
                // Snake tail
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawArc((x-bw*0.2).toFloat(), (y+bw*0.1).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.45).toFloat(), (0).toFloat(), ((270).toFloat() - (0).toFloat()), false, stroke)
                for (side in floatArrayOf(-1f, 1f)) {
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.05).toFloat())
                path.lineTo((x + side * bw * 0.42).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x + side * bw * 0.35).toFloat(), (y + bw * 0.05).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            }
            38 -> { // SOLAR SCARAB: Sacred scarab pushing a giant glowing sun sphere
                // Sun sphere
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.2).toFloat(), (y-bw*0.05).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.1).toFloat(), (y-bw*0.35).toFloat(), (x+bw*0.1).toFloat(), (y-bw*0.15).toFloat(), fill.apply { color = inv })
                // Scarab body
                c.drawOval((x-bw*0.28).toFloat(), (y-bw*0.05).toFloat(), (x+bw*0.28).toFloat(), (y+bw*0.4).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.28).toFloat(), (y-bw*0.05).toFloat(), (x+bw*0.28).toFloat(), (y+bw*0.4).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x).toFloat(), (y - bw * 0.05).toFloat(), (x).toFloat(), (y + bw * 0.4).toFloat(), stroke)
            }
            39 -> { // MANTA LEVIATHAN: Curved sweeping delta fins with central glowing bioluminescent eye
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.3).toFloat())
                path.lineTo((x - bw * 0.48).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x - bw * 0.25).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.25).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + bw * 0.48).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawOval((x-bw*0.1).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.1).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = col })
                c.drawOval((x-3).toFloat(), (y-3).toFloat(), (x+3).toFloat(), (y+3).toFloat(), fill.apply { color = inv })
            }
            40 -> { // HYPER SPIDER (8-eyed web mistress): Huge arachnid with web net lines
                c.drawOval((x-bw*0.22).toFloat(), (y-bw*0.22).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.22).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.22).toFloat(), (y-bw*0.22).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.22).toFloat(), stroke)
                // 8 mini eyes
                for (i in 0 until 8) {
                val a = (i * PI / 4).toFloat()
                c.drawOval((x+cos(a)*bw*0.12-2).toFloat(), (y+sin(a)*bw*0.12-2).toFloat(), (x+cos(a)*bw*0.12+2).toFloat(), (y+sin(a)*bw*0.12+2).toFloat(), fill.apply { color = col })
                // Long spidery legs
                for (i in 0 until 4) {
                for (side in floatArrayOf(-1f, 1f)) {
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x + side * bw * 0.2).toFloat(), (y - bw * 0.15 + i * bw * 0.1).toFloat(), (x + side * bw * 0.46).toFloat(), (y - bw * 0.3 + i * bw * 0.2).toFloat(), stroke)
            }
            }
            }
            }
            41 -> { // VIPER DREAD: Twin coiled vipers facing opposite directions
                for (side in floatArrayOf(-1f, 1f)) {
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawArc((x+side*bw*0.15-bw*0.15).toFloat(), (y-bw*0.3).toFloat(), (x+side*bw*0.15+bw*0.15).toFloat(), (y+bw*0.3).toFloat(), (0).toFloat(), ((360).toFloat() - (0).toFloat()), false, stroke)
                path.reset()
                path.moveTo((x + side * bw * 0.15).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + side * bw * 0.25).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x + side * bw * 0.05).toFloat(), (y - bw * 0.45).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            }
            42 -> { // SHADOW GARGOYLE: Winged demon skull with horns and bat wings
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.2).toFloat())
                path.lineTo((x - bw * 0.18).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.18).toFloat(), (y - bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                // Horns
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawLine((x - bw * 0.15).toFloat(), (y - bw * 0.15).toFloat(), (x - bw * 0.35).toFloat(), (y - bw * 0.4).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawLine((x + bw * 0.15).toFloat(), (y - bw * 0.15).toFloat(), (x + bw * 0.4).toFloat(), (y - bw * 0.4).toFloat(), stroke)
                // Bat wings
                for (side in floatArrayOf(-1f, 1f)) {
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.05).toFloat())
                path.lineTo((x + side * bw * 0.48).toFloat(), (y - bw * 0.25).toFloat())
                path.lineTo((x + side * bw * 0.35).toFloat(), (y + bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
            }
            }
            43 -> { // ARMORED ANCHORITE: Walking naval fortress with massive claw braces
                c.drawRect((x-bw*0.35).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.2).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.35).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.2).toFloat(), stroke)
                c.drawOval((x-bw*0.14).toFloat(), (y-bw*0.14).toFloat(), (x+bw*0.14).toFloat(), (y+bw*0.14).toFloat(), fill.apply { color = col })
                // Side claw braces
                for (side in floatArrayOf(-1f, 1f)) {
                path.reset()
                path.moveTo((x + side * bw * 0.35).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x + side * bw * 0.48).toFloat(), (y).toFloat())
                path.lineTo((x + side * bw * 0.35).toFloat(), (y + bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            }
            44 -> { // OCTOPUS PRIMARCH: Domed mantle with 8 radial segmented tentacle arms
                c.drawOval((x-bw*0.25).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.25).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.25).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.25).toFloat(), stroke)
                c.drawOval((x-bw*0.1).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.1).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = col })
                for (i in 0 until 8) {
                val a = (i * PI / 4).toFloat()
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x + cos(a) * bw * 0.25).toFloat(), (y + sin(a) * bw * 0.25).toFloat(), (x + cos(a) * bw * 0.46).toFloat(), (y + sin(a) * bw * 0.46).toFloat(), stroke)
            }
            }
            45 -> { // GIGANTIC SCORPION QUEEN: Broad body, twin segmented tail stingers
                c.drawOval((x-bw*0.22).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.35).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.22).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.35).toFloat(), stroke)
                // Twin curved stingers
                for (side in floatArrayOf(-1f, 1f)) {
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawArc((x+side*bw*0.15-bw*0.15).toFloat(), (y-bw*0.42).toFloat(), (x+side*bw*0.15+bw*0.15).toFloat(), (y+bw*0.1).toFloat(), (180).toFloat(), ((360).toFloat() - (180).toFloat()), false, stroke)
                path.reset()
                path.moveTo((x + side * bw * 0.3).toFloat(), (y - bw * 0.25).toFloat())
                path.lineTo((x + side * bw * 0.38).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x + side * bw * 0.2).toFloat(), (y - bw * 0.35).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            }
            46 -> { // CHITIN DRONE: Heavy flying armored beetle with 4 articulated wing foils
                c.drawRect((x-bw*0.15).toFloat(), (y-bw*0.3).toFloat(), (x+bw*0.15).toFloat(), (y+bw*0.3).toFloat(), fill.apply { color = col })
                for (i in 0 until 2) {
                for (side in floatArrayOf(-1f, 1f)) {
                val segY = (y - bw*0.15 + i * bw*0.25).toFloat()
                path.reset()
                path.moveTo((x + side * bw * 0.15).toFloat(), (y).toFloat())
                path.lineTo((x + side * bw * 0.45).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + side * bw * 0.4).toFloat(), (y + bw * 0.1).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
            }
            }
            }
            47 -> { // ANCIENT LEVIATHAN WYRM: 8-node spine wyrm with huge horned predator head
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x - bw * 0.25).toFloat(), (y - bw * 0.2).toFloat())
                path.lineTo((x + bw * 0.25).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                // Horns
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x - bw * 0.2).toFloat(), (y - bw * 0.2).toFloat(), (x - bw * 0.4).toFloat(), (y - bw * 0.35).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x + bw * 0.2).toFloat(), (y - bw * 0.2).toFloat(), (x + bw * 0.4).toFloat(), (y - bw * 0.35).toFloat(), stroke)
                for (i in 0 until 6) {
                val segY = (y - bw*0.1 + i * bw*0.09).toFloat()
                c.drawOval((x-bw*0.18+i*2).toFloat(), (y-6).toFloat(), (x+bw*0.18-i*2).toFloat(), (y+6).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.18+i*2).toFloat(), (y-6).toFloat(), (x+bw*0.18-i*2).toFloat(), (y+6).toFloat(), stroke)
            }
            }
            48 -> { // VOID SPHINX: Winged guardian lion with armored crown and laser chest
                c.drawRect((x-bw*0.2).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.3).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.2).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.3).toFloat(), stroke)
                // Crown head
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x - bw * 0.18).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.18).toFloat(), (y - bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawOval((x-bw*0.1).toFloat(), (y).toFloat(), (x+bw*0.1).toFloat(), (y+bw*0.18).toFloat(), fill.apply { color = col })
                for (side in floatArrayOf(-1f, 1f)) {
                path.reset()
                path.moveTo((x + side * bw * 0.2).toFloat(), (y - bw * 0.1).toFloat())
                path.lineTo((x + side * bw * 0.45).toFloat(), (y - bw * 0.3).toFloat())
                path.lineTo((x + side * bw * 0.38).toFloat(), (y + bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            }
            49 -> { // THE OMEGA HYPER-CORE (FINAL 50TH BOSS): The ultimate planetary singularity entity
                val r1 = (bw*0.45).toFloat()
                val r2 = (bw*0.32).toFloat()
                val r3 = (bw*0.16).toFloat()
                stroke.color = col; stroke.strokeWidth = 6f
                c.drawOval((x-r1).toFloat(), (y-r1).toFloat(), (x+r1).toFloat(), (y+r1).toFloat(), stroke)
                stroke.color = GRAY; stroke.strokeWidth = 4f
                c.drawOval((x-r2).toFloat(), (y-r2).toFloat(), (x+r2).toFloat(), (y+r2).toFloat(), stroke)
                c.drawOval((x-r3).toFloat(), (y-r3).toFloat(), (x+r3).toFloat(), (y+r3).toFloat(), fill.apply { color = col })
                c.drawOval((x-5).toFloat(), (y-5).toFloat(), (x+5).toFloat(), (y+5).toFloat(), fill.apply { color = inv })
                // 8 radiating mechanical tentacles
                for (i in 0 until 8) {
                val a = (i * PI / 4).toFloat()
                val tx = (x + cos(a)*r1).toFloat()
                val ty = (y + sin(a)*r1).toFloat()
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((tx).toFloat(), (ty).toFloat(), (tx + cos(a) * 14).toFloat(), (ty + sin(a) * 14).toFloat(), stroke)
                c.drawRect((tx+cos(a)*14-4).toFloat(), (ty+sin(a)*14-4).toFloat(), (tx+cos(a)*14+4).toFloat(), (ty+sin(a)*14+4).toFloat(), fill.apply { color = col })
            }
            }
        }
    }

    fun drawSegment(c: Canvas, x: Float, y: Float, r: Float, head: Boolean, flash: Boolean) {
        ring(c, x, y, r, 7f, if (flash) BLACK else WHITE)
        if (flash) circle(c, x, y, r, WHITE)
        if (head) {
            rect(c, x - r * 0.5f, y + r * 0.6f, r, r * 0.5f, WHITE)
            rect(c, x - r * 0.5f, y - r * 1.1f, r, r * 0.5f, WHITE)
            circle(c, x, y + r * 0.2f, r * 0.22f, WHITE)
        } else {
            circle(c, x, y, r * 0.3f, if (flash) BLACK else GRAY)
        }
    }

    fun drawWeak(c: Canvas, x: Float, y: Float, r: Float, t: Long) {
        if ((t / 160) % 2L == 0L) {
            stroke.color = WHITE
            stroke.strokeWidth = 4f
            val o = r * 1.5f
            val l = r * 0.6f
            c.drawLine(x - o, y - o, x - o + l, y - o, stroke)
            c.drawLine(x - o, y - o, x - o, y - o + l, stroke)
            c.drawLine(x + o, y - o, x + o - l, y - o, stroke)
            c.drawLine(x + o, y - o, x + o, y - o + l, stroke)
            c.drawLine(x - o, y + o, x - o + l, y + o, stroke)
            c.drawLine(x - o, y + o, x - o, y + o - l, stroke)
            c.drawLine(x + o, y + o, x + o - l, y + o, stroke)
            c.drawLine(x + o, y + o, x + o, y + o - l, stroke)
        }
    }
}

// (box kind ids live in World.kt)
