package dpr.svich.natureprophet

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import dpr.svich.natureprophet.bluetooth.BluetoothService
import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager

import androidx.core.content.ContextCompat
import java.util.jar.Manifest


class MainActivity : FragmentActivity() {

    private lateinit var viewPager: ViewPager2

    private val REQUEST_BT_ENABLE = 1
    private val REQUEST_BT_PERMISSION = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.pager)

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        checkPermission()
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(this,
                "Ваше устройство не боддерживает технологию Bluetooth",
                Toast.LENGTH_LONG).show()
        } else {
            if (!bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE)
            } else {
                // start bluetooth service
                val intent = Intent(this, BluetoothService::class.java)
                startService(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_BT_ENABLE){
            if(resultCode == RESULT_OK){
                // start bluetooth service
                val intent = Intent(this, BluetoothService::class.java)
                startService(intent)
            } else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this,
                    "Для работы приложения необходимо влючить Bluetooth",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa){
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> CurrentStateFragment()
                1 -> ChartsFragment()
                else -> CurrentStateFragment()
            }
        }
    }

    private fun checkPermission() {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                android.Manifest.permission.BLUETOOTH
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(android.Manifest.permission.BLUETOOTH), REQUEST_BT_PERMISSION)
        }
    }
}