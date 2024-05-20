package top.youngxhui.wallet.lnd.recv.stream

import android.util.Log
import lndmobile.RecvStream

abstract class IRecvStream:RecvStream {
    override fun onError(p0: Exception?) {
        Log.e(this.javaClass.name, "failed on error $p0")
    }
}