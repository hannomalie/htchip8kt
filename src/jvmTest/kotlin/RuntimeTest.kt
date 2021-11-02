import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

internal class RuntimeTest {
    @Test
    fun `execute ibm logo test`() {
        Runtime().run {
            Emulator().run {
                load(Game(File("IBMLogo.ch8").readBytes()))
                execute()
            }
        }
    }
    @Test
    fun testX() {
        assertTrue(false)
    }
}