import java.awt.event.KeyEvent
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


actual class Runtime {
    actual val keyListener = KeyListener()
    actual val renderer: Renderer = SwingRenderer(keyListener)
    actual fun Emulator.execute() {

        val timerExecutor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
        timerExecutor.scheduleAtFixedRate({
            if(delay > 0u) { delay-- }
            if(sound > 0u) { sound-- }
        }, 0, 16, TimeUnit.MILLISECONDS)

        timerExecutor.scheduleAtFixedRate({
          step()
        }, 0, 1, TimeUnit.MILLISECONDS).get()
    }
}

fun main() = Runtime().run {
    Emulator(this).execute()
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