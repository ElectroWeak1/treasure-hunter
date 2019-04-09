package sk.stuba.fiit.ui.treasure

import org.junit.Assert.assertEquals
import org.junit.Test
import sk.stuba.fiit.ui.treasure.evolution.EvolutionEngine
import sk.stuba.fiit.ui.treasure.evolution.FitnessResult

class PopulationTest {
    @Test
    fun `Test simple evolution`() {
        val engine = EvolutionEngine(20, 2) { chromosome ->
            val bits = chromosome.genes.sumBy { it.bitCount(8) }
            if (bits == chromosome.genes.size * 8) {
                FitnessResult.Success(bits)
            } else {
                FitnessResult.Next(bits)
            }
        }
        val result = engine.stream().maxBy { it.chromosome.fitness }!!
        println("${result.chromosome}\nGeneration: ${result.step.population.generation}")
        assertEquals(16, result.chromosome.fitness)
    }

    /*@Test
    fun `Test evolution`() {
        val world = Worlds.TEST_WORLD

        val evolution = EvolutionEngine(20, 64) { chromosomes ->
            val memory = Memory(64, chromosomes.genes)
            val vm = VirtualMachine(memory)
            vm.execute()

            val path = Path.fromString(vm.programOutput)
            world.reset()
            try {
                world.treasureHunter.move(path)
            } catch (exception: InvalidPathException) {}

            if (world.treasureHunter.treasuresCollected >= world.treasurePositions.size) {
                FitnessResult.Success
            }

            FitnessResult.Next(world.treasureHunter.treasuresCollected)
        }
//        evolution.selectionMethod = TournamentSelection(3)
//        evolution.mutationMethods = listOf(MutationMethod.DEFAULT with 0.5f, MutationMethod.REORDER with 0.5f)
//        evolution.mutationRate = 0.05f

        val result: Chromosome = evolution.stream().maxBy { it.fitness }!!
        val program = Program.parse(result.genes)
        println(program)
    }*/
}