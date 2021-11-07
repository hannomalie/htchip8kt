class Game(val bytyes: ByteArray)

expect class Renderer {
    var drawGrid: Boolean
    fun setEmulator(emulator: Emulator)
    fun requestDraw()
}
@OptIn(ExperimentalUnsignedTypes::class)
class Emulator(val renderer: Renderer) {
    var awaitingKeyIndexPressed: UInt? = null
    var skipNextInstruction = false
    private val display = Display()
    private var opCode: OpCode = NoOp
    internal var drawRequested = true
    internal val memory = ByteArray(4096) { 0 }
    internal val V = ByteArray(16) { 0 }

    internal var I: UShort = 0u
    internal var programCounter: UShort = gameOffset.toUShort()

    val frameBuffer = Array(64) {
        BooleanArray(32) { false }
    }
    init {
        renderer.setEmulator(this)
    }

    fun step() {
        val firstByte = memory[programCounter.toInt()]
        val secondByte = memory[(programCounter.toInt() + 1)]
        val nibbles = Nibbles(firstByte.toUByte(), secondByte.toUByte())
        opCode = nibbles.toOpcode()

        if(!skipNextInstruction && awaitingKeyIndexPressed == null) {
            opCode.run {
                execute()
                if(drawRequested) {
                    display.run {
                        draw()
                        renderer.requestDraw()
                    }
                    drawRequested = false
                }
                programCounter = (programCounter + 2.toUShort()).toUShort()
            }
            skipNextInstruction = false
        }
    }

    fun load(game: Game) {
        game.bytyes.forEachIndexed { index, byte ->
            memory[gameOffset + index] = byte
        }
        programCounter = gameOffset.toUShort()
    }

    fun restart() {
        programCounter = gameOffset.toUShort()
        skipNextInstruction = false
        awaitingKeyIndexPressed = null
    }

    companion object {
        const val gameOffset = 512
    }
}

class Display {

    fun Emulator.draw() {
        print(
            frameBuffer.joinToString("\n") { line ->
                line.joinToString("") { value -> if (value) "X" else "_" }
            }
        )
        println()
    }
}

expect class Runtime {
    fun Emulator.execute()
}
