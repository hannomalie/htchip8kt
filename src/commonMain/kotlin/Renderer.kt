interface Renderer {
    var drawGrid: Boolean
    var emulator: Emulator?
    fun draw()
}

class CommandlineRenderer : Renderer {
    override var emulator: Emulator? = null
    override var drawGrid = false

    override fun draw(): Unit = emulator?.let { emulator ->
        print(
            emulator.frameBuffer.joinToString("\n") { line ->
                line.joinToString("") { value -> if (value) "X" else "_" }
            }
        )
        println()
    } ?: Unit
}
