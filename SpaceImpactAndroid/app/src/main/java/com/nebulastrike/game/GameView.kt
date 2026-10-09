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
        const val BUILD_TAG = "v1.1.5"
    }

    enum class State { TITLE, PLAY, PAUSE, OVER, END, SHOP, SETTINGS, HIGHSCORE, CREDITS }

    var state = State.TITLE
        private set

    private val world = World(save, sound)
    private var shopFilter = "ALL"
    private var shopPage = 0
    private var shopViewingIdx = 0
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
        sound.on = false
        super.onDetachedFromWindow()
    }

    fun onPauseGame() {
        running = false
        Choreographer.getInstance().removeFrameCallback(frameCb)
        sound.music.stop()
        sound.on = false
        if (state == State.PLAY) {
            state = State.PAUSE
            moveId = -1
            fireIds.clear()
            world.input.firing = false
            world.input.clearMove()
        }
    }

    fun onResumeGame() {
        sound.on = save.snd()
        sound.music.enabled = save.snd()
        if (!running) {
            running = true
            lastFrame = 0L
            Choreographer.getInstance().postFrameCallback(frameCb)
        }
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
                if (save.fx()) sound.music.start(world.boss != null && !world.boss!!.gone)
            }
            else -> {}
        }
    }

    private fun pauseGame() {
        state = State.PAUSE
        moveId = -1
        fireIds.clear()
        world.input.firing = false
        world.input.clearMove()
        sound.music.stop()
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
                    if (state == State.TITLE && e.actionMasked == MotionEvent.ACTION_DOWN) {
                        if (y > height - dp(70f)) {
                            if (x < width / 2f) {
                                save.setSnd(!save.snd())
                                sound.on = save.snd()
                                sound.music.enabled = save.snd()
                            } else {
                                save.setFx(!save.fx())
                            }
                            sound.click(); return true
                        }
                        if (y > height * 0.40f && y < height * 0.58f) {
                            // Cycle through unlocked ships for immediate play
                            val unlockedShips = (0 until 100).filter { save.isShipUnlocked(it) }
                            if (unlockedShips.isNotEmpty()) {
                                val curPos = unlockedShips.indexOf(save.shipIndex()).coerceAtLeast(0)
                                val nextPos = if (x < width * 0.4f) (curPos - 1 + unlockedShips.size) % unlockedShips.size
                                              else (curPos + 1) % unlockedShips.size
                                save.setShipIndex(unlockedShips[nextPos])
                                shopViewingIdx = unlockedShips[nextPos]
                            }
                            sound.click(); return true
                        }
                        // Menu button rows
                        if (y >= height * 0.60f && y < height * 0.68f) {
                            pressFire(); return true // 1. START
                        }
                        if (y >= height * 0.68f && y < height * 0.75f) {
                            sound.click()
                            if (x < width / 2f) state = State.SHOP else state = State.SETTINGS
                            return true
                        }
                        if (y >= height * 0.75f && y < height * 0.83f) {
                            sound.click()
                            if (x < width / 2f) state = State.HIGHSCORE else state = State.CREDITS
                            return true
                        }
                        return true // Consume all other touches on Title screen to prevent accidental game starts
                    }
                    if ((state == State.SHOP || state == State.SETTINGS || state == State.HIGHSCORE || state == State.CREDITS) && e.actionMasked == MotionEvent.ACTION_DOWN) {
                        sound.click()
                        if (state == State.SHOP) {
                            if (y > height * 0.14f && y < height * 0.19f) { // filter category
                                val cats = arrayOf("ALL", "EASY", "MEDIUM", "HARD", "VERY_HARD", "FINAL", "LEGEND")
                                val curIdx = cats.indexOf(shopFilter).coerceAtLeast(0)
                                shopFilter = cats[(curIdx + 1) % cats.size]
                                return true
                            }
                            if (y > height * 0.26f && y < height * 0.35f) { // switch viewed ship
                                val ships = if (shopFilter == "ALL") ALL_100_PLAYER_SHIPS else ALL_100_PLAYER_SHIPS.filter { it.diff.equals(shopFilter, true) }
                                if (ships.isNotEmpty()) {
                                    val curSub = ships.indexOfFirst { it.num == ALL_100_PLAYER_SHIPS[shopViewingIdx].num }.coerceAtLeast(0)
                                    val nextSub = if (x < width * 0.4f) (curSub - 1 + ships.size) % ships.size else (curSub + 1) % ships.size
                                    shopViewingIdx = ships[nextSub].num - 1
                                }
                                return true
                            }
                            if (y > height * 0.35f && y < height * 0.42f) { // BUY or EQUIP ship
                                val curShip = ALL_100_PLAYER_SHIPS[shopViewingIdx]
                                if (!save.isShipUnlocked(shopViewingIdx)) {
                                    if (save.coins() >= curShip.cost) {
                                        save.addCoins(-curShip.cost)
                                        save.unlockShip(shopViewingIdx)
                                        save.setShipIndex(shopViewingIdx)
                                        sound.win()
                                    }
                                } else {
                                    save.setShipIndex(shopViewingIdx)
                                    sound.click()
                                }
                                return true
                            }
                            // Upgrades for currently viewed ship
                            val targetShipIdx = shopViewingIdx
                            if (save.isShipUnlocked(targetShipIdx)) {
                                if (y > height * 0.49f && y < height * 0.55f) { // 1. buy pwr upgrade
                                    if (save.coins() >= 600 && save.upPwr(targetShipIdx) < 10) {
                                        save.addCoins(-600); save.addUpPwr(targetShipIdx, 1)
                                    }
                                    return true
                                }
                                if (y > height * 0.55f && y < height * 0.61f) { // 2. buy spd upgrade
                                    if (save.coins() >= 750 && save.upSpd(targetShipIdx) < 10) {
                                        save.addCoins(-750); save.addUpSpd(targetShipIdx, 1)
                                    }
                                    return true
                                }
                                if (y > height * 0.61f && y < height * 0.67f) { // 3. buy energy/dur upgrade
                                    if (save.coins() >= 800 && save.upDur(targetShipIdx) < 10) {
                                        save.addCoins(-800); save.addUpDur(targetShipIdx, 1)
                                    }
                                    return true
                                }
                                if (y > height * 0.67f && y < height * 0.73f) { // 4. buy hp upgrade
                                    if (save.coins() >= 900 && save.upHp(targetShipIdx) < 200) {
                                        save.addCoins(-900); save.addUpHp(targetShipIdx, 20)
                                    }
                                    return true
                                }
                                if (y > height * 0.73f && y < height * 0.79f) { // 5. buy skill slot
                                    if (save.coins() >= 1200 && save.upSkillSlots(targetShipIdx) < 4) {
                                        save.addCoins(-1200); save.unlockSkillSlot(targetShipIdx)
                                    }
                                    return true
                                }
                                if (y > height * 0.79f && y < height * 0.85f) { // 6. elite overdrive
                                    if (save.coins() >= 2000 && save.upElite(targetShipIdx) < 10) {
                                        save.addCoins(-2000); save.addUpElite(targetShipIdx, 1)
                                    }
                                    return true
                                }
                            }
                            if (y > height * 0.85f && y < height * 0.91f) { // 7. craft parts
                                if (save.shipParts() >= 5) { save.addShipPart(-5); save.addCoins(2500) }
                                return true
                            }
                        }
                        if (state == State.SETTINGS) {
                            if (y > height * 0.32f && y < height * 0.40f) { save.setSnd(!save.snd()); sound.on = save.snd(); return true }
                            if (y > height * 0.40f && y < height * 0.48f) { save.setFx(!save.fx()); return true }
                            if (y > height * 0.48f && y < height * 0.56f) {
                                if (x < width / 2f) save.setVolume((save.volume() - 0.1f).coerceAtLeast(0.1f))
                                else save.setVolume((save.volume() + 0.1f).coerceAtMost(1.0f))
                                return true
                            }
                        }
                        // Return to Title
                        state = State.TITLE
                        return true
                    }
                    if (state == State.PAUSE && e.actionMasked == MotionEvent.ACTION_DOWN) {
                        sound.click()
                        if (y > height * 0.42f && y < height * 0.48f) { // 1. Resume
                            state = State.PLAY
                            if (save.fx()) sound.music.start(world.boss != null && !world.boss!!.gone)
                            return true
                        }
                        if (y > height * 0.49f && y < height * 0.55f) { // 2. Settings
                            state = State.SETTINGS; return true
                        }
                        if (y > height * 0.56f && y < height * 0.62f) { // 3. Restart
                            pressFire(); return true
                        }
                        if (y > height * 0.63f && y < height * 0.70f) { // 4. Main Menu
                            state = State.TITLE; sound.music.stop(); return true
                        }
                        state = State.PLAY
                        if (save.fx()) sound.music.start(world.boss != null && !world.boss!!.gone)
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
        when (state) {
            State.TITLE -> drawTitle(c, w, h, t)
            State.SHOP -> drawShop(c, w, h, t)
            State.SETTINGS -> drawSettings(c, w, h)
            State.HIGHSCORE -> drawHighscore(c, w, h)
            State.CREDITS -> drawCredits(c, w, h)
            else -> {
                drawWorld(c, t)
                drawHud(c, w, h)
                when (state) {
                    State.PAUSE -> {
                        centerText(c, "MISSION PAUSED", w / 2f, h * 0.35f, 38f)
                        txt.color = WHITE
                        centerText(c, "[ 1. RESUME MISSION ]", w / 2f, h * 0.45f, 22f)
                        centerText(c, "[ 2. SETTINGS / AUDIO ]", w / 2f, h * 0.52f, 20f)
                        centerText(c, "[ 3. RESTART RUN ]", w / 2f, h * 0.59f, 20f)
                        centerText(c, "[ 4. MAIN MENU ]", w / 2f, h * 0.66f, 20f)
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
        }
        c.restore()
        if (save.fx()) scanBmp?.let { c.drawBitmap(it, 0f, 0f, null) }
    }

    private fun centerText(c: Canvas, s: String, x: Float, y: Float, sizeSp: Float) {
        txt.textSize = sizeSp * resources.displayMetrics.scaledDensity / 2.2f
        txt.color = WHITE
        c.drawText(s, x, y, txt)
    }


    private fun drawShop(c: Canvas, w: Float, h: Float, t: Long) {
        centerText(c, "STARSHIP HANGAR & UPGRADES", w / 2f, h * 0.06f, 26f)
        val curShip = ALL_100_PLAYER_SHIPS[shopViewingIdx]
        val isUnlocked = save.isShipUnlocked(shopViewingIdx)
        val isEquipped = (save.shipIndex() == shopViewingIdx)
        val coins = save.coins()
        txt.color = WHITE
        centerText(c, "BANK: $coins COINS  |  PARTS: ${save.shipParts()}", w / 2f, h * 0.10f, 18f)

        // Category filter bar
        txt.color = GRAY
        centerText(c, "< CATEGORY: $shopFilter >", w / 2f, h * 0.14f, 17f)

        // Display current preview ship
        Art.drawPlayer(c, w / 2f, h * 0.22f, 36f, t, false, shopViewingIdx)
        txt.color = WHITE
        centerText(c, "<  #${curShip.num}: ${curShip.name.uppercase()}  >", w / 2f, h * 0.29f, 20f)
        txt.color = GRAY
        centerText(c, "TIER: ${curShip.diff}  |  BASE COST: ${curShip.cost} C", w / 2f, h * 0.33f, 15f)

        // Purchase / Equip Button
        val btnY = h * 0.38f
        val btnW = w * 0.72f
        val btnH = 48f
        val bx1 = (w - btnW) / 2f
        val bx2 = bx1 + btnW
        fill.color = DARK
        c.drawRect(bx1, btnY - btnH / 2f, bx2, btnY + btnH / 2f, fill)
        Art.stroke.color = WHITE; Art.stroke.strokeWidth = 3f
        c.drawRect(bx1, btnY - btnH / 2f, bx2, btnY + btnH / 2f, Art.stroke)

        if (!isUnlocked) {
            val canAfford = coins >= curShip.cost
            txt.color = if (canAfford) WHITE else GRAY
            centerText(c, "[ BUY SHIP: ${curShip.cost} COINS ]", w / 2f, btnY + 7f, 17f)
        } else if (isEquipped) {
            txt.color = WHITE
            centerText(c, "== EQUIPPED & READY ==", w / 2f, btnY + 7f, 17f)
        } else {
            txt.color = WHITE
            centerText(c, "[ EQUIP THIS SHIP ]", w / 2f, btnY + 7f, 17f)
        }

        // Stats summary for this ship
        val pwrTotal = curShip.power + save.upPwr(shopViewingIdx) + save.upElite(shopViewingIdx)
        val spdTotal = curShip.speed + save.upSpd(shopViewingIdx) + save.upElite(shopViewingIdx)
        val durTotal = curShip.durability + save.upDur(shopViewingIdx) + save.upElite(shopViewingIdx)
        val hpTotal = curShip.health + save.upHp(shopViewingIdx) + save.upElite(shopViewingIdx) * 20
        val nrgTotal = (curShip.energy * 25) + save.upDur(shopViewingIdx) * 10
        txt.color = WHITE
        centerText(c, "PWR: $pwrTotal  SPD: $spdTotal  NRG: $nrgTotal  HP: $hpTotal", w / 2f, h * 0.43f, 15f)
        txt.color = GRAY
        centerText(c, "SPECIAL SKILL: [${curShip.skill.uppercase()}]", w / 2f, h * 0.465f, 15f)

        // Upgrade Buttons with Visual Progress Bars (specific to this ship!)
        fun drawUpgradeRow(title: String, cost: String, lvl: Int, maxLvl: Int, yPos: Float) {
            val barW = w * 0.36f
            val barX = w * 0.58f
            txt.textAlign = Paint.Align.LEFT
            txt.textSize = 15f * resources.displayMetrics.scaledDensity / 2.2f
            txt.color = if (isUnlocked) WHITE else GRAY
            c.drawText(title, w * 0.08f, yPos + 6f, txt)

            // Draw progress bar background & fill
            fill.color = DARK
            c.drawRect(barX, yPos - 10f, barX + barW, yPos + 8f, fill)
            Art.stroke.color = if (isUnlocked) WHITE else GRAY; Art.stroke.strokeWidth = 2f
            c.drawRect(barX, yPos - 10f, barX + barW, yPos + 8f, Art.stroke)
            val fillW = barW * (lvl.toFloat() / maxLvl.coerceAtLeast(1)).coerceIn(0f, 1f)
            fill.color = if (isUnlocked) WHITE else GRAY
            c.drawRect(barX, yPos - 10f, barX + fillW, yPos + 8f, fill)

            txt.textAlign = Paint.Align.LEFT
            txt.textSize = 12f * resources.displayMetrics.scaledDensity / 2.2f
            txt.color = if (lvl >= maxLvl || !isUnlocked) GRAY else WHITE
            c.drawText(if (lvl >= maxLvl) "MAX" else cost, barX + barW + 10f, yPos + 5f, txt)
            txt.textAlign = Paint.Align.CENTER
        }

        drawUpgradeRow("1. POWER", "600 C", save.upPwr(shopViewingIdx), 10, h * 0.52f)
        drawUpgradeRow("2. SPEED", "750 C", save.upSpd(shopViewingIdx), 10, h * 0.58f)
        drawUpgradeRow("3. ENERGY/DUR", "800 C", save.upDur(shopViewingIdx), 10, h * 0.64f)
        drawUpgradeRow("4. HULL +20", "900 C", save.upHp(shopViewingIdx) / 20, 10, h * 0.70f)
        drawUpgradeRow("5. SKILL SLOTS", "1200 C", save.upSkillSlots(shopViewingIdx) - 1, 3, h * 0.76f)
        drawUpgradeRow("6. ELITE OVERDRIVE", "2000 C", save.upElite(shopViewingIdx), 10, h * 0.82f)

        // Craft part row
        txt.color = WHITE
        centerText(c, "[ 7. CRAFT PART REWARD (5 PARTS -> 2500 C) ]", w / 2f, h * 0.88f, 16f)

        txt.color = GRAY
        centerText(c, "< TAP BOTTOM TO RETURN TO MENU >", w / 2f, h * 0.94f, 16f)
    }

    private fun drawSettings(c: Canvas, w: Float, h: Float) {
        centerText(c, "SETTINGS", w / 2f, h * 0.22f, 36f)
        centerText(c, "SND (SFX): " + if (save.snd()) "ON" else "OFF", w / 2f, h * 0.36f, 24f)
        centerText(c, "FX (MUSIC): " + if (save.fx()) "ON" else "OFF", w / 2f, h * 0.44f, 24f)
        val volPct = (save.volume() * 100).toInt()
        centerText(c, "VOLUME: [ - ] $volPct% [ + ]", w / 2f, h * 0.52f, 24f)
        centerText(c, "DISPLAY: MONOCHROME CRT", w / 2f, h * 0.60f, 20f)
        centerText(c, "ORIENTATION: PORTRAIT", w / 2f, h * 0.66f, 20f)

        txt.color = GRAY
        centerText(c, "< TAP BOTTOM TO RETURN >", w / 2f, h * 0.85f, 18f)
    }

    private fun drawHighscore(c: Canvas, w: Float, h: Float) {
        centerText(c, "HALL OF FAME", w / 2f, h * 0.22f, 36f)
        val hi = save.hi()
        centerText(c, "RANK 1: %06d PTS".format(hi), w / 2f, h * 0.36f, 24f)
        centerText(c, "RANK 2: %06d PTS".format((hi * 0.75).toInt()), w / 2f, h * 0.43f, 20f)
        centerText(c, "RANK 3: %06d PTS".format((hi * 0.55).toInt()), w / 2f, h * 0.49f, 20f)
        centerText(c, "RANK 4: %06d PTS".format((hi * 0.35).toInt()), w / 2f, h * 0.55f, 20f)
        centerText(c, "RANK 5: %06d PTS".format((hi * 0.20).toInt()), w / 2f, h * 0.61f, 20f)

        txt.color = GRAY
        centerText(c, "< TAP BOTTOM TO RETURN >", w / 2f, h * 0.85f, 18f)
    }

    private fun drawCredits(c: Canvas, w: Float, h: Float) {
        centerText(c, "VOID//RUN CREDITS", w / 2f, h * 0.20f, 32f)
        centerText(c, "INSPIRED BY NOKIA 3310", w / 2f, h * 0.32f, 20f)
        centerText(c, "SPACE IMPACT ARCADE", w / 2f, h * 0.37f, 18f)
        txt.color = GRAY
        centerText(c, "100 ALIEN ENEMY SHIPS", w / 2f, h * 0.46f, 18f)
        centerText(c, "100 PLAYER STARSHIPS", w / 2f, h * 0.51f, 18f)
        centerText(c, "50 HANDCRAFTED SKILL BOSSES", w / 2f, h * 0.56f, 18f)
        centerText(c, "GEOMETRICA RETRO ENGINE", w / 2f, h * 0.63f, 18f)
        centerText(c, "DEVELOPED FOR ANDROID", w / 2f, h * 0.68f, 18f)

        centerText(c, "< TAP BOTTOM TO RETURN >", w / 2f, h * 0.85f, 18f)
    }

    private fun drawTitle(c: Canvas, w: Float, h: Float, t: Long) {
        centerText(c, "VOID//RUN", w / 2f, h * 0.34f, 64f)
        centerText(c, "HI %06d".format(save.hi()), w / 2f, h * 0.34f + 52f, 20f)
        txt.textSize = 13f * resources.displayMetrics.scaledDensity / 2.2f
        txt.color = GRAY
        txt.textAlign = Paint.Align.RIGHT
        c.drawText(BUILD_TAG, w - 24f, 34f, txt)
        txt.textAlign = Paint.Align.CENTER
        if ((t / 500) % 2L == 0L) centerText(c, "[ 1. START RUN ]", w / 2f, h * 0.64f, 24f)
        centerText(c, "[ 2. SHOP ]     [ 3. SETTINGS ]", w / 2f, h * 0.72f, 19f)
        centerText(c, "[ 4. HIGHSCORE ]  [ 5. CREDITS ]", w / 2f, h * 0.78f, 19f)
        txt.textSize = 15f * resources.displayMetrics.scaledDensity / 2.2f
        txt.color = GRAY
        c.drawText("SND " + if (save.snd()) "ON" else "OFF", w * 0.25f, h - 35f, txt)
        c.drawText("FX " + if (save.fx()) "ON" else "OFF", w * 0.75f, h - 35f, txt)

        // Hangar Ship Selector
        val curShip = ALL_100_PLAYER_SHIPS[save.shipIndex()]
        val isUnlocked = save.isShipUnlocked(save.shipIndex())
        txt.textSize = 18f * resources.displayMetrics.scaledDensity / 2.2f
        txt.color = WHITE
        centerText(c, "<  SHIP #${curShip.num}: ${curShip.name.uppercase()}  >", w / 2f, h * 0.50f, 20f)
        txt.textSize = 14f * resources.displayMetrics.scaledDensity / 2.2f
        txt.color = if (isUnlocked) WHITE else GRAY
        val lockTag = if (isUnlocked) "[READY]" else "[LOCKED: ${curShip.cost} C - GO TO SHOP]"
        centerText(c, "$lockTag  NRG: ${curShip.energy * 25}  SKILL: [${curShip.skill.uppercase()}]", w / 2f, h * 0.54f, 14f)
        Art.drawPlayer(c, w / 2f, h * 0.44f, 38f, t, false, save.shipIndex())
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
            Art.drawPlayer(c, p.x, p.y, 30f, t, p.invuln > 0, save.shipIndex())
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
        if (world.streak >= 4) {
            txt.textSize = 16f * resources.displayMetrics.scaledDensity / 2.6f
            txt.color = GRAY
            c.drawText("x" + (1 + world.streak / 10).coerceAtMost(5) + " COMBO", 18f, 70f, txt)
        }
        txt.textSize = 14f * resources.displayMetrics.scaledDensity / 2.6f
        txt.color = WHITE
        c.drawText("${save.coins()} C", 18f, 92f, txt)
        txt.textAlign = Paint.Align.RIGHT
        txt.textSize = 14f * resources.displayMetrics.scaledDensity / 2.6f
        txt.color = GRAY
        c.drawText("HULL", w - 180f, 22f, txt)
        val p = world.player
        val numPips = 6
        val filledPips = ((p.hits.toFloat() / p.maxHits.coerceAtLeast(1)) * numPips).toInt().coerceIn(0, numPips)
        for (i in 0 until numPips) {
            val bx = w - 24f - (numPips - 1 - i) * 25f
            if (i < filledPips) {
                fill.color = WHITE
                c.drawRect(bx - 9f, 10f, bx + 9f, 28f, fill)
            } else {
                Art.stroke.color = GRAY
                Art.stroke.strokeWidth = 2f
                c.drawRect(bx - 9f, 10f, bx + 9f, 28f, Art.stroke)
            }
        }

        // ENERGY BAR (Gauge underneath Hull)
        txt.color = GRAY
        txt.textSize = 13f * resources.displayMetrics.scaledDensity / 2.6f
        c.drawText("NRG", w - 180f, 48f, txt)
        val nrgBarW = 142f
        val nrgBarX = w - 170f
        fill.color = DARK
        c.drawRect(nrgBarX, 36f, nrgBarX + nrgBarW, 50f, fill)
        Art.stroke.color = WHITE
        Art.stroke.strokeWidth = 2f
        c.drawRect(nrgBarX, 36f, nrgBarX + nrgBarW, 50f, Art.stroke)
        val nrgFrac = (p.energy / p.maxEnergy.coerceAtLeast(1f)).coerceIn(0f, 1f)
        fill.color = WHITE
        c.drawRect(nrgBarX, 36f, nrgBarX + nrgBarW * nrgFrac, 50f, fill)

        txt.textAlign = Paint.Align.CENTER
        txt.textSize = 18f * resources.displayMetrics.scaledDensity / 2.6f
        txt.color = GRAY
        c.drawText("II", w - 30f, 85f, txt)
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
