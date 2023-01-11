package com.sefatombul.permissionassist

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

/**
 * Created by Sefa TOMBUL on 11.01.2023
 **/


object PermissionHelper {

    fun shouldShowRequestPermissionRationale(
        activity: Activity,
        permission: String
    ): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity, permission
        )
    }

    fun shouldShowRequestAllPermissionRationale(
        activity: Activity,
        permissionsRequired: Array<String>,
        f: (allGranted: Boolean, grantedList: ArrayList<String>, deniedList: ArrayList<String>) -> Unit
    ) {
        var allGranted = true
        val grantedList = ArrayList<String>()
        val deniedList = ArrayList<String>()
        permissionsRequired.forEach {
            val result = shouldShowRequestPermissionRationale(activity, it)
            allGranted = allGranted && result
            if (result) {
                grantedList.add(it)
            } else {
                deniedList.add(it)
            }
        }
        f(allGranted, grantedList, deniedList)
    }

    fun checkSelfPermission(
        context: Context,
        permission: String
    ): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }


    fun checkSelfAllPermission(
        context: Context,
        permissionsRequired: Array<String>,
        f: (allGranted: Boolean, grantedList: ArrayList<String>, deniedList: ArrayList<String>) -> Unit
    ) {
        var allGranted = true
        val grantedList = ArrayList<String>()
        val deniedList = ArrayList<String>()
        permissionsRequired.forEach {
            val result = checkSelfPermission(context, it)
            allGranted = allGranted && result
            if (result) {
                grantedList.add(it)
            } else {
                deniedList.add(it)
            }
        }
        f(allGranted, grantedList, deniedList)
    }

}