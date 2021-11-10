import com.github.weisj.darklaf.LafManager
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.KeyboardFocusManager
import javax.swing.*


class SwingRenderer private constructor(private val keyListener: KeyListener) : JPanel(), Renderer {
    override var drawGrid = false
    override var emulator: Emulator? = null
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        emulator?.frameBuffer?.let { frameBuffer ->
            frameBuffer.forEachColumnIndexed { columnIndex, column ->
                column.forEachRowIndexed { rowIndex, row ->
                    g.color = if(row) Color.BLACK else Color.WHITE
                    g.fillRect(RECT_X + (columnIndex * RECT_WIDTH), RECT_Y + (rowIndex * RECT_HEIGHT), RECT_WIDTH, RECT_HEIGHT)
                    if(drawGrid) {
                        g.color = Color.BLACK
                        g.drawRect(RECT_X + (columnIndex * RECT_WIDTH), RECT_Y + (rowIndex * RECT_HEIGHT), RECT_WIDTH, RECT_HEIGHT)
                    }
                }
            }
        }
    }
    override fun draw() {
        SwingUtilities.invokeAndWait { repaint() }
    }

    init {
        LafManager.install()

        val frame = JFrame("CHIP 8 powered by Kotlin multiplatform")
        val menuBar = JMenuBar()
        menuBar.add(JButton("Restart").apply {
            addActionListener {
                emulator?.restart()
            }
        })
        menuBar.add(JCheckBox("Draw Grid").apply {
            addActionListener {
                emulator?.renderer?.let { renderer ->
                    renderer.drawGrid = !renderer.drawGrid
                    draw()
                }
            }
        })
        frame.jMenuBar = menuBar
        frame.size = Dimension(1280, 720)
        frame.preferredSize = frame.size
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.contentPane.add(this)
        frame.pack()
        frame.isLocationByPlatform = true
        KeyboardFocusManager
            .getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(keyListener)
        frame.isVisible = true
    }

    companion object {
        private const val RECT_X = 10
        private const val RECT_Y = RECT_X
        private const val RECT_WIDTH = 10
        private const val RECT_HEIGHT = RECT_WIDTH

        operator fun invoke(keyListener: KeyListener): SwingRenderer {
            var renderer: SwingRenderer? = null
            SwingUtilities.invokeAndWait {
                renderer = SwingRenderer(keyListener)
            }
            return renderer!!
        }
    }
}