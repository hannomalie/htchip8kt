import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


actual class Runtime {
    actual fun Emulator.execute() {
        val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
        executor.scheduleAtFixedRate(::step, 0, 66, TimeUnit.MILLISECONDS).get()
    }
}

fun main() = Runtime().run {
    Emulator(SwingRenderer()).execute()
}