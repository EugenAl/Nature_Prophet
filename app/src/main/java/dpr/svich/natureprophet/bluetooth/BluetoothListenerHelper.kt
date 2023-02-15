package dpr.svich.natureprophet.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class BluetoothListenerHelper {

    companion object{
        private var mBluetoothBroatcastReceiver: BluetoothBroadcastReceiver? = null

        class BluetoothBroadcastReceiver : BroadcastReceiver(){
            private var mGlobalListener: IBluetoothListener? = null

            public fun setBluetoothListener(listener: IBluetoothListener){
                mGlobalListener = listener
            }

            public fun removeBluetoothListener(listener: IBluetoothListener):Boolean{
                if(mGlobalListener == listener){
                    mGlobalListener = null
                }
                return mGlobalListener == null
            }

            override fun onReceive(context: Context?, intent: Intent?) {
                val device =
                    intent!!.getParcelableExtra<BluetoothDevice>(BluetoothUtils.EXTRA_DEVICE)
                val message = intent.getStringExtra(BluetoothUtils.EXTRA_MESSAGE)

                when(intent.action){
                    BluetoothUtils.ACTION_DEVICE_FOUND ->{
                        mGlobalListener!!.onDeviceDiscovered(device)
                    }
                    BluetoothUtils.ACTION_DISCOVERY_STARTED -> {
                        mGlobalListener!!.onDiscoveryStarted()
                    }
                    BluetoothUtils.ACTION_DISCOVERY_STOPPED -> {
                        mGlobalListener!!.onDiscoveryStopped()
                    }
                    BluetoothUtils.ACTION_DEVICE_CONNECTED -> {
                        mGlobalListener!!.onDeviceConnected(device)
                    }
                    BluetoothUtils.ACTION_MESSAGE_RECEIVED -> {
                        mGlobalListener!!.onMessageReceived(device, message)
                    }
                    BluetoothUtils.ACTION_MESSAGE_SENT -> {
                        mGlobalListener!!.onMessageSent(device)
                    }
                    BluetoothUtils.ACTION_CONNECTION_ERROR -> {
                        mGlobalListener!!.onError(message)
                    }
                    BluetoothUtils.ACTION_DEVICE_DISCONNECTED -> {
                        mGlobalListener!!.onDeviceDisconnected()
                    }
                }
            }
        }

        public fun registerBluetoothListener(context: Context?, listener: IBluetoothListener){
            if(mBluetoothBroatcastReceiver == null){
                mBluetoothBroatcastReceiver = BluetoothBroadcastReceiver()

                val intentFilter = IntentFilter().also {
                    it.addAction(BluetoothUtils.ACTION_DEVICE_FOUND)
                    it.addAction(BluetoothUtils.ACTION_DISCOVERY_STARTED)
                    it.addAction(BluetoothUtils.ACTION_DISCOVERY_STOPPED)
                    it.addAction(BluetoothUtils.ACTION_DEVICE_CONNECTED)
                    it.addAction(BluetoothUtils.ACTION_MESSAGE_RECEIVED)
                    it.addAction(BluetoothUtils.ACTION_MESSAGE_SENT)
                    it.addAction(BluetoothUtils.ACTION_CONNECTION_ERROR)
                    it.addAction(BluetoothUtils.ACTION_DEVICE_DISCONNECTED)
                }

                LocalBroadcastManager.getInstance(context!!).registerReceiver(
                    mBluetoothBroatcastReceiver!!, intentFilter
                )
            }

            mBluetoothBroatcastReceiver!!.setBluetoothListener(listener)
        }

        public fun unregisterBluetoothListener(context: Context?, listener: IBluetoothListener){
            mBluetoothBroatcastReceiver?.let {
                val empty = it.removeBluetoothListener(listener)

                if(empty){
                    LocalBroadcastManager.getInstance(context!!)
                        .unregisterReceiver(it)
                }
            }
            mBluetoothBroatcastReceiver = null
        }
    }
}