import imgui.ImGui
import imgui.app.Application
import imgui.app.Configuration
import imgui.flag.ImGuiWindowFlags
import org.lwjgl.glfw.GLFW
import java.util.concurrent.ConcurrentHashMap

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
    }

    override fun process() {
        val windowWidth = ImGui.getIO().displaySizeX
        val windowCenterX = 0.5f * windowWidth
        val windowHeight = ImGui.getIO().displaySizeX
        val windowCenterY = 0.5f * windowHeight

        ImGui.setNextWindowBgAlpha(0f)
        ImGui.setNextWindowSize(windowWidth, windowHeight)
        ImGui.setNextWindowPos(0f, 0f)
        val flags =
            ImGuiWindowFlags.NoTitleBar or ImGuiWindowFlags.NoResize or
            ImGuiWindowFlags.NoMove or ImGuiWindowFlags.NoScrollbar
        ImGui.begin("CHIP-8", flags)

        emulator?.frameBuffer?.forEachRowIndexed { rowIndex, row ->
            var rowString = ""
            row.forEachColumnIndexed { columnIndex, column ->
                val color = if (column) "#" else " "
                rowString += color
//                ImGui.getWindowDrawList().addRectFilled(windowCenterX, windowCenterY, 10f, 10f, Color.green.rgb)
            }
            ImGui.text(rowString)
        }
        ImGui.text("Framerate: " + ImGui.getIO().framerate)


        handleKeys()
        ImGui.end()
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

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(ImGuiRenderer())
        }
    }

    override fun draw() { }
}