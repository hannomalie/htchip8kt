@OptIn(ExperimentalUnsignedTypes::class)
class Emulator(runtime: Runtime) {

    internal val renderer: Renderer = runtime.renderer
    internal val keyListener: KeyListener = runtime.keyListener

    var awaitingKeyIndexPressed: UInt? = null
    var skipNextInstruction = false
    private val display = Display()
    private var currentOpCode: OpCode = NoOp
    internal var drawRequested = true
    internal val memory = ByteArray(4096) { 0 }.apply {
        Font.values().forEachIndexed { letterIndex, it ->
            it.sprite.bytes.forEachIndexed { index, letterUByte ->
                this[Font.bytesPerLetter * letterIndex + index] = letterUByte.toByte()
            }
        }
    }
    internal val V = ByteArray(16) { 0 }

    internal var I: UShort = 0u
    internal var programCounter: UShort = gameOffset.toUShort()
    internal val stack = Array<UShort>(16) { 0u }
    internal var stackPointer = 0
    internal val keysDown by keyListener::keysDown

    internal var delay = 0u
    internal var sound = 0u

    val frameBuffer = createFrameBuffer()

    init {
        renderer.emulator = this
    }

    fun step(deltaSeconds: Float) {
        val firstByte = memory[programCounter.toInt()]
        val secondByte = memory[(programCounter.toInt() + 1)]
        val nibbles = Nibbles(firstByte.toUByte(), secondByte.toUByte())
        currentOpCode = nibbles.toOpcode()
        programCounter = (programCounter + 2.toUShort()).toUShort()

        when (val awaitingKeyIndexPressed = awaitingKeyIndexPressed) {
            null -> if(skipNextInstruction) {
                skipNextInstruction = false
            } else {
                currentOpCode.run {
                    execute()
                    renderer.update(deltaSeconds)
                    if(renderer.crt || drawRequested || frameBufferDirty) {
                        display.run {
                            renderer.draw()
                        }
                        drawRequested = false
                    }
                }
            }
            else -> {
                if(keysDown.map { it.index }.contains(awaitingKeyIndexPressed)) {
                    this.awaitingKeyIndexPressed = null
                }
                programCounter = (programCounter - 2.toUShort()).toUShort()
            }
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
    private fun createFrameBuffer() = Array(Display.dimension.y) {
        BooleanArray(Display.dimension.x) { false }
    }
    companion object {
        const val gameOffset = 512
        const val fontOffset = 50
    }
}


