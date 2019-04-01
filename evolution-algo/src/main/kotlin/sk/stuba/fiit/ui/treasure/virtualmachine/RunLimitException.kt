package sk.stuba.fiit.ui.treasure.virtualmachine

class RunLimitException(runLimit: Int) : Exception("Run limit achieved ($runLimit)")