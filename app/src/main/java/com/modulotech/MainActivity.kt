package com.modulotech

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SubscriptionManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.modulotech.utilities.*
import com.modulotech.workers.UploadWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        const val WORKER_SYNC_NAME = "worker_sync_5"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.navHostFragment)
        toolbar.setupWithNavController(navController)

        requestStoragePermission()
    }

    private fun requestStoragePermission(): Boolean {
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
                setupWorker()
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
            setupWorker()
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
            && grantResults[4] == PackageManager.PERMISSION_GRANTED) {
            setupWorker()
        }
    }

    private fun setupWorker() {
        Logger.i("setupWorker")
        val uploadRequest = PeriodicWorkRequestBuilder<UploadWorker>(15, TimeUnit.MINUTES)
                .addTag(WORKER_SYNC_NAME)
                .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WORKER_SYNC_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            uploadRequest
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.navHostFragment).navigateUp()
    }
}