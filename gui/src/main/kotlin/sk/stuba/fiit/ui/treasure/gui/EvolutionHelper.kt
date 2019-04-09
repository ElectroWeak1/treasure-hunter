package sk.stuba.fiit.ui.treasure.gui

import kotlinx.coroutines.*
import sk.stuba.fiit.ui.treasure.evolution.EvolutionEngine
import sk.stuba.fiit.ui.treasure.evolution.EvolutionStep
import sk.stuba.fiit.ui.treasure.evolution.FitnessResult
import sk.stuba.fiit.ui.treasure.evolution.selection.RouletteSelection
import sk.stuba.fiit.ui.treasure.evolution.selection.TournamentSelection
import sk.stuba.fiit.ui.treasure.virtualmachine.Memory
import sk.stuba.fiit.ui.treasure.virtualmachine.VirtualMachine
import sk.stuba.fiit.ui.treasure.world.Operator
import sk.stuba.fiit.ui.treasure.world.Path
import sk.stuba.fiit.ui.treasure.world.buildWorld

class EvolutionHelper {
    val originalWorld = buildWorld(7, 7) {
        placeTreasure(1, 4)
        placeTreasure(2, 2)
        placeTreasure(3, 6)
        placeTreasure(4, 1)
        placeTreasure(5, 4)
        placeTreasureHunter(6, 3)
    }

    val isRunning: Boolean
        get() {
            return engine?.isRunning ?: false
        }

    private var engine: EvolutionEngine? = null

    suspend fun run(
            populationSize: Int,
            chromosomeSize: Int,
            mutationChance: Float,
            eliteClones: Int,
            crossoverMethod: CrossoverMethod,
            variableMutation: Boolean,
            endAfterSolution: Boolean,
            fitnessLimit: Int,
            showEach: Int,
            onNewBest: (Int, Int, List<Operator>) -> Unit = { _, _, _ -> },
            onGraphUpdate: (EvolutionGraphData) -> Unit = {},
            onMutationChange: (Float) -> Unit = {}
    ): Deferred<EvolutionGraphData> = coroutineScope {
        async(Dispatchers.Default) {
            val checkFunction: (EvolutionStep) -> Boolean = {
                val data = it.bestFitness.data<Pair<Int, List<Operator>>>()
                (data.first >= originalWorld.treasureCount && endAfterSolution) || (fitnessLimit != -1 && it.bestFitness.fitness >= fitnessLimit)
            }
            engine = EvolutionEngine(populationSize, chromosomeSize, mutationChance, eliteClones, variableMutation, checkFunction) {
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
            engine?.setOnNewBest { generation, chromosome ->
                launch(Dispatchers.Main) {
                    onNewBest(chromosome.fitness, generation, chromosome.data<Pair<Int, List<Operator>>>().second)
                }
            }
            engine?.setOnMutationChange {
                launch(Dispatchers.Main) {
                    onMutationChange(it)
                }
            }
            engine?.selectionMethod = when (crossoverMethod) {
                CrossoverMethod.TOURNAMENT -> TournamentSelection(3)
                CrossoverMethod.ROULETTE -> RouletteSelection()
            }
            val result = engine?.streamPopulation()?.map { step ->
                EvolutionGraphData(
                        step.population.generation,
                        step.population.chromosomes.maxBy { it.fitness }!!.fitness,
                        step.population.chromosomes.sumBy { it.fitness } / step.population.chromosomes.count(),
                        step.population.chromosomes.minBy { it.fitness }!!.fitness
                )
            }?.onEach {
                if (it.generation % showEach == 0) {
                    launch(Dispatchers.Main) {
                        onGraphUpdate(it)
                    }
                }
            }?.last()!!
            result
        }
    }

    fun stop() {
        if (isRunning) {
            engine?.stop()
        }
    }
}