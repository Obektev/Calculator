package me.obektev.calc.memory

data class MemorySnapshot(
    val value: Double,
    val active: Boolean,
)

class CalculatorMemory {
    private var value: Double = 0.0
    private var active: Boolean = false

    val hasValue: Boolean
        get() = active

    fun store(number: Double) {
        value = number
        active = true
    }

    fun recall(): Double = value

    fun clear() {
        value = 0.0
        active = false
    }

    fun add(number: Double) {
        value += number
        active = true
    }

    fun subtract(number: Double) {
        value -= number
        active = true
    }

    fun snapshot(): MemorySnapshot = MemorySnapshot(value = value, active = active)

    fun restore(snapshot: MemorySnapshot) {
        value = snapshot.value
        active = snapshot.active
    }
}
