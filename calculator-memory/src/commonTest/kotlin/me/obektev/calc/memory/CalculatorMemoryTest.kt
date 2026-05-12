package me.obektev.calc.memory

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CalculatorMemoryTest {
    @Test
    fun storesAddsSubtractsAndRestoresMemory() {
        val memory = CalculatorMemory()

        memory.store(10.0)
        memory.add(5.0)
        val snapshot = memory.snapshot()
        memory.subtract(3.0)

        assertTrue(memory.hasValue)
        assertEquals(12.0, memory.recall())

        memory.restore(snapshot)
        assertEquals(15.0, memory.recall())

        memory.clear()
        assertFalse(memory.hasValue)
    }
}
