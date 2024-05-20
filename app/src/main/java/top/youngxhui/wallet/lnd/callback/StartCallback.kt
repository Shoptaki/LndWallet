package top.youngxhui.wallet.lnd.callback

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import lndmobile.Callback

class StartCallback : Callback {
    val responseState = mutableStateOf(false)
    override fun onError(e: Exception?) {
        Log.i("lnd", "on error $e")
    }

    override fun onResponse(ba: ByteArray?) {
        Log.i("lnd", "start success")

        responseState.value = true
    }

}