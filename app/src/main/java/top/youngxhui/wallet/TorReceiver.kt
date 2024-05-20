package top.youngxhui.wallet

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import org.torproject.jni.TorService

class TorReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val status = intent.getStringExtra(TorService.EXTRA_STATUS)
        Toast.makeText(context, "Tor $status", Toast.LENGTH_SHORT).show()
    }
}