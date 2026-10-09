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

    fun volume(): Float = p.getFloat("vol", 1.0f)
    fun setVolume(v: Float) {
        p.edit().putFloat("vol", v.coerceIn(0f, 1.0f)).apply()
    }

    fun shipIndex(): Int = p.getInt("ship_idx", 0).coerceIn(0, 99)
    fun setShipIndex(idx: Int) {
        p.edit().putInt("ship_idx", idx.coerceIn(0, 99)).apply()
    }

    fun coins(): Int = p.getInt("coins", 50000) // generous starting bank
    fun addCoins(amount: Int) {
        p.edit().putInt("coins", (coins() + amount).coerceAtLeast(0)).apply()
    }

    // Upgrades
    fun upPwr(): Int = p.getInt("up_pwr", 0)
    fun addUpPwr(v: Int = 1) = p.edit().putInt("up_pwr", upPwr() + v).apply()

    fun upSpd(): Int = p.getInt("up_spd", 0)
    fun addUpSpd(v: Int = 1) = p.edit().putInt("up_spd", upSpd() + v).apply()

    fun upDur(): Int = p.getInt("up_dur", 0)
    fun addUpDur(v: Int = 1) = p.edit().putInt("up_dur", upDur() + v).apply()

    fun upHp(): Int = p.getInt("up_hp", 0)
    fun addUpHp(v: Int = 20) = p.edit().putInt("up_hp", upHp() + v).apply()

    fun upSkillSlots(): Int = p.getInt("up_skill", 1)
    fun unlockSkillSlot() = p.edit().putInt("up_skill", (upSkillSlots() + 1).coerceAtMost(4)).apply()

    fun upElite(): Int = p.getInt("up_elite", 0)
    fun addUpElite(v: Int = 1) = p.edit().putInt("up_elite", upElite() + v).apply()

    fun shipParts(): Int = p.getInt("ship_parts", 0)
    fun addShipPart(v: Int = 1) = p.edit().putInt("ship_parts", shipParts() + v).apply()
}
