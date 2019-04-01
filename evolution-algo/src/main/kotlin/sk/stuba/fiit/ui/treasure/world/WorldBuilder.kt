package sk.stuba.fiit.ui.treasure.world

import sk.stuba.fiit.ui.treasure.by

fun buildWorld(rows: Int, columns: Int, block: WorldBuilder.() -> Unit): World {
    val worldBuilder = WorldBuilder(rows, columns)
    block(worldBuilder)
    return World(worldBuilder.map, worldBuilder.treasuresPosition, worldBuilder.treasureHunterPosition)
}

class WorldBuilder(
    rows: Int,
    columns: Int,
    val map: Array<Array<Tile>> = Array(rows) { Array(columns) { Tile.GROUND } }
) {
    val treasuresPosition = mutableListOf<Position>()
    var treasureHunterPosition = 0 by 0

    fun placeTreasure(row: Int, column: Int) {
        map[row][column] = Tile.TREASURE
        treasuresPosition += row by column
    }

    fun placeTreasureHunter(row: Int, column: Int) {
        map[row][column] = Tile.TREASURE_HUNTER_START
        treasureHunterPosition = row by column
    }
}

enum class Tile(val type: String) {
    GROUND("G"), TREASURE("T"), TREASURE_HUNTER_START("S")
}