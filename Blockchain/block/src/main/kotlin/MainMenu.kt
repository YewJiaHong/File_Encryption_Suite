import java.util.logging.Level

fun main(args: Array<String>) {
   val blockchain = Blockchain()
   val miner = Miner()

   val block0 = Block(0, "transaction1 ", Constants.GENESIS_PREVIOUS_HASH)
   miner.mine(block0, blockchain)

   val block1 = Block(1, "transaction 2", blockchain.getBlockchain[blockchain.size - 1].getHash.toString())
   miner.mine(block1, blockchain)

   Constants.logger.log(Level.FINE, blockchain.toString())
   Constants.logger.log(Level.FINE, "Miner's Reward: ${miner.getReward}")
   println(blockchain.toString())
   println("Miner's Reward: ${miner.getReward}")
}