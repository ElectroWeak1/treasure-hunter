package sk.stuba.fiit.ui.treasure.evolution

import java.util.*
import java.util.function.BiConsumer
import java.util.function.BinaryOperator
import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.Collector

object EvolutionCollectors {

    class Holder<T>(var instance: T? = null)

    class BestChromosomeCollector : Collector<Population, Holder<Chromosome>, Chromosome> {
        override fun characteristics(): MutableSet<Collector.Characteristics> =
            Collections.emptySet<Collector.Characteristics>()

        override fun supplier() = Supplier { Holder<Chromosome>() }

        override fun accumulator() = BiConsumer<Holder<Chromosome>, Population> { t, u ->
            u.chromosomes.maxBy { it.fitness }?.let {
                if (t.instance == null || t.instance!!.fitness < it.fitness) {
                    t.instance = it
                }
            }
        }

        override fun combiner() = BinaryOperator<Holder<Chromosome>> { t, u ->
            if (t.instance != null && u.instance != null) {
                if (t.instance!!.fitness > u.instance!!.fitness) t else u
            } else {
                t
            }
        }

        override fun finisher() = Function<Holder<Chromosome>, Chromosome> { it.instance!! }
    }

    fun bestChromosome() = BestChromosomeCollector()
}