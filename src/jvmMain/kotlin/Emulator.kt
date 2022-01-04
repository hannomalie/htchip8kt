import java.awt.event.KeyEvent
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class JvmRuntime(
    override val keyListener: KeyListener,
    override val renderer: Renderer,
): Runtime {
    constructor(renderer: SwingRenderer = SwingRenderer()): this(renderer.keyListener, renderer)
    constructor(renderer: ImGuiRenderer): this(renderer, renderer)

    override fun Emulator.execute() {

        val timerExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        timerExecutor.scheduleAtFixedRate({
            if(delay > 0u) { delay-- }
            if(sound > 0u) { sound-- }
        }, 0, 16, TimeUnit.MILLISECONDS)

        val intervalInMs: Long = 1
        timerExecutor.scheduleAtFixedRate({
          step(1000f/intervalInMs.toFloat())
        }, 0, intervalInMs, TimeUnit.MILLISECONDS).get()
    }
}

object SwingMain {
    @JvmStatic
    fun main(args: Array<String>) {
        JvmRuntime().run {
            Emulator(this).run {
                load(Game(javaClass.getResourceAsStream("Space Invaders [David Winter].ch8").readBytes()))
                execute()
            }
        }
    }
}

object ImguiMain {
    @JvmStatic
    fun main(args: Array<String>) {
        JvmRuntime(ImGuiRenderer()).run {
            Emulator(this).run {
                load(Game(javaClass.getResourceAsStream("Space Invaders [David Winter].ch8").readBytes()))
                execute()
            }
        }
    }
}

val KeyEvent.keyOrNull
    get() = when (keyCode) {
        KeyEvent.VK_1 -> Keys.Number1
        KeyEvent.VK_2 -> Keys.Number2
        KeyEvent.VK_3 -> Keys.Number3
        KeyEvent.VK_4 -> Keys.C

        KeyEvent.VK_Q -> Keys.Number4
        KeyEvent.VK_W -> Keys.Number5
        KeyEvent.VK_E -> Keys.Number6
        KeyEvent.VK_R -> Keys.D

        KeyEvent.VK_A -> Keys.Number7
        KeyEvent.VK_S -> Keys.Number8
        KeyEvent.VK_D -> Keys.Number9
        KeyEvent.VK_F -> Keys.E

        KeyEvent.VK_Y -> Keys.A
        KeyEvent.VK_X -> Keys.Number0
        KeyEvent.VK_C -> Keys.B
        KeyEvent.VK_V -> Keys.F
        else -> null
    }