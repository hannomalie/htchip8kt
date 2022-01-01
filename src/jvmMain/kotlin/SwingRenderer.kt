import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.KeyboardFocusManager
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.*
import kotlin.math.max


val black = Color(0, 0, 0, 255)
val white = Color(255, 255, 255, 255)
val green = Color(0, 185, 0, 255)

class SwingRenderer constructor(internal val keyListener: SwingKeyListener) : JPanel(), Renderer {
    override var drawGrid = false
    override var crt = false
    override var emulator: Emulator? = null
        set(value) {
            drawGridJCheckBox.isSelected = value?.renderer?.drawGrid ?: false
            crtEffectJCheckBox.isSelected = value?.renderer?.crt ?: false
            field = value
        }

    private var lastFrameBuffer = createFloatFrameBuffer()
    private val bufferedImage = BufferedImage(
        Display.dimension.x,
        Display.dimension.y,
        BufferedImage.TYPE_INT_ARGB,
    )


    override fun update(deltaSeconds: Float) {
        emulator?.let { emulator ->
            lastFrameBuffer.forEachIndexed { columnIndex, column ->
                column.forEachIndexed { rowIndex, row ->
                    if (emulator.frameBuffer[columnIndex][rowIndex]) {
                        column[rowIndex] = 1.0f
                    } else if(row > 0) {
                        column[rowIndex] = max(0f, row - 0.01f)
                    }
                }
            }
        }
    }

    private val alphaColorCache = mutableListOf<Color?>().apply {
        repeat(256) { add(it, Color(black.red, black.green, black.blue, it)) }
    }

    private val pixel = IntArray(4)
    override fun paintComponent(g: Graphics) {
//        In case of strange bugs, reread the documentation
//        and change back from ui.update to super.paintComponent
//        super.paintComponent(g)
        ui.update(g, this)

        emulator?.frameBuffer?.let { frameBuffer ->
            if(crt) {
                lastFrameBuffer.forEachIndexed { rowIndex, row ->
                    row.forEachIndexed { columnIndex, column ->
                        val color = if (column > 0) {
                            val alpha = (column * 255).toInt()
                            alphaColorCache[alpha]!!
                        } else {
                            white
                        }
                        bufferedImage.setRGBEfficiently(columnIndex, rowIndex, color)
                    }
                }
            } else {
                frameBuffer.forEachRowIndexed { rowIndex, row ->
                    row.forEachColumnIndexed { columnIndex, column ->
                        val color = if (column) black else white
                        bufferedImage.setRGBEfficiently(columnIndex, rowIndex, color)
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
                frameBuffer.forEachRowIndexed { rowIndex, row ->
                    row.forEachColumnIndexed { columnIndex, column ->
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

    private fun BufferedImage.setRGBEfficiently(columnIndex: Int, rowIndex: Int, color: Color) {
        raster.setDataElements(
            columnIndex, rowIndex, colorModel.getDataElements(color.rgb, pixel)
        )
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
    private val crtEffectJCheckBox = Swing.invokeAndWait {
        JCheckBox("CRT effect").apply {
            addActionListener {
                emulator?.renderer?.let { renderer ->
                    renderer.crt = !renderer.crt
                    draw()
                }
            }
        }
    }
    init {
        System.setProperty("java.home", "dummyoverride")
        val fontConfig: String? = System.getProperty("sun.awt.fontconfig")
        if(fontConfig != null) {
            println("Using font config $fontConfig")
        } else {
            val fontConfigFile = File("./fontconfig.properties")
            println("Saving and using default font config ${fontConfigFile.absolutePath} .\n" +
                    "When you don't like that, pass in system property 'sun.awt.fontconfig' pointing to your font config.")
            fontConfigFile.writeText(javaClass.classLoader.getResource("fontconfig.properties").readText())
            System.setProperty("sun.awt.fontconfig", fontConfigFile.absolutePath)
        }
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
//        LafManager.install()


        JFrame("CHIP 8 powered by Kotlin multiplatform").apply {
            jMenuBar = JMenuBar().apply {
                add(JButton("Restart").apply {
                    addActionListener {
                        emulator?.restart()
                    }
                })
                add(drawGridJCheckBox)
                add(crtEffectJCheckBox)
            }
            size = Dimension(
                4 * padding + Display.dimension.x * pixelWidth,
                8 * padding + Display.dimension.y * pixelWidth
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
