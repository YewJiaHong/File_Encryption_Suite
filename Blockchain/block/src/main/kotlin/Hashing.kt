import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.logging.Level

class Hashing {
    companion object {
        fun getHash(pt: String): String {
            val digest: MessageDigest

            try{
                digest = MessageDigest.getInstance("SHA-256")
            } catch (e:NoSuchAlgorithmException){
                Constants.logger.log(Level.SEVERE, "No such algorithm")
                throw e
            }

            val hash = digest.digest(pt.toByteArray())

            val hexadecimalString = StringBuffer()
            for (element in hash) {
                val hexadecimal = Integer.toHexString(0xff and element.toInt())
                if (hexadecimal.length == 1) hexadecimalString.append('0')
                hexadecimalString.append(hexadecimal)
            }
            return hexadecimalString.toString()
        }
    }
}