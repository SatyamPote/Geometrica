package com.nebulastrike.game

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager

class MainActivity : Activity() {

    private lateinit var save: Save
    private lateinit var sound: Sound
    private var gameView: GameView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        save = Save(this)
        sound = Sound(this)
        sound.on = save.snd()
        sound.music.enabled = save.snd()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        gameView = GameView(this, save, sound)
        setContentView(gameView)
    }

    override fun onDestroy() {
        sound.music.stop()
        super.onDestroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUi()
    }

    private fun hideSystemUi() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            )
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        val gv = gameView
        if (gv != null && !gv.onBackPressed()) return
        super.onBackPressed()
    }
}
