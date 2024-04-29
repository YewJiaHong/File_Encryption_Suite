import java.util.logging.Level
import kotlin.properties.Delegates

class Miner {
    private var reward by Delegates.notNull<Double>()

    val getReward: Double get(){
        return reward
    }

    init {
        reward = 0.00
    }

    fun mine(block: Block, blockchain: Blockchain){
        while(notGoldenHash(block)){
            block.generateHash()
        }

        Constants.logger.log(Level.FINE, "$block has just mined")
        Constants.logger.log(Level.FINE, "With Value ${block.getHash}")

        blockchain.addBlock(block)
        reward += Constants.MINER_REWARD
    }

    private fun notGoldenHash(block: Block): Boolean{
        Constants.logger.log(Level.FINEST, "tryign to find hash")
        val blockHash = block.getHash.toString()
        val leadingZeros = String(CharArray(Constants.DIFFICULTY)).replace('\u0000', '0')
        return blockHash.substring(0, Constants.DIFFICULTY) != leadingZeros
    }
}