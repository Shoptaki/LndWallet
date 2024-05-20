package top.youngxhui.wallet.lnd.callback

import androidx.compose.runtime.mutableStateOf
import lnrpc.LightningOuterClass

class GetNetworkCallback:ICallback() {
    val responseState = mutableStateOf(LightningOuterClass.NetworkInfo.getDefaultInstance()) // MutableState to hold the response


    override fun onResponse(p0: ByteArray?) {
        responseState.value = LightningOuterClass.NetworkInfo.parseFrom(p0)
    }
}