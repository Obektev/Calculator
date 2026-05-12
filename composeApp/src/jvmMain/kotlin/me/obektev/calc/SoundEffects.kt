package me.obektev.calc

import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import kotlin.math.PI
import kotlin.math.sin

object SoundEffects {
    fun playForToken(token: String) {
        when (token) {
            in DIGITS, "." -> playTone(frequency = 880.0, durationMs = 45, volume = 0.25)
            "+", "-", "*", "/", "%", "+/-" -> playTone(frequency = 520.0, durationMs = 70, volume = 0.28)
            "=" -> playTone(frequency = 1046.5, durationMs = 90, volume = 0.32)
            "C", "CE" -> playTone(frequency = 220.0, durationMs = 90, volume = 0.3)
            "MC", "MR", "MS", "M+", "M-" -> playTone(frequency = 660.0, durationMs = 65, volume = 0.24)
            "sin", "cos", "tan", "ln", "sqrt", "x2", "1/x", "DEG", "RAD" ->
                playTone(frequency = 740.0, durationMs = 75, volume = 0.25)
            else -> playMenu()
        }
    }

    fun playUndo() {
        playTone(frequency = 330.0, durationMs = 85, volume = 0.25)
    }

    fun playModeSwitch() {
        playTone(frequency = 784.0, durationMs = 90, volume = 0.28)
    }

    fun playMenu() {
        playTone(frequency = 620.0, durationMs = 55, volume = 0.22)
    }

    private fun playTone(frequency: Double, durationMs: Int, volume: Double) {
        Thread {
            runCatching {
                val sampleRate = 44_100f
                val format = AudioFormat(sampleRate, 8, 1, true, false)
                val line = AudioSystem.getSourceDataLine(format)
                val samples = (durationMs * sampleRate / 1000).toInt()
                val data = ByteArray(samples)

                for (index in data.indices) {
                    val angle = 2.0 * PI * index * frequency / sampleRate
                    data[index] = (sin(angle) * 127.0 * volume).toInt().toByte()
                }

                line.open(format)
                line.start()
                line.write(data, 0, data.size)
                line.drain()
                line.stop()
                line.close()
            }
        }.start()
    }

    private val DIGITS = setOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
}
