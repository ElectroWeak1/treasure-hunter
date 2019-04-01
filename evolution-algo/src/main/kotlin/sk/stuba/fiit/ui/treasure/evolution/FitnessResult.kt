package sk.stuba.fiit.ui.treasure.evolution

sealed class FitnessResult {
    data class Success(val fitness: Int) : FitnessResult()
    data class Next(val fitness: Int) : FitnessResult()
}