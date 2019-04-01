package sk.stuba.fiit.ui.treasure.virtualmachine

fun createProgram(build: ProgramBuilder.() -> Unit): IntArray {
    val builder = ProgramBuilder()
    build(builder)
    return builder.getProgram()
}

class ProgramBuilder {
    private val program = mutableListOf<Int>()

    infix fun inc(address: Int) = program.add(0x00 or address)
    infix fun dec(address: Int) = program.add(0x40 or address)
    infix fun jmp(address: Int) = program.add(0x80 or address)
    infix fun print(address: Int) = program.add(0xc0 or address)

    fun getProgram() = program.toIntArray()
}