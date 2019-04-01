package sk.stuba.fiit.ui.treasure.world

data class Position(
    var row: Int = 0,
    var column: Int = 0
) {
    fun add(rows: Int, columns: Int) {
        row += rows
        column += columns
    }

    fun set(position: Position) {
        row = position.row
        column = position.column
    }

    fun reset() {
        row = 0
        column = 0
    }
}