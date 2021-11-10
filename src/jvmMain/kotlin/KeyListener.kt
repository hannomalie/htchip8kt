import java.awt.KeyEventDispatcher
import java.awt.event.KeyEvent
import javax.swing.SwingUtilities

actual class KeyListener: KeyEventDispatcher {
    private val _keysDown = mutableSetOf<Keys>()
    actual val keysDown: Set<Keys> = _keysDown

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