package com.example.fileencryptionsuite.EncryptionThings

import android.util.Log
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom


class ECDH {
    private val TAG = "ECDH"

    fun sharedKeygeneration(firstKeyPair: ArrayList<Any>, secondKeyPair: ArrayList<Any>, curve: EllipticCurve): Point?{
        return multiScalar(firstKeyPair[0] as BigInteger, secondKeyPair[1] as Point, curve)
    }

    fun makeKeypair(userInput: String?, curve: EllipticCurve): ArrayList<Any> {
        Log.d(TAG, "make_keypair")
        val ret = ArrayList<Any>()
        val publicKey: Point
        val privateKey: BigInteger

        if (userInput.isNullOrEmpty()){
            privateKey = BigInteger(generatePrivateKeyStringBasedOnUserInput(""), 16)
            publicKey = multiScalar(privateKey, curve.getG(), curve)!!
        } else{
            privateKey = BigInteger(generatePrivateKeyStringBasedOnUserInput(userInput), 16)
            publicKey = multiScalar(privateKey, curve.getG(), curve)!!
        }
        ret.add(0, privateKey)
        ret.add(1, publicKey)
        return ret
    }

    private fun generatePrivateKeyStringBasedOnUserInput(input: String?): String{
        val secureRandom = SecureRandom()

        val randomBytes: ByteArray
        if (!input.isNullOrEmpty()){ //have user input
            val digest: MessageDigest

            try {
                digest = MessageDigest.getInstance("SHA-384")
            }catch (e: NoSuchAlgorithmException){
                Log.e(TAG, "generatePrivateKeyStringBasedOnUserInput: ", e)
                throw e
            }

            val seedBytes = input.toByteArray()
            val randomData = ByteArray(64)
            secureRandom.nextBytes(randomData)
            digest.update(seedBytes)
            digest.update(randomData)
            randomBytes = digest.digest()

        } else{ //no user input
            randomBytes = ByteArray(64)
            secureRandom.nextBytes(randomBytes)
        }

        val hexadecimalRetString = StringBuilder()
        for (b: Byte in randomBytes)
            hexadecimalRetString.append(String.format("%02X", b))

        return hexadecimalRetString.toString()
    }

    private fun inverseKModP(k: BigInteger, p:BigInteger): BigInteger?{
        Log.d(TAG, "inverseKModP")
        //Inverse of K modulo P
        when (k.compareTo(BigInteger.ZERO)){
            0 ->throw ArithmeticException("k is zero")
            -1 -> return (inverseKModP(k.negate(), p)?.let { p.subtract(it) })
        }

        var s = BigInteger.ZERO
        var oldS = BigInteger.ONE
        var t = BigInteger.ONE
        var oldT = BigInteger.ZERO
        var r = p; var oldR = k

        while (r != BigInteger.ZERO){
            val quotient = oldR.divide(r)

            val tempR = r
            r = oldR.subtract(quotient.multiply(r))
            oldR = tempR

            val tempS = s
            s = oldS.subtract(quotient.multiply(s))
            oldS = tempS

            val tempT = t
            t = oldT.subtract(quotient.multiply(t))
            oldT = tempT
        }

        val gcd = oldR; val x = oldS; val y = oldT

        return if (gcd == BigInteger.ONE && (((k.multiply(x)).mod(p).equals(BigInteger.ONE))))//gcd == 1 and (k*x)%p == 1
            x.mod(p)
        else
            null
    }

    private fun isOnCurve(point: Point?, curve: EllipticCurve): Boolean{
        Log.d(TAG, "isOnCurve")
        if (point == null){
            //if is null then the point is at infinity
            return true
        }

        val x = point.getX(); val y = point.getY()
        val ySquared = y.multiply(y)
        val xCubed = x.multiply(x).multiply(x)
        val ax = curve.getA().multiply(x)
        val expression = ySquared.subtract(xCubed).subtract(ax).subtract(curve.getB())
        val remainder = expression.mod(curve.getP())
        // (y * y - x * x * x - curve.a * x - curve.b) % curve.p == 0

        return (remainder.equals(BigInteger.ZERO))
    }

    private fun addPoints(point1: Point?, point2: Point?, curve: EllipticCurve): Point?{
        Log.d(TAG, "addPoints")
        if (!isOnCurve(point1, curve) || !(isOnCurve(point2, curve)))
            return null

        if (point1 == null)
            return point2

        if (point2 == null)
            return point1

        val x1 = point1.getX(); val y1 = point1.getY()
        val x2 = point2.getX(); val y2 = point2.getY()

        if((x1 == x2) && y1 != y2)
            return null

        val m: BigInteger = if (x1 == x2){ //if point1 == point2
            val x1Squared = x1.multiply(x1)
            val x1SquaredTimesThree = x1Squared.multiply(BigInteger.valueOf(3))
            val y1TimesTwo = y1.multiply(BigInteger.valueOf(2))

            inverseKModP(y1TimesTwo, curve.getP())?.let {(x1SquaredTimesThree.add(curve.getA())).multiply(it)}!!
        } else{ //point1 != point2
            val y1Subtracty2 = y1.subtract(y2)
            val x1Subtractx2 = x1.subtract(x2)

            inverseKModP(x1Subtractx2, curve.getP())?.let { (y1Subtracty2).multiply(it) }!!
        }

        val x3 = (m.multiply(m)).subtract(x1).subtract(x2)
        val y3 = y1.add((m.multiply((x3.subtract(x1)))))


        val result = Point(x3.mod(curve.getP()), (y3.negate()).mod(curve.getP()))

        return if (isOnCurve(result, curve))
            result
        else
            null
    }

    private fun multiScalar(k: BigInteger, point: Point?, curve: EllipticCurve): Point?{
        Log.d(TAG, "multiScalar")
        if(!isOnCurve(point, curve))
            return null

        if ((k.mod(curve.getN())).equals(BigInteger.ZERO) || point == null)
            return null

        if ((k < BigInteger.ZERO)){
            point.setX(point.getX().negate())
            point.setY(point.getY().negate())
            return multiScalar(k.negate(), point, curve)
        }

        var result: Point? = null
        var addend = point

        var tempK = k
        while (tempK > BigInteger.ZERO){
            if (tempK.testBit(0)) //Add
                result = addPoints(result, addend, curve)

            //Double
            addend = addPoints(addend, addend, curve)

            tempK = tempK.shiftRight(1)
        }

        return if (isOnCurve(result, curve))
            result
        else
            null
    }
}