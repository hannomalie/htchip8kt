interface Renderer {
    var drawGrid: Boolean
    var crtEffect: Boolean
    var emulator: Emulator?
    fun draw()
    fun update(deltaSeconds: Float) { }
}

class CommandlineRenderer : Renderer {
    override var emulator: Emulator? = null
    override var drawGrid = false
    override var crtEffect = true

    override fun draw(): Unit = emulator?.let { emulator ->
        print(
            emulator.frameBuffer.joinToString("\n") { line ->
                line.joinToString("") { value -> if (value) "X" else "_" }
            }
        )
        println()
    } ?: Unit
}
