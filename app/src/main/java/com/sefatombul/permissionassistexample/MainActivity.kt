package com.sefatombul.permissionassistexample

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sefatombul.permissionassist.PermissionAssist
import com.sefatombul.permissionassistexample.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionsClass: PermissionAssist

    private var permissions = arrayOf(
        Manifest.permission.CALL_PHONE,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()
        handleClickEventListeners()

    }

    private fun initialize() {
        permissionsClass = PermissionAssist(this@MainActivity)
    }

    private fun handleClickEventListeners() {
        binding.apply {
            btnRequest.setOnClickListener {
                permissionsClass
                    .permissionDialogTitle("Application Permissions")
                    .permissionDialogDescription("Please grant permissions for the app to work stable.")
                    .permissionDialogAllowText("Allow")
                    .permissionDialogNegativeText("Cancel")
                    .permissionDialogOpenSettingsButtonText("Settings")
                    .setNegativeButtonVisible(true)
                    .cancelable(false)
                    .requestPermissions(permissions) { allGranted, grantedList, deniedList ->
                        println("allGranted : $allGranted")
                        grantedList.forEach {
                            println("grantedList : $it")
                        }
                        deniedList.forEach {
                            println("deniedList : $it")
                        }
                    }
            }
        }
    }


}