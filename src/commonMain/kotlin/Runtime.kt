expect class Runtime {
    fun Emulator.execute()
}

operator fun ByteArray.get(index: UInt): UInt {
    return this[index.toInt()].toUInt() and 0b0000000011111111u
}

operator fun ByteArray.set(index: UInt, value: Byte) {
    this[index.toInt()] = value
}
