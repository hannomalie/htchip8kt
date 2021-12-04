import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.io.File

internal class RuntimeTest {
    @Test
    @Tag("manual")
    fun `execute ibm logo test`() {
        JvmRuntime().run {
            Emulator(this).run {
                load(Game(File("IBMLogo.ch8").readBytes()))
                execute()
            }
        }
    }
    @Test
    @Tag("manual")
    fun `execute rom test`() {
        JvmRuntime().run {
            Emulator(this).run {
                load(Game(File("test_opcode.ch8").readBytes()))
                execute()
            }
        }
    }

    @Test
    @Tag("manual")
    fun `execute space invaders`() {
        JvmRuntime().run {
            Emulator(this).run {
                load(Game(File("Space Invaders [David Winter].ch8").readBytes()))
                execute()
            }
        }
    }

    @Test
    @Tag("manual")
    fun `execute space invaders with text renderer`() {
        JvmRuntime(
            renderer = CommandlineRenderer(),
            keyListener = ConsoleKeyListener()
        ).run {
            Emulator(this).run {
                load(Game(File("Space Invaders [David Winter].ch8").readBytes()))
                execute()
            }
        }
    }
}