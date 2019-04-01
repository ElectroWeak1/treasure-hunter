package sk.stuba.fiit.ui.treasure.virtualmachine

class SegmentationFaultException(address: Int) : Exception("Segmentation fault (0x${address.toString(16)})")