import javax.swing.SwingUtilities

object Swing {
    inline fun invokeLater(crossinline block: () -> Unit) = if(SwingUtilities.isEventDispatchThread()) {
        block()
    } else {
        SwingUtilities.invokeLater { block() }
    }

    inline fun <T> invokeAndWait(crossinline block: () -> T) = if(SwingUtilities.isEventDispatchThread()) {
        block()
    } else {
        var result: T? = null
        SwingUtilities.invokeAndWait {
            result = block()
        }
        result!!
    }
}