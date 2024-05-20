package top.youngxhui.wallet.lnd.recv.stream

import android.util.Log
import lnrpc.LightningOuterClass

class PaymentRecvStream:IRecvStream() {

    override fun onResponse(p0: ByteArray?) {
        var response = LightningOuterClass.Payment.parseFrom(p0)
        Log.i("Lnd", "recv response $response")
    }
}