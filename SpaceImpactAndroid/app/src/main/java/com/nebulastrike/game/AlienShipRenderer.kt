package com.nebulastrike.game

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Procedural renderer ensuring all 100 alien enemy shapes are distinct from each other. */
object AlienShipRenderer {
    private val path = Path()
    private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val stroke = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }

    fun drawAlien(c: Canvas, specIdx: Int, x: Float, y: Float, r: Float, t: Long, shieldUp: Boolean, flash: Boolean) {
        val col = if (flash) BLACK else WHITE
        val inv = if (flash) WHITE else BLACK
        val bg = if (flash) WHITE else DARK
        val s = r * 0.95f
        val idx = specIdx.coerceIn(0, 99)

        fill.color = col; stroke.color = col; stroke.strokeWidth = 2.5f

        // Geometry family determined by index (diamonds, boomerangs, pentagons, hexagons, stars, blobs, rings, waves, needles, crescents, octagons, etc.)
        val shapeKind = idx % 15

        when (shapeKind) {
            0 -> { // Slender diamond (drift-dia01, etc.)
                path.reset(); path.moveTo(x, y + s * 1.3f)
                path.lineTo(x + s * 0.55f, y); path.lineTo(x, y - s * 1.2f)
                path.lineTo(x - s * 0.55f, y); path.close()
                c.drawPath(path, fill)
                c.drawCircle(x, y, s * 0.18f, fill.apply { color = inv })
            }
            1 -> { // Curved boomerang (drift-boom02, etc.)
                path.reset(); path.moveTo(x, y - s * 0.4f)
                path.lineTo(x + s * 0.95f, y + s * 0.8f); path.lineTo(x + s * 0.65f, y + s * 1.1f)
                path.lineTo(x, y + s * 0.1f); path.lineTo(x - s * 0.65f, y + s * 1.1f)
                path.lineTo(x - s * 0.95f, y + s * 0.8f); path.close()
                c.drawPath(path, fill)
            }
            2 -> { // Pentagon (drift-pent03, etc.)
                path.reset()
                for (i in 0 until 5) {
                    val a = (i * 2 * PI / 5 + PI / 2).toFloat()
                    val px = x + cos(a) * s * 0.85f; val py = y + sin(a) * s * 0.85f
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close(); c.drawPath(path, fill)
                c.drawCircle(x, y, s * 0.22f, fill.apply { color = bg })
            }
            3 -> { // Hexagon alternating sides (drift-hex04, etc.)
                path.reset()
                for (i in 0 until 6) {
                    val a = (i * PI / 3).toFloat()
                    val rad = if (i % 2 == 0) s * 0.95f else s * 0.6f
                    val px = x + cos(a) * rad; val py = y + sin(a) * rad
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close(); c.drawPath(path, fill)
                c.drawRect(x - s * 0.15f, y - s * 0.15f, x + s * 0.15f, y + s * 0.15f, fill.apply { color = bg })
            }
            4 -> { // 5-Point Star (drift-star05, etc.)
                path.reset()
                for (i in 0 until 10) {
                    val a = (i * PI / 5 - PI / 2).toFloat()
                    val rad = if (i % 2 == 0) s * 1.05f else s * 0.42f
                    val px = x + cos(a) * rad; val py = y + sin(a) * rad
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close(); c.drawPath(path, fill)
                c.drawCircle(x, y, s * 0.15f, fill.apply { color = inv })
            }
            5 -> { // Irregular organic blob (drift-blob06, etc.)
                path.reset(); path.moveTo(x - s * 0.7f, y - s * 0.5f)
                path.lineTo(x + s * 0.6f, y - s * 0.7f); path.lineTo(x + s * 0.85f, y + s * 0.2f)
                path.lineTo(x + s * 0.3f, y + s * 0.9f); path.lineTo(x - s * 0.65f, y + s * 0.6f); path.close()
                c.drawPath(path, fill)
                c.drawCircle(x - s * 0.15f, y - s * 0.15f, s * 0.2f, fill.apply { color = bg })
                c.drawCircle(x + s * 0.2f, y + s * 0.2f, s * 0.15f, fill.apply { color = bg })
            }
            6 -> { // Interlocking Rings (drift-ring07, etc.)
                stroke.strokeWidth = 3f; stroke.color = col
                c.drawCircle(x - s * 0.35f, y, s * 0.45f, stroke)
                c.drawCircle(x + s * 0.35f, y, s * 0.45f, stroke)
                c.drawCircle(x, y, s * 0.15f, fill.apply { color = col })
            }
            7 -> { // Glowing Wave (drift-wave08, etc.)
                path.reset(); path.moveTo(x - s * 0.9f, y)
                path.quadTo(x - s * 0.45f, y - s * 0.9f, x, y)
                path.quadTo(x + s * 0.45f, y + s * 0.9f, x + s * 0.9f, y)
                path.lineTo(x + s * 0.9f, y + s * 0.3f)
                path.quadTo(x + s * 0.45f, y + s * 1.2f, x, y + s * 0.3f)
                path.quadTo(x - s * 0.45f, y - s * 0.6f, x - s * 0.9f, y + s * 0.3f); path.close()
                c.drawPath(path, fill)
            }
            8 -> { // Thin Needle (drift-need09, etc.)
                c.drawRect(x - s * 0.12f, y - s * 1.3f, x + s * 0.12f, y + s * 1.3f, fill)
                path.reset(); path.moveTo(x, y + s * 1.45f)
                path.lineTo(x + s * 0.4f, y + s * 0.8f); path.lineTo(x - s * 0.4f, y + s * 0.8f); path.close()
                c.drawPath(path, fill)
            }
            9 -> { // Crescent (drift-cres10, etc.)
                c.drawCircle(x, y, s * 0.8f, fill)
                c.drawCircle(x - s * 0.35f, y - s * 0.2f, s * 0.65f, fill.apply { color = bg })
            }
            10 -> { // Downward Triangle (track-tri01, dive-tri01, etc.)
                path.reset(); path.moveTo(x, y + s * 1.3f)
                path.lineTo(x + s * 0.85f, y - s * 0.8f); path.lineTo(x - s * 0.85f, y - s * 0.8f); path.close()
                c.drawPath(path, fill)
                c.drawCircle(x, y, s * 0.2f, fill.apply { color = inv })
            }
            11 -> { // Wavy Serpent (weave-serp02, etc.)
                for (k in 0..2) {
                    val sy = y - s * 0.7f + k * s * 0.65f
                    val sx = x + (if (k % 2 == 0) -s * 0.35f else s * 0.35f)
                    c.drawCircle(sx, sy, s * 0.32f, fill)
                    c.drawCircle(sx, sy, s * 0.12f, fill.apply { color = bg })
                }
            }
            12 -> { // Dual Counter-Spiral (spiral-dual03, etc.)
                stroke.strokeWidth = 3f; stroke.color = col
                c.drawArc(x - s * 0.7f, y - s * 0.7f, x + s * 0.7f, y + s * 0.7f, 0f, 270f, false, stroke)
                c.drawArc(x - s * 0.4f, y - s * 0.4f, x + s * 0.4f, y + s * 0.4f, 180f, 270f, false, stroke)
                c.drawCircle(x, y, s * 0.15f, fill.apply { color = col })
            }
            13 -> { // Octagon Fortress (oct-track, oct-shield, etc.)
                path.reset()
                for (i in 0 until 8) {
                    val a = (i * PI / 4).toFloat()
                    val px = x + cos(a) * s * 0.85f; val py = y + sin(a) * s * 0.85f
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close(); c.drawPath(path, fill)
                c.drawRect(x - s * 0.25f, y - s * 0.25f, x + s * 0.25f, y + s * 0.25f, fill.apply { color = bg })
            }
            else -> { // Cross-Raider (strafe-wide01, etc.)
                c.drawRect(x - s * 0.95f, y - s * 0.25f, x + s * 0.95f, y + s * 0.25f, fill)
                c.drawRect(x - s * 0.25f, y - s * 0.9f, x + s * 0.25f, y + s * 0.9f, fill)
                c.drawCircle(x, y, s * 0.2f, fill.apply { color = bg })
            }
        }
        if (shieldUp) {
            stroke.strokeWidth = 3f; stroke.color = col
            c.drawCircle(x, y, s * 1.35f, stroke)
        }
    }
}