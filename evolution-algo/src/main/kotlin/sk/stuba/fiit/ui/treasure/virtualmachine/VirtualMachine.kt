package sk.stuba.fiit.ui.treasure.virtualmachine

import java.io.StringWriter

class VirtualMachine(
    private val memory: Memory,
    private val runLimit: Int = 500
) {
    private var currentAddress = 0
    private var executedInstructionCount = 0
    private val outputWriter = StringWriter()

    val programOutput: String
        get() {
            outputWriter.flush()
            return outputWriter.toString()
        }

    fun decode(address: Int): Instruction {
        if (address < 0 || address >= memory.size) {
            throw SegmentationFaultException(address)
        }
        val value = memory[address]
        val decoded = (value and 0xc0) shr 6
        val instructionAddress = (value and 0xc0.inv()) and 0xff

        return when (decoded) {
            0x00 -> Instruction.Increment(instructionAddress)
            0x01 -> Instruction.Decrement(instructionAddress)
            0x02 -> Instruction.Jump(instructionAddress)
            0x03 -> Instruction.Print(instructionAddress)
            else -> Instruction.Invalid
        }
    }

    fun execute() {
        try {
            while (true) {
                if (!step()) {
                    break
                }
            }
        } catch (exception: SegmentationFaultException) {}
    }

    fun step(): Boolean {
        if (executedInstructionCount >= runLimit) {
            return false
        }
        val nextInstruction = decode(currentAddress)
        execute(nextInstruction)
        currentAddress++
        executedInstructionCount++
        return true
    }

    private fun execute(instruction: Instruction) {
        when (instruction) {
            is Instruction.Increment -> {
                memory[instruction.address]++
            }
            is Instruction.Decrement -> {
                memory[instruction.address]--
            }
            is Instruction.Jump -> {
                currentAddress = instruction.address - 1
            }
            is Instruction.Print -> {
                val output = print(instruction.address)
                outputWriter.write("$output ")
            }
            else -> throw IllegalStateException("Invalid instruction")
        }
    }

    private fun print(address: Int): String {
        val value = memory[address]
        val bits = getNumberOfBits(value)
        return when (bits) {
            0, 1, 2 -> "U"
            3, 4 -> "D"
            5, 6 -> "R"
            7, 8 -> "L"
            else -> "X"
        }
    }

    private fun getNumberOfBits(value: Int): Int {
        var bits = 0
        for (i in 0 until 8) {
            if ((value shr i) and 0x01 == 1) {
                bits++
            }
        }
        return bits
    }
}