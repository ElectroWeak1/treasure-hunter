package sk.stuba.fiit.ui.treasure.evolution.selection

import sk.stuba.fiit.ui.treasure.evolution.Chromosome
import sk.stuba.fiit.ui.treasure.evolution.Population
import java.lang.IllegalArgumentException
import kotlin.random.Random

class RouletteSelection : SelectionMethod {
    override fun select(population: Population, exclude: Chromosome?): Chromosome {
        var count = 0
        val maxFitness = population.chromosomes.sumBy { it.fitness }
        val random = Random.nextInt(maxFitness)
        population.chromosomes.filter { it != exclude }.sortedByDescending { it.fitness }.forEach {
            count += it.fitness
            if (count >= random) {
                return it
            }
        }
        throw IllegalArgumentException("Error selecting")
    }
}