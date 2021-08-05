package com.modulotech

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.modulotech.adapters.RestaurantAdapter
import com.modulotech.databinding.ActivityMainBinding
import com.modulotech.utilities.*
import com.modulotech.workers.UploadWorker
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val WORKER_SYNC_NAME = "record_worker_sync"
        const val WORKER_DURATION: Long = 30 // minutes
    }
    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: RestaurantAdapter

    private val synchronizedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Logger.i("Receive action update data list")
            updateDisplayInfo()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(toolbar)

        initViews()
        registerReceiver()
        requestStoragePermission()
    }

    override fun onDestroy() {
        unregisterReceiver()
        super.onDestroy()
    }

    private fun registerReceiver() {
        val filter = IntentFilter(ACTION_UPDATE_SYNCHRONIZED_LIST)
        registerReceiver(synchronizedReceiver, filter)
    }

    private fun unregisterReceiver() {
        unregisterReceiver(synchronizedReceiver)
    }

    private fun initViews() {
        binding.btnRequestPermission.setOnClickListener(this)
        adapter = RestaurantAdapter()
        binding?.restaurantList.adapter = adapter
    }

    private fun requestStoragePermission(): Boolean {
        Logger.i("requestStoragePermission")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_CALL_LOG)
                == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.WRITE_CALL_LOG)
                == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                onPermissionAllow()
                true
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.WRITE_CALL_LOG,
                        Manifest.permission.READ_PHONE_STATE),
                    1
                )
                false
            }
        } else {
            onPermissionAllow()
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && grantResults[1] == PackageManager.PERMISSION_GRANTED
            && grantResults[2] == PackageManager.PERMISSION_GRANTED
            && grantResults[3] == PackageManager.PERMISSION_GRANTED
            && grantResults[4] == PackageManager.PERMISSION_GRANTED
        ) {
            onPermissionAllow()
        } else {
            binding.containerPermissionNotAllow.visibility = View.VISIBLE
        }
    }

    private fun onPermissionAllow() {
        Logger.i("onPermissionAllow")
        binding.containerPermissionNotAllow.visibility = View.GONE
        updateDisplayInfo()
        val uploadRequest = PeriodicWorkRequestBuilder<UploadWorker>(WORKER_DURATION, TimeUnit.MINUTES)
                .addTag(WORKER_SYNC_NAME)
                .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WORKER_SYNC_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            uploadRequest
        )

        // For test
        /*GlobalScope.launch {
            forTest()
        }*/
    }

    private fun forTest() {
        val lastDayAgo = 5 //days
        val list = getCallInfo(applicationContext, nowByMiniSecond() - lastDayAgo * 24 * 60 * 60 * 1000)
    }

    private fun updateDisplayInfo() {
        binding.containerStatus.visibility = View.VISIBLE
        val lastSynchronizedTime = SharedPreferencesManager.getLastSynchronizedTime(this)
        if (lastSynchronizedTime > 0) {
            binding.lastSynchronizedTime.text = convertTimeStampToString(lastSynchronizedTime)
        } else {
            binding.lastSynchronizedTime.text = "Not yet"
        }
        binding.synchronizedDuration.text = "$WORKER_DURATION minutes"
        val list = SharedPreferencesManager.getSynchronizedCallList(this)
        if (list.isNotEmpty()) {
            adapter.submitList(list)
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btnRequestPermission) {
            requestStoragePermission()
        }
    }
}