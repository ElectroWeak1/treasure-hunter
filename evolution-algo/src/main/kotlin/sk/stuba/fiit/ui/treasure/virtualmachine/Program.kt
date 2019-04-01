package sk.stuba.fiit.ui.treasure.virtualmachine

class Program(private val instructions: List<Instruction>) {
    override fun toString(): String {
        val builder = StringBuilder()
        instructions.forEach {
            builder.append(when (it) {
                is Instruction.Increment -> "inc 0x${it.address.toString(16)}\n"
                is Instruction.Decrement -> "dec 0x${it.address.toString(16)}\n"
                is Instruction.Print -> "print 0x${it.address.toString(16)}\n"
                is Instruction.Jump -> "jmp 0x${it.address.toString(16)}\n"
                else -> "Invalid instruction\n"
            })
        }
        return builder.toString()
    }

    companion object {
        // TODO: Duplicate code in VM -- this class is just for printing, delete this class or remove duplication
        //  or refactor so it doesn't duplicate code
        fun parse(program: IntArray): Program {
            val instructions = mutableListOf<Instruction>()
            program.forEach {
                val decoded = (it and 0xc0) shr 6
                val instructionAddress = (it and 0xc0.inv()) and 0xff

                val instruction = when (decoded) {
                    0x00 -> Instruction.Increment(instructionAddress)
                    0x01 -> Instruction.Decrement(instructionAddress)
                    0x02 -> Instruction.Jump(instructionAddress)
                    0x03 -> Instruction.Print(instructionAddress)
                    else -> Instruction.Invalid
                }
                instructions += instruction
            }
            return Program(instructions)
        }
    }
}
