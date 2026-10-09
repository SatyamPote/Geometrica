package com.nebulastrike.game

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-definition procedural renderer for the 7 specialized player starships,
 * including their dual side-by-side companion drone attachments, continuous lasers, and missile pods.
 */
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

        // Engine exhaust plumes
        fill.color = col
        c.drawRect(x - s * 0.14f, y + s * 0.75f, x + s * 0.14f, y + s * 0.75f + s * 0.55f * f, fill)
        fill.color = inv
        c.drawRect(x - s * 0.07f, y + s * 1.05f, x + s * 0.07f, y + s * 1.05f + s * 0.35f * f, fill)

        val idx = shipIdx.coerceIn(0, 6)
        fill.color = col; stroke.color = col; stroke.strokeWidth = 2.5f

        when (idx) {
            0 -> {
                // #1: STAR-BLASTER (Vanguard Needle-Dart with Wingtip Micro-Missile Pods)
                path.reset(); path.moveTo(x, y - s * 1.35f)
                path.lineTo(x + s * 0.45f, y + s * 0.65f)
                path.lineTo(x, y + s * 0.4f)
                path.lineTo(x - s * 0.45f, y + s * 0.65f); path.close()
                c.drawPath(path, fill)
                // Missile pods on wings
                c.drawRect(x - s * 0.55f, y + s * 0.2f, x - s * 0.4f, y + s * 0.6f, fill)
                c.drawRect(x + s * 0.4f, y + s * 0.2f, x + s * 0.55f, y + s * 0.6f, fill)
                // Cockpit slit
                c.drawRect(x - s * 0.08f, y - s * 0.35f, x + s * 0.08f, y + s * 0.1f, fill.apply { color = bg })
            }
            1 -> {
                // #2: ION-LANCER (Continuous Beam Rail Destroyer with Front Emitter Prisms)
                path.reset(); path.moveTo(x, y - s * 1.45f)
                path.lineTo(x + s * 0.22f, y - s * 0.5f)
                path.lineTo(x + s * 0.65f, y + s * 0.65f)
                path.lineTo(x + s * 0.18f, y + s * 0.45f)
                path.lineTo(x - s * 0.18f, y + s * 0.45f)
                path.lineTo(x - s * 0.65f, y + s * 0.65f)
                path.lineTo(x - s * 0.22f, y - s * 0.5f); path.close()
                c.drawPath(path, fill)
                // Core emitter barrel
                c.drawRect(x - s * 0.08f, y - s * 1.45f, x + s * 0.08f, y + s * 0.2f, fill.apply { color = bg })
                c.drawCircle(x, y - s * 0.15f, s * 0.14f, fill.apply { color = col })
            }
            2 -> {
                // #3: VALKYRIE-WING (Squadron Command + Dual Side-by-Side Drone Attachments)
                // Main Command Hull
                path.reset(); path.moveTo(x, y - s * 1.25f)
                path.lineTo(x + s * 0.55f, y + s * 0.6f)
                path.lineTo(x, y + s * 0.35f)
                path.lineTo(x - s * 0.55f, y + s * 0.6f); path.close()
                c.drawPath(path, fill)
                c.drawCircle(x, y - s * 0.1f, s * 0.16f, fill.apply { color = bg })

                // Drone 1 (Left side attachment)
                val d1x = x - s * 1.15f; val d1y = y + s * 0.15f
                drawCompanionDrone(c, d1x, d1y, s * 0.42f, t)
                // Energy tether to main ship
                stroke.color = inv; stroke.strokeWidth = 1.5f
                c.drawLine(x - s * 0.2f, y, d1x, d1y, stroke)

                // Drone 2 (Right side attachment)
                val d2x = x + s * 1.15f; val d2y = y + s * 0.15f
                drawCompanionDrone(c, d2x, d2y, s * 0.42f, t)
                c.drawLine(x + s * 0.2f, y, d2x, d2y, stroke)
            }
            3 -> {
                // #4: NOVA-BARRAGE (Heavy Armored Missile Artillery Platform)
                path.reset(); path.moveTo(x - s * 0.25f, y - s * 1.1f)
                path.lineTo(x + s * 0.25f, y - s * 1.1f)
                path.lineTo(x + s * 0.85f, y + s * 0.7f)
                path.lineTo(x + s * 0.35f, y + s * 0.55f)
                path.lineTo(x - s * 0.35f, y + s * 0.55f)
                path.lineTo(x - s * 0.85f, y + s * 0.7f); path.close()
                c.drawPath(path, fill)
                // Quad Rocket Launch Tubes
                c.drawRect(x - s * 0.7f, y - s * 0.2f, x - s * 0.5f, y + s * 0.5f, fill.apply { color = bg })
                c.drawRect(x - s * 0.45f, y - s * 0.35f, x - s * 0.25f, y + s * 0.4f, fill.apply { color = bg })
                c.drawRect(x + s * 0.25f, y - s * 0.35f, x + s * 0.45f, y + s * 0.4f, fill.apply { color = bg })
                c.drawRect(x + s * 0.5f, y - s * 0.2f, x + s * 0.7f, y + s * 0.5f, fill.apply { color = bg })
            }
            4 -> {
                // #5: TEMPEST-TITAN (Electric Arc Vanguard with Dual Tesla Coils)
                path.reset()
                for (i in 0 until 6) {
                    val a = (i * PI / 3 - PI / 2).toFloat()
                    val px = x + cos(a) * s * 0.9f; val py = y + sin(a) * s * 0.9f
                    if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close(); c.drawPath(path, fill)
                // Tesla Emitter Coils
                c.drawCircle(x, y, s * 0.38f, fill.apply { color = bg })
                c.drawCircle(x, y, s * 0.2f, fill.apply { color = col })
                c.drawRect(x - s * 0.95f, y - s * 0.4f, x - s * 0.8f, y + s * 0.2f, fill)
                c.drawRect(x + s * 0.8f, y - s * 0.4f, x + s * 0.95f, y + s * 0.2f, fill)
            }
            5 -> {
                // #6: CHRONO-PHANTOM (Stealth Tachyon Mirage with Dual Stealth Escorts)
                path.reset(); path.moveTo(x, y - s * 1.4f)
                path.lineTo(x + s * 0.8f, y + s * 0.5f)
                path.lineTo(x + s * 0.4f, y + s * 0.8f)
                path.lineTo(x, y + s * 0.5f)
                path.lineTo(x - s * 0.4f, y + s * 0.8f)
                path.lineTo(x - s * 0.8f, y + s * 0.5f); path.close()
                c.drawPath(path, fill)
                c.drawRect(x - s * 0.1f, y - s * 0.4f, x + s * 0.1f, y + s * 0.2f, fill.apply { color = bg })

                // Stealth Companion Drones
                val d1x = x - s * 1.1f; val d1y = y + s * 0.2f
                drawCompanionDrone(c, d1x, d1y, s * 0.38f, t)
                val d2x = x + s * 1.1f; val d2y = y + s * 0.2f
                drawCompanionDrone(c, d2x, d2y, s * 0.38f, t)
            }
            6 -> {
                // #7: OMEGA-DREADNOUGHT (Ultimate Flagship + Core Beam + Dual Flanking Gunships)
                path.reset(); path.moveTo(x, y - s * 1.5f)
                path.lineTo(x + s * 0.35f, y - s * 0.6f)
                path.lineTo(x + s * 0.95f, y + s * 0.7f)
                path.lineTo(x + s * 0.4f, y + s * 0.5f)
                path.lineTo(x, y + s * 0.35f)
                path.lineTo(x - s * 0.4f, y + s * 0.5f)
                path.lineTo(x - s * 0.95f, y + s * 0.7f)
                path.lineTo(x - s * 0.35f, y - s * 0.6f); path.close()
                c.drawPath(path, fill)
                // Core Disintegrator chamber
                c.drawCircle(x, y - s * 0.2f, s * 0.28f, fill.apply { color = bg })
                c.drawCircle(x, y - s * 0.2f, s * 0.14f, fill.apply { color = col })

                // Heavy Gunship Companions flanking left and right
                val d1x = x - s * 1.35f; val d1y = y + s * 0.1f
                drawHeavyGunship(c, d1x, d1y, s * 0.48f, t)
                stroke.color = col; stroke.strokeWidth = 2f
                c.drawLine(x - s * 0.4f, y + s * 0.2f, d1x, d1y, stroke)

                val d2x = x + s * 1.35f; val d2y = y + s * 0.1f
                drawHeavyGunship(c, d2x, d2y, s * 0.48f, t)
                c.drawLine(x + s * 0.4f, y + s * 0.2f, d2x, d2y, stroke)
            }
        }
    }

    private fun drawCompanionDrone(c: Canvas, dx: Float, dy: Float, ds: Float, t: Long) {
        val f = if ((t / 90) % 2L == 0L) 1f else 0.6f
        fill.color = WHITE
        // Drone engine plume
        c.drawRect(dx - ds * 0.2f, dy + ds * 0.6f, dx + ds * 0.2f, dy + ds * 0.6f + ds * 0.6f * f, fill)
        // Drone body
        path.reset(); path.moveTo(dx, dy - ds * 1.2f)
        path.lineTo(dx + ds * 0.8f, dy + ds * 0.6f)
        path.lineTo(dx, dy + ds * 0.3f)
        path.lineTo(dx - ds * 0.8f, dy + ds * 0.6f); path.close()
        c.drawPath(path, fill)
        c.drawCircle(dx, dy, ds * 0.25f, fill.apply { color = BLACK })
    }

    private fun drawHeavyGunship(c: Canvas, dx: Float, dy: Float, ds: Float, t: Long) {
        val f = if ((t / 90) % 2L == 0L) 1f else 0.6f
        fill.color = WHITE
        c.drawRect(dx - ds * 0.3f, dy + ds * 0.65f, dx + ds * 0.3f, dy + ds * 0.65f + ds * 0.7f * f, fill)
        path.reset(); path.moveTo(dx, dy - ds * 1.3f)
        path.lineTo(dx + ds * 0.9f, dy + ds * 0.5f)
        path.lineTo(dx + ds * 0.3f, dy + ds * 0.8f)
        path.lineTo(dx - ds * 0.3f, dy + ds * 0.8f)
        path.lineTo(dx - ds * 0.9f, dy + ds * 0.5f); path.close()
        c.drawPath(path, fill)
        c.drawRect(dx - ds * 0.15f, dy - ds * 0.3f, dx + ds * 0.15f, dy + ds * 0.2f, fill.apply { color = BLACK })
    }
}