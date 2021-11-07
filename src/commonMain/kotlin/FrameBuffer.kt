typealias FrameBuffer = Array<BooleanArray>
inline fun FrameBuffer.forEachColumnIndexed(block: (Int, BooleanArray) -> Unit) = forEachIndexed(block)
inline fun BooleanArray.forEachRowIndexed(block: (Int, Boolean) -> Unit) = forEachIndexed(block)