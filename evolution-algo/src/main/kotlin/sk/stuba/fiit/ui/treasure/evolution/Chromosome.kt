package sk.stuba.fiit.ui.treasure.evolution

import kotlin.random.Random

data class Chromosome(
    val genes: IntArray,
    var fitness: Int = NO_FITNESS
) {
    var dataHolder: Any? = null

    @Suppress("UNCHECKED_CAST")
    fun <T> data(): T = dataHolder as T

    fun crossover(other: Chromosome): Chromosome {
        val crossoverPoint = Random.nextInt(genes.size)
        val offspringGenes = IntArray(genes.size) {
            if (it == crossoverPoint) {
                val mask = Random.nextBits(8)
                (genes[it] and mask) or (other.genes[it] and (mask.inv() and 0xff))
            } else {
                if (it < crossoverPoint) genes[it] else other.genes[it]
            }
        }
        return Chromosome(offspringGenes)
    }

    fun mutate(chance: Float) {
        // Make Random.nextFloat() go from 0 (exclusive) to 1 (inclusive) so with 0 chance mutation will not happen
        if (1 - Random.nextFloat() <= chance) {
            genes[Random.nextInt(genes.size)] = Random.nextBits(8)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Chromosome) return false

        if (!genes.contentEquals(other.genes)) return false
        if (fitness != other.fitness) return false

        return true
    }

    override fun hashCode(): Int {
        var result = genes.contentHashCode()
        result = 31 * result + fitness
        return result
    }

    companion object {
        const val NO_FITNESS = -1
    }
}