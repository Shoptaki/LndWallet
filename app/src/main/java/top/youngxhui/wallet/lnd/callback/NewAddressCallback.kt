package top.youngxhui.wallet.lnd.callback

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import lnrpc.LightningOuterClass

class NewAddressCallback:ICallback() {

    val responseState = mutableStateOf<String>("")
    override fun onResponse(p0: ByteArray?) {

        val resp = LightningOuterClass.NewAddressResponse.parseFrom(p0)
        Log.i("Lnd", "new address success ${resp.address}")
        responseState.value = resp.address
        Log.i("Lnd", "new address success ${responseState.value}")
    }
}