package sk.stuba.fiit.ui.treasure

import sk.stuba.fiit.ui.treasure.world.Position
import sk.stuba.fiit.ui.treasure.world.Tile

infix fun Int.by(value: Int) = Position(this, value)

fun Array<Array<Tile>>.deepCopy() = Array(size) { get(it).clone() }

fun Int.bitCount(bits: Int): Int {
    var bitCount = 0
    repeat(bits) {
        if ((this shr it) and 0x01 > 0) bitCount++
    }
    return bitCount
}
