package sk.stuba.fiit.ui.treasure.evolution

import kotlinx.coroutines.*
import sk.stuba.fiit.ui.treasure.evolution.selection.SelectionMethod
import sk.stuba.fiit.ui.treasure.evolution.selection.TournamentSelection

class EvolutionEngine(
    private val populationSize: Int,
    private val chromosomeSize: Int,
    private val mutationChance: Float = 0.05f,
    private val eliteClones: Int = 1,
    private val variableMutation: Boolean = false,
    private val fitnessFunction: suspend (Chromosome) -> FitnessResult
) {
    var selectionMethod: SelectionMethod = TournamentSelection(3)
    var isRunning = false
    private var sameFitnessCount = 0
    private var lastFitness = 0
    private var mutationVariation = 0.0f
    private var mutationListener: (Float) -> Unit = {}

    private fun computeFitness(population: Population): Boolean {
        var success = false
        runBlocking {
            val jobs = mutableListOf<Job>()
            population.chromosomes.forEach {
                jobs += launch(Dispatchers.Default) {
                    val fitnessResult = fitnessFunction(it)
                    when (fitnessResult) {
                        is FitnessResult.Next -> it.fitness = fitnessResult.fitness
                        is FitnessResult.Success -> {
                            it.fitness = fitnessResult.fitness
                            success = true
                        }
                    }
                }
            }
            jobs.forEach { it.join() }
        }
        return success
    }

    fun stream() = streamPopulation().flatMap { population -> population.chromosomes.map { EvolutionResult(it, population) }.asSequence() }

    fun streamPopulation(): Sequence<Population> {
        val initialPopulation = Population(populationSize, chromosomeSize, 0)
        var success = computeFitness(initialPopulation)
        isRunning = true
        return generateSequence(initialPopulation) { population ->
            if (isRunning) {
                if (!success) {
                    if (variableMutation) {
                        val bestFitness = population.chromosomes.maxBy { it.fitness }!!.fitness
                        if (bestFitness == lastFitness) {
                            sameFitnessCount++
                        } else {
                            mutationVariation = 0.0f
                            mutationListener(mutationVariation)
                            sameFitnessCount = 0
                        }
                        lastFitness = bestFitness
                    }

                    if (sameFitnessCount >= 10000) {
                        if (population.generation % 2000 == 0) {
                            if (mutationVariation < 0.5f) {
                                mutationVariation += 0.01f
                            }
                            mutationListener(mutationVariation)
                        }
                    }

                    val offsprings = mutableListOf<Chromosome>()
                    repeat(eliteClones) {
                        offsprings += population.chromosomes.filter { !offsprings.contains(it) }.maxBy { it.fitness }!!.copy()
                    }
                    while (offsprings.size < populationSize) {
                        val first = selectionMethod.select(population)
                        val second = selectionMethod.select(population)
                        val offspring = first.crossover(second)
                        offspring.mutate(mutationChance + mutationVariation)
                        offsprings += offspring
                    }
                    val nextPopulation = Population(
                            populationSize,
                            chromosomeSize,
                            population.generation + 1,
                            chromosomes = offsprings.toTypedArray()
                    )
                    success = computeFitness(nextPopulation)
                    nextPopulation
                } else null
            } else null
        }
    }

    fun stop() {
        isRunning = false
    }

    fun setOnMutationChange(mutationListener: (Float) -> Unit) {
        this.mutationListener = mutationListener
    }
}