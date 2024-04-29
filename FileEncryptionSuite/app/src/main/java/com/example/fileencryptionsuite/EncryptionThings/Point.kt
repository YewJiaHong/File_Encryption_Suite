package com.example.fileencryptionsuite.EncryptionThings

import java.math.BigInteger

class Point(x: BigInteger, y: BigInteger) {
    private var x: BigInteger = x; private var y: BigInteger = y;

    fun getX(): BigInteger{
        return x
    }

    fun getY(): BigInteger{
        return y
    }

    fun setX(x: BigInteger){
        this.x = x;
    }

    fun setY(y: BigInteger){
        this.y = y
    }

    override fun toString(): String {
        return "(" + getX() + ", " + getY() + ")";
    }
}