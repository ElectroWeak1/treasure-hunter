package sk.stuba.fiit.ui.treasure

import sk.stuba.fiit.ui.treasure.virtualmachine.createProgram

object Programs {
    val BASIC_PROGRAM = createProgram {
        inc(0)
        inc(31)
        inc(16)
        dec(16)
        inc(5)
        print(0)
    }
}