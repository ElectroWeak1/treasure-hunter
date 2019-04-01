package sk.stuba.fiit.ui.treasure.evolution.selection

import sk.stuba.fiit.ui.treasure.evolution.Chromosome
import sk.stuba.fiit.ui.treasure.evolution.Population

interface SelectionMethod {
    fun select(population: Population, exclude: Chromosome? = null): Chromosome
}