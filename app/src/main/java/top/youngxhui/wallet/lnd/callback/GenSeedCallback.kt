package top.youngxhui.wallet.lnd.callback

import androidx.compose.runtime.mutableStateOf
import lnrpc.Walletunlocker

/**
 * GenSeedCallback 生成种子
 */
class GenSeedCallback:ICallback() {
    val responseState = mutableStateOf<List<String>>(listOf()) // MutableState to hold the response

    override fun onResponse(p0: ByteArray?) {
        val resp = Walletunlocker.GenSeedResponse.parseFrom(p0)
        responseState.value =
            resp.cipherSeedMnemonicList // Update the MutableState with response content
    }
}

