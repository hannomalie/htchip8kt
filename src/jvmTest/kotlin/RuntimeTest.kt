import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.io.File
import javax.swing.SwingUtilities

internal class RuntimeTest {
    @Test
    @Tag("manual")
    fun `execute ibm logo test`() {
        Runtime().run {
            Emulator(SwingRenderer()).run {
                load(Game(File("IBMLogo.ch8").readBytes()))
                execute()
            }
        }
    }
    @Test
    @Tag("manual")
    fun `execute rom test`() {
        Runtime().run {
            Emulator(SwingRenderer()).run {
                load(Game(File("test_opcode.ch8").readBytes()))
                execute()
            }
        }
    }
}