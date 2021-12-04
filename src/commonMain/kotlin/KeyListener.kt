interface KeyListener {
    val keysDown: Set<Keys>
    fun update() { }
}

enum class Keys(val index: UInt) {
    Number1(0u), Number2(1u), Number3(2u), C(3u),
    Number4(4u), Number5(5u), Number6(6u), D(7u),
    Number7(8u), Number8(9u), Number9(10u), E(11u),
    A(12u), Number0(13u), B(14u), F(15u),
    ;
}