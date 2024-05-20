package top.youngxhui.wallet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lightspark.composeqr.QrCodeView
import lndmobile.Lndmobile
import lnrpc.LightningOuterClass
import routerrpc.RouterOuterClass
import top.youngxhui.wallet.lnd.callback.ListInvoicesCallback
import top.youngxhui.wallet.lnd.callback.ListPeersCallback
import top.youngxhui.wallet.lnd.recv.stream.PaymentRecvStream

@Composable
fun BitcoinScreen(navController: NavHostController) {


    val listInvoicesCallback = remember { ListInvoicesCallback() }

    val paymentRequest = remember { mutableStateOf("") }
    Column {
        Button(onClick = {
            val req = LightningOuterClass.ListInvoiceRequest.getDefaultInstance()
            Lndmobile.listInvoices(req.toByteArray(), listInvoicesCallback)
        }) {
            Text(text = "查看所有收据")
        }
        LazyColumn {
            items(listInvoicesCallback.responseState.value.invoicesList) {
                SelectionContainer {
                    Column {
                        Text(text = it.paymentRequest)
                        QrCodeView(data = it.paymentRequest, modifier = Modifier.size(300.dp))
                    }

                }

            }
        }

        Text(text = "连接 LSP")
        Text(text = "LSP: ")


        Button(onClick = {

            val req =
                LightningOuterClass.ListPeersRequest.newBuilder().setLatestError(false).build()

            Lndmobile.listPeers(req.toByteArray(), ListPeersCallback())


        }) {
            Text(text = "查看 peer")
        }


        Text(text = "创建通道")

        Button(onClick = { /*TODO*/ }) {
            Text(text = "开启通道")
        }




        OutlinedTextField(paymentRequest.value, onValueChange = {
            paymentRequest.value = it
        }, label = { Text(text = "PaymentRequest") })

        Button(onClick = {

            val req = RouterOuterClass.SendPaymentRequest.newBuilder()
                .setFeeLimitMsat(0)
                .setPaymentRequest(paymentRequest.value)
                .setAmtMsat(100)
                .setTimeoutSeconds(3000)
                .build()
            Lndmobile.routerSendPaymentV2(req.toByteArray(), PaymentRecvStream())


        }) {
            Text(text = "发送")
        }
    }


}