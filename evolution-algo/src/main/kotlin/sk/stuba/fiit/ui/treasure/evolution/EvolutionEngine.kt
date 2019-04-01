package sk.stuba.fiit.ui.treasure.evolution

import kotlinx.coroutines.*
import sk.stuba.fiit.ui.treasure.evolution.selection.SelectionMethod
import sk.stuba.fiit.ui.treasure.evolution.selection.TournamentSelection

class EvolutionEngine(
    private val populationSize: Int,
    private val chromosomeSize: Int,
    private val mutationChance: Float = 0.05f,
    private val eliteClones: Int = 1,
    private val fitnessFunction: (Chromosome) -> FitnessResult
) {
    var selectionMethod: SelectionMethod = TournamentSelection(3)

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

    fun stream(): Sequence<EvolutionResult> {
        val initialPopulation = Population(populationSize, chromosomeSize, 0)
        var success = computeFitness(initialPopulation)
        return generateSequence(initialPopulation) { population ->
            if (success) {
                null
            } else {
                val offsprings = mutableListOf<Chromosome>()
                repeat(eliteClones) {
                    offsprings += population.chromosomes.filter { !offsprings.contains(it) }.maxBy { it.fitness }!!.copy()
                }
                while (offsprings.size < populationSize) {
                    val first = selectionMethod.select(population)
                    val second = selectionMethod.select(population)
                    val offspring = first.crossover(second)
                    offspring.mutate(mutationChance)
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
            }
        }.flatMap { population -> population.chromosomes.map { EvolutionResult(it, population) }.asSequence() }
    }
}