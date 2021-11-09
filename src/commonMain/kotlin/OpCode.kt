import kotlin.experimental.and
import kotlin.experimental.or
import kotlin.experimental.xor
import kotlin.random.Random

sealed interface OpCode {
    val nibbles: Nibbles
    fun Emulator.execute()
}
object NoOp: OpCode {
    override val nibbles = Nibbles(0u.toUByte(), 0u.toUByte())
    override fun Emulator.execute() { }
}

data class ClearScreen(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        frameBuffer.forEach {
            it.forEachIndexed { index, row ->
                it[index] = false
            }
        }
    }
}
data class Ret(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        programCounter = stack[stackPointer]
        stackPointer--
    }
}
data class Jump(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        programCounter = nibbles.nnn.toUShort()
    }
}
data class CallMachineCode(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        programCounter = nibbles.nnn.toUShort()
    }
}
data class Call(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        stackPointer++
        stack[stackPointer] = programCounter
        programCounter = nibbles.nnn.toUShort()
    }
}
data class Flow(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        programCounter = (V[0x00].toUShort() + nibbles.nnn.toUShort()).toUShort()
    }
}
data class Random(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        V[nibbles.x] = Random.nextBytes(1)[0]
    }
}
data class SetIndex(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        I = nibbles.nnn.toUShort()
    }
}
data class SkipNextIfKeyPressed(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        skipNextInstruction = keysPressed.contains(V[nibbles.x])
    }
}
data class SkipNextIfKeyNotPressed(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        skipNextInstruction = !keysPressed.contains(V[nibbles.x])
    }
}
data class Draw(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        V[0x0F] = 0
        val sprites = mutableListOf<Byte>()
        repeat(nibbles.d.toInt()) {
            sprites.add(memory[I.toInt() + it])
        }
        val xStart = V[nibbles.x.toInt()].toInt()
        val yStart = V[nibbles.y.toInt()].toInt()
        repeat(8) { currentX ->
            sprites.forEachIndexed { currentY, sprite ->
                val valueAfter = BitSet(sprite)[7-currentX]
                val resultingXCoord = (xStart + currentX) % Display.dimension.x
                val resultingYCoord = (yStart + currentY) % Display.dimension.y
                val valueBefore = frameBuffer[resultingXCoord][resultingYCoord]
                val setBit = valueBefore xor valueAfter
                frameBuffer[resultingXCoord][resultingYCoord] = setBit
                if (setBit) {
                    drawRequested = true
                }
                if (valueBefore && !valueAfter) {
                    V[0x0F] = 1
                }
            }
        }
    }
}
data class SetX(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        V[nibbles.x] = nibbles.nn.toUShort().toByte()
    }
}
data class AddX(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        V[nibbles.x] = (V[nibbles.x.toInt()].toUShort() + nibbles.nn.toUShort()).toByte()
    }
}
data class SetXToY(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        V[nibbles.x] = V[nibbles.y.toInt()]
    }
}
data class SetXToXOrY(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        V[nibbles.x] = V[nibbles.x.toInt()] or V[nibbles.y.toInt()]
    }
}
data class SetXToXAndY(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        V[nibbles.x] = V[nibbles.x.toInt()] and V[nibbles.y.toInt()]
    }
}
data class SetXToXXOrY(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        V[nibbles.x] = V[nibbles.x.toInt()] xor V[nibbles.y.toInt()]
    }
}
data class AddYToX(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        val result = V[nibbles.x] + V[nibbles.y.toInt()].toUInt()
        V[nibbles.x] = result.toByte()
        if(result > 255u) V[0x0F] = 1
    }
}
// TODO: Not correct
data class SubtractYFromX(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        val vX = V[nibbles.x]
        val vY = V[nibbles.y.toInt()].toUInt()
        V[nibbles.x] = (vX - vY).toByte()
        if (vX < vY) V[0x0F] = 0
    }
}
// TODO: Not correct
data class ShiftRight(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        V[nibbles.x] = (V[nibbles.x] shr 1).toByte()
    }
}
data class SetXToYSubtractX(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        V[nibbles.x] = (V[nibbles.y.toInt()].toUInt() - V[nibbles.x]).toByte()
    }
}
data class ShiftLeft(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        val vX = V[nibbles.x]
        if((vX shr 7) == 1u) V[0x0F] = 1
        V[nibbles.x] = (vX shl 1).toByte()
    }
}

data class SkipNextInstructionIfEqual(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        if(V[nibbles.x] == nibbles.nn) skipNextInstruction = true
    }
}
data class SkipNextInstructionIfNotEqual(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        if(V[nibbles.x] != nibbles.nn) skipNextInstruction = true
    }
}
data class SkipNextInstructionIfRegistersEqual(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        if(V[nibbles.x.toInt()] == V[nibbles.y.toInt()]) skipNextInstruction = true
    }
}
data class SkipNextInstructionIfRegistersNotEqual(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        if(V[nibbles.x.toInt()] != V[nibbles.y.toInt()]) skipNextInstruction = true
    }
}
data class KeyOp(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        awaitingKeyIndexPressed = nibbles.a
    }
}
data class Timer(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        V[nibbles.x] = delay.toByte()
    }
}
data class SetDelayToX(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        delay = V[nibbles.x]
    }
}
data class SetSoundToX(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        sound = V[nibbles.x]
    }
}
data class AddXToINoVF(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        I = (I + V[nibbles.x.toInt()].toUShort()).toUShort()
    }
}
data class SetIToSpriteAtX(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        println("Not implemented (SetIToSpriteAtX): $nibbles")
    }
}
data class BCD(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        val vX = V[nibbles.x] and 0b0000000011111111u
        val ones = (vX % 10u).toByte()
        val tens = ((vX % 100u) / 10u).toByte()
        val hundreds = (vX / 100u).toByte()

        memory[I.toInt()] = hundreds
        memory[I.toInt() + 1] = tens
        memory[I.toInt() + 2] = ones
    }
}
data class RegDump(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        repeat(nibbles.x.toInt() + 1) {
            memory[I.toInt() + it] = V[it]
        }
    }
}
data class RegLoad(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        repeat(nibbles.x.toInt() + 1) {
            V[it] = memory[I.toInt() + it]
        }
    }
}
data class Unknown(override val nibbles: Nibbles): OpCode {
    override fun Emulator.execute() {
        println("Unknown opcode: $nibbles")
    }
}
