package com.nebulastrike.game

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Procedural renderer for all 100 distinct player spaceships. */
object PlayerShipRenderer {
    private val path = Path()
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }

    fun drawShip(c: Canvas, shipIdx: Int, x: Float, y: Float, s: Float, t: Long, blink: Boolean) {
        if (blink && (t / 80) % 2L == 0L) return
        val col = WHITE
        val bg = BLACK
        val inv = GRAY
        val f = if ((t / 90) % 2L == 0L) 1f else 0.6f
        // Engine plume
        fill.color = col
        c.drawRect(x - s * 0.14f, y + s * 0.75f, x + s * 0.14f, y + s * 0.75f + s * 0.5f * f, fill)
        fill.color = inv
        c.drawRect(x - s * 0.07f, y + s * 1.05f, x + s * 0.07f, y + s * 1.05f + s * 0.35f * f, fill)

        val idx = shipIdx.coerceIn(0, 99)
        val archetype = idx % 20
        val tier = idx / 20

        fill.color = col; stroke.color = col; stroke.strokeWidth = 2.5f

        when (archetype) {
            0 -> { // Interceptor (needle dart with swept strakes)
                path.reset(); path.moveTo(x, y - s * 1.3f)
                path.lineTo(x + s * (0.35f + tier * 0.06f), y + s * 0.6f)
                path.lineTo(x, y + s * 0.4f)
                path.lineTo(x - s * (0.35f + tier * 0.06f), y + s * 0.6f); path.close()
                c.drawPath(path, fill)
                c.drawCircle(x, y - s * 0.15f, s * 0.15f, fill.apply { color = bg })
            }
            1 -> { // Striker (delta wing with forward canards)
                path.reset(); path.moveTo(x, y - s * 1.1f)
                path.lineTo(x + s * 0.75f, y + s * 0.7f); path.lineTo(x + s * 0.25f, y + s * 0.5f)
                path.lineTo(x, y + s * 0.7f); path.lineTo(x - s * 0.25f, y + s * 0.5f)
                path.lineTo(x - s * 0.75f, y + s * 0.7f); path.close()
                c.drawPath(path, fill)
                c.drawRect(x - s * 0.12f, y - s * 0.2f, x + s * 0.12f, y + s * 0.25f, fill.apply { color = bg })
            }
            2 -> { // Defender (heavy hexagonal plated dreadnought)
                path.reset()
                for (i in 0 until 6) {
                    val a = (i * PI / 3 - PI / 2).toFloat()
                    val px = x + cos(a) * s * 0.85f; val py = y + sin(a) * s * 0.85f
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close(); c.drawPath(path, fill)
                c.drawCircle(x, y, s * 0.35f, fill.apply { color = bg })
                c.drawCircle(x, y, s * 0.18f, fill.apply { color = col })
            }
            3 -> { // Raider (asymmetric high-agility stealth wing)
                path.reset(); path.moveTo(x, y - s * 1.2f)
                path.lineTo(x + s * 0.85f, y + s * 0.4f); path.lineTo(x + s * 0.3f, y + s * 0.8f)
                path.lineTo(x - s * 0.45f, y + s * 0.7f); path.lineTo(x - s * 0.85f, y + s * 0.2f); path.close()
                c.drawPath(path, fill)
                c.drawRect(x - s * 0.1f, y - s * 0.15f, x + s * 0.1f, y + s * 0.25f, fill.apply { color = bg })
            }
            4 -> { // Bomber (twin fuselage heavy platform)
                c.drawRect(x - s * 0.75f, y - s * 0.8f, x - s * 0.25f, y + s * 0.8f, fill)
                c.drawRect(x + s * 0.25f, y - s * 0.8f, x + s * 0.75f, y + s * 0.8f, fill)
                c.drawRect(x - s * 0.35f, y - s * 0.2f, x + s * 0.35f, y + s * 0.3f, fill)
                c.drawCircle(x, y - s * 0.4f, s * 0.25f, fill.apply { color = col })
                c.drawCircle(x, y - s * 0.4f, s * 0.12f, fill.apply { color = bg })
            }
            5 -> { // Scout (sleek diamond lance)
                path.reset(); path.moveTo(x, y - s * 1.4f)
                path.lineTo(x + s * 0.5f, y); path.lineTo(x, y + s * 0.9f)
                path.lineTo(x - s * 0.5f, y); path.close()
                c.drawPath(path, fill)
                c.drawLine(x, y - s * 1.2f, x, y + s * 0.7f, stroke.apply { color = bg })
            }
            6 -> { // Vanguard (cruciform star striker)
                c.drawRect(x - s * 0.18f, y - s * 1.2f, x + s * 0.18f, y + s * 0.9f, fill)
                c.drawRect(x - s * 0.85f, y - s * 0.2f, x + s * 0.85f, y + s * 0.3f, fill)
                c.drawCircle(x, y, s * 0.25f, fill.apply { color = bg })
                c.drawCircle(x, y, s * 0.12f, fill.apply { color = col })
            }
            7 -> { // Extinguisher (forked twin-nose cruiser)
                path.reset(); path.moveTo(x - s * 0.5f, y - s * 1.1f)
                path.lineTo(x - s * 0.2f, y - s * 0.2f); path.lineTo(x + s * 0.2f, y - s * 0.2f)
                path.lineTo(x + s * 0.5f, y - s * 1.1f); path.lineTo(x + s * 0.65f, y + s * 0.7f)
                path.lineTo(x - s * 0.65f, y + s * 0.7f); path.close()
                c.drawPath(path, fill)
                c.drawCircle(x, y + s * 0.15f, s * 0.2f, fill.apply { color = bg })
            }
            8 -> { // Pursuer (triple-fin predator)
                path.reset(); path.moveTo(x, y - s * 1.25f)
                path.lineTo(x + s * 0.3f, y + s * 0.6f); path.lineTo(x + s * 0.85f, y + s * 0.9f)
                path.lineTo(x, y + s * 0.4f)
                path.lineTo(x - s * 0.85f, y + s * 0.9f); path.lineTo(x - s * 0.3f, y + s * 0.6f); path.close()
                c.drawPath(path, fill)
                c.drawRect(x - s * 0.1f, y - s * 0.3f, x + s * 0.1f, y + s * 0.1f, fill.apply { color = bg })
            }
            9 -> { // Annihilator (heavy hammerhead fortress)
                c.drawRect(x - s * 0.85f, y - s * 1.1f, x + s * 0.85f, y - s * 0.5f, fill)
                c.drawRect(x - s * 0.35f, y - s * 0.5f, x + s * 0.35f, y + s * 0.8f, fill)
                c.drawCircle(x, y - s * 0.8f, s * 0.18f, fill.apply { color = bg })
                c.drawCircle(x, y + s * 0.15f, s * 0.18f, fill.apply { color = bg })
            }
            10 -> { // Guardian (circular aegis with quad strakes)
                c.drawCircle(x, y, s * 0.7f, fill)
                c.drawCircle(x, y, s * 0.45f, fill.apply { color = bg })
                c.drawCircle(x, y, s * 0.22f, fill.apply { color = col })
                c.drawRect(x - s * 0.15f, y - s * 1.2f, x + s * 0.15f, y - s * 0.7f, fill.apply { color = col })
            }
            11 -> { // Interceptor-Elite (extended needle with dual ring boosters)
                path.reset(); path.moveTo(x, y - s * 1.45f)
                path.lineTo(x + s * 0.65f, y + s * 0.7f); path.lineTo(x, y + s * 0.4f)
                path.lineTo(x - s * 0.65f, y + s * 0.7f); path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawCircle(x - s * 0.35f, y + s * 0.4f, s * 0.18f, fill.apply { color = bg })
                c.drawCircle(x + s * 0.35f, y + s * 0.4f, s * 0.18f, fill.apply { color = bg })
            }
            12 -> { // Striker-Elite (forward swept predatory hawk)
                path.reset(); path.moveTo(x, y - s * 0.8f)
                path.lineTo(x + s * 0.95f, y - s * 0.3f); path.lineTo(x + s * 0.4f, y + s * 0.8f)
                path.lineTo(x, y + s * 0.35f); path.lineTo(x - s * 0.4f, y + s * 0.8f)
                path.lineTo(x - s * 0.95f, y - s * 0.3f); path.close()
                c.drawPath(path, fill.apply { color = col })
                c.drawCircle(x, y - s * 0.15f, s * 0.2f, fill.apply { color = bg })
            }
            13 -> { // Defender-Elite (heavy octagonal bastion)
                path.reset()
                for (i in 0 until 8) {
                    val a = (i * PI / 4 - PI / 8).toFloat()
                    val px = x + cos(a) * s * 0.85f; val py = y + sin(a) * s * 0.85f
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close(); c.drawPath(path, fill.apply { color = col })
                c.drawRect(x - s * 0.25f, y - s * 0.25f, x + s * 0.25f, y + s * 0.25f, fill.apply { color = bg })
            }
            14 -> { // Raider-Elite (multi-tiered stealth prism)
                path.reset(); path.moveTo(x, y - s * 1.3f)
                path.lineTo(x + s * 0.8f, y + s * 0.8f); path.lineTo(x, y + s * 0.4f)
                path.lineTo(x - s * 0.8f, y + s * 0.8f); path.close()
                c.drawPath(path, fill.apply { color = col })
                path.reset(); path.moveTo(x, y - s * 0.7f)
                path.lineTo(x + s * 0.4f, y + s * 0.3f); path.lineTo(x, y + s * 0.1f)
                path.lineTo(x - s * 0.4f, y + s * 0.3f); path.close()
                c.drawPath(path, fill.apply { color = bg })
            }
            15 -> { // Bomber-Elite (triple sponson dread-bomber)
                c.drawRect(x - s * 0.85f, y - s * 0.7f, x - s * 0.45f, y + s * 0.8f, fill.apply { color = col })
                c.drawRect(x + s * 0.45f, y - s * 0.7f, x + s * 0.85f, y + s * 0.8f, fill.apply { color = col })
                c.drawRect(x - s * 0.25f, y - s * 1.1f, x + s * 0.25f, y + s * 0.85f, fill.apply { color = col })
                c.drawCircle(x, y - s * 0.4f, s * 0.15f, fill.apply { color = bg })
            }
            16 -> { // Scout-Elite (tri-ring focal explorer)
                c.drawCircle(x, y - s * 0.5f, s * 0.35f, fill.apply { color = col })
                c.drawCircle(x - s * 0.45f, y + s * 0.35f, s * 0.35f, fill.apply { color = col })
                c.drawCircle(x + s * 0.45f, y + s * 0.35f, s * 0.35f, fill.apply { color = col })
                c.drawCircle(x, y, s * 0.2f, fill.apply { color = bg })
            }
            17 -> { // Vanguard-Elite (segmented chevron spearhead)
                for (k in 0..2) {
                    val cy = y - s * 0.8f + k * s * 0.65f
                    path.reset(); path.moveTo(x, cy - s * 0.4f)
                    path.lineTo(x + s * 0.65f, cy + s * 0.3f); path.lineTo(x, cy + s * 0.1f)
                    path.lineTo(x - s * 0.65f, cy + s * 0.3f); path.close()
                    c.drawPath(path, fill.apply { color = if (k == 1) bg else col })
                }
            }
            18 -> { // Extinguisher-Elite (winged caduceus medical cruiser)
                c.drawRect(x - s * 0.18f, y - s * 1.2f, x + s * 0.18f, y + s * 0.9f, fill.apply { color = col })
                c.drawRect(x - s * 0.7f, y - s * 0.4f, x + s * 0.7f, y - s * 0.05f, fill.apply { color = col })
                c.drawCircle(x, y - s * 0.8f, s * 0.22f, fill.apply { color = col })
                c.drawCircle(x, y - s * 0.8f, s * 0.1f, fill.apply { color = bg })
            }
            else -> { // Pursuer-Elite (cosmic stellar sovereign)
                path.reset()
                for (i in 0 until 5) {
                    val a = (i * 4 * PI / 5 - PI / 2).toFloat()
                    val px = x + cos(a) * s * 0.95f; val py = y + sin(a) * s * 0.95f
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close(); c.drawPath(path, fill.apply { color = col })
                c.drawCircle(x, y, s * 0.25f, fill.apply { color = bg })
                c.drawCircle(x, y, s * 0.1f, fill.apply { color = col })
            }
        }
    }
}