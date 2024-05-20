package top.youngxhui.wallet

import android.os.Build
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.google.protobuf.ByteString
import lndmobile.Lndmobile
import lnrpc.LightningOuterClass
import lnrpc.Walletunlocker
import top.youngxhui.wallet.lnd.callback.ConnectPeerCallback
import top.youngxhui.wallet.lnd.callback.GenSeedCallback
import top.youngxhui.wallet.lnd.callback.GetNetworkCallback
import top.youngxhui.wallet.lnd.callback.ICallback
import top.youngxhui.wallet.lnd.callback.InitWalletCallback
import top.youngxhui.wallet.lnd.callback.NewAddressCallback
import top.youngxhui.wallet.lnd.callback.StartCallback
import top.youngxhui.wallet.lnd.callback.UnlockWalletCallback
import walletrpc.Walletkit
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.PrintWriter
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URL


class WalletBalanceCallback : ICallback() {
    val responseState =
        mutableStateOf(LightningOuterClass.WalletBalanceResponse.getDefaultInstance())

    override fun onResponse(p0: ByteArray?) {
        responseState.value = LightningOuterClass.WalletBalanceResponse.parseFrom(p0)
        Log.d("balance", responseState.value.toString())
    }
}

class GetInfoCallback : ICallback() {
    val responseState =
        mutableStateOf<LightningOuterClass.GetInfoResponse>(LightningOuterClass.GetInfoResponse.getDefaultInstance())

    override fun onResponse(p0: ByteArray?) {

        val resp = LightningOuterClass.GetInfoResponse.parseFrom(p0)

        Log.i("Node Info", resp.toString())
        responseState.value = resp
    }
}

class AddressListCallback : ICallback() {
    val responseState = mutableStateOf(Walletkit.ListAddressesResponse.getDefaultInstance())
    override fun onResponse(p0: ByteArray?) {

        val resp = Walletkit.ListAddressesResponse.parseFrom(p0)
        Log.i("AddressList", resp.toString())
        responseState.value = resp
    }

}

class AddInvoiceCallback : ICallback() {
    val responseState = mutableStateOf(LightningOuterClass.AddInvoiceResponse.getDefaultInstance())
    override fun onResponse(p0: ByteArray?) {
        val resp = LightningOuterClass.AddInvoiceResponse.parseFrom(p0)
        responseState.value = resp
    }
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeScreen(navController: NavHostController) {
    val genseedCallback =
        remember { GenSeedCallback() } // Remember the GenseedCallback

    val bitcoinAddress = remember { NewAddressCallback() }
    val getinfoCallback = remember { GetInfoCallback() }
    val getNetworkInfoCallback = remember { GetNetworkCallback() }
    var addressListCallback = remember {
        AddressListCallback()
    }
    val balanceCallback = remember { WalletBalanceCallback() }
    val addInvoiceCallback = remember { AddInvoiceCallback() }
    val startCallBack = remember { StartCallback() }
    val context = LocalContext.current
    val scpoe = rememberCoroutineScope()
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val url = remember { mutableStateOf("") }
    val tempurl = "check.torproject.org"
    val peerHost = remember {
        mutableStateOf("")
    }
    val peerPublicKey = remember {
        mutableStateOf("")
    }
    LaunchedEffect(key1 = startCallBack) {

//        context.registerReceiver(
//            TorReceiver(), IntentFilter(TorService.ACTION_STATUS),
//            Context.RECEIVER_NOT_EXPORTED
//        )
//
//        context.bindService(
//            Intent(context, TorService::class.java),
//            top.youngxhui.wallet.services.TorService(), BIND_AUTO_CREATE
//        )


        val file = context.filesDir
        Log.i("Lnd Start", "file $file")
        // 写入文件

        val filename = "$file/config/lnd.conf";

        val config = """[Application Options]
debuglevel=info
no-macaroons=1
maxbackoff=2s
nolisten=1

[Routing]
routing.assumechanvalid=1

[Bitcoin]
bitcoin.active=1
bitcoin.testnet=1
bitcoin.node=neutrino

[Neutrino]
neutrino.addpeer=btcd-testnet.lightning.computer
neutrino.feeurl=https://nodes.lightning.computer/fees/v1/btc-fee-estimates.json

[autopilot]
autopilot.active=0
autopilot.private=1
autopilot.minconfs=1
autopilot.conftarget=16
autopilot.allocation=1.0
autopilot.heuristic=externalscore:0.95
autopilot.heuristic=preferential:0.05
        """.trimIndent()

//        try {
        try {
            val mkdirs = File(filename).parentFile?.mkdirs()

            val out = PrintWriter(filename)
            out.println(config)
            out.close()


        } catch (e: Exception) {
            Log.e("Lnd start", "mkdirs error ${e.printStackTrace()}")
        }


//            new File(filename).getParentFile().mkdirs();
//            PrintWriter out = new PrintWriter(filename);
//            out.println(config);
//            out.close();
//        } catch (Exception e) {
//            promise.reject("Couldn't write: " + filename, e);
//            return;
//        }
//        promise.resolve("File written: " + filename);
//    }
        val cacheFileName = "$file/config/cache";

        val param = "--lnddir=$cacheFileName --bitcoin.active  --bitcoin.testnet  --bitcoin.node=neutrino --debuglevel=debug  --tlsdisableautofill  --no-macaroons --bitcoin.dnsseed=lseed.bitcoinstats.com --feeurl=https://nodes.lightning.computer/fees/v1/btc-fee-estimates.json "
        Log.i("Lnd Start", "param $param")
        Lndmobile.start(
            param,
            startCallBack
        )
    }


    LazyColumn {

        item {
            Text(text = "LND Status ${startCallBack.responseState.value}")
            Text(text = genseedCallback.responseState.value.toString()) // Display response content
            Button(onClick = {
                val seed = Walletunlocker.GenSeedRequest.getDefaultInstance()
                Lndmobile.genSeed(seed.toByteArray(), genseedCallback)


                // var req1 = NewAddressRequest.newBuilder()


            }) {
                Text(text = "Click")
            }
        }
        item {
            Row {
                Button(onClick = {
                    val pw = ByteString.copyFromUtf8("12345678")
                    val cipherSeed = genseedCallback.responseState.value
                    val req = Walletunlocker.InitWalletRequest.newBuilder().setWalletPassword(pw)

                        .addAllCipherSeedMnemonic(
                            cipherSeed
                        )
                        .build()

                    Lndmobile.initWallet(req.toByteArray(), InitWalletCallback())

                }) {
                    Text(text = "初始化钱包")
                }

                Spacer(modifier = Modifier.width(30.dp))
                OutlinedButton(onClick = {

                    val req = Walletunlocker.UnlockWalletRequest.newBuilder()
                        .setWalletPassword(ByteString.copyFromUtf8("12345678")).build()
                    Lndmobile.unlockWallet(req.toByteArray(), UnlockWalletCallback())
                }) {
                    Text(text = "解锁钱包")
                }
            }

        }

        item {
            Text(text = "Height: ${getinfoCallback.responseState.value.blockHeight}")
            //     Text(text = "NetWork ${getinfo.responseState.value.chainsList[0].chain} ${getinfo.responseState.value.chainsList[0].network}")
            SelectionContainer {
                Text(text = "public key ${getinfoCallback.responseState.value.identityPubkey}")
            }

Text(text = "URL ${getinfoCallback.responseState.value.urisList}")
            Text(text = "version ${getinfoCallback.responseState.value.version}")


            Button(onClick = {
                val req = LightningOuterClass.GetInfoRequest.getDefaultInstance()
                Lndmobile.getInfo(req.toByteArray(), getinfoCallback)


                val netReq = LightningOuterClass.NetworkInfoRequest.getDefaultInstance()
                Lndmobile.getNetworkInfo(netReq.toByteArray(), getNetworkInfoCallback)
            }) {
                Text(text = "查看基本信息")
            }
        }

        item {
            Text(text = "总余额：${balanceCallback.responseState.value.totalBalance}")
            Text(text = "未确认余额：${balanceCallback.responseState.value.unconfirmedBalance}")
            Text(text = "可用余额：${balanceCallback.responseState.value.confirmedBalance}")


            Button(onClick = {
                val request = LightningOuterClass.WalletBalanceRequest.getDefaultInstance()
                Lndmobile.walletBalance(
                    request.toByteArray(),
                    balanceCallback
                )
            }) {
                Text(text = "余额查询")
            }

        }
        item {
            Button(onClick = {
                val addressRequest = LightningOuterClass.NewAddressRequest.newBuilder()
                    .setType(LightningOuterClass.AddressType.WITNESS_PUBKEY_HASH)
                    .build()

                Lndmobile.newAddress(
                    addressRequest.toByteArray(),
                    bitcoinAddress
                )
                Log.i(
                    "Lnd",
                    "new address success ${bitcoinAddress.responseState.value}"
                )
            }) {
                Text(text = "生成比特币地址")
            }
            SelectionContainer {
                Text(text = bitcoinAddress.responseState.value.toString())

            }
            Button(onClick = {
                val res = Walletkit.ListAddressesRequest.getDefaultInstance()
                Lndmobile.walletKitListAddresses(res.toByteArray(), addressListCallback)

            }) {
                Text(text = "显示地址")
            }

            AnimatedVisibility(visible = addressListCallback.responseState.value.accountWithAddressesOrBuilderList.isNotEmpty()) {

                LazyColumn {
                    items(addressListCallback.responseState.value.accountWithAddressesList) {
                        AddressBalanceRaw(address = it.addressesList.toString() , balance = 0)
                    }
                }
            }
        }
        item {

            Button(onClick = {
                val request = LightningOuterClass.Invoice.newBuilder()
                    .setMemo("test amp")
                    .setIsAmp(true)
                    .setAmtPaidSat(100).build()

                Lndmobile.addInvoice(request.toByteArray(), addInvoiceCallback)


            }) {
                Text(text = "生成发票")
            }
            AnimatedVisibility(visible = !addInvoiceCallback.responseState.value.paymentAddr.isEmpty) {
                SelectionContainer {

                    Text(text = addInvoiceCallback.responseState.value.paymentRequest.toString())

                }
            }


        }

        item {

            Text(text = "连接 peer")

            OutlinedTextField(
                value = peerPublicKey.value,
                onValueChange = { peerPublicKey.value = it },
                placeholder = {
                    Text(text = "peer public key")
                })
            OutlinedTextField(
                value = peerHost.value,
                onValueChange = { peerHost.value = it },
                placeholder = {
                    Text(text = "peer host")
                })
            Button(onClick = {
                val lightningAddress = LightningOuterClass.LightningAddress.newBuilder()
                    .setHost(peerHost.value)
                    .setPubkey(peerPublicKey.value)
                    .build()
                val req =
                    LightningOuterClass.ConnectPeerRequest.newBuilder().setAddr(lightningAddress)
                        .setPerm(false)
                        .setTimeout(5)
                        .build()
                Lndmobile.connectPeer(req.toByteArray(), ConnectPeerCallback())
            }) {
                Text(text = "连接 peer")
            }
        }

        item {
            Button(
                onClick = {
//                if (status.value == "Not connected") {
//                    Toast.makeText(
//                        ctx,
//                        "Please connect to Tor network first",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                } else {
                    url.value = tempurl
//                }
                },

                ) {
                Text(
                    text = "Reload",

                    )
            }
        }
        item {
            Column(modifier = Modifier.size(400.dp, 800.dp)) {
                AndroidView(
                    factory = {
                        WebView(it).apply {
                            layoutParams = android.view.ViewGroup.LayoutParams(
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            val genWebViewClient = GenericWebViewClient()
                            genWebViewClient.setRequestCounterListener(
                                object : GenericWebViewClient.RequestCounterListener {
                                    override fun countChanged(requestCount: Int) {
                                        //reqCount.value = requestCount
                                    }
                                }
                            )
                            webViewClient = genWebViewClient
                        }
                    },
                    update = { view ->
                        view.loadUrl(url.value.toHttpsPrefix())
                    },
                )

            }


        }
    }


}

fun String.toHttpsPrefix(): String =
    if (isNotEmpty() && !startsWith("https://") && !startsWith("http://")) {
        "https://$this"
    } else if (startsWith("http://")) {
        replace("http://", "https://")
    } else this

internal class GenericWebViewClient : WebViewClient() {
    private var requestCounter = 0

    internal interface RequestCounterListener {
        fun countChanged(requestCount: Int)
    }

    @Volatile
    private var requestCounterListener: RequestCounterListener? = null
    fun setRequestCounterListener(requestCounterListener: RequestCounterListener?) {
        this.requestCounterListener = requestCounterListener
    }

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse {
        requestCounter++
        requestCounterListener?.countChanged(requestCounter)
        val urlString = request.url.toString().split("#".toRegex()).toTypedArray()[0]
        try {
            val connection: HttpURLConnection
            val proxied = true
            connection = if (proxied) {
                val proxy = Proxy(Proxy.Type.SOCKS, InetSocketAddress("localhost", 9050))
                URL(urlString).openConnection(proxy) as HttpURLConnection
            } else {
                URL(urlString).openConnection() as HttpURLConnection
            }
            connection.requestMethod = request.method
            for ((key, value) in request.requestHeaders) {
                connection.setRequestProperty(key, value)
            }

            // transform response to required format for WebResourceResponse parameters
            val `in`: InputStream = BufferedInputStream(connection.inputStream)
            val encoding = connection.contentEncoding
            connection.headerFields
            val responseHeaders: MutableMap<String, String> = HashMap()
            for (key in connection.headerFields.keys) {
                if (key != null && key.isNotEmpty()) {
                    responseHeaders[key] = connection.getHeaderField(key)
                }
            }
            var mimeType = "text/plain"
            if (connection.contentType != null && connection.contentType.isNotEmpty()) {
                mimeType = connection.contentType.split("; ".toRegex()).toTypedArray()[0]
            }
            return WebResourceResponse(
                mimeType,
                encoding,
                connection.responseCode,
                connection.responseMessage,
                responseHeaders,
                `in`
            )
            //return new WebResourceResponse(mimeType, "binary", in);
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // failed doing proxied http request: return empty response
        return WebResourceResponse(
            "text/plain",
            "UTF-8",
            204,
            "No Content",
            HashMap(),
            ByteArrayInputStream(byteArrayOf())
        )
    }
}


@Composable
fun AddressBalanceRaw(address: String, balance: Long) {

    SelectionContainer {
        Column {
            Text(text = address)
            Text(text = "$balance")

            Spacer(modifier = Modifier.height(2.dp))
        }
    }

}