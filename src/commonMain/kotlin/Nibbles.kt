data class Nibbles(val firstByte: UByte, val secondByte: UByte) {
    val a = (firstByte.toUInt() shr 4) and 0b0000000000001111u
    val b = (firstByte.toUInt() and 0b0000000000001111u)
    val c = (secondByte.toUInt() shr 4) and 0b0000000000001111u
    val d = (secondByte.toUInt() and 0b0000000000001111u)
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
    0u -> when {
        b == 0u && c == 0x0u && d == 0x0u -> NoOp
        b == 0u && c == 0xEu && d == 0x0u -> ClearScreen(this)
        b == 0u && c == 0xEu && d == 0xEu -> Ret(this)
        else -> CallMachineCode(this)
    }
    1u -> Jump(this)
    2u -> Call(this)
    3u -> SkipNextInstructionIfEqual(this)
    4u -> SkipNextInstructionIfNotEqual(this)
    5u -> SkipNextInstructionIfRegistersEqual(this)
    6u -> SetX(this)
    7u -> AddX(this)
    8u -> when(d) {
        0u -> SetXToY(this)
        1u -> SetXToXOrY(this)
        2u -> SetXToXAndY(this)
        3u -> SetXToXXOrY(this)
        4u -> AddYToX(this)
        5u -> SubtractYFromX(this)
        6u -> ShiftRight(this)
        7u -> SetXToYSubtractX(this)
        0xEu -> ShiftLeft(this)
        else -> Unknown(this)
    }
    9u -> SkipNextInstructionIfRegistersNotEqual(this)
    0xAu -> SetIndex(this)
    0xBu -> Flow(this)
    0xCu -> Random(this)
    0xDu -> Draw(this)
    0xEu -> when (c) {
        9u -> when(d) {
            0xEu -> SkipNextIfKeyPressed(this)
            else -> Unknown(this)
        }
        0xAu -> when(d) {
            1u -> SkipNextIfKeyNotPressed(this)
            else -> Unknown(this)
        }
        else -> Unknown(this)
    }
    0xFu -> {
        when (c) {
            0u -> when (d) {
                7u -> Timer(this)
                0xAu -> KeyOp(this)
                else -> Unknown(this)
            }
            1u -> when(d) {
                5u -> SetDelayToX(this)
                8u -> SetSoundToX(this)
                0xEu -> AddXToINoVF(this)
                else -> Unknown(this)
            }
            2u -> when(d) {
                9u -> SetIToSpriteAtX(this)
                else -> Unknown(this)
            }
            3u -> when(d) {
                3u -> BCD(this)
                else -> Unknown(this)
            }
            5u -> when(d) {
                5u -> RegDump(this)
                else -> Unknown(this)
            }
            6u -> when(d) {
                5u -> RegLoad(this)
                else -> Unknown(this)
            }
            else -> Unknown(this)
        }
    }
    else -> Unknown(this)
}