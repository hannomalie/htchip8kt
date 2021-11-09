@OptIn(ExperimentalUnsignedTypes::class)
class Emulator(val renderer: Renderer) {
    var awaitingKeyIndexPressed: UInt? = null
    var skipNextInstruction = false
    private val display = Display()
    private var currentOpCode: OpCode = NoOp
    internal var drawRequested = true
    internal val memory = ByteArray(4096) { 0 }
    internal val V = ByteArray(16) { 0 }

    internal var I: UShort = 0u
    internal var programCounter: UShort = gameOffset.toUShort()
    internal val stack = Array<UShort>(16) { 0u }
    internal var stackPointer = 0
    internal var keysPressed = mutableSetOf<UInt>()

    internal var delay = 0u
    internal var sound = 0u

    val frameBuffer = Array(Display.dimension.x) {
        BooleanArray(Display.dimension.y) { false }
    }

    init {
        renderer.emulator = this
    }

    fun step() {
        val firstByte = memory[programCounter.toInt()]
        val secondByte = memory[(programCounter.toInt() + 1)]
        val nibbles = Nibbles(firstByte.toUByte(), secondByte.toUByte())
        currentOpCode = nibbles.toOpcode()
        programCounter = (programCounter + 2.toUShort()).toUShort()

        if(awaitingKeyIndexPressed == null) {
            if(skipNextInstruction) {
                skipNextInstruction = false
            } else {
                currentOpCode.run {
                    execute()
                    if(drawRequested || frameBufferDirty) {
                        display.run {
                            renderer.draw()
                        }
                        drawRequested = false
                    }
                }
            }
        } else {
            // TODO: check for key input here
        }
    }

    val frameBufferDirty: Boolean get() = V[0x0F].toUInt() == 1u && currentOpCode is Draw

    fun load(game: Game) {
        game.bytyes.forEachIndexed { index, byte ->
            memory[gameOffset + index] = byte
        }
        restart()
    }

    fun restart() {
        programCounter = gameOffset.toUShort()
        skipNextInstruction = false
        awaitingKeyIndexPressed = null
        clearFrameBuffer()
    }
    fun clearFrameBuffer() {
        frameBuffer.forEach {
            it.forEachIndexed { index, row ->
                it[index] = false
            }
        }
    }
    companion object {
        const val gameOffset = 512
    }
}

