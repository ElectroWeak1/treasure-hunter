package sk.stuba.fiit.ui.treasure

import org.junit.Assert.*
import org.junit.Test
import sk.stuba.fiit.ui.treasure.world.InvalidPathException
import sk.stuba.fiit.ui.treasure.world.Path
import sk.stuba.fiit.ui.treasure.world.buildWorld

class WorldTest {
    @Test
    fun `Test world move`() {
        val world = Worlds.TEST_WORLD

        val path = Path.fromString("R U L L L U U U R U R R R R D D")

        world.reset()
        try {
            world.treasureHunter.move(path)
            println(world)
            assertEquals(world.treasureCount, world.treasureHunter.treasuresCollected)
        } catch (exception: InvalidPathException) {
            println("Invalid path: $path")
        }
    }
}