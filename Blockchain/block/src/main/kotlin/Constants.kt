import java.util.logging.Logger

class Constants {
    companion object {
        const val DIFFICULTY = 1
        const val MINER_REWARD = 10
        const val GENESIS_PREVIOUS_HASH = "0000000000000000000000000000000000000000000000000000000000000000"
        val logger: Logger = Logger.getLogger(Constants::class.java.name)
    }
}