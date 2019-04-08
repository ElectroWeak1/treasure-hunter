package sk.stuba.fiit.ui.treasure.world

import sk.stuba.fiit.ui.treasure.deepCopy
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.concurrent.withLock

class World(
    private val originalMap: Array<Array<Tile>>,
    private val treasurePositions: List<Position>,
    val treasureHunterStartPosition: Position
) {
    val pool = Pool(this)

    class Pool(val world: World) {
        private val lock = ReentrantLock()
        private val pool = Stack<World>()

        fun take(): World {
            lock.withLock {
                if (pool.empty()) {
                    return world.copy()
                }
                return pool.pop()
            }
        }

        fun give(world: World) = lock.withLock { pool.push(world) }
    }

    val rows = originalMap.size
    val columns = if (originalMap.isNullOrEmpty()) 0 else originalMap[0].size
    val treasureCount: Int by lazy(LazyThreadSafetyMode.NONE) { treasurePositions.size }

    var map = originalMap.deepCopy()
    val treasureHunter = TreasureHunter(this, treasureHunterStartPosition)

    fun copy() = World(originalMap.deepCopy(), ArrayList(treasurePositions.map { it.copy() }), treasureHunterStartPosition.copy())

    fun reset() {
        map = originalMap.deepCopy()
        treasureHunter.reset()
    }

    fun isOutside(position: Position) = position.row < 0 || position.row >= rows
            || position.column < 0 || position.column >= columns

    override fun toString() = map.joinToString(separator = "\n") { it.joinToString(separator = " ") { it.type } }

    companion object {
        const val FITNESS_MAX = 1.0f

        fun fromFile(filePath: String): World {
            TODO("Implement loading world from file")
        }
    }
}