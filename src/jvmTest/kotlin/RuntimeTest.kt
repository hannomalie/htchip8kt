import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.io.File
import javax.swing.SwingUtilities

internal class RuntimeTest {
    @Test
    @Tag("manual")
    fun `execute ibm logo test`() {
        Runtime().run {
            var renderer: Renderer? = null
            SwingUtilities.invokeAndWait {
                renderer = Renderer()
            }
            Emulator(renderer!!).run {
                load(Game(File("IBMLogo.ch8").readBytes()))
                execute()
            }
        }
    }
}