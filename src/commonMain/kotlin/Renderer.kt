interface Renderer {
    var drawGrid: Boolean
    var emulator: Emulator?
    fun draw()
    fun update(deltaSeconds: Float) {}
}

class CommandlineRenderer : Renderer {
    override var emulator: Emulator? = null
    override var drawGrid = false

    override fun draw(): Unit = emulator?.let { emulator ->
        print((0 until Display.dimension.y).joinToString("\n") { row ->
            emulator.frameBuffer[row].joinToString("") { if(it) "*" else " " }
        })
    } ?: Unit
}
