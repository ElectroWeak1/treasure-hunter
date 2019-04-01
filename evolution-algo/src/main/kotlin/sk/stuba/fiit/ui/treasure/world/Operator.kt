package sk.stuba.fiit.ui.treasure.world

enum class Operator(val value: String, val rowShift: Int, val columnShift: Int) {
    LEFT("L", 0, -1),
    RIGHT("R", 0, 1),
    UP("U", -1, 0),
    DOWN("D", 1, 0)
}