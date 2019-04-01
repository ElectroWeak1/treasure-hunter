package sk.stuba.fiit.ui.treasure

import sk.stuba.fiit.ui.treasure.evolution.EvolutionEngine
import sk.stuba.fiit.ui.treasure.evolution.FitnessResult
import sk.stuba.fiit.ui.treasure.virtualmachine.*
import sk.stuba.fiit.ui.treasure.world.InvalidPathException
import sk.stuba.fiit.ui.treasure.world.Path
import sk.stuba.fiit.ui.treasure.world.buildWorld

fun main() {
    var count = 0
    var best = 0
    val startTime = System.currentTimeMillis()
    var time = System.currentTimeMillis()

    val originalWorld = buildWorld(7, 7) {
        placeTreasure(1, 4)
        placeTreasure(2, 2)
        placeTreasure(3, 6)
        placeTreasure(4, 1)
        placeTreasure(5, 4)
        placeTreasureHunter(6, 3)
    }
    val engine = EvolutionEngine(200, 64, 0.1f, 5) {
        val world = originalWorld.pool.take()

        val vm = VirtualMachine(Memory(64, it.genes))
        vm.execute()

        val path = Path.fromString(vm.programOutput)
        world.reset()
        val validPath = world.treasureHunter.move(path)

        val fitness = Math.max(0, world.treasureHunter.treasuresCollected * 20 - world.treasureHunter.steps)

        val collected = world.treasureHunter.treasuresCollected
        val treasures = world.treasureCount
        originalWorld.pool.give(world)

        count++
        if (count % (1000 * 100) == 0) {
            println("Generation ${count / 100}: Fitness: $fitness ($collected/$treasures) (Time: ${System.currentTimeMillis() - time} ms) $validPath")
            time = System.currentTimeMillis()
        }

        if (fitness > best) {
            best = fitness
            println("New best: ${count / 100}: Fitness: $fitness ($collected/$treasures) (Time: ${System.currentTimeMillis() - time} ms) $validPath")
        }

        if (collected >= treasures && fitness >= 84) {
            println("Found path ${count / 100}: Fitness: $fitness ($collected/$treasures) (Time: ${System.currentTimeMillis() - time} ms) $validPath")
            FitnessResult.Success(fitness)
        } else {
            FitnessResult.Next(fitness)
        }
    }

    val result = engine.stream().maxBy { it.chromosome.fitness }!!
    val program = Program.parse(result.chromosome.genes)
    println("Generation: ${result.population.generation}, ${result.chromosome}")
    println("Time taken: ${System.currentTimeMillis() - startTime} ms")
    println(program)
}