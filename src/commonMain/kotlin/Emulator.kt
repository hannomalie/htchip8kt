class Game(val bytyes: ByteArray)

@OptIn(ExperimentalUnsignedTypes::class)
class Emulator {
    private var opCode: UShort = 0u
    private val memory = ByteArray(4096) { 0 }
    private val registers = ByteArray(16) { 0 }

    private var index: UShort = 0u
    private var programCounter: UShort = 0u

    val displayValues = Array(32) {
        BooleanArray(64) { false }
    }


    fun load(game: Game) {
        game.bytyes.forEachIndexed { index, byte ->
            memory[gameOffset + index] = byte
        }
        programCounter = 0u
    }
    companion object {
        const val gameOffset = 512
    }
}

class Display {

    fun Emulator.draw() {
        print(
            displayValues.joinToString("\n") { line ->
                line.joinToString("") { value -> if (value) "X" else "_" }
            }
        )
        println()
    }
}

expect class Runtime {
    fun Emulator.execute()
}
