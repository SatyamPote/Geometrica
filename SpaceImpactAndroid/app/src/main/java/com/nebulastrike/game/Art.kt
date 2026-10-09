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
const val P_COIN = 5
const val P_COIN_RARE = 6
const val P_PART = 7

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

    fun drawPixelatedAnimatedTitle(c: Canvas, text: String, cx: Float, cy: Float, t: Long) {
        val letterMaps = mapOf(
            'G' to arrayOf(" ### ", "#   #", "#    ", "# ###", "#   #", "#   #", " ### "),
            'E' to arrayOf("#####", "#    ", "#    ", "#### ", "#    ", "#    ", "#####"),
            'O' to arrayOf(" ### ", "#   #", "#   #", "#   #", "#   #", "#   #", " ### "),
            'M' to arrayOf("#   #", "## ##", "# # #", "#   #", "#   #", "#   #", "#   #"),
            'T' to arrayOf("#####", "  #  ", "  #  ", "  #  ", "  #  ", "  #  ", "  #  "),
            'R' to arrayOf("#### ", "#   #", "#   #", "#### ", "#  # ", "#   #", "#   #"),
            'I' to arrayOf(" ### ", "  #  ", "  #  ", "  #  ", "  #  ", "  #  ", " ### "),
            'C' to arrayOf(" ### ", "#   #", "#    ", "#    ", "#    ", "#   #", " ### "),
            'A' to arrayOf(" ### ", "#   #", "#   #", "#####", "#   #", "#   #", "#   #")
        )

        val pxSize = 7.4f
        val letterSpacing = 12f
        val letterW = 5 * pxSize
        val totalW = text.length * letterW + (text.length - 1) * letterSpacing
        val startX = cx - totalW / 2f

        for (i in text.indices) {
            val ch = text[i]
            val rows = letterMaps[ch] ?: continue
            val lx = startX + i * (letterW + letterSpacing)
            val waveY = cy + (kotlin.math.sin(t * 0.0032 + i * 0.46).toFloat() * 12f)

            // Draw Drop Shadow (Pixelated Dark Charcoal Shadow Blocks)
            fill.color = 0xFF1C2026.toInt()
            for (r in rows.indices) {
                val rowStr = rows[r]
                for (col in rowStr.indices) {
                    if (rowStr[col] == '#') {
                        val px = lx + col * pxSize
                        val py = waveY + r * pxSize
                        c.drawRect(px + 4f, py + 4f, px + pxSize + 4f, py + pxSize + 4f, fill)
                    }
                }
            }

            // Draw Foreground Pixel Blocks (Crisp Retro Arcade White Blocks)
            fill.color = WHITE
            for (r in rows.indices) {
                val rowStr = rows[r]
                for (col in rowStr.indices) {
                    if (rowStr[col] == '#') {
                        val px = lx + col * pxSize
                        val py = waveY + r * pxSize
                        c.drawRect(px, py, px + pxSize - 0.8f, py + pxSize - 0.8f, fill)
                    }
                }
            }
        }
    }

    // ---------------- player (faces up) ----------------
    fun drawPlayer(c: Canvas, x: Float, y: Float, s: Float, t: Long, blink: Boolean, shipIdx: Int = 0) {
        PlayerShipRenderer.drawShip(c, shipIdx, x, y, s, t, blink)
        return
    }
    fun drawPlayerLegacy(c: Canvas, x: Float, y: Float, s: Float, t: Long, blink: Boolean) {
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
        AlienShipRenderer.drawAlien(c, spec, x, y, r, t, shieldUp, flash)
    }

    fun drawShot(c: Canvas, x: Float, y: Float, big: Boolean) {
        if (big) rect(c, x - 4f, y - 14f, 8f, 28f, WHITE)
        else rect(c, x - 2.5f, y - 9f, 5f, 18f, WHITE)
    }

    fun drawMissile(c: Canvas, x: Float, y: Float, vx: Float, vy: Float, t: Long) {
        val angle = Math.toDegrees(kotlin.math.atan2(vy.toDouble(), vx.toDouble())).toFloat() + 90f
        c.save()
        c.translate(x, y)
        c.rotate(angle)
        // Missile Body
        fill.color = WHITE
        c.drawRect(-3.5f, -12f, 3.5f, 10f, fill)
        // Missile Nosecone
        path.reset()
        path.moveTo(0f, -18f)
        path.lineTo(3.5f, -12f)
        path.lineTo(-3.5f, -12f)
        path.close()
        c.drawPath(path, fill)
        // Missile Fins
        c.drawRect(-7f, 4f, 7f, 10f, fill)
        // Rocket plume
        val f = if ((t / 50) % 2L == 0L) 1f else 0.5f
        fill.color = if ((t / 70) % 2L == 0L) 0xFFFF1744.toInt() else 0xFFFF9100.toInt()
        c.drawRect(-2.5f, 10f, 2.5f, 10f + 14f * f, fill)
        c.restore()
    }

    fun drawContinuousLaser(c: Canvas, px: Float, py: Float, pwr: Int, t: Long) {
        val w = 18f + pwr * 2.5f
        // Outer glow
        stroke.color = 0xFF00E5FF.toInt()
        stroke.strokeWidth = w
        stroke.alpha = 110 + (kotlin.math.sin(t / 40.0) * 40).toInt()
        c.drawLine(px, py - 30f, px, 0f, stroke)
        // Inner intense core beam
        stroke.color = WHITE
        stroke.strokeWidth = w * 0.45f
        stroke.alpha = 255
        c.drawLine(px, py - 30f, px, 0f, stroke)
        stroke.alpha = 255
        // Emitter flare
        fill.color = 0xFF00E5FF.toInt()
        c.drawCircle(px, py - 30f, w * 0.8f, fill)
        fill.color = WHITE
        c.drawCircle(px, py - 30f, w * 0.4f, fill)
    }

    fun drawFoeShot(c: Canvas, x: Float, y: Float, r: Float) {
        // Sharp plasma spark: central diamond plus cross needles (never a square box)
        stroke.color = WHITE
        stroke.strokeWidth = 2.5f
        c.drawLine(x - r * 1.2f, y, x + r * 1.2f, y, stroke)
        c.drawLine(x, y - r * 1.4f, x, y + r * 1.4f, stroke)
        path.reset()
        path.moveTo(x, y - r * 0.9f)
        path.lineTo(x + r * 0.6f, y)
        path.lineTo(x, y + r * 0.9f)
        path.lineTo(x - r * 0.6f, y)
        path.close()
        fill.color = WHITE
        c.drawPath(path, fill)
    }

    // ---------------- pickups (Vibrant Colors for Instant Differentiation) ----------------
    fun drawItem(c: Canvas, kind: Int, x: Float, y: Float, r: Float, t: Long, txt: Paint) {
        // Distinct Colors
        val RED_HEART = 0xFFFF1744.toInt()
        val GOLD_COIN = 0xFFFFD700.toInt()
        val RARE_GOLD = 0xFFFF9100.toInt()
        val CYAN_RAPID = 0xFF00E5FF.toInt()
        val GREEN_DUAL = 0xFF00E676.toInt()
        val PURPLE_SPREAD = 0xFFE040FB.toInt()
        val ORANGE_PIERCE = 0xFFFF6D00.toInt()
        val TEAL_PART = 0xFF1DE9B6.toInt()

        val mainCol = when (kind) {
            4 -> RED_HEART
            P_COIN -> GOLD_COIN
            P_COIN_RARE -> RARE_GOLD
            P_RAPID -> CYAN_RAPID
            P_DOUBLE -> GREEN_DUAL
            P_SPREAD -> PURPLE_SPREAD
            P_PIERCE -> ORANGE_PIERCE
            P_PART -> TEAL_PART
            else -> GOLD_COIN
        }

        // 1. Outer Expanding Beacon Pulse in Pickup's Color
        val pulsePhase = ((t % 750) / 750f)
        val pulseR = r * (1.1f + pulsePhase * 0.95f)
        stroke.color = mainCol
        stroke.strokeWidth = 2.5f
        stroke.alpha = ((1f - pulsePhase) * 220).toInt().coerceIn(0, 255)
        c.drawCircle(x, y, pulseR, stroke)
        stroke.alpha = 255

        when (kind) {
            4 -> { // RED HEART PICKUP (Pulsing vibrant red heart with medical cross)
                val beat = 1f + sin(t * 0.008f).toFloat() * 0.14f
                val hr = r * 0.9f * beat
                val u = hr / 3f

                fill.color = RED_HEART
                c.drawCircle(x - 0.8f * u, y - 0.6f * u, 0.95f * u, fill)
                c.drawCircle(x + 0.8f * u, y - 0.6f * u, 0.95f * u, fill)
                path.reset()
                path.moveTo(x - 1.75f * u, y - 0.3f * u)
                path.lineTo(x, y + 1.9f * u)
                path.lineTo(x + 1.75f * u, y - 0.3f * u)
                path.close()
                c.drawPath(path, fill)

                // White Medical Cross in center
                fill.color = WHITE
                c.drawRect(x - 2.5f, y - 0.8f * u, x + 2.5f, y + 0.6f * u, fill)
                c.drawRect(x - 0.75f * u, y - 2.5f, x + 0.75f * u, y + 2.5f, fill)

                txt.textAlign = Paint.Align.CENTER
                txt.textSize = 10f
                txt.color = RED_HEART
                c.drawText("♥ +HULL", x, y + r + 14f, txt)
            }
            P_COIN, P_COIN_RARE -> { // GOLDEN COIN PICKUP (Vibrant Gold 3D spinning coin)
                val isRare = (kind == P_COIN_RARE)
                val coinCol = if (isRare) RARE_GOLD else GOLD_COIN
                val spin = cos(t * 0.007f).toFloat()
                val coinW = (r * 0.92f * kotlin.math.abs(spin)).coerceAtLeast(4f)
                val coinH = r * 0.92f

                fill.color = coinCol
                c.drawOval(x - coinW, y - coinH, x + coinW, y + coinH, fill)
                stroke.color = WHITE
                stroke.strokeWidth = 2f
                c.drawOval(x - coinW, y - coinH, x + coinW, y + coinH, stroke)

                if (coinW > 7f) {
                    stroke.color = 0xFF8B6508.toInt()
                    stroke.strokeWidth = 1.5f
                    c.drawOval(x - coinW * 0.72f, y - coinH * 0.72f, x + coinW * 0.72f, y + coinH * 0.72f, stroke)
                    txt.textAlign = Paint.Align.CENTER
                    txt.textSize = r * 0.95f
                    txt.color = BLACK
                    c.drawText(if (isRare) "★" else "C", x, y + coinH * 0.36f, txt)
                }

                // Sparkle glints
                val sa = (t * 0.005f).toFloat()
                val sx = x + cos(sa) * r * 1.35f
                val sy = y + sin(sa) * r * 1.35f
                stroke.color = WHITE; stroke.strokeWidth = 2f
                c.drawLine(sx - 3.5f, sy, sx + 3.5f, sy, stroke)
                c.drawLine(sx, sy - 3.5f, sx, sy + 3.5f, stroke)

                txt.textAlign = Paint.Align.CENTER
                txt.textSize = 10f
                txt.color = coinCol
                c.drawText(if (isRare) "+500 C GOLD" else "+100 C", x, y + r + 14f, txt)
            }
            P_PART -> { // TEAL SHIP TECH PART (Rotating 4-tooth cyan/teal gear)
                val rot = (t * 0.003f).toFloat()
                fill.color = DARK
                c.drawCircle(x, y, r * 0.85f, fill)
                stroke.color = TEAL_PART
                stroke.strokeWidth = 3f
                c.drawCircle(x, y, r * 0.85f, stroke)

                for (i in 0 until 4) {
                    val a = rot + i * PI.toFloat() / 2f
                    val tx = x + cos(a) * r * 0.85f
                    val ty = y + sin(a) * r * 0.85f
                    c.drawRect(tx - 3.5f, ty - 3.5f, tx + 3.5f, ty + 3.5f, fill.apply { color = TEAL_PART })
                }
                fill.color = TEAL_PART
                c.drawCircle(x, y, r * 0.35f, fill)
                fill.color = WHITE
                c.drawCircle(x, y, r * 0.15f, fill)

                txt.textAlign = Paint.Align.CENTER
                txt.textSize = 10f
                txt.color = TEAL_PART
                c.drawText("⚙ PART", x, y + r + 14f, txt)
            }
            else -> { // COLORFUL POWER-UP CAPSULES (Cyan Rapid, Green Dual, Purple Spread, Orange Pierce)
                val names = arrayOf("RAPID", "DUAL", "SPREAD", "PIERCE")
                val letters = arrayOf("R", "2", "S", "P")
                val pIdx = kind.coerceIn(0, 3)

                path.reset()
                val capR = r * 0.95f
                for (i in 0 until 8) {
                    val a = (i * PI / 4).toFloat()
                    val px = x + cos(a) * capR
                    val py = y + sin(a) * capR
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close()

                fill.color = mainCol
                c.drawPath(path, fill)
                stroke.color = WHITE
                stroke.strokeWidth = 2.5f
                c.drawPath(path, stroke)

                // High-contrast badge inside
                fill.color = BLACK
                c.drawCircle(x, y, r * 0.58f, fill)
                txt.textAlign = Paint.Align.CENTER
                txt.textSize = r * 0.95f
                txt.color = mainCol
                c.drawText(letters[pIdx], x, y + r * 0.34f, txt)

                txt.textSize = 10f
                txt.color = mainCol
                c.drawText("[ ${names[pIdx]} ]", x, y + r + 14f, txt)
            }
        }
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

        val lowId = id.lowercase()
        when (lowId) {
            "prism", "0", "boss0" -> { // #1 PRISM (ID: prism)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.48).toFloat())
                path.lineTo((x + bw * 0.4).toFloat(), (y).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.48).toFloat())
                path.lineTo((x - bw * 0.4).toFloat(), (y).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.28).toFloat())
                path.lineTo((x + bw * 0.2).toFloat(), (y).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.28).toFloat())
                path.lineTo((x - bw * 0.2).toFloat(), (y).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x - bw * 0.4).toFloat(), (y).toFloat(), (x + bw * 0.4).toFloat(), (y).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x).toFloat(), (y - bw * 0.48).toFloat(), (x).toFloat(), (y + bw * 0.48).toFloat(), stroke)
            }
            "cascade", "1", "boss1" -> { // #2 CASCADE (ID: cascade)
                for (i in 0 until 5) {
                    val segY = (y - bw * 0.4f + i * bw * 0.18f)
                    val gap = (i - 2) * 12f
                    val left_x2 = (x - bw * 0.45f).coerceAtLeast(x + gap - 12f)
                    val right_x1 = (x + bw * 0.45f).coerceAtMost(x + gap + 12f)
                    c.drawRect(x - bw * 0.45f, segY, left_x2, segY + bw * 0.1f, fill.apply { color = col })
                    c.drawRect(right_x1, segY, x + bw * 0.45f, segY + bw * 0.1f, fill.apply { color = col })
                }
            }
            "gauntlet", "2", "boss2" -> { // #3 GAUNTLET (ID: gauntlet)
                c.drawRect((x-bw*0.45).toFloat(), (y-bw*0.35).toFloat(), (x-bw*0.15).toFloat(), (y+bw*0.35).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.45).toFloat(), (y-bw*0.35).toFloat(), (x-bw*0.15).toFloat(), (y+bw*0.35).toFloat(), stroke)
                c.drawRect((x+bw*0.15).toFloat(), (y-bw*0.35).toFloat(), (x+bw*0.45).toFloat(), (y+bw*0.35).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x+bw*0.15).toFloat(), (y-bw*0.35).toFloat(), (x+bw*0.45).toFloat(), (y+bw*0.35).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawLine((x - bw * 0.15).toFloat(), (y).toFloat(), (x - bw * 0.05).toFloat(), (y).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawLine((x + bw * 0.05).toFloat(), (y).toFloat(), (x + bw * 0.15).toFloat(), (y).toFloat(), stroke)
            }
            "vortex", "3", "boss3" -> { // #4 VORTEX (ID: vortex)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.42).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.42).toFloat(), (y+bw*0.42).toFloat(), stroke)
                stroke.color = GRAY; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.26).toFloat(), (y-bw*0.26).toFloat(), (x+bw*0.26).toFloat(), (y+bw*0.26).toFloat(), stroke)
                c.drawOval((x-bw*0.1).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.1).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = col })
                for (i in 0 until 6) {
                val a = (i * PI / 3).toFloat()
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawArc((x-bw*0.35).toFloat(), (y-bw*0.35).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.35).toFloat(), (i*60).toFloat(), 45f, false, stroke)
            }
            }
            "beacon", "4", "boss4" -> { // #5 BEACON (ID: beacon)
                c.drawRect((x-bw*0.12).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.45).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.12).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.45).toFloat(), stroke)
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.2).toFloat(), (y-bw*0.18).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.06).toFloat(), (y-bw*0.33).toFloat(), (x+bw*0.06).toFloat(), (y-bw*0.21).toFloat(), fill.apply { color = inv })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.35).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.1).toFloat(), stroke)
            }
            "splitter", "5", "boss5" -> { // #6 SPLITTER (ID: splitter)
                path.reset()
                path.moveTo((x - bw * 0.05).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x - bw * 0.42).toFloat(), (y + bw * 0.3).toFloat())
                path.lineTo((x - bw * 0.05).toFloat(), (y + bw * 0.3).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                path.reset()
                path.moveTo((x + bw * 0.05).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x + bw * 0.42).toFloat(), (y + bw * 0.3).toFloat())
                path.lineTo((x + bw * 0.05).toFloat(), (y + bw * 0.3).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawOval((x-bw*0.18).toFloat(), (y+bw*0.05).toFloat(), (x-bw*0.08).toFloat(), (y+bw*0.15).toFloat(), fill.apply { color = col })
                c.drawOval((x+bw*0.08).toFloat(), (y+bw*0.05).toFloat(), (x+bw*0.18).toFloat(), (y+bw*0.15).toFloat(), fill.apply { color = col })
            }
            "orbit", "6", "boss6" -> { // #7 ORBIT (ID: orbit)
                c.drawOval((x-bw*0.24).toFloat(), (y-bw*0.24).toFloat(), (x+bw*0.24).toFloat(), (y+bw*0.24).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.24).toFloat(), (y-bw*0.24).toFloat(), (x+bw*0.24).toFloat(), (y+bw*0.24).toFloat(), stroke)
                c.drawOval((x-bw*0.08).toFloat(), (y-bw*0.08).toFloat(), (x+bw*0.08).toFloat(), (y+bw*0.08).toFloat(), fill.apply { color = col })
                for (i in 0 until 8) {
                    val a = (i * PI / 4.0).toFloat()
                    val tx = (x + cos(a.toDouble()) * bw * 0.38f).toFloat()
                    val ty = (y + sin(a.toDouble()) * bw * 0.38f).toFloat()
                    path.reset()
                    path.moveTo(tx, ty - 6f)
                    path.lineTo(tx + 6f, ty + 6f)
                    path.lineTo(tx - 6f, ty + 6f)
                    path.close()
                    c.drawPath(path, fill.apply { color = col })
                }
            }
            "sawtooth", "7", "boss7" -> { // #8 SAWTOOTH (ID: sawtooth)
                path.reset()
                for (i in 0 until 12) {
                    val r = if (i % 2 == 0) bw * 0.42f else bw * 0.24f
                    val a = i * PI / 6.0
                    val px = (x + cos(a) * r).toFloat()
                    val py = (y + sin(a) * r).toFloat()
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawOval(x - bw * 0.1f, y - bw * 0.1f, x + bw * 0.1f, y + bw * 0.1f, fill.apply { color = col })
            }
            "tower", "8", "boss8" -> { // #9 TOWER (ID: tower)
                c.drawRect((x-bw*0.25).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.45).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.25).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.45).toFloat(), stroke)
                for (row in 0 until 4) {
                for (ci in 0 until 2) {
                val wx = (x - bw*0.15 + ci * bw*0.2).toFloat()
                val wy = (y - bw*0.35 + row * bw*0.2).toFloat()
                c.drawRect((wx-8).toFloat(), (wy-8).toFloat(), (wx+8).toFloat(), (wy+8).toFloat(), fill.apply { color = col })
            }
            }
            }
            "mesh", "9", "boss9" -> { // #10 MESH (ID: mesh)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.4).toFloat(), (y-bw*0.4).toFloat(), (x+bw*0.4).toFloat(), (y+bw*0.4).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x - bw * 0.4).toFloat(), (y).toFloat(), (x + bw * 0.4).toFloat(), (y).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x).toFloat(), (y - bw * 0.4).toFloat(), (x).toFloat(), (y + bw * 0.4).toFloat(), stroke)
                c.drawRect((x-bw*0.2).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.2).toFloat(), fill.apply { color = col })
                c.drawOval((x-5).toFloat(), (y-5).toFloat(), (x+5).toFloat(), (y+5).toFloat(), fill.apply { color = inv })
            }
            "flare", "10", "boss10" -> { // #11 FLARE (ID: flare)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x - bw * 0.38).toFloat(), (y + bw * 0.35).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.38).toFloat(), (y + bw * 0.35).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                for (i in floatArrayOf(-bw*0.25f, 0f, bw*0.25f)) {
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x + i).toFloat(), (y - bw * 0.45).toFloat(), (x + i * 1.8).toFloat(), (y - bw * 0.1).toFloat(), stroke)
            }
            }
            "delta", "11", "boss11" -> { // #12 DELTA (ID: delta)
                path.reset()
                path.moveTo((x).toFloat(), (y + bw * 0.4).toFloat())
                path.lineTo((x - bw * 0.45).toFloat(), (y - bw * 0.25).toFloat())
                path.lineTo((x).toFloat(), (y - bw * 0.05).toFloat())
                path.lineTo((x + bw * 0.45).toFloat(), (y - bw * 0.25).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawRect((x-bw*0.08).toFloat(), (y-bw*0.35).toFloat(), (x+bw*0.08).toFloat(), (y+bw*0.2).toFloat(), fill.apply { color = col })
            }
            "web", "12", "boss12" -> { // #13 WEB (ID: web)
                for (r in floatArrayOf(0.42f, 0.28f, 0.14f)) {
                    stroke.color = col; stroke.strokeWidth = 2f
                    c.drawOval(x - bw * r, y - bw * r, x + bw * r, y + bw * r, stroke)
                }
                for (i in 0 until 8) {
                    val a = (i * PI / 4.0).toFloat()
                    stroke.color = col; stroke.strokeWidth = 2f
                    c.drawLine(x, y, (x + cos(a.toDouble()) * bw * 0.42f).toFloat(), (y + sin(a.toDouble()) * bw * 0.42f).toFloat(), stroke)
                }
            }
            "prismatic", "13", "boss13" -> { // #14 PRISMATIC (ID: prismatic)
                path.reset()
                for (i in 0 until 6) {
                    val a = i * PI / 3.0
                    val px = (x + cos(a) * bw * 0.4f).toFloat()
                    val py = (y + sin(a) * bw * 0.4f).toFloat()
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                path.reset()
                path.moveTo(x, y - bw * 0.2f)
                path.lineTo(x + bw * 0.18f, y)
                path.lineTo(x, y + bw * 0.2f)
                path.lineTo(x - bw * 0.18f, y)
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            "helix", "14", "boss14" -> { // #15 HELIX (ID: helix)
                for (i in 0 until 5) {
                val segY = (y - bw*0.36 + i * bw*0.18).toFloat()
                val w = (bw * 0.35).toFloat()
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-w).toFloat(), (y-bw*0.06).toFloat(), (x+w).toFloat(), (y+bw*0.06).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x - bw * 0.25).toFloat(), (y - bw * 0.36).toFloat(), (x + bw * 0.25).toFloat(), (y + bw * 0.36).toFloat(), stroke)
            }
            }
            "lockbox", "15", "boss15" -> { // #16 LOCKBOX (ID: lockbox)
                c.drawRect((x-bw*0.35).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.42).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.35).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.35).toFloat(), (y+bw*0.42).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 5f
                c.drawArc((x-bw*0.22).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.22).toFloat(), (y).toFloat(), (180).toFloat(), ((360).toFloat() - (180).toFloat()), false, stroke)
                c.drawOval((x-8).toFloat(), (y+bw*0.1-8).toFloat(), (x+8).toFloat(), (y+bw*0.1+8).toFloat(), fill.apply { color = col })
                path.reset()
                path.moveTo((x - 4).toFloat(), (y + bw * 0.1).toFloat())
                path.lineTo((x + 4).toFloat(), (y + bw * 0.1).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.25).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            "flareon", "16", "boss16" -> { // #17 FLAREON (ID: flareon)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x - bw * 0.45).toFloat(), (y).toFloat())
                path.lineTo((x - bw * 0.15).toFloat(), (y + bw * 0.35).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.1).toFloat())
                path.lineTo((x + bw * 0.35).toFloat(), (y + bw * 0.35).toFloat())
                path.lineTo((x + bw * 0.45).toFloat(), (y - bw * 0.2).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
                c.drawOval((x-bw*0.1).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.1).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = col })
            }
            "obsidian", "17", "boss17" -> { // #18 OBSIDIAN (ID: obsidian)
                path.reset()
                path.moveTo(x, y - bw * 0.45f)
                path.lineTo(x + bw * 0.35f, y - bw * 0.25f)
                path.lineTo(x + bw * 0.45f, y + bw * 0.1f)
                path.lineTo(x + bw * 0.2f, y + bw * 0.42f)
                path.lineTo(x - bw * 0.3f, y + bw * 0.35f)
                path.lineTo(x - bw * 0.45f, y - bw * 0.15f)
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                path.reset()
                path.moveTo(x - bw * 0.1f, y - bw * 0.2f)
                path.lineTo(x + bw * 0.15f, y - bw * 0.1f)
                path.lineTo(x, y + bw * 0.2f)
                path.lineTo(x - bw * 0.2f, y + bw * 0.1f)
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            "glyph", "18", "boss18" -> { // #19 GLYPH (ID: glyph)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.32).toFloat(), (y-bw*0.38).toFloat(), (x+bw*0.32).toFloat(), (y+bw*0.38).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x - bw * 0.32).toFloat(), (y - bw * 0.38).toFloat(), (x + bw * 0.32).toFloat(), (y + bw * 0.38).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x + bw * 0.32).toFloat(), (y - bw * 0.38).toFloat(), (x - bw * 0.32).toFloat(), (y + bw * 0.38).toFloat(), stroke)
                c.drawOval((x-bw*0.12).toFloat(), (y-bw*0.12).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.12).toFloat(), fill.apply { color = col })
            }
            "siphon", "19", "boss19" -> { // #20 SIPHON (ID: siphon)
                path.reset()
                path.moveTo((x - bw * 0.35).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x + bw * 0.35).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x + bw * 0.06).toFloat(), (y).toFloat())
                path.lineTo((x + bw * 0.35).toFloat(), (y + bw * 0.4).toFloat())
                path.lineTo((x - bw * 0.35).toFloat(), (y + bw * 0.4).toFloat())
                path.lineTo((x - bw * 0.06).toFloat(), (y).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawOval((x-bw*0.12).toFloat(), (y-bw*0.25).toFloat(), (x+bw*0.12).toFloat(), (y-bw*0.15).toFloat(), fill.apply { color = col })
                c.drawOval((x-bw*0.12).toFloat(), (y+bw*0.15).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.25).toFloat(), fill.apply { color = col })
            }
            "pulse", "20", "boss20" -> { // #21 PULSE (ID: pulse)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawOval((x-bw*0.42).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.42).toFloat(), (y+bw*0.42).toFloat(), stroke)
                stroke.color = GRAY; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.22).toFloat(), (y-bw*0.22).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.22).toFloat(), stroke)
                c.drawOval((x-bw*0.08).toFloat(), (y-bw*0.08).toFloat(), (x+bw*0.08).toFloat(), (y+bw*0.08).toFloat(), fill.apply { color = col })
            }
            "spirebreak", "21", "boss21" -> { // #22 SPIREBREAK (ID: spirebreak)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x - bw * 0.15).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.15).toFloat(), (y - bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawRect((x-bw*0.22).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.12).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.22).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.12).toFloat(), stroke)
                c.drawRect((x-bw*0.32).toFloat(), (y+bw*0.18).toFloat(), (x+bw*0.32).toFloat(), (y+bw*0.42).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.32).toFloat(), (y+bw*0.18).toFloat(), (x+bw*0.32).toFloat(), (y+bw*0.42).toFloat(), stroke)
            }
            "facet", "22", "boss22" -> { // #23 FACET (ID: facet)
                path.reset()
                for (i in 0 until 8) {
                    val a = i * PI / 4.0
                    val px = (x + cos(a) * bw * 0.38f).toFloat()
                    val py = (y + sin(a) * bw * 0.38f).toFloat()
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                stroke.strokeWidth = 1.5f
                for (i in 0 until 8) {
                    val a = i * PI / 4.0
                    c.drawLine(x, y, (x + cos(a) * bw * 0.38f).toFloat(), (y + sin(a) * bw * 0.38f).toFloat(), stroke)
                }
            }
            "vortex2", "23", "boss23" -> { // #24 VORTEX2 (ID: vortex2)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.42).toFloat(), (y-bw*0.22).toFloat(), (x).toFloat(), (y+bw*0.22).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x).toFloat(), (y-bw*0.22).toFloat(), (x+bw*0.42).toFloat(), (y+bw*0.22).toFloat(), stroke)
                c.drawOval((x-bw*0.21-6).toFloat(), (y-6).toFloat(), (x-bw*0.21+6).toFloat(), (y+6).toFloat(), fill.apply { color = col })
                c.drawOval((x+bw*0.21-6).toFloat(), (y-6).toFloat(), (x+bw*0.21+6).toFloat(), (y+6).toFloat(), fill.apply { color = col })
            }
            "cradle", "24", "boss24" -> { // #25 CRADLE (ID: cradle)
                stroke.color = col; stroke.strokeWidth = 5f
                c.drawArc((x-bw*0.4).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.4).toFloat(), (y+bw*0.42).toFloat(), (0).toFloat(), ((180).toFloat() - (0).toFloat()), false, stroke)
                c.drawOval((x-bw*0.15).toFloat(), (y-bw*0.05).toFloat(), (x+bw*0.15).toFloat(), (y+bw*0.25).toFloat(), fill.apply { color = col })
                c.drawOval((x-5).toFloat(), (y+bw*0.1-5).toFloat(), (x+5).toFloat(), (y+bw*0.1+5).toFloat(), fill.apply { color = inv })
            }
            "skyline", "25", "boss25" -> { // #26 SKYLINE (ID: skyline)
                val heights = floatArrayOf(0.25f, 0.45f, 0.35f, 0.5f, 0.3f)
                for (i in 0 until 5) {
                    val hVal = heights[i]
                    val segX = x - bw * 0.4f + i * bw * 0.18f
                    c.drawRect(segX, y + bw * 0.4f - bw * hVal, segX + bw * 0.15f, y + bw * 0.4f, fill.apply { color = bg })
                    stroke.color = col; stroke.strokeWidth = 2f
                    c.drawRect(segX, y + bw * 0.4f - bw * hVal, segX + bw * 0.15f, y + bw * 0.4f, stroke)
                    c.drawRect(segX + 4f, y + bw * 0.4f - bw * hVal + 6f, segX + bw * 0.15f - 4f, y + bw * 0.4f - bw * hVal + 14f, fill.apply { color = col })
                }
            }
            "echo", "26", "boss26" -> { // #27 ECHO (ID: echo)
                path.reset()
                path.moveTo((x - bw * 0.45).toFloat(), (y + bw * 0.3).toFloat())
                path.lineTo((x).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x + bw * 0.45).toFloat(), (y + bw * 0.3).toFloat())
                path.lineTo((x + bw * 0.3).toFloat(), (y + bw * 0.35).toFloat())
                path.lineTo((x).toFloat(), (y - bw * 0.15).toFloat())
                path.lineTo((x - bw * 0.3).toFloat(), (y + bw * 0.35).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawOval((x-8).toFloat(), (y-bw*0.15).toFloat(), (x+8).toFloat(), (y-bw*0.01).toFloat(), fill.apply { color = col })
            }
            "needle", "27", "boss27" -> { // #28 NEEDLE (ID: needle)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawLine((x).toFloat(), (y - bw * 0.45).toFloat(), (x).toFloat(), (y + bw * 0.45).toFloat(), stroke)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x - bw * 0.12).toFloat(), (y - bw * 0.25).toFloat())
                path.lineTo((x).toFloat(), (y - bw * 0.05).toFloat())
                path.lineTo((x + bw * 0.12).toFloat(), (y - bw * 0.25).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawRect((x-bw*0.2).toFloat(), (y+bw*0.35).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.45).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.2).toFloat(), (y+bw*0.35).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.45).toFloat(), stroke)
            }
            "coil", "28", "boss28" -> { // #29 COIL (ID: coil)
                for (i in 0 until 6) {
                val segY = (y - bw*0.38 + i * bw*0.14).toFloat()
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x - bw * 0.3).toFloat(), (y).toFloat(), (x + bw * 0.3).toFloat(), (y + bw * 0.07).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x + bw * 0.3).toFloat(), (y + bw * 0.07).toFloat(), (x - bw * 0.3).toFloat(), (y + bw * 0.14).toFloat(), stroke)
            }
            }
            "sentinel-guard", "29", "boss29" -> { // #30 SENTINEL-GUARD (ID: sentinel-guard)
                c.drawOval((x-bw*0.18).toFloat(), (y-bw*0.18).toFloat(), (x+bw*0.18).toFloat(), (y+bw*0.18).toFloat(), fill.apply { color = col })
                c.drawOval((x-6).toFloat(), (y-6).toFloat(), (x+6).toFloat(), (y+6).toFloat(), fill.apply { color = inv })
                for (i in 0 until 4) {
                    val a = (i * PI / 2.0).toFloat()
                    val px = (x + cos(a.toDouble()) * bw * 0.32f).toFloat()
                    val py = (y + sin(a.toDouble()) * bw * 0.32f).toFloat()
                    c.drawRect(px - 8f, py - 8f, px + 8f, py + 8f, fill.apply { color = bg })
                    stroke.color = col; stroke.strokeWidth = 2f
                    c.drawRect(px - 8f, py - 8f, px + 8f, py + 8f, stroke)
                }
            }
            "rampart", "30", "boss30" -> { // #31 RAMPART (ID: rampart)
                c.drawRect((x-bw*0.45).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.45).toFloat(), (y+bw*0.35).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.45).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.45).toFloat(), (y+bw*0.35).toFloat(), stroke)
                for (i in 0 until 4) {
                    val segX = x - bw * 0.38f + i * bw * 0.22f
                    c.drawRect(segX, y - bw * 0.25f, segX + bw * 0.12f, y - bw * 0.1f, fill.apply { color = col })
                }
            }
            "sentinel-core", "31", "boss31" -> { // #32 SENTINEL-CORE (ID: sentinel-core)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval((x-bw*0.42).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.42).toFloat(), (y+bw*0.42).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.26).toFloat(), (y-bw*0.26).toFloat(), (x+bw*0.26).toFloat(), (y+bw*0.26).toFloat(), stroke)
                c.drawRect((x-bw*0.12).toFloat(), (y-bw*0.12).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.12).toFloat(), fill.apply { color = col })
            }
            "havoc", "32", "boss32" -> { // #33 HAVOC (ID: havoc)
                path.reset()
                for (i in 0 until 16) {
                    val r = if (i % 2 == 0) bw * 0.45f else bw * 0.18f
                    val a = i * PI / 8.0
                    val px = (x + cos(a) * r).toFloat()
                    val py = (y + sin(a) * r).toFloat()
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawOval(x - bw * 0.08f, y - bw * 0.08f, x + bw * 0.08f, y + bw * 0.08f, fill.apply { color = inv })
            }
            "sentinel-wing", "33", "boss33" -> { // #34 SENTINEL-WING (ID: sentinel-wing)
                path.reset()
                path.moveTo((x - bw * 0.45).toFloat(), (y - bw * 0.3).toFloat())
                path.lineTo((x + bw * 0.35).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x + bw * 0.2).toFloat(), (y + bw * 0.35).toFloat())
                path.lineTo((x - bw * 0.15).toFloat(), (y + bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawLine((x - bw * 0.45).toFloat(), (y - bw * 0.3).toFloat(), (x + bw * 0.2).toFloat(), (y + bw * 0.35).toFloat(), stroke)
                c.drawOval((x-8).toFloat(), (y-8).toFloat(), (x+8).toFloat(), (y+8).toFloat(), fill.apply { color = col })
            }
            "sentinel-tower", "34", "boss34" -> { // #35 SENTINEL-TOWER (ID: sentinel-tower)
                c.drawRect((x-bw*0.25).toFloat(), (y).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.42).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.25).toFloat(), (y).toFloat(), (x+bw*0.25).toFloat(), (y+bw*0.42).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawLine((x).toFloat(), (y).toFloat(), (x).toFloat(), (y - bw * 0.45).toFloat(), stroke)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.45).toFloat())
                path.lineTo((x + bw * 0.3).toFloat(), (y - bw * 0.35).toFloat())
                path.lineTo((x).toFloat(), (y - bw * 0.25).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
            }
            "cataclysm", "35", "boss35" -> { // #36 CATACLYSM (ID: cataclysm)
                c.drawOval((x-bw*0.45).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.45).toFloat(), (y-bw*0.05).toFloat(), fill.apply { color = col })
                c.drawRect((x-bw*0.12).toFloat(), (y-bw*0.05).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.45).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.12).toFloat(), (y-bw*0.05).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.45).toFloat(), stroke)
                c.drawOval((x-bw*0.25).toFloat(), (y-bw*0.32).toFloat(), (x-bw*0.15).toFloat(), (y-bw*0.22).toFloat(), fill.apply { color = inv })
                c.drawOval((x+bw*0.15).toFloat(), (y-bw*0.32).toFloat(), (x+bw*0.25).toFloat(), (y-bw*0.22).toFloat(), fill.apply { color = inv })
            }
            "sentinel-spike", "36", "boss36" -> { // #37 SENTINEL-SPIKE (ID: sentinel-spike)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.48).toFloat())
                path.lineTo((x - bw * 0.12).toFloat(), (y + bw * 0.15).toFloat())
                path.lineTo((x + bw * 0.12).toFloat(), (y + bw * 0.15).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawOval((x-bw*0.3).toFloat(), (y+bw*0.15).toFloat(), (x+bw*0.3).toFloat(), (y+bw*0.42).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.3).toFloat(), (y+bw*0.15).toFloat(), (x+bw*0.3).toFloat(), (y+bw*0.42).toFloat(), stroke)
                c.drawOval((x-6).toFloat(), (y-bw*0.1).toFloat(), (x+6).toFloat(), (y).toFloat(), fill.apply { color = inv })
            }
            "sentinel-ring", "37", "boss37" -> { // #38 SENTINEL-RING (ID: sentinel-ring)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawOval((x-bw*0.38).toFloat(), (y-bw*0.38).toFloat(), (x+bw*0.38).toFloat(), (y+bw*0.38).toFloat(), stroke)
                c.drawOval((x-bw*0.15).toFloat(), (y-bw*0.15).toFloat(), (x+bw*0.15).toFloat(), (y+bw*0.15).toFloat(), fill.apply { color = col })
                for (i in 0 until 4) {
                val a = (i * PI / 2 + 0.3).toFloat()
                c.drawOval((x+cos(a)*bw*0.38-8).toFloat(), (y+sin(a)*bw*0.38-8).toFloat(), (x+cos(a)*bw*0.38+8).toFloat(), (y+sin(a)*bw*0.38+8).toFloat(), fill.apply { color = col })
            }
            }
            "sentinel-cross", "38", "boss38" -> { // #39 SENTINEL-CROSS (ID: sentinel-cross)
                c.drawRect((x-bw*0.42).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.42).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.42).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.42).toFloat(), (y+bw*0.1).toFloat(), stroke)
                c.drawRect((x-bw*0.1).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.1).toFloat(), (y+bw*0.42).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.1).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.1).toFloat(), (y+bw*0.42).toFloat(), stroke)
                c.drawOval((x-bw*0.12).toFloat(), (y-bw*0.12).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.12).toFloat(), fill.apply { color = col })
            }
            "sentinel-nest", "39", "boss39" -> { // #40 SENTINEL-NEST (ID: sentinel-nest)
                c.drawOval((x-bw*0.2).toFloat(), (y-bw*0.2).toFloat(), (x+bw*0.2).toFloat(), (y+bw*0.2).toFloat(), fill.apply { color = col })
                for (i in 0 until 6) {
                    val a = (i * PI / 3.0).toFloat()
                    val px = (x + cos(a.toDouble()) * bw * 0.32f).toFloat()
                    val py = (y + sin(a.toDouble()) * bw * 0.32f).toFloat()
                    c.drawOval(px - 10f, py - 10f, px + 10f, py + 10f, fill.apply { color = bg })
                    stroke.color = col; stroke.strokeWidth = 2f
                    c.drawOval(px - 10f, py - 10f, px + 10f, py + 10f, stroke)
                }
            }
            "dominion", "40", "boss40" -> { // #41 DOMINION (ID: dominion)
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawRect((x-bw*0.44).toFloat(), (y-bw*0.38).toFloat(), (x+bw*0.44).toFloat(), (y+bw*0.38).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x - bw * 0.15).toFloat(), (y - bw * 0.38).toFloat(), (x - bw * 0.15).toFloat(), (y + bw * 0.38).toFloat(), stroke)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine((x + bw * 0.15).toFloat(), (y - bw * 0.38).toFloat(), (x + bw * 0.15).toFloat(), (y + bw * 0.38).toFloat(), stroke)
                c.drawRect((x-bw*0.15).toFloat(), (y-bw*0.38).toFloat(), (x+bw*0.15).toFloat(), (y+bw*0.38).toFloat(), fill.apply { color = col })
            }
            "sentinel-web", "41", "boss41" -> { // #42 SENTINEL-WEB (ID: sentinel-web)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.4).toFloat(), (y-bw*0.4).toFloat(), (x+bw*0.4).toFloat(), (y+bw*0.4).toFloat(), stroke)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.4).toFloat())
                path.lineTo((x + bw * 0.4).toFloat(), (y).toFloat())
                path.lineTo((x).toFloat(), (y + bw * 0.4).toFloat())
                path.lineTo((x - bw * 0.4).toFloat(), (y).toFloat())
                path.close()
                stroke.color = col; stroke.strokeWidth = 2f; c.drawPath(path, stroke)
                c.drawOval((x-bw*0.1).toFloat(), (y-bw*0.1).toFloat(), (x+bw*0.1).toFloat(), (y+bw*0.1).toFloat(), fill.apply { color = col })
            }
            "colossus-prime", "42", "boss42" -> { // #43 COLOSSUS-PRIME (ID: colossus-prime)
                c.drawRect(x - bw * 0.45f, y - bw * 0.35f, x + bw * 0.45f, y + bw * 0.1f, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawRect(x - bw * 0.45f, y - bw * 0.35f, x + bw * 0.45f, y + bw * 0.1f, stroke)
                for (k in floatArrayOf(-bw * 0.3f, -bw * 0.1f, bw * 0.1f, bw * 0.3f)) {
                    stroke.color = col; stroke.strokeWidth = 3f
                    c.drawLine(x + k, y + bw * 0.1f, x + k, y + bw * 0.45f, stroke)
                    c.drawRect(x + k - 5f, y + bw * 0.42f, x + k + 5f, y + bw * 0.48f, fill.apply { color = col })
                }
            }
            "spire-guard", "43", "boss43" -> { // #44 SPIRE-GUARD (ID: spire-guard)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.48).toFloat())
                path.lineTo((x - bw * 0.2).toFloat(), (y + bw * 0.38).toFloat())
                path.lineTo((x + bw * 0.2).toFloat(), (y + bw * 0.38).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawOval((x-bw*0.35-8).toFloat(), (y-8).toFloat(), (x-bw*0.35+8).toFloat(), (y+8).toFloat(), fill.apply { color = col })
                c.drawOval((x+bw*0.35-8).toFloat(), (y-8).toFloat(), (x+bw*0.35+8).toFloat(), (y+8).toFloat(), fill.apply { color = col })
            }
            "ring-maiden", "44", "boss44" -> { // #45 RING-MAIDEN (ID: ring-maiden)
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawOval(x - bw * 0.25f, y - bw * 0.3f, x + bw * 0.25f, y + bw * 0.2f, stroke)
                c.drawOval(x - bw * 0.08f, y - bw * 0.1f, x + bw * 0.08f, y + bw * 0.06f, fill.apply { color = col })
                for (side in floatArrayOf(-1f, 1f)) {
                    val xa = if (side < 0) x - bw * 0.48f else x + bw * 0.2f
                    val xb = if (side < 0) x - bw * 0.2f else x + bw * 0.48f
                    stroke.color = col; stroke.strokeWidth = 3f
                    c.drawArc(xa, y - bw * 0.1f, xb, y + bw * 0.45f, 0f, 180f, false, stroke)
                }
            }
            "cross-break", "45", "boss45" -> { // #46 CROSS-BREAK (ID: cross-break)
                c.drawRect((x-bw*0.12).toFloat(), (y-bw*0.12).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.12).toFloat(), fill.apply { color = col })
                c.drawRect((x-bw*0.45).toFloat(), (y-bw*0.08).toFloat(), (x-bw*0.2).toFloat(), (y+bw*0.08).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.45).toFloat(), (y-bw*0.08).toFloat(), (x-bw*0.2).toFloat(), (y+bw*0.08).toFloat(), stroke)
                c.drawRect((x+bw*0.2).toFloat(), (y-bw*0.08).toFloat(), (x+bw*0.45).toFloat(), (y+bw*0.08).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x+bw*0.2).toFloat(), (y-bw*0.08).toFloat(), (x+bw*0.45).toFloat(), (y+bw*0.08).toFloat(), stroke)
                c.drawRect((x-bw*0.08).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.08).toFloat(), (y-bw*0.2).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.08).toFloat(), (y-bw*0.45).toFloat(), (x+bw*0.08).toFloat(), (y-bw*0.2).toFloat(), stroke)
                c.drawRect((x-bw*0.08).toFloat(), (y+bw*0.2).toFloat(), (x+bw*0.08).toFloat(), (y+bw*0.45).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawRect((x-bw*0.08).toFloat(), (y+bw*0.2).toFloat(), (x+bw*0.08).toFloat(), (y+bw*0.45).toFloat(), stroke)
            }
            "nest-guard", "46", "boss46" -> { // #47 NEST-GUARD (ID: nest-guard)
                c.drawOval((x-bw*0.42).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.42).toFloat(), (y+bw*0.42).toFloat(), fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 4f
                c.drawOval((x-bw*0.42).toFloat(), (y-bw*0.42).toFloat(), (x+bw*0.42).toFloat(), (y+bw*0.42).toFloat(), stroke)
                c.drawOval((x-bw*0.26).toFloat(), (y-bw*0.26).toFloat(), (x+bw*0.26).toFloat(), (y+bw*0.26).toFloat(), fill.apply { color = inv })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.26).toFloat(), (y-bw*0.26).toFloat(), (x+bw*0.26).toFloat(), (y+bw*0.26).toFloat(), stroke)
                c.drawOval((x-bw*0.12).toFloat(), (y-bw*0.12).toFloat(), (x+bw*0.12).toFloat(), (y+bw*0.12).toFloat(), fill.apply { color = col })
            }
            "ultimate", "47", "boss47" -> { // #48 ULTIMATE (ID: ultimate)
                path.reset()
                path.moveTo((x).toFloat(), (y - bw * 0.48).toFloat())
                path.lineTo((x + bw * 0.45).toFloat(), (y - bw * 0.1).toFloat())
                path.lineTo((x + bw * 0.35).toFloat(), (y + bw * 0.42).toFloat())
                path.lineTo((x - bw * 0.35).toFloat(), (y + bw * 0.42).toFloat())
                path.lineTo((x - bw * 0.45).toFloat(), (y - bw * 0.1).toFloat())
                path.close()
                c.drawPath(path, fill.apply { color = bg })
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawOval((x-bw*0.22).toFloat(), (y-bw*0.22).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.22).toFloat(), fill.apply { color = inv })
                stroke.color = col; stroke.strokeWidth = 3f
                c.drawOval((x-bw*0.22).toFloat(), (y-bw*0.22).toFloat(), (x+bw*0.22).toFloat(), (y+bw*0.22).toFloat(), stroke)
                c.drawOval((x-bw*0.09).toFloat(), (y-bw*0.09).toFloat(), (x+bw*0.09).toFloat(), (y+bw*0.09).toFloat(), fill.apply { color = col })
            }
            "void-walker", "custom-49", "48", "boss48" -> { // #49 VOID-WALKER (ID: void-walker)
                // Intangible phasing entity with shifting translucent diamond shell & core
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawOval(x - bw * 0.4f, y - bw * 0.35f, x + bw * 0.4f, y + bw * 0.35f, stroke)
                path.reset()
                path.moveTo(x, y - bw * 0.45f)
                path.lineTo(x + bw * 0.35f, y)
                path.lineTo(x, y + bw * 0.45f)
                path.lineTo(x - bw * 0.35f, y)
                path.close()
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawOval(x - bw * 0.12f, y - bw * 0.12f, x + bw * 0.12f, y + bw * 0.12f, fill.apply { color = col })
            }
            "star-forge", "custom-50", "49", "boss49" -> { // #50 STAR-FORGE (ID: star-forge)
                // Rotating 5-point pentagram star with orbiting rune nodes
                path.reset()
                for (step in 0 until 5) {
                    val aOuter = (step * 4 * PI / 5 - PI / 2).toFloat()
                    val px = x + cos(aOuter) * bw * 0.42f
                    val py = y + sin(aOuter) * bw * 0.42f
                    if (step == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close()
                stroke.color = col; stroke.strokeWidth = 3f; c.drawPath(path, stroke)
                c.drawOval(x - bw * 0.1f, y - bw * 0.1f, x + bw * 0.1f, y + bw * 0.1f, fill.apply { color = col })
                for (sIdx in 0 until 4) {
                    val sa = (sIdx * PI / 2 + 0.4f).toFloat()
                    c.drawRect(x + cos(sa) * bw * 0.48f - 5f, y + sin(sa) * bw * 0.48f - 5f,
                               x + cos(sa) * bw * 0.48f + 5f, y + sin(sa) * bw * 0.48f + 5f, fill.apply { color = col })
                }
            }
            else -> {
                // Fallback to index if passed as number or bossN
                val fallbackIdx = (lowId.replace("boss", "").toIntOrNull() ?: 0) % 50
                // Match fallback index
                when (fallbackIdx) {
                    0 -> c.drawRect(x - bw * 0.3f, y - bw * 0.3f, x + bw * 0.3f, y + bw * 0.3f, fill.apply { color = col })
                    else -> c.drawCircle(x, y, bw * 0.3f, fill.apply { color = col })
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

    fun drawEnemyHealthBar(c: Canvas, x: Float, y: Float, r: Float, hp: Float, maxHp: Float, flash: Boolean) {
        if (hp >= maxHp && maxHp < 40f) return
        val w = (r * 1.8f).coerceIn(24f, 70f)
        val h = 4f
        val bx = x - w / 2f
        val by = y - r - 8f
        val frac = (hp / maxHp.coerceAtLeast(1f)).coerceIn(0f, 1f)
        fill.color = DARK
        c.drawRect(bx, by, bx + w, by + h, fill)
        stroke.color = if (flash) WHITE else GRAY
        stroke.strokeWidth = 1f
        c.drawRect(bx, by, bx + w, by + h, stroke)
        fill.color = if (flash) BLACK else WHITE
        c.drawRect(bx + 1f, by + 1f, bx + 1f + (w - 2f) * frac, by + h - 1f, fill)
    }

    fun drawBossHealthBar(
        c: Canvas,
        cx: Float,
        y: Float,
        w: Float,
        bossTitle: String,
        bossId: String,
        hp: Float,
        maxHp: Float,
        lagHp: Float,
        curPhase: Int,
        totalPhases: Int,
        shieldActive: Boolean,
        shieldTimeLeft: Float,
        flash: Boolean,
        t: Long,
        txt: Paint
    ) {
        val barW = w * 0.72f
        val barH = 14f
        val bx1 = cx - barW / 2f
        val bx2 = cx + barW / 2f
        val by1 = y + 16f
        val by2 = by1 + barH
        val frac = (hp / maxHp.coerceAtLeast(1f)).coerceIn(0f, 1f)
        val lagFrac = (lagHp / maxHp.coerceAtLeast(1f)).coerceIn(0f, 1f)
        val isEnraged = frac < 0.28f || (curPhase >= totalPhases - 1 && totalPhases > 1)

        // 1. Header Text: Boss Title, Phase Pips, and Shield Status
        txt.textAlign = Paint.Align.CENTER
        txt.textSize = 13.5f
        txt.color = if (flash) BLACK else WHITE
        val phasePips = (0 until totalPhases).joinToString(" ") { if (it <= curPhase) "◆" else "◇" }
        c.drawText("[ ${bossTitle.uppercase()} ]  $phasePips", cx, y + 8f, txt)

        // Shield status badge
        val shieldTag = if (shieldActive) "[ 🛡 SHIELD ON: %.1fs ]".format(shieldTimeLeft)
                        else "[ ⚔ VULNERABLE: %.1fs ]".format(shieldTimeLeft)
        txt.textSize = 11f
        txt.color = if (shieldActive) 0xFF00E5FF.toInt() else 0xFF00E676.toInt()
        c.drawText(shieldTag, cx, y + 21f, txt)

        val by1Adj = by1 + 10f
        val by2Adj = by2 + 10f

        // 2. Bar Background Card & Frame
        fill.color = DARK
        c.drawRect(bx1 - 4f, by1Adj - 4f, bx2 + 4f, by2Adj + 4f, fill)
        stroke.color = if (shieldActive) 0xFF00E5FF.toInt() else if (isEnraged && (t / 120) % 2L == 0L) WHITE else GRAY
        stroke.strokeWidth = 2f
        c.drawRect(bx1 - 4f, by1Adj - 4f, bx2 + 4f, by2Adj + 4f, stroke)

        // Chamfered corner accents
        c.drawLine(bx1 - 8f, by1Adj - 4f, bx1 - 4f, by1Adj - 4f, stroke)
        c.drawLine(bx1 - 4f, by1Adj - 8f, bx1 - 4f, by1Adj - 4f, stroke)
        c.drawLine(bx2 + 8f, by1Adj - 4f, bx2 + 4f, by1Adj - 4f, stroke)
        c.drawLine(bx2 + 4f, by1Adj - 8f, bx2 + 4f, by1Adj - 4f, stroke)
        c.drawLine(bx1 - 8f, by2Adj + 4f, bx1 - 4f, by2Adj + 4f, stroke)
        c.drawLine(bx1 - 4f, by2Adj + 8f, bx1 - 4f, by2Adj + 4f, stroke)
        c.drawLine(bx2 + 8f, by2Adj + 4f, bx2 + 4f, by2Adj + 4f, stroke)
        c.drawLine(bx2 + 4f, by2Adj + 8f, bx2 + 4f, by2Adj + 4f, stroke)

        // 3. Lag Fill (Gray trailing bar)
        if (lagFrac > frac) {
            fill.color = GRAY
            c.drawRect(bx1, by1Adj, bx1 + barW * lagFrac, by2Adj, fill)
        }

        // 4. Primary Active Health Fill
        fill.color = if (flash) BLACK else WHITE
        c.drawRect(bx1, by1Adj, bx1 + barW * frac, by2Adj, fill)

        // 5. Phase Divider Ticks
        stroke.color = BLACK
        stroke.strokeWidth = 2f
        for (i in 1 until totalPhases) {
            val tickX = bx1 + barW * (i.toFloat() / totalPhases)
            c.drawLine(tickX, by1Adj, tickX, by2Adj, stroke)
        }

        // 6. Subtitle / Numeric Readout
        txt.textSize = 10.5f
        txt.color = if (isEnraged) WHITE else GRAY
        val hpPct = (frac * 100).toInt()
        val hpStatus = if (isEnraged) "/// CRITICAL STATE ///" else "THREAT LEVEL: HIGH"
        c.drawText("$hpStatus  |  ${hp.toInt()}/${maxHp.toInt()} HP ($hpPct%)", cx, by2Adj + 13f, txt)
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
