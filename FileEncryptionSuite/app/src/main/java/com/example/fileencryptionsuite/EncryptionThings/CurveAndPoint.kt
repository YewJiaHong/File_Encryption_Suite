package com.example.androidFileEncryptionSuite.encryptionThings

import com.example.fileencryptionsuite.EncryptionThings.EllipticCurve
import com.example.fileencryptionsuite.EncryptionThings.Point
import java.math.BigInteger

class CurveAndPoint {
    private val g = Point(
        BigInteger("79be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798", 16),
        BigInteger("483ada7726a3c4655da4fbfc0e1108a8fd17b448a68554199c47d08ffb10d4b8", 16)
    )

    val curve = EllipticCurve(
        "secp256k1",
        BigInteger("fffffffffffffffffffffffffffffffffffffffffffffffffffffffefffffc2f",16),
        BigInteger("0", 16),
        BigInteger("7", 16),
        g,
        BigInteger("fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141", 16),
        BigInteger("1", 16)
    )
}