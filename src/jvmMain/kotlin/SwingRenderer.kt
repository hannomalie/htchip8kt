import com.github.weisj.darklaf.LafManager
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Image.SCALE_FAST
import java.awt.KeyboardFocusManager
import java.awt.image.BufferedImage
import javax.swing.*


val black = Color(0, 0, 0, 255)
val white = Color(255, 255, 255, 255)
val green = Color(0, 185, 0, 255)

class SwingRenderer constructor(internal val keyListener: SwingKeyListener) : JPanel(), Renderer {
    override var drawGrid = false
    override var emulator: Emulator? = null
        set(value) {
            drawGridJCheckBox.isSelected = value?.renderer?.drawGrid ?: false
            field = value
        }

    private var lastFrameBuffer = createFloatFrameBuffer()
    private val bufferedImage = BufferedImage(
        Display.dimension.x,
        Display.dimension.y,
        BufferedImage.TYPE_INT_RGB,
    )


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
        emulator?.frameBuffer?.let { frameBuffer ->
            frameBuffer.forEachColumnIndexed { rowIndex, row ->
                row.forEachRowIndexed { columnIndex, column ->
                    if (column) {
                        bufferedImage.setRGB(
                            columnIndex,
                            rowIndex,
                            Color.black.rgb
                        )
                    } else {
                        bufferedImage.setRGB(
                            columnIndex,
                            rowIndex,
                            Color.white.rgb
                        )
                    }
                }
            }
        }
        g.drawImage(bufferedImage, padding, padding,
            Display.dimension.x * pixelWidth,
            Display.dimension.y * pixelHeight,
            null
        )
        if(drawGrid) {
            emulator?.frameBuffer?.let { frameBuffer ->
                frameBuffer.forEachColumnIndexed { rowIndex, row ->
                    row.forEachRowIndexed { columnIndex, column ->
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

        operator fun invoke(keyListener: SwingKeyListener = SwingKeyListener()) = Swing.invokeAndWait {
            SwingRenderer(keyListener)
        }

    }
}

private inline fun <E> MutableList<E>.computeIfAbsent(alpha: Int, function: () -> E): E {
    if (get(alpha) == null) add(alpha, function())

    return this[alpha]
}

private fun createFloatFrameBuffer() = Array(Display.dimension.y) {
    FloatArray(Display.dimension.x) { 0f }
}
