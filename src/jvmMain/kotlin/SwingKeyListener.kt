import java.awt.KeyEventDispatcher
import java.awt.event.KeyEvent
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentHashMap


class SwingKeyListener: KeyEventDispatcher, KeyListener {
    private val _keysDown = ConcurrentHashMap.newKeySet<Keys>()
    override val keysDown: Set<Keys> = _keysDown

    override fun dispatchKeyEvent(e: KeyEvent): Boolean {
        e.keyOrNull?.also {
            if(e.id == KeyEvent.KEY_PRESSED || e.id == KeyEvent.KEY_TYPED) {
                _keysDown.add(it)
            } else if(e.id == KeyEvent.KEY_RELEASED) {
                _keysDown.remove(it)
            }
        }
        return true
     }
}

class ConsoleKeyListener: KeyListener {
    private val _keysDown = ConcurrentHashMap.newKeySet<Keys>()
    override val keysDown: Set<Keys> = _keysDown
    val reader = BufferedReader(InputStreamReader(System.`in`))

    override fun update() {
        val key = when (reader.readLine().first()) {
            '1' -> Keys.Number1
            '2' -> Keys.Number2
            '3' -> Keys.Number3
            '4' -> Keys.C

            'Q' -> Keys.Number4
            'W' -> Keys.Number5
            'E' -> Keys.Number6
            'R' -> Keys.D

            'A' -> Keys.Number7
            'S' -> Keys.Number8
            'D' -> Keys.Number9
            'F' -> Keys.E

            'Y' -> Keys.A
            'X' -> Keys.Number0
            'C' -> Keys.B
            'V' -> Keys.F
            else -> null
        }
        _keysDown.add(key)
    }
}
