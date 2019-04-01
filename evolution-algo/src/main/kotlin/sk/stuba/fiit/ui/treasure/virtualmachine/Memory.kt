package sk.stuba.fiit.ui.treasure.virtualmachine

data class Memory(
    val size: Int,
    val program: IntArray
) {
    private val memory = IntArray(size)

    init {
        program.copyInto(memory)
    }

    constructor(size: Int, build: ProgramBuilder.() -> Unit) : this(size,
        createProgram(build)
    )

    operator fun get(index: Int) = memory[index]

    operator fun set(index: Int, value: Int) {
        memory[index] = value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Memory) return false

        if (size != other.size) return false
        if (!memory.contentEquals(other.memory)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = size
        result = 31 * result + memory.contentHashCode()
        return result
    }

    override fun toString() = "Memory(size=$size, memory=[${memory.joinToString()}])"
}