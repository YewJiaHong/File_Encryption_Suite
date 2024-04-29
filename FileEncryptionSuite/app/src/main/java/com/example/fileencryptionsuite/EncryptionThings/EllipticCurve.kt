package com.example.fileencryptionsuite.EncryptionThings

import com.example.fileencryptionsuite.EncryptionThings.Point
import java.math.BigInteger

class EllipticCurve(private val name: String,
                    private val p: BigInteger,
                    private val a: BigInteger,
                    private val b: BigInteger,
                    private val g: Point,
                    private val n: BigInteger,
                    private val h: BigInteger) {

    fun getP(): BigInteger{
        return p;
    }
    fun getA(): BigInteger{
        return a;
    }
    fun getB(): BigInteger{
        return b;
    }
    fun getN(): BigInteger{
        return n;
    }
    fun getH(): BigInteger{
        return h;
    }
    fun getG(): Point {
        return g;
    }
    fun getName(): String{
        return name;
    }
}