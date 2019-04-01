package sk.stuba.fiit.ui.treasure

import org.junit.Assert.*
import org.junit.Test
import sk.stuba.fiit.ui.treasure.virtualmachine.Memory

class MemoryTest {
    @Test
    fun `Test memory access`() {
        val memSize = 64
        val memory = Memory(memSize, Programs.BASIC_PROGRAM)

        memory[memSize - 1] = 0b1111_0000

        println(memory)

        assertEquals(0b1111_0000, memory[memSize - 1])
        assertEquals(0b0000_0000, memory[memSize - 2])
        assertEquals(0b0101_0000, memory[3])
    }

}
