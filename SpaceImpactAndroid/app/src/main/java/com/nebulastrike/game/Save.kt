package com.nebulastrike.game

import android.content.Context

/** Minimal persistence: best score + two toggles. Nothing else. */
class Save(context: Context) {
    private val p = context.getSharedPreferences("voidrun", Context.MODE_PRIVATE)

    fun hi(): Int = p.getInt("hi", 0)
    fun saveHi(score: Int) {
        if (score > hi()) p.edit().putInt("hi", score).apply()
    }

    fun snd(): Boolean = p.getBoolean("snd", true)
    fun setSnd(v: Boolean) {
        p.edit().putBoolean("snd", v).apply()
    }

    fun fx(): Boolean = p.getBoolean("fx", true)
    fun setFx(v: Boolean) {
        p.edit().putBoolean("fx", v).apply()
    }

    fun shipIndex(): Int = p.getInt("ship_idx", 0).coerceIn(0, 99)
    fun setShipIndex(idx: Int) {
        p.edit().putInt("ship_idx", idx.coerceIn(0, 99)).apply()
    }

    fun coins(): Int = p.getInt("coins", 50000) // generous starting bank
    fun addCoins(amount: Int) {
        p.edit().putInt("coins", (coins() + amount).coerceAtLeast(0)).apply()
    }
}
