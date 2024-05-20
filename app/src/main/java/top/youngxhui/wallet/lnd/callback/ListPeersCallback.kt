package top.youngxhui.wallet.lnd.callback

import android.util.Log
import lnrpc.LightningOuterClass

class ListPeersCallback : ICallback() {
    override fun onResponse(p0: ByteArray?) {
        var resp = LightningOuterClass.ListPeersResponse.parseFrom(p0)
        Log.i("peer", "list peer success ${resp}")

    }
}