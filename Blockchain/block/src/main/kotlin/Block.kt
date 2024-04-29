import java.security.SecureRandom
import kotlin.properties.Delegates

class Block(private val id: Int, val transaction: String, private var preHash: String) {
    private lateinit var hashValue: String
    private var time by Delegates.notNull<Long>()
    private val nonce: ByteArray = ByteArray(4)

    init{
        time = System.currentTimeMillis()
        generateHash()
    }

    val getHash: String? get() {
        return if (this::hashValue.isInitialized)
            hashValue
        else
            null
    }

    var previousHash: String
    get(){
        return preHash
    }
    set(value){
        preHash = value
    }


    private fun randomNonce(){
        SecureRandom().nextBytes(nonce)
    }

    fun generateHash(){
        randomNonce()
        val pt = id.toString() + this.previousHash + time.toString() + String(nonce) + transaction

        this.hashValue = Hashing.getHash(pt)
    }

    override fun toString(): String {
        return "Block $id - $transaction:\nHash Value: $hashValue\nPreviousHash: $previousHash"
    }
}