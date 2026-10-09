package com.nebulastrike.game

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.SystemClock
import android.view.Choreographer
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import kotlin.math.min
import kotlin.random.Random

class GameView(context: Context, private val save: Save, private val sound: Sound) :
    View(context), WorldListener {

    companion object {
        const val BUILD_TAG = "v14"
    }

    enum class State { TITLE, PLAY, PAUSE, OVER, END }

    var state = State.TITLE
        private set

    private val world = World(save, sound)
    private val density = resources.displayMetrics.density
    private fun dp(v: Float) = v * density

    private val txt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.MONOSPACE
        color = WHITE
    }
    private val fill = Paint().apply { isAntiAlias = false }

    private class Star(var x: Float, var y: Float, val layer: Int, val size: Float)
    private val stars = mutableListOf<Star>()
    private val rnd = Random(7)
    private var scanBmp: Bitmap? = null

    private val keys = mutableSetOf<Int>()
    private var moveId = -1
    private var lastTX = 0f
    private var lastTY = 0f
    private var moveInit = false
    private val fireIds = mutableSetOf<Int>()

    private var running = false
    private var lastFrame = 0L

    private val frameCb = object : Choreographer.FrameCallback {
        override fun doFrame(t: Long) {
            if (!running) return
            Choreographer.getInstance().postFrameCallback(this)
            val now = SystemClock.uptimeMillis()
            val dt = if (lastFrame == 0L) 0.016f else ((now - lastFrame) / 1000f).coerceIn(0f, 0.05f)
            lastFrame = now
            if (state == State.PLAY) {
                applyKeys()
                world.update(dt)
            }
            invalidate()
        }
    }

    init {
        isFocusableInTouchMode = true
        world.listener = this
        sound.on = save.snd()
        sound.music.enabled = save.snd()
        for (i in 0 until 130) {
            stars.add(Star(rnd.nextFloat(), rnd.nextFloat(), rnd.nextInt(3), 1f + rnd.nextFloat() * 2f))
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        running = true
        lastFrame = 0L
        Choreographer.getInstance().postFrameCallback(frameCb)
        requestFocus()
    }

    override fun onDetachedFromWindow() {
        running = false
        Choreographer.getInstance().removeFrameCallback(frameCb)
        sound.music.stop()
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        world.w = w.toFloat()
        world.h = h.toFloat()
        buildScan(w, h)
        if (world.elapsed == 0f) {
            world.player.x = w / 2f
            world.player.y = h * 0.78f
        } else {
            world.player.x = world.player.x.coerceIn(30f, w - 30f)
            world.player.y = world.player.y.coerceIn(h * 0.3f, h - 46f)
        }
    }

    private fun buildScan(w: Int, h: Int) {
        scanBmp?.recycle()
        val b = Bitmap.createBitmap(w.coerceAtLeast(1), h.coerceAtLeast(1), Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        val p = Paint()
        p.color = 0x55000000
        var y = 0f
        while (y < h) {
            c.drawRect(0f, y, w.toFloat(), y + 2f, p)
            y += 6f
        }
        scanBmp = b
    }

    // ---------------- state ----------------
    private fun pressFire() {
        when (state) {
            State.TITLE, State.OVER, State.END -> {
                sound.click()
                world.reset()
                world.player.x = world.w / 2f
                world.player.y = world.h * 0.78f
                state = State.PLAY
                sound.music.start(false)
            }
            State.PAUSE -> {
                sound.click()
                state = State.PLAY
            }
            State.PLAY -> {}
        }
    }

    private fun pauseGame() {
        state = State.PAUSE
        moveId = -1
        fireIds.clear()
        world.input.firing = false
        world.input.clearMove()
    }

    // ---------------- input: any touch moves AND fires ----------------
    override fun onTouchEvent(e: MotionEvent): Boolean {
        val idx = e.actionIndex
        val id = e.getPointerId(idx)
        val x = e.getX(idx)
        val y = e.getY(idx)
        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                if (state != State.PLAY) {
                    if (state == State.TITLE && e.actionMasked == MotionEvent.ACTION_DOWN && y > height - dp(90f)) {
                        if (x < width / 2f) {
                            save.setSnd(!save.snd())
                            sound.on = save.snd()
                            sound.music.enabled = save.snd()
                        } else {
                            save.setFx(!save.fx())
                        }
                        sound.click()
                        return true
                    }
                    pressFire()
                    return true
                }
                if (x > width - dp(90f) && y < dp(90f)) {
                    pauseGame()
                    sound.click()
                    return true
                }
                if (moveId < 0) {
                    moveId = id
                    lastTX = x
                    lastTY = y
                    moveInit = false
                }
                fireIds.add(id)
                world.input.firing = true
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (state != State.PLAY) return true
                for (i in 0 until e.pointerCount) {
                    if (e.getPointerId(i) == moveId) {
                        val px = e.getX(i)
                        val py = e.getY(i)
                        if (!moveInit) {
                            lastTX = px
                            lastTY = py
                            moveInit = true
                        } else {
                            val p = world.player
                            p.x = (p.x + (px - lastTX) * 1.5f).coerceIn(30f, world.w - 30f)
                            p.y = (p.y + (py - lastTY) * 1.5f).coerceIn(world.h * 0.3f, world.h - 46f)
                            lastTX = px
                            lastTY = py
                        }
                        break
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                if (id == moveId) moveId = -1
                fireIds.remove(id)
                if (fireIds.isEmpty() && !keys.contains(KeyEvent.KEYCODE_SPACE)) world.input.firing = false
                return true
            }
        }
        return super.onTouchEvent(e)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        keys.add(keyCode)
        when (keyCode) {
            KeyEvent.KEYCODE_SPACE -> {
                if (state == State.PLAY) world.input.firing = true
                else pressFire()
                return true
            }
            KeyEvent.KEYCODE_P, KeyEvent.KEYCODE_ESCAPE -> {
                if (state == State.PLAY) pauseGame()
                else if (state == State.PAUSE) state = State.PLAY
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        keys.remove(keyCode)
        if (keyCode == KeyEvent.KEYCODE_SPACE && fireIds.isEmpty()) world.input.firing = false
        val dirs = setOf(
            KeyEvent.KEYCODE_A, KeyEvent.KEYCODE_D, KeyEvent.KEYCODE_W, KeyEvent.KEYCODE_S,
            KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN
        )
        if (keys.none { dirs.contains(it) }) world.input.clearMove()
        return super.onKeyUp(keyCode, event)
    }

    private fun applyKeys() {
        var mx = 0f
        var my = 0f
        if (keys.contains(KeyEvent.KEYCODE_A) || keys.contains(KeyEvent.KEYCODE_DPAD_LEFT)) mx -= 1f
        if (keys.contains(KeyEvent.KEYCODE_D) || keys.contains(KeyEvent.KEYCODE_DPAD_RIGHT)) mx += 1f
        if (keys.contains(KeyEvent.KEYCODE_W) || keys.contains(KeyEvent.KEYCODE_DPAD_UP)) my -= 1f
        if (keys.contains(KeyEvent.KEYCODE_S) || keys.contains(KeyEvent.KEYCODE_DPAD_DOWN)) my += 1f
        if (mx != 0f || my != 0f) {
            world.input.mx = mx
            world.input.my = my
        }
    }

    fun onBackPressed(): Boolean {
        return when (state) {
            State.PLAY -> {
                pauseGame()
                false
            }
            State.PAUSE -> {
                state = State.TITLE
                sound.music.stop()
                false
            }
            else -> true
        }
    }

    override fun gameOver(score: Int) {
        state = State.OVER
    }

    override fun runComplete() {
        save.saveHi(world.score)
        state = State.END
        sound.music.stop()
        sound.win()
    }

    // ================================================================
    override fun onDraw(c: Canvas) {
        val t = SystemClock.uptimeMillis()
        val w = width.toFloat()
        val h = height.toFloat()
        c.save()
        if (save.fx() && world.shake > 0 && state == State.PLAY) {
            c.translate((rnd.nextFloat() - 0.5f) * world.shake, (rnd.nextFloat() - 0.5f) * world.shake)
        }
        fill.color = BLACK
        c.drawRect(0f, 0f, w, h, fill)
        drawStars(c, w, h)
        if (state == State.TITLE) {
            drawTitle(c, w, h, t)
        } else {
            drawWorld(c, t)
            drawHud(c, w, h)
            when (state) {
                State.PAUSE -> {
                    centerText(c, "PAUSED", w / 2f, h * 0.42f, 44f)
                    centerText(c, "PRESS FIRE TO RESUME", w / 2f, h * 0.42f + 60f, 20f)
                }
                State.OVER -> {
                    centerText(c, "SIGNAL LOST", w / 2f, h * 0.36f, 44f)
                    centerText(c, "SCORE %06d".format(world.score), w / 2f, h * 0.36f + 60f, 24f)
                    if ((t / 500) % 2L == 0L) centerText(c, "PRESS FIRE", w / 2f, h * 0.36f + 120f, 22f)
                }
                State.END -> {
                    centerText(c, "SIGNAL RESTORED", w / 2f, h * 0.3f, 40f)
                    centerText(c, "TRANSMISSION COMPLETE", w / 2f, h * 0.3f + 56f, 22f)
                    centerText(c, "THANK YOU FOR PLAYING", w / 2f, h * 0.3f + 100f, 22f)
                    centerText(c, "SCORE %06d".format(world.score), w / 2f, h * 0.3f + 156f, 22f)
                    if ((t / 500) % 2L == 0L) centerText(c, "PRESS FIRE", w / 2f, h * 0.3f + 216f, 22f)
                }
                else -> {}
            }
            world.warn?.let {
                if ((t / 160) % 2L == 0L) centerText(c, "WARNING", w / 2f, h * 0.3f, 40f)
            }
        }
        c.restore()
        if (save.fx()) scanBmp?.let { c.drawBitmap(it, 0f, 0f, null) }
    }

    private fun centerText(c: Canvas, s: String, x: Float, y: Float, sizeSp: Float) {
        txt.textSize = sizeSp * resources.displayMetrics.scaledDensity / 2.2f
        txt.color = WHITE
        c.drawText(s, x, y, txt)
    }

    private fun drawTitle(c: Canvas, w: Float, h: Float, t: Long) {
        centerText(c, "VOID//RUN", w / 2f, h * 0.34f, 64f)
        centerText(c, "HI %06d".format(save.hi()), w / 2f, h * 0.34f + 52f, 20f)
        txt.textSize = 13f * resources.displayMetrics.scaledDensity / 2.2f
        txt.color = GRAY
        txt.textAlign = Paint.Align.RIGHT
        c.drawText(BUILD_TAG, w - 24f, 34f, txt)
        txt.textAlign = Paint.Align.CENTER
        if ((t / 500) % 2L == 0L) centerText(c, "PRESS FIRE", w / 2f, h * 0.62f, 26f)
        txt.textSize = 15f * resources.displayMetrics.scaledDensity / 2.2f
        txt.color = GRAY
        c.drawText("SND " + if (save.snd()) "ON" else "OFF", w * 0.25f, h - 40f, txt)
        c.drawText("FX " + if (save.fx()) "ON" else "OFF", w * 0.75f, h - 40f, txt)
    }

    private fun drawStars(c: Canvas, w: Float, h: Float) {
        val boost = if (state == State.PLAY) world.scrollM else 0.4f
        for (s in stars) {
            if (state == State.PLAY || state == State.TITLE) {
                s.y += (0.02f + s.layer * 0.05f) * boost
                if (s.y > 1.02f) {
                    s.y = -0.02f
                    s.x = rnd.nextFloat()
                }
            }
            fill.color = if (s.layer == 2) WHITE else GRAY
            val sz = s.size * (w / 720f)
            c.drawRect(s.x * w, s.y * h, s.x * w + sz, s.y * h + sz, fill)
        }
    }

    private fun drawWorld(c: Canvas, t: Long) {
        val wd = world
        for (r in wd.rocks) Art.drawRock(c, r.x, r.y, r.r, r.rot)
        for (b in wd.boxes) {
            if (!b.gone) Art.drawBox(c, b, wd.w, t)
        }
        for (e in wd.enemies) {
            if (!e.gone) Art.drawEnemy(c, e.ai, e.spec, e.x, e.y, e.r, t, e.shieldUp, e.flash > 0)
        }
        wd.boss?.draw(c, t, wd.elapsed)
        for (m in wd.items) Art.drawItem(c, m.kind, m.x, m.y, 22f, t, txt)
        for (b in wd.shots) Art.drawShot(c, b.x, b.y, b.big)
        for (b in wd.foeShots) Art.drawFoeShot(c, b.x, b.y, b.r)
        for (bm in wd.beams) {
            if (bm.tele > 0) {
                if ((t / 120) % 2L == 0L) {
                    Art.stroke.color = GRAY
                    Art.stroke.strokeWidth = 4f
                    c.drawLine(bm.x1, bm.y1, bm.x2, bm.y2, Art.stroke)
                }
            } else {
                Art.stroke.color = WHITE
                Art.stroke.strokeWidth = bm.w
                Art.stroke.alpha = 90
                c.drawLine(bm.x1, bm.y1, bm.x2, bm.y2, Art.stroke)
                Art.stroke.alpha = 255
                Art.stroke.strokeWidth = bm.w * 0.35f
                c.drawLine(bm.x1, bm.y1, bm.x2, bm.y2, Art.stroke)
            }
        }
        val p = wd.player
        if (!p.dead && state == State.PLAY) {
            Art.drawPlayer(c, p.x, p.y, 30f, t, p.invuln > 0)
        }
        for (pt in wd.parts) {
            fill.color = WHITE
            val a = (pt.life / pt.maxLife).coerceIn(0f, 1f)
            val s = pt.size * a
            c.drawRect(pt.x - s / 2, pt.y - s / 2, pt.x + s / 2, pt.y + s / 2, fill)
        }
        for (ft in wd.texts) {
            txt.textSize = 20f * resources.displayMetrics.scaledDensity / 2.6f
            txt.color = WHITE
            txt.alpha = (255 * (ft.life).coerceIn(0f, 1f)).toInt()
            c.drawText(ft.text, ft.x, ft.y, txt)
        }
        txt.alpha = 255
    }

    private fun drawHud(c: Canvas, w: Float, h: Float) {
        txt.textAlign = Paint.Align.LEFT
        txt.textSize = 26f * resources.displayMetrics.scaledDensity / 2.6f
        txt.color = WHITE
        c.drawText("%06d".format(world.score), 18f, 44f, txt)
        if (world.streak >= 8) {
            txt.textSize = 16f * resources.displayMetrics.scaledDensity / 2.6f
            txt.color = GRAY
            c.drawText("x" + (1 + world.streak / 10).coerceAtMost(5), 18f, 70f, txt)
        }
        txt.textAlign = Paint.Align.CENTER
        txt.textSize = 15f * resources.displayMetrics.scaledDensity / 2.6f
        txt.color = GRAY
        c.drawText("HULL", w - 200f, 24f, txt)
        for (i in 0 until 6) {
            val bx = w - 26f - i * 26f
            if (i < world.player.hits) {
                fill.color = WHITE
                c.drawRect(bx - 9f, 32f, bx + 9f, 54f, fill)
            } else {
                Art.stroke.color = GRAY
                Art.stroke.strokeWidth = 3f
                c.drawRect(bx - 9f, 32f, bx + 9f, 54f, Art.stroke)
            }
        }
        txt.textSize = 18f * resources.displayMetrics.scaledDensity / 2.6f
        txt.color = GRAY
        c.drawText("II", w - 30f, 92f, txt)
        val bo = world.boss
        if (bo != null && bo.entered && !bo.gone) {
            val bw = w * 0.6f
            val frac = (bo.hp / bo.maxHp).coerceIn(0f, 1f)
            txt.textSize = 17f * resources.displayMetrics.scaledDensity / 2.6f
            txt.color = WHITE
            c.drawText(bo.def.title + "  ${bo.phaseIdx + 1}/${bo.phases.size}", w / 2f, 30f, txt)
            fill.color = DARK
            c.drawRect((w - bw) / 2f, 38f, (w + bw) / 2f, 54f, fill)
            fill.color = WHITE
            c.drawRect((w - bw) / 2f, 38f, (w - bw) / 2f + bw * frac, 54f, fill)
            Art.stroke.color = WHITE
            Art.stroke.strokeWidth = 3f
            c.drawRect((w - bw) / 2f, 38f, (w + bw) / 2f, 54f, Art.stroke)
        }
        val p = world.player
        var px = 24f
        txt.textSize = 16f * resources.displayMetrics.scaledDensity / 2.6f
        val hh = h - 24f
        if (p.rapidT > 0) {
            txt.color = WHITE
            c.drawText("R", px, hh, txt); px += 28f
        }
        if (p.doubleT > 0) {
            txt.color = WHITE
            c.drawText("2", px, hh, txt); px += 28f
        }
        if (p.spreadT > 0) {
            txt.color = WHITE
            c.drawText("S", px, hh, txt); px += 28f
        }
        if (p.pierceT > 0) {
            txt.color = WHITE
            c.drawText("P", px, hh, txt)
        }
    }
}
