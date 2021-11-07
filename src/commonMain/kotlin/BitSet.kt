expect class BitSet(size: Int) {
    operator fun get(index: Int): Boolean
    fun set(index: Int, value: Boolean)
    fun clear(index: Int)
    fun or(another: BitSet)
}
expect fun BitSet(byte: Byte): BitSet