package com.modulotech

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
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
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(toolbar)

        initViews()
        requestStoragePermission()
    }

    private fun initViews() {
        binding.btnRequestPermission.setOnClickListener(this)
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
            binding.containerPermissionOK.visibility = View.GONE
        }
    }

    private fun onPermissionAllow() {
        Logger.i("onPermissionAllow")
        binding.containerPermissionOK.visibility = View.VISIBLE
        binding.containerPermissionNotAllow.visibility = View.GONE
        val uploadRequest = PeriodicWorkRequestBuilder<UploadWorker>(30, TimeUnit.MINUTES)
                .addTag(WORKER_SYNC_NAME)
                .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WORKER_SYNC_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            uploadRequest
        )
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btnRequestPermission) {
            requestStoragePermission()
        }
    }
}