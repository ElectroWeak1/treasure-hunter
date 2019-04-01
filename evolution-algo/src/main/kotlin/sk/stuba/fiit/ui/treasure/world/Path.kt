package sk.stuba.fiit.ui.treasure.world

data class Path(val operations: List<Operator>) {
    companion object {
        fun fromString(path: String): Path {
            val operators = mutableListOf<Operator>()
            for (char in path) {
                if (char == ' ') continue
                Operator.values().find { it.value == char.toString() }?.let { operators += it }
            }
            return Path(operators)
        }
    }
}