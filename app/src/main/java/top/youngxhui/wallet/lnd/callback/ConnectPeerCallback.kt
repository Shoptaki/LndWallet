package top.youngxhui.wallet.lnd.callback

import android.util.Log
import lnrpc.LightningOuterClass

class ConnectPeerCallback : ICallback() {

    override fun onResponse(p0: ByteArray?) {

        var parseFrom = LightningOuterClass.ConnectPeerResponse.parseFrom(p0)
        Log.i("Peer", "connect peer success ${parseFrom}")
    }

}