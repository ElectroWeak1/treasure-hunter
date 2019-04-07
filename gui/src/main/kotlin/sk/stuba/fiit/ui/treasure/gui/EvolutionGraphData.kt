package sk.stuba.fiit.ui.treasure.gui

data class EvolutionGraphData(
        val generation: Int,
        val fitnessBest: Int,
        val fitnessAverage: Int,
        val fitnessWorst: Int
)