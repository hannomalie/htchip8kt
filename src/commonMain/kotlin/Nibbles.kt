data class Nibbles(val firstByte: UByte, val secondByte: UByte) {
    val a = (firstByte.toUInt() shr 4) and 0b0000000000001111.toUInt()
    val b = (firstByte.toUInt() and 0b0000000000001111.toUInt())
    val c = (secondByte.toUInt() shr 4) and 0b0000000000001111.toUInt()
    val d = (secondByte.toUInt() and 0b0000000000001111.toUInt())
    val n = a
    val nn = secondByte.toUInt()
    val nnn = b.shl(8) + c.shl(4) + d
    val x = b
    val y = c

    override fun toString(): String {
        return """
            |first byte ${firstByte.toString(radix = 2)}
            |second byte ${secondByte.toString(radix = 2)}
            |${a.toString(radix = 2)} dezimal($a) hex(${a.toString(radix = 16)})
            |${b.toString(radix = 2)} dezimal($b) hex(${b.toString(radix = 16)})
            |${c.toString(radix = 2)} dezimal($c) hex(${c.toString(radix = 16)})
            |${d.toString(radix = 2)} dezimal($d) hex(${d.toString(radix = 16)})
        """.trimMargin()
    }
}
fun Nibbles.toOpcode(): OpCode = when(a) {
    0u -> {
        when {
            b == 0u && c == (0x0E).toUInt() -> ClearScreen(this)
            b == 0u && c == 0x0u && c == 0x0u -> NoOp
            else -> null
        }
    }
    1u -> Jump(this)
    3u -> SkipNextInstructionIfEqual(this)
    4u -> SkipNextInstructionIfNotEqual(this)
    6u -> SetRegisterX(this)
    7u -> AddToRegisterX(this)
    (0x0A).toUInt() -> SetIndex(this)
    (0x0D).toUInt() -> Draw(this)
    (0x0F).toUInt() -> {
        when {
            c == 0u -> {
                when {
                    d == 7u -> Timer(this)
                    else -> KeyOp(this)
                }
            }
            else -> null
        }
    }
    else -> null
} ?: Unknown(this)