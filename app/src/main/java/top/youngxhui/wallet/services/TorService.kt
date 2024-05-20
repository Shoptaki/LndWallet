package top.youngxhui.wallet.services


import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import org.torproject.jni.TorService

class TorService : ServiceConnection {
    override fun onServiceConnected(
        name: ComponentName,
        service: IBinder
    ) {
        //moved torService to a local variable, since we only need it once
        val torService = (service as TorService.LocalBinder).service
//        torService.torControlConnection.setConf("ControlPort","9051")
//        torService.torControlConnection.setConf("SOCKPort","9050")


//        while (torService.tor  .torControlConnection == null) {
//            try {
//                Thread.sleep(500)
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
//        }
        Log.i("Tor service","start")

        // status.value = "Tor Control Connection established"
    }

    override fun onServiceDisconnected(name: ComponentName) {}

}