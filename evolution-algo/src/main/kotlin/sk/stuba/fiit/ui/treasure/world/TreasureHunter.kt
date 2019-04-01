package sk.stuba.fiit.ui.treasure.world

class TreasureHunter(
    private val world: World,
    private val originalPosition: Position
) {
    var position = originalPosition.copy()
    var steps = 0
    var treasuresCollected = 0

    fun move(path: Path): List<Operator> {
        val validOperations = mutableListOf<Operator>()
        path.operations.forEach {
            position.add(it.rowShift, it.columnShift)
            steps++
            validOperations += it
            if (world.isOutside(position)) {
                position.add(-it.rowShift, -it.columnShift)
                steps--
                validOperations -= it
                return validOperations
            }
            if (world.map[position.row][position.column] == Tile.TREASURE) {
                treasuresCollected++
                world.map[position.row][position.column] = Tile.GROUND
                if (treasuresCollected >= world.treasureCount) return validOperations
            }
        }
        return validOperations
    }

    fun reset() {
        position.set(originalPosition)
        steps = 0
        treasuresCollected = 0
    }
}