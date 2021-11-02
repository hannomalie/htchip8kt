import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

actual class Runtime {
    private val display = Display()
    actual fun Emulator.execute() {

        val draw = { display.run { draw() } }

        val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
        executor.scheduleAtFixedRate(draw, 0, 66, TimeUnit.MILLISECONDS).get()
    }
}

fun main() = Runtime().run {
    Emulator().execute()
}