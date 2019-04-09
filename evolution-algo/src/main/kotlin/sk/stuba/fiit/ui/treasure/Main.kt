package sk.stuba.fiit.ui.treasure

import sk.stuba.fiit.ui.treasure.evolution.EvolutionEngine
import sk.stuba.fiit.ui.treasure.evolution.EvolutionStep
import sk.stuba.fiit.ui.treasure.virtualmachine.Memory
import sk.stuba.fiit.ui.treasure.virtualmachine.Program
import sk.stuba.fiit.ui.treasure.virtualmachine.VirtualMachine
import sk.stuba.fiit.ui.treasure.world.Operator
import sk.stuba.fiit.ui.treasure.world.Path
import sk.stuba.fiit.ui.treasure.world.buildWorld

fun main() {
    val originalWorld = buildWorld(7, 7) {
        placeTreasure(1, 4)
        placeTreasure(2, 2)
        placeTreasure(3, 6)
        placeTreasure(4, 1)
        placeTreasure(5, 4)
        placeTreasureHunter(6, 3)
    }
    val checkFunction: (EvolutionStep) -> Boolean = {
        it.bestFitness.data<Pair<Int, List<Operator>>>().first >= originalWorld.treasureCount
    }
    val engine = EvolutionEngine(200, 64, 0.05f, 5, true, checkFunction) {
        val world = originalWorld.pool.take()

        val vm = VirtualMachine(Memory(it.genes.size, it.genes))
        vm.execute()

        val path = Path.fromString(vm.programOutput)
        world.reset()

        val validPath = world.treasureHunter.move(path)
        val fitness = Math.max(0, world.treasureHunter.treasuresCollected * 20 - world.treasureHunter.steps)
        originalWorld.pool.give(world)

        it.dataHolder = world.treasureHunter.treasuresCollected to validPath
        fitness
    }

    engine.setOnNewBest { generation, chromosome ->
        val data = chromosome.data<Pair<Int, List<Operator>>>()
        println("New best: $generation: Fitness: ${chromosome.fitness} (${data.first}/${originalWorld.treasureCount}) ${data.second.joinToString()}")
    }

    val result = engine.streamPopulation().onEach {
        if (it.population.generation % 250 == 0) {
            val data = it.bestFitness.data<Pair<Int, List<Operator>>>()
            println("Generation ${it.population.generation}: Fitness: ${it.bestFitness} (${data.first}/${originalWorld.treasureCount}) ${data.second.joinToString()}")
        }
    }.maxBy { it.bestFitness.fitness }!!
    val program = Program.parse(result.bestFitness.genes)
    println("Generation: ${result.population.generation}, ${result.bestFitness}")
    println(program)
}