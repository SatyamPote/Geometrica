package com.nebulastrike.game

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Procedural alien enemy renderer.
 * Strictly uses sharp angular geometry (prisms, delta wings, razorbacks, chevrons,
 * faceted armor plates, interlocking polygons) so they never look like circular power-ups or coins.
 */
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

        val shapeKind = idx % 15

        when (shapeKind) {
            0 -> { // Slender angular diamond with cockpit slit
                path.reset(); path.moveTo(x, y + s * 1.3f)
                path.lineTo(x + s * 0.6f, y); path.lineTo(x, y - s * 1.2f)
                path.lineTo(x - s * 0.6f, y); path.close()
                c.drawPath(path, fill)
                c.drawRect(x - s * 0.12f, y - s * 0.2f, x + s * 0.12f, y + s * 0.2f, fill.apply { color = inv })
            }
            1 -> { // Curved angular boomerang chevron
                path.reset(); path.moveTo(x, y - s * 0.4f)
                path.lineTo(x + s * 0.95f, y + s * 0.8f); path.lineTo(x + s * 0.65f, y + s * 1.1f)
                path.lineTo(x, y + s * 0.1f); path.lineTo(x - s * 0.65f, y + s * 1.1f)
                path.lineTo(x - s * 0.95f, y + s * 0.8f); path.close()
                c.drawPath(path, fill)
                c.drawRect(x - s * 0.1f, y + s * 0.3f, x + s * 0.1f, y + s * 0.5f, fill.apply { color = inv })
            }
            2 -> { // Sharpened faceted pentagon
                path.reset()
                for (i in 0 until 5) {
                    val a = (i * 2 * PI / 5 + PI / 2).toFloat()
                    val px = x + cos(a) * s * 0.88f; val py = y + sin(a) * s * 0.88f
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close(); c.drawPath(path, fill)
                c.drawRect(x - s * 0.2f, y - s * 0.2f, x + s * 0.2f, y + s * 0.2f, fill.apply { color = bg })
            }
            3 -> { // Hexagonal gun-corsair with side sponsons
                path.reset()
                for (i in 0 until 6) {
                    val a = (i * PI / 3).toFloat()
                    val rad = if (i % 2 == 0) s * 0.95f else s * 0.6f
                    val px = x + cos(a) * rad; val py = y + sin(a) * rad
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close(); c.drawPath(path, fill)
                c.drawRect(x - s * 0.18f, y - s * 0.18f, x + s * 0.18f, y + s * 0.18f, fill.apply { color = bg })
            }
            4 -> { // 5-Point Angular Star Strike Fighter
                path.reset()
                for (i in 0 until 10) {
                    val a = (i * PI / 5 - PI / 2).toFloat()
                    val rad = if (i % 2 == 0) s * 1.05f else s * 0.42f
                    val px = x + cos(a) * rad; val py = y + sin(a) * rad
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close(); c.drawPath(path, fill)
                c.drawRect(x - s * 0.15f, y - s * 0.15f, x + s * 0.15f, y + s * 0.15f, fill.apply { color = inv })
            }
            5 -> { // Asymmetrical stealth stealth-wing
                path.reset(); path.moveTo(x - s * 0.75f, y - s * 0.5f)
                path.lineTo(x + s * 0.65f, y - s * 0.7f); path.lineTo(x + s * 0.9f, y + s * 0.2f)
                path.lineTo(x + s * 0.35f, y + s * 0.95f); path.lineTo(x - s * 0.7f, y + s * 0.65f); path.close()
                c.drawPath(path, fill)
                c.drawRect(x - s * 0.15f, y - s * 0.15f, x + s * 0.25f, y + s * 0.25f, fill.apply { color = bg })
            }
            6 -> { // Interlocking angular brackets
                stroke.strokeWidth = 3f; stroke.color = col
                c.drawRect(x - s * 0.6f, y - s * 0.6f, x + s * 0.2f, y + s * 0.2f, stroke)
                c.drawRect(x - s * 0.2f, y - s * 0.2f, x + s * 0.6f, y + s * 0.6f, stroke)
                c.drawRect(x - s * 0.15f, y - s * 0.15f, x + s * 0.15f, y + s * 0.15f, fill.apply { color = col })
            }
            7 -> { // Stepped chevron blade
                path.reset(); path.moveTo(x, y + s * 1.3f)
                path.lineTo(x + s * 0.8f, y - s * 0.3f); path.lineTo(x + s * 0.5f, y - s * 0.3f)
                path.lineTo(x + s * 0.7f, y - s * 0.9f); path.lineTo(x, y - s * 0.5f)
                path.lineTo(x - s * 0.7f, y - s * 0.9f); path.lineTo(x - s * 0.5f, y - s * 0.3f)
                path.lineTo(x - s * 0.8f, y - s * 0.3f); path.close()
                c.drawPath(path, fill)
            }
            8 -> { // Long spearhead needle
                c.drawRect(x - s * 0.15f, y - s * 1.3f, x + s * 0.15f, y + s * 1.3f, fill)
                path.reset(); path.moveTo(x, y + s * 1.55f)
                path.lineTo(x + s * 0.45f, y + s * 0.7f); path.lineTo(x - s * 0.45f, y + s * 0.7f); path.close()
                c.drawPath(path, fill)
                c.drawRect(x - s * 0.6f, y - s * 0.2f, x + s * 0.6f, y + s * 0.1f, fill)
            }
            9 -> { // Swept angular crescent sickle
                path.reset()
                path.moveTo(x, y + s * 1.2f)
                path.lineTo(x + s * 0.8f, y)
                path.lineTo(x + s * 0.3f, y - s * 0.8f)
                path.lineTo(x - s * 0.3f, y - s * 0.8f)
                path.lineTo(x - s * 0.8f, y)
                path.lineTo(x, y + s * 0.5f)
                path.close()
                c.drawPath(path, fill)
                c.drawRect(x - s * 0.15f, y - s * 0.2f, x + s * 0.15f, y + s * 0.1f, fill.apply { color = bg })
            }
            10 -> { // Downward heavy wedge triangle
                path.reset(); path.moveTo(x, y + s * 1.35f)
                path.lineTo(x + s * 0.9f, y - s * 0.85f); path.lineTo(x - s * 0.9f, y - s * 0.85f); path.close()
                c.drawPath(path, fill)
                c.drawRect(x - s * 0.2f, y - s * 0.2f, x + s * 0.2f, y + s * 0.2f, fill.apply { color = inv })
            }
            11 -> { // Stepped faceted serpent segment
                c.drawRect(x - s * 0.4f, y - s * 0.9f, x + s * 0.4f, y - s * 0.35f, fill)
                c.drawRect(x - s * 0.65f, y - s * 0.3f, x + s * 0.65f, y + s * 0.25f, fill)
                c.drawRect(x - s * 0.35f, y + s * 0.3f, x + s * 0.35f, y + s * 0.85f, fill)
                c.drawRect(x - s * 0.12f, y - s * 0.1f, x + s * 0.12f, y + s * 0.1f, fill.apply { color = bg })
            }
            12 -> { // Dual counter-rotating angular brackets
                stroke.strokeWidth = 3f; stroke.color = col
                c.drawRect(x - s * 0.7f, y - s * 0.7f, x + s * 0.7f, y + s * 0.7f, stroke)
                c.drawRect(x - s * 0.35f, y - s * 0.35f, x + s * 0.35f, y + s * 0.35f, stroke)
                c.drawRect(x - s * 0.15f, y - s * 0.15f, x + s * 0.15f, y + s * 0.15f, fill.apply { color = col })
            }
            13 -> { // Octagonal Dread Fortress
                path.reset()
                for (i in 0 until 8) {
                    val a = (i * PI / 4).toFloat()
                    val px = x + cos(a) * s * 0.85f; val py = y + sin(a) * s * 0.85f
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close(); c.drawPath(path, fill)
                c.drawRect(x - s * 0.28f, y - s * 0.28f, x + s * 0.28f, y + s * 0.28f, fill.apply { color = bg })
            }
            else -> { // Cross-Raider interceptor
                c.drawRect(x - s * 0.95f, y - s * 0.25f, x + s * 0.95f, y + s * 0.25f, fill)
                c.drawRect(x - s * 0.25f, y - s * 0.9f, x + s * 0.25f, y + s * 0.9f, fill)
                c.drawRect(x - s * 0.2f, y - s * 0.2f, x + s * 0.2f, y + s * 0.2f, fill.apply { color = bg })
            }
        }
        if (shieldUp) {
            stroke.strokeWidth = 3f; stroke.color = col
            // Hexagonal barrier shield instead of circle
            path.reset()
            for (i in 0 until 6) {
                val a = (i * PI / 3).toFloat()
                val px = x + cos(a) * s * 1.35f; val py = y + sin(a) * s * 1.35f
                if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
            }
            path.close()
            c.drawPath(path, stroke)
        }
    }
}