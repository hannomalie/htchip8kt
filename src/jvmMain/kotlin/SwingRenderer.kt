import com.github.weisj.darklaf.LafManager
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.KeyboardFocusManager
import javax.swing.*


val black = Color(0, 0, 0, 255)
val white = Color(255, 255, 255, 255)
val green = Color(255, 255, 255, 255)

class SwingRenderer private constructor(private val keyListener: KeyListener) : JPanel(), Renderer {
    override var drawGrid = false
    override var crtEffect = true
    override var emulator: Emulator? = null
        set(value) {
            crtEffectCheckbox.isSelected = value?.renderer?.crtEffect ?: false
            drawGridJCheckBox.isSelected = value?.renderer?.drawGrid ?: false
            field = value
        }

    private var lastFrameBuffer = Array(Display.dimension.x) {
        FloatArray(Display.dimension.y) { 0f }
    }

    override fun update(deltaSeconds: Float) {
        emulator?.let { emulator ->
            lastFrameBuffer.forEachIndexed { columnIndex, column ->
                column.forEachIndexed { rowIndex, row ->
                    if (row > 0) {
                        column[rowIndex] = kotlin.math.max(0f, row - 0.02f)
                    }
                    if (emulator.frameBuffer[columnIndex][rowIndex]) {
                        column[rowIndex] = 1.0f
                    }
                }
            }
        }
    }

    private val alphaColorCache = mutableListOf<Color?>().apply {
        repeat(256) { add(it, null) }
    }
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        g.color = white
        g.fillRect(
            padding,
            padding,
            Display.dimension.x * pixelWidth,
            Display.dimension.y * pixelHeight
        )
        if (crtEffect) {
            lastFrameBuffer.forEachIndexed { columnIndex, column ->
                column.forEachIndexed { rowIndex, row ->
                    if (column[rowIndex] > 0) {
                        val alpha = (column[rowIndex] * 255).toInt()
                        g.color = alphaColorCache.computeIfAbsent(alpha) { Color(black.red, black.green, black.blue, alpha) }
                        g.fillRect(
                            padding + (columnIndex * pixelWidth),
                            padding + (rowIndex * pixelHeight),
                            pixelWidth,
                            pixelHeight
                        )
                    }
                    if (drawGrid) {
                        g.color = black
                        g.drawRect(
                            padding + (columnIndex * pixelWidth),
                            padding + (rowIndex * pixelHeight),
                            pixelWidth,
                            pixelHeight
                        )
                    }
                }
            }
        } else {
            emulator?.frameBuffer?.let { frameBuffer ->
                frameBuffer.forEachColumnIndexed { columnIndex, column ->
                    column.forEachRowIndexed { rowIndex, row ->
                        g.color = if (row) {
                            black
                        } else white
                        g.fillRect(
                            padding + (columnIndex * pixelWidth),
                            padding + (rowIndex * pixelHeight),
                            pixelWidth,
                            pixelHeight
                        )
                        if (drawGrid) {
                            g.color = black
                            g.drawRect(
                                padding + (columnIndex * pixelWidth),
                                padding + (rowIndex * pixelHeight),
                                pixelWidth,
                                pixelHeight
                            )
                        }
                    }
                }
            }
        }
    }

    override fun draw() {
        Swing.invokeAndWait {
            repaint()
        }
    }

    private val drawGridJCheckBox = Swing.invokeAndWait {
        JCheckBox("Draw Grid").apply {
            addActionListener {
                emulator?.renderer?.let { renderer ->
                    renderer.drawGrid = !renderer.drawGrid
                    draw()
                }
            }
        }
    }
    private val crtEffectCheckbox = Swing.invokeAndWait {
        JCheckBox("CRT effect").apply {
            addActionListener {
                emulator?.renderer?.let { renderer ->
                    renderer.crtEffect = !renderer.crtEffect
                    draw()
                }
            }
        }
    }

    init {
        LafManager.install()

        JFrame("CHIP 8 powered by Kotlin multiplatform").apply {
            jMenuBar = JMenuBar().apply {
                add(JButton("Restart").apply {
                    addActionListener {
                        emulator?.restart()
                    }
                })
                add(drawGridJCheckBox)
                add(crtEffectCheckbox)
            }
            size = Dimension(
                2 * padding + Display.dimension.x * pixelWidth,
                6 * padding + Display.dimension.y * pixelWidth
            )
            preferredSize = size
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            contentPane.add(this@SwingRenderer)
            isResizable = false
            pack()
            isLocationByPlatform = true
            KeyboardFocusManager
                .getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(keyListener)
            isVisible = true
        }
    }

    companion object {
        private const val padding = 10
        private const val pixelWidth = 10
        private const val pixelHeight = pixelWidth

        operator fun invoke(keyListener: KeyListener) = Swing.invokeAndWait {
            SwingRenderer(keyListener)
        }
    }
}

private inline fun <E> MutableList<E>.computeIfAbsent(alpha: Int, function: () -> E): E {
    if(get(alpha) == null) add(alpha, function())

    return this[alpha]
}
