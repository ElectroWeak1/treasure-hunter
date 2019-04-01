package sk.stuba.fiit.ui.treasure.evolution.selection

import sk.stuba.fiit.ui.treasure.evolution.Chromosome
import sk.stuba.fiit.ui.treasure.evolution.Population

class TournamentSelection(private val size: Int) : SelectionMethod {
    override fun select(population: Population, exclude: Chromosome?): Chromosome {
        return population.select(size).filter { it != exclude }.maxBy { it.fitness }!!
    }
}