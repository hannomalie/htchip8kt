typealias FrameBuffer = Array<BooleanArray>
inline fun FrameBuffer.forEachRowIndexed(block: (Int, BooleanArray) -> Unit) = forEachIndexed(block)
inline fun BooleanArray.forEachColumnIndexed(block: (Int, Boolean) -> Unit) = forEachIndexed(block)