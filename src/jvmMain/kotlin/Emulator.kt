import com.github.weisj.darklaf.LafManager
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.SystemColor.menu
import java.util.BitSet
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.swing.*


actual class Runtime {
    actual fun Emulator.execute() {

        val step = {
            try {
                step()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
        executor.scheduleAtFixedRate(step, 0, 66, TimeUnit.MILLISECONDS).get()
    }
}
actual typealias BitSet = BitSet
actual fun BitSet(byte: Byte) = BitSet.valueOf(byteArrayOf(byte))

actual class Renderer : JPanel() {
    actual var drawGrid = false
    private var emulator: Emulator? = null
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

    actual fun setEmulator(emulator: Emulator) {
        this.emulator = emulator
    }
    actual fun requestDraw() {
        SwingUtilities.invokeLater { repaint() }
    }

    init {
        LafManager.install();
        val frame = JFrame("DrawRect")
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
        frame.isVisible = true
    }

    companion object {
        private const val RECT_X = 10
        private const val RECT_Y = RECT_X
        private const val RECT_WIDTH = 10
        private const val RECT_HEIGHT = RECT_WIDTH
    }
}

fun main() = Runtime().run {
    var renderer: Renderer? = null
    SwingUtilities.invokeAndWait {
        renderer = Renderer()
    }
    Emulator(renderer!!).execute()
}