package sk.stuba.fiit.ui.treasure.evolution

import kotlin.random.Random

class Population(
    private val populationSize: Int,
    private val chromosomeSize: Int,
    val generation: Int,
    private val randomCount: Int = 64,
    val chromosomes: Array<Chromosome> = Array(populationSize) { Chromosome(IntArray(chromosomeSize) {
        if (it > randomCount) 0 else Random.nextBits(8)
    }) }
) {
    fun select(count: Int): List<Chromosome> {
        val selection = mutableListOf<Chromosome>()
        repeat(count) { selection.add(chromosomes[Random.nextInt(populationSize - 1)]) }
        return selection
    }
}