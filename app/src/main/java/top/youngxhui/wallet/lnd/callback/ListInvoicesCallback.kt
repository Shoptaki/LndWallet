package top.youngxhui.wallet.lnd.callback

import androidx.compose.runtime.mutableStateOf
import lnrpc.LightningOuterClass

/**
 * 获取所有的收据
 */
class ListInvoicesCallback:ICallback() {
    val responseState = mutableStateOf(LightningOuterClass.ListInvoiceResponse.getDefaultInstance())
    override fun onResponse(p0: ByteArray?) {
        val resp = LightningOuterClass.ListInvoiceResponse.parseFrom(p0)
        responseState.value = resp
    }
}