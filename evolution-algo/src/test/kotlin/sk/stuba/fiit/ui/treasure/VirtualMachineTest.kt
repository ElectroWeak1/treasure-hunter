package sk.stuba.fiit.ui.treasure

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import sk.stuba.fiit.ui.treasure.virtualmachine.*

class VirtualMachineTest {
    @Test
    fun `Test execute instruction`() {
        val memory = Memory(64, Programs.BASIC_PROGRAM)
        val vm = VirtualMachine(memory)

        val instruction = vm.decode(0)
        vm.step()
        println("------------------------------")

        assertTrue(instruction is Instruction.Increment)
        assertEquals(0b0000_0001, memory[0])
    }

    @Test
    fun `Test basic program execution`() {
        val memory = Memory(64) {
            inc(0)
            inc(31)
            inc(16)
            dec(16)
            inc(5)
            print(0)
            jmp(4)
        }
        val vm = VirtualMachine(memory)

        try {
            vm.execute()
        } catch (exception: RunLimitException) {
            println(exception.message)
        } catch (exception: SegmentationFaultException) {
            println(exception.message)
        }
        println("------------------------------")

        assertTrue(vm.programOutput.startsWith("R U U U D U"))
    }
}