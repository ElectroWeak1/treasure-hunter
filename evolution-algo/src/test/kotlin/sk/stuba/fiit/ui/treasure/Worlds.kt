package sk.stuba.fiit.ui.treasure

import sk.stuba.fiit.ui.treasure.world.buildWorld

object Worlds {
    val TEST_WORLD = buildWorld(7, 7) {
        placeTreasure(1, 4)
        placeTreasure(2, 2)
        placeTreasure(3, 6)
        placeTreasure(4, 1)
        placeTreasure(5, 4)
        placeTreasureHunter(6, 3)
    }
}