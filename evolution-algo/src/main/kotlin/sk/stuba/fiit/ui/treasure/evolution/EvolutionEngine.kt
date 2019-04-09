package sk.stuba.fiit.ui.treasure.evolution

import javafx.application.Application.launch
import kotlinx.coroutines.*
import sk.stuba.fiit.ui.treasure.evolution.selection.SelectionMethod
import sk.stuba.fiit.ui.treasure.evolution.selection.TournamentSelection
import java.util.*

data class EvolutionStep(
        val population: Population,
        val bestFitness: Chromosome,
        val worstFitness: Chromosome,
        val averageFitness: Float
)

class EvolutionEngine(
        private val populationSize: Int,
        private val chromosomeSize: Int,
        private val mutationChance: Float = 0.05f,
        private val eliteClones: Int = 1,
        private val variableMutation: Boolean = false,
        private val checkFunction: (EvolutionStep) -> Boolean,
        private val fitnessFunction: suspend (Chromosome) -> Int
) {
    var selectionMethod: SelectionMethod = TournamentSelection(3)
    var isRunning = false
    private val variableMutationThreshold = 2_000_000 / populationSize
    private val variableMutationStep = 600_000 / populationSize
    private var sameFitnessCount = 0
    private var lastFitness = 0
    private var mutationVariation = 0.0f
    private var onNewBestListener: (Int, Chromosome) -> Unit = { _, _ -> }
    private var mutationListener: (Float) -> Unit = {}

    private fun computeFitness(population: Population): Chromosome {
        var best = population.chromosomes.first()
        runBlocking {
            val jobs = mutableListOf<Job>()
            population.chromosomes.forEach {
                jobs += launch(Dispatchers.Default) {
                    it.fitness = fitnessFunction(it)
                    if (it.fitness > best.fitness) {
                        best = it
                    }
                }
            }
            jobs.forEach { it.join() }
        }
        return best
    }

    fun stream() = streamPopulation().flatMap { step -> step.population.chromosomes.map { EvolutionResult(it, step) }.asSequence() }

    fun streamPopulation(): Sequence<EvolutionStep> {
        val initialPopulation = Population(populationSize, chromosomeSize, 0)
        var best = computeFitness(initialPopulation)
        var found = false
        val initialFitnesses = computeFitnesses(initialPopulation.chromosomes.toList())
        isRunning = true
        return generateSequence(EvolutionStep(initialPopulation, initialFitnesses.first, initialFitnesses.second,
                initialFitnesses.third)) { step ->
            if (isRunning) {
                if (!found) {
                    found = checkFunction(step)
                    if (!found) {
                        if (variableMutation) {
                            updateSameFitnessCounter(step.bestFitness.fitness)
                        }
                        updateVariableMutation(step.population.generation)
                        val offsprings = Collections.synchronizedList(ArrayList<Chromosome>(populationSize))
                        repeatConcurrent(eliteClones) {
                            offsprings += step.population.chromosomes.filter { !offsprings.contains(it) }.maxBy { it.fitness }!!.copy()
                        }
                        repeatConcurrent(populationSize - offsprings.size) {
                            val first = selectionMethod.select(step.population)
                            val second = selectionMethod.select(step.population)
                            val offspring = first.crossover(second)
                            offspring.mutate(mutationChance + mutationVariation)
                            offsprings += offspring
                        }
                        val fitnesses = computeFitnesses(offsprings)
                        val nextPopulation = Population(
                                populationSize,
                                chromosomeSize,
                                step.population.generation + 1,
                                chromosomes = offsprings.toTypedArray()
                        )
                        val nextStep = EvolutionStep(nextPopulation, fitnesses.first, fitnesses.second, fitnesses.third)
                        val currentBest = computeFitness(nextPopulation)
                        if (currentBest.fitness > best.fitness) {
                            best = currentBest
                            onNewBestListener(step.population.generation, currentBest)
                        }
                        nextStep
                    } else {
                        null
                    }
                } else null
            } else null
        }
    }

    fun stop() {
        isRunning = false
    }

    fun setOnNewBest(onNewBestListener: (Int, Chromosome) -> Unit) {
        this.onNewBestListener = onNewBestListener
    }

    fun setOnMutationChange(mutationListener: (Float) -> Unit) {
        this.mutationListener = mutationListener
    }

    private fun computeFitnesses(population: List<Chromosome>): Triple<Chromosome, Chromosome, Float> {
        val bestFitness = population.maxBy { it.fitness }!!
        val worstFitness = population.minBy { it.fitness }!!
        val averageFitness = population.sumBy { it.fitness }.toFloat() / population.size
        return Triple(bestFitness, worstFitness, averageFitness)
    }

    private fun updateVariableMutation(generation: Int) {
        if (sameFitnessCount >= variableMutationThreshold) {
            if (generation % variableMutationStep == 0) {
                if (mutationVariation < 0.5f) {
                    mutationVariation += 0.01f
                }
                mutationListener(mutationVariation)
            }
        }
    }

    private fun updateSameFitnessCounter(fitness: Int) {
        if (fitness == lastFitness) {
            sameFitnessCount++
        } else {
            mutationVariation = 0.0f
            mutationListener(mutationVariation)
            sameFitnessCount = 0
        }
        lastFitness = fitness
    }

    private fun repeatConcurrent(times: Int, dispatcher: CoroutineDispatcher = Dispatchers.Default, block: () -> Unit) {
        runBlocking {
            val jobs = mutableListOf<Job>()
            repeat(times) {
                jobs += launch(dispatcher) { block() }
            }
            jobs.forEach { it.join() }
        }
    }
}