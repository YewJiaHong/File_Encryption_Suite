class Blockchain {
    private var blockchain = ArrayList<Block>()

    val getBlockchain: ArrayList<Block> get() {
        return blockchain
    }
    val size: Int get(){
        return blockchain.size
    }

    fun addBlock(block: Block){
        blockchain.add(block)
    }

    override fun toString(): String {
        var returnText = "Blockchain: \n"
        for (block in blockchain){
            returnText += block.toString() + "\n"
        }
        return returnText
    }

}