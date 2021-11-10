expect class KeyListener {
    val keysDown: Set<Keys>
}

enum class Keys(val index: UInt) {
    `1`(0u), `2`(1u), `3`(2u), C(3u),
    `4`(4u), `5`(5u), `6`(6u), D(7u),
    `7`(8u), `8`(9u), `9`(10u), E(11u),
    A(12u), `0`(13u), B(14u), F(15u),
    ;
}