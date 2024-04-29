package com.example.fileencryptionsuite.EncryptionThings

import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptFile {

    fun hash(fileContent: ByteArray): String{
        val digest: MessageDigest

        try{
            digest = MessageDigest.getInstance("SHA-256")
        } catch(e: NoSuchAlgorithmException){
            Log.e(TAG, "hash: ", e)
            throw e
        }

        return  java.util.Base64.getEncoder().encodeToString(digest.digest(fileContent))
    }

    fun encrypt(fileContent: ByteArray, key: Point): ByteArray {
        val digest: MessageDigest

        try {
            digest = MessageDigest.getInstance("SHA-384")
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "encrypt: ", e)
            throw e
        }

        val hash = digest.digest(key.getX().toString().toByteArray()) //hash the key to get aesKey and iv
        val aesKey = Arrays.copyOfRange(hash, 0, 32);
        val iv = Arrays.copyOfRange(hash, 32, 48)

        val sKey: SecretKey = SecretKeySpec(aesKey, "AES") //use the key and iv to generate key and iv
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding") //using AES CBC PKCS7
        cipher.init(Cipher.ENCRYPT_MODE, sKey, ivSpec)

        val encryptedByteArray = cipher.doFinal(fileContent)
        return encryptedByteArray
    }

    fun decrypt(encryptedByteArray: ByteArray, key: Point): ByteArray {
        val digest: MessageDigest

        try {
            digest = MessageDigest.getInstance("SHA-384")
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "decrypt: ", e)
            throw e
        }

        val hash = digest.digest(key.getX().toString().toByteArray()) // Hash the key to get AES key and IV
        val aesKey = Arrays.copyOfRange(hash, 0, 32)
        val iv = Arrays.copyOfRange(hash, 32, 48)

        val sKey: SecretKey = SecretKeySpec(aesKey, "AES") // Use the key and IV to generate key and IV
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding") // Using AES CBC PKCS7
        cipher.init(Cipher.DECRYPT_MODE, sKey, ivSpec)

        val decryptedByteArray:ByteArray?
        try {
            decryptedByteArray = cipher.doFinal(encryptedByteArray)
        } catch(e: Exception){
            throw e
        }

        return decryptedByteArray ?: byteArrayOf(Byte.MIN_VALUE) //if != null return itself else return minvalue bytearray
    }



    companion object {
        private const val TAG = "EncryptFile"
    }
}