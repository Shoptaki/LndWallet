package top.youngxhui.wallet.lnd.callback

import android.util.Log
import lndmobile.Callback

abstract class ICallback : Callback {

    override fun onError(p0: java.lang.Exception?) {
        Log.e(this.javaClass.name, "failed on error $p0")
    }

    override fun onResponse(p0: ByteArray?) {
        Log.i(this.javaClass.name, "success on response $p0")
    }
}
