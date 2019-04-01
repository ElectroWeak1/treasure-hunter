package sk.stuba.fiit.ui.treasure.virtualmachine

sealed class Instruction {
    data class Increment(val address: Int) : Instruction()
    data class Decrement(val address: Int) : Instruction()
    data class Jump(val address: Int) : Instruction()
    data class Print(val address: Int) : Instruction()
    object Invalid : Instruction()
}