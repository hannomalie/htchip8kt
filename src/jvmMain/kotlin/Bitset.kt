import java.util.BitSet

actual typealias BitSet = BitSet
actual fun BitSet(byte: Byte) = BitSet.valueOf(byteArrayOf(byte))
