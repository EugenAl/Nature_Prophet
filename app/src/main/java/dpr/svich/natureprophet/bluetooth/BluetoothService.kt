package dpr.svich.natureprophet.bluetooth

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dpr.svich.natureprophet.ThisApplication
import dpr.svich.natureprophet.repository.Params
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothService : Service() {

    private val binder = LocalBinder()
    private val TAG = "MyBluetoothService"

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var pairedDevices: MutableSet<BluetoothDevice>
    private var connectedDevice: BluetoothDevice? = null
    private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val address = "00:21:13:00:2F:E9"
    private val RESULT_INTENT = 15

    // Bluetooth connections
    private var connectThread: ConnectThread? = null
    private var connectedThread: ConnectedThread? = null

    override fun onCreate() {
        super.onCreate()
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        Log.i(TAG, "service created. BT: $bluetoothAdapter")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "service started")
        val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(address)
        startConnection(device)
        return START_STICKY
    }

    inner class LocalBinder : Binder() {

    }

    private inner class ConnectThread(device: BluetoothDevice) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE){
            device.createRfcommSocketToServiceRecord(MY_UUID)
        }

        override fun run() {
            bluetoothAdapter.cancelDiscovery()

            try {
                mmSocket?.let{ socket ->
                    socket.connect()
                    startConnectedThread(socket)
                }
            } catch (e: IOException){
                Log.e(TAG, "socket connect error", e)
            }
        }

        fun cancel(){
            try{
                mmSocket?.close()
            } catch (e:IOException){
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }

    @Synchronized
    private fun startConnection(device: BluetoothDevice?){
        Log.d(TAG, "start device connection")
        connectThread = ConnectThread(device!!)
        connectThread!!.start()
    }

    @Synchronized
    private fun startConnectedThread(bluetoothSocket: BluetoothSocket?){
        Log.d(TAG, "start device data transfer")
        connectedThread = ConnectedThread(bluetoothSocket!!)
        connectedThread!!.start()
    }

    private inner class ConnectedThread(private val mmSocket: BluetoothSocket): Thread(){

        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

        override fun run() {
            var numBytes: Int   //  bytes returned from read()
            val stringBuilder = StringBuilder()

            while(true){
                numBytes = try{
                    mmInStream.read(mmBuffer)
                } catch (e:IOException){
                    Log.e(TAG, "input stream was disconnected", e)
                    break
                }
                Log.d(TAG, "read input stream")

                val message = String(mmBuffer, 0, numBytes)
                stringBuilder.append(message)
                val endOfLineIndex = stringBuilder.indexOf("\r\n")
                if(endOfLineIndex>0){
                    val result = stringBuilder.substring(0,endOfLineIndex)
                    stringBuilder.delete(0, stringBuilder.length)
                    Log.d(TAG, "message received. Message: $result")
                    pushToDatabase(result)
                }
            }
        }

        // Call this method from the main activity to shut down the connection.
        fun cancel(){
            try{
                mmSocket.close()
            } catch (e:IOException){
                Log.e(TAG, "Couldn't close connection")
            }
        }

        // Add to database data from device
        @DelicateCoroutinesApi
        fun pushToDatabase(message: String){
            val data = message.split(" ")
            val repository = (application as ThisApplication).repository
            (application as ThisApplication).applicationScope.launch {
                repository.insert(Params(0,data[0].toInt(),
                    data[2].toInt(), data[1].toInt(), System.currentTimeMillis()))
            }
            Log.d(TAG, "message pushed to db")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    private fun pushBroadcastMessage(action: String, device: BluetoothDevice?, message: String?) {
        val intent = Intent(action)
        if (device != null) {
            intent.putExtra(BluetoothUtils.EXTRA_DEVICE, device)
        }
        if (message != null) {
            intent.putExtra(BluetoothUtils.EXTRA_MESSAGE, message)
        }
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }
}