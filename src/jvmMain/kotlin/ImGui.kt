import Emulator.Companion.createFrameBuffer
import imgui.ImGui
import imgui.app.Application
import imgui.app.Configuration
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImInt
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.GLFW_FALSE
import org.lwjgl.glfw.GLFW.GLFW_RESIZABLE
import java.awt.Color
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess

class ImGuiRenderer : Application(), Renderer, KeyListener {

    override var drawGrid = false
    override var crt = false
    override var emulator: Emulator? = null
    private val _keysDown = ConcurrentHashMap.newKeySet<Keys>()
    override val keysDown: Set<Keys> = _keysDown

    init {
        Thread { launch(this) }.start()
    }
    override fun configure(config: Configuration) {
        config.title = "htchip8kt on ImGui rocks!"
        config.width = 1200
        config.height = 720
    }

    private val renderMode = ImInt(0)
    private val copyingFrameBuffer = AtomicBoolean(false)
    private val frameBufferCopy = createFrameBuffer()

    override fun process() {
        while(copyingFrameBuffer.get()) {
            Thread.onSpinWait()
        }

        val windowWidth = ImGui.getIO().displaySizeX
        val windowHeight = ImGui.getIO().displaySizeX

        ImGui.setNextWindowBgAlpha(0f)
        ImGui.setNextWindowSize(windowWidth, windowHeight)
        ImGui.setNextWindowPos(0f, 0f)
        val flags =
            ImGuiWindowFlags.NoTitleBar or ImGuiWindowFlags.NoResize or
            ImGuiWindowFlags.NoMove or ImGuiWindowFlags.NoScrollbar
        ImGui.begin("CHIP-8", flags)

        ImGui.text("Render Mode: "); ImGui.sameLine()
        ImGui.radioButton("Text", renderMode, 0); ImGui.sameLine()
        ImGui.radioButton("Blocks", renderMode, 1)

        ImGui.text("Framerate: " + ImGui.getIO().framerate)
        when(renderMode.get()) {
            0 -> {
                frameBufferCopy.forEachRowIndexed { _, row ->
                    row.forEachColumnIndexed { _, column ->
                        ImGui.textWrapped(if (column) "#" else " "); ImGui.sameLine()
                    }
                    ImGui.text("\n")
                }
            }
            1 -> {
                frameBufferCopy.forEachRowIndexed { rowIndex, row ->
                    row.forEachColumnIndexed { columnIndex, column ->
                        val color = if (column) Color.black else Color.white
                        ImGui.getWindowDrawList().addRectFilled(
                            columnIndex.toFloat() * pixelWidth,
                            padding + rowIndex.toFloat() * pixelHeight,
                            (columnIndex.toFloat() * pixelWidth) + pixelWidth.toFloat(),
                            padding + (rowIndex.toFloat() * pixelHeight) + pixelHeight.toFloat(),
                            color.rgb
                        )
                    }
                }
            }
        }


        handleKeys()
        ImGui.end()
    }

    override fun draw() {
        copyingFrameBuffer.getAndSet(true)

        emulator?.frameBuffer?.let {
            it.forEachRowIndexed { rowIndex, row ->
                val copyRow = frameBufferCopy[rowIndex]
                row.forEachColumnIndexed { columnIndex, column ->
                    copyRow[columnIndex] = column
                }
            }
        }

        copyingFrameBuffer.getAndSet(false)
    }
    private fun handleKeys() {
        _keysDown.clear()
        when {
            ImGui.isKeyDown(GLFW.GLFW_KEY_1) -> _keysDown.add(Keys.Number1)
            ImGui.isKeyDown(GLFW.GLFW_KEY_2) -> _keysDown.add(Keys.Number2)
            ImGui.isKeyDown(GLFW.GLFW_KEY_3) -> _keysDown.add(Keys.Number3)
            ImGui.isKeyDown(GLFW.GLFW_KEY_4) -> _keysDown.add(Keys.C)
            ImGui.isKeyDown(GLFW.GLFW_KEY_Q) -> _keysDown.add(Keys.Number4)
            ImGui.isKeyDown(GLFW.GLFW_KEY_W) -> _keysDown.add(Keys.Number5)
            ImGui.isKeyDown(GLFW.GLFW_KEY_E) -> _keysDown.add(Keys.Number6)
            ImGui.isKeyDown(GLFW.GLFW_KEY_R) -> _keysDown.add(Keys.D)
            ImGui.isKeyDown(GLFW.GLFW_KEY_A) -> _keysDown.add(Keys.Number7)
            ImGui.isKeyDown(GLFW.GLFW_KEY_S) -> _keysDown.add(Keys.Number8)
            ImGui.isKeyDown(GLFW.GLFW_KEY_D) -> _keysDown.add(Keys.Number5)
            ImGui.isKeyDown(GLFW.GLFW_KEY_F) -> _keysDown.add(Keys.F)
            ImGui.isKeyDown(GLFW.GLFW_KEY_Y) -> _keysDown.add(Keys.A)
            ImGui.isKeyDown(GLFW.GLFW_KEY_X) -> _keysDown.add(Keys.Number0)
            ImGui.isKeyDown(GLFW.GLFW_KEY_C) -> _keysDown.add(Keys.B)
            ImGui.isKeyDown(GLFW.GLFW_KEY_V) -> _keysDown.add(Keys.F)
        }
    }

    override fun disposeWindow() {
        exitProcess(1)
    }
    companion object {
        private const val pixelWidth = 15
        private const val pixelHeight = pixelWidth
        private const val padding = 5 * pixelWidth

        @JvmStatic
        fun main(args: Array<String>) {
            launch(ImGuiRenderer())
        }
    }
}