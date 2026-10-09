package com.nebulastrike.game

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.sign

/** Original procedural retro SFX + a minimal pulse-loop. No external audio. */
class Sound(context: Context) {

    var on: Boolean = true
    val music = Pulse()

    fun shoot() = blip(900.0, 1500.0, 55, 0.10f)
    fun boom(small: Boolean) {
        noise(if (small) 140 else 320, 0.28f)
        blip(180.0, 45.0, if (small) 140 else 300, 0.20f)
    }

    fun hurt() {
        blip(300.0, 90.0, 220, 0.28f)
        noise(200, 0.22f)
    }

    fun pickup() {
        tone(660.0, 60, 0.18f)
        tone(990.0, 90, 0.18f, 55)
    }

    fun warn() {
        repeat(3) { i ->
            tone(520.0, 130, 0.22f, i * 200)
            tone(390.0, 130, 0.22f, i * 200 + 100)
        }
    }

    fun bossHit() = blip(500.0, 300.0, 60, 0.12f)
    fun click() = tone(740.0, 40, 0.12f)
    fun over() {
        doubleArrayOf(330.0, 262.0, 196.0, 131.0).forEachIndexed { i, f ->
            tone(f, 220, 0.20f, i * 180)
        }
    }

    fun win() {
        doubleArrayOf(523.0, 659.0, 784.0, 1046.0).forEachIndexed { i, f ->
            tone(f, 150, 0.20f, i * 120)
        }
    }

    private fun blip(f0: Double, f1: Double, ms: Int, vol: Float) {
        if (!on) return
        thread(name = "sfx", isDaemon = true) {
            try {
                val rate = 22050
                val n = (rate * ms / 1000).coerceAtLeast(1)
                val buf = ShortArray(n)
                var phase = 0.0
                for (i in 0 until n) {
                    val f = f0 + (f1 - f0) * i / n
                    phase += 2.0 * PI * f / rate
                    val s = sign(sin(phase)) * 0.6 + sin(phase) * 0.4
                    val env = 1.0 - i.toDouble() / n
                    buf[i] = (s * env * vol * Short.MAX_VALUE).toInt().toShort()
                }
                playStatic(buf, rate)
            } catch (_: Exception) {
            }
        }
    }

    private fun noise(ms: Int, vol: Float) {
        if (!on) return
        thread(name = "sfx", isDaemon = true) {
            try {
                val rate = 22050
                val n = (rate * ms / 1000).coerceAtLeast(1)
                val buf = ShortArray(n)
                var last = 0.0
                for (i in 0 until n) {
                    val w = Math.random() * 2.0 - 1.0
                    last += 0.25 * (w - last)
                    val env = 1.0 - i.toDouble() / n
                    buf[i] = (last * env * vol * 2.0 * Short.MAX_VALUE).toInt()
                        .coerceIn(-Short.MAX_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
                playStatic(buf, rate)
            } catch (_: Exception) {
            }
        }
    }

    fun tone(freq: Double, ms: Int, vol: Float, delayMs: Int = 0) {
        if (!on) return
        thread(name = "sfx", isDaemon = true) {
            try {
                if (delayMs > 0) Thread.sleep(delayMs.toLong())
                if (!on) return@thread
                val rate = 22050
                val n = (rate * ms / 1000).coerceAtLeast(1)
                val buf = ShortArray(n)
                for (i in 0 until n) {
                    val t = i.toDouble() / rate
                    val attack = (i.toDouble() / (rate * 0.01)).coerceAtMost(1.0)
                    val decay = 1.0 - i.toDouble() / n
                    buf[i] = (sin(2.0 * PI * freq * t) * attack * decay * decay * vol * Short.MAX_VALUE).toInt().toShort()
                }
                playStatic(buf, rate)
            } catch (_: Exception) {
            }
        }
    }

    private fun playStatic(buf: ShortArray, rate: Int) {
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(rate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buf.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()
        track.write(buf, 0, buf.size)
        track.play()
        Thread.sleep((buf.size * 1000L / rate) + 40L)
        track.stop()
        track.release()
    }

    /** Minimal two-oscillator pulse loop. Original pattern. */
    inner class Pulse {
        @Volatile var enabled: Boolean = true
        @Volatile private var running = false
        @Volatile private var fast: Boolean = false
        private var worker: Thread? = null
        private val line = intArrayOf(0, 0, 7, 0, 5, 3, 0, 10)

        fun start(boss: Boolean) {
            fast = boss
            if (running) return
            running = true
            worker = thread(name = "music", isDaemon = true) {
                var step = 0
                while (running) {
                    try {
                        if (enabled) {
                            val root = if (fast) 110.0 else 98.0
                            val f = root * Math.pow(2.0, line[step % line.size] / 12.0)
                            val rate = 22050
                            val n = rate * (if (fast) 140 else 170) / 1000
                            val buf = ShortArray(n)
                            for (i in 0 until n) {
                                val t = i.toDouble() / rate
                                val s = sign(sin(2.0 * PI * f * t)) * 0.4
                                buf[i] = (s * (1.0 - 0.6 * i / n) * 0.09f * Short.MAX_VALUE).toInt().toShort()
                            }
                            playStatic(buf, rate)
                        }
                    } catch (_: Exception) {
                    }
                    step++
                    try {
                        Thread.sleep(if (fast) 150L else 185L)
                    } catch (_: InterruptedException) {
                        break
                    }
                }
            }
        }

        fun stop() {
            running = false
            worker?.interrupt()
            worker = null
        }
    }
}
