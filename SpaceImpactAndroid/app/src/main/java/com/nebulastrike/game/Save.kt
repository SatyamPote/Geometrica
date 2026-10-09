package com.nebulastrike.game

import android.content.Context

/**
 * Persistence for VoidRun:
 * - High score
 * - Sound / FX toggles and volume
 * - Coins and Parts inventory
 * - Per-ship unlock states (Ship #0 is unlocked by default, others purchased with coins)
 * - Per-ship upgrade levels (Power, Speed, Durability, Health, Elite)
 */
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

    // Active equipped ship index
    fun shipIndex(): Int = p.getInt("ship_idx", 0).coerceIn(0, 99)
    fun setShipIndex(idx: Int) {
        p.edit().putInt("ship_idx", idx.coerceIn(0, 99)).apply()
    }

    // Ship unlock system: ship 0 (Interceptor) is unlocked by default, others require coins
    fun isShipUnlocked(idx: Int): Boolean {
        if (idx == 0) return true
        return p.getBoolean("ship_${idx}_unlocked", false)
    }

    fun unlockShip(idx: Int) {
        p.edit().putBoolean("ship_${idx}_unlocked", true).apply()
    }

    // Economy
    fun coins(): Int = p.getInt("coins", 5000) // starting player bank
    fun addCoins(amount: Int) {
        p.edit().putInt("coins", (coins() + amount).coerceAtLeast(0)).apply()
    }

    fun shipParts(): Int = p.getInt("ship_parts", 0)
    fun addShipPart(v: Int = 1) = p.edit().putInt("ship_parts", (shipParts() + v).coerceAtLeast(0)).apply()

    // Per-ship Upgrades (each plane upgrades independently!)
    fun upPwr(shipIdx: Int = shipIndex()): Int = p.getInt("ship_${shipIdx}_pwr", 0)
    fun addUpPwr(shipIdx: Int = shipIndex(), v: Int = 1) =
        p.edit().putInt("ship_${shipIdx}_pwr", (upPwr(shipIdx) + v).coerceAtMost(10)).apply()

    fun upSpd(shipIdx: Int = shipIndex()): Int = p.getInt("ship_${shipIdx}_spd", 0)
    fun addUpSpd(shipIdx: Int = shipIndex(), v: Int = 1) =
        p.edit().putInt("ship_${shipIdx}_spd", (upSpd(shipIdx) + v).coerceAtMost(10)).apply()

    fun upDur(shipIdx: Int = shipIndex()): Int = p.getInt("ship_${shipIdx}_dur", 0)
    fun addUpDur(shipIdx: Int = shipIndex(), v: Int = 1) =
        p.edit().putInt("ship_${shipIdx}_dur", (upDur(shipIdx) + v).coerceAtMost(10)).apply()

    fun upHp(shipIdx: Int = shipIndex()): Int = p.getInt("ship_${shipIdx}_hp", 0)
    fun addUpHp(shipIdx: Int = shipIndex(), v: Int = 20) =
        p.edit().putInt("ship_${shipIdx}_hp", (upHp(shipIdx) + v).coerceAtMost(200)).apply()

    fun upSkillSlots(shipIdx: Int = shipIndex()): Int = p.getInt("ship_${shipIdx}_skill", 1)
    fun unlockSkillSlot(shipIdx: Int = shipIndex()) =
        p.edit().putInt("ship_${shipIdx}_skill", (upSkillSlots(shipIdx) + 1).coerceAtMost(4)).apply()

    fun upElite(shipIdx: Int = shipIndex()): Int = p.getInt("ship_${shipIdx}_elite", 0)
    fun addUpElite(shipIdx: Int = shipIndex(), v: Int = 1) =
        p.edit().putInt("ship_${shipIdx}_elite", (upElite(shipIdx) + v).coerceAtMost(10)).apply()
}
