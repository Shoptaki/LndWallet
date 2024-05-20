package top.youngxhui.wallet.lnd.callback

import android.util.Log

class InitWalletCallback:ICallback() {
    override fun onResponse(p0: ByteArray?) {
        Log.i("Lnd", "init wallet success")
    }
}