package com.sefatombul.permissionassist

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog

/**
 * Created by Sefa TOMBUL on 11.01.2023
 **/


class PermissionAssist constructor(private val activity: ComponentActivity) {
    private var permissionsRequired = arrayOf<String>()

    private var isStartRequestMultiplePermissions = false
    private var isOpenSettings = false
    private var requestMultiplePermissions: ActivityResultLauncher<Array<String>>? = null
    private var startSettingsForResult: ActivityResultLauncher<Intent>? = null

    /**
     * Permission Dialog Default Values
     * */
    private var dialogTitle = "Permissions"
    private var dialogNegativeText = "Cancel"
    private var dialogDescription = "Please grant the necessary permissions."
    private var dialogAllowText = "Allow"
    private var dialogOpenSettingsButtonText = "Settings"

    private var isCancelableDialog = true
    private var negativeButtonVisible = false

    private var permissionRequestListener: ((allGranted: Boolean, grantedList: ArrayList<String>, deniedList: ArrayList<String>) -> Unit)? =
        null

    init {
        requestMultiplePermissions = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            var allGranted = true
            val grantedList = ArrayList<String>()
            val deniedList = ArrayList<String>()
            permissions.entries.forEach { entrie ->
                allGranted = allGranted && entrie.value
                if (entrie.value) {
                    grantedList.add(entrie.key)
                } else {
                    deniedList.add(entrie.key)
                }
            }

            if (allGranted) {
                permissionRequestListener?.let {
                    it(allGranted, grantedList, deniedList)
                }
            } else {
                PermissionHelper.shouldShowRequestAllPermissionRationale(
                    activity,
                    deniedList.toTypedArray()
                ) { shouldShowAllGranted, shouldShowGrantedList, shouldShowDeniedList ->
                    if (!shouldShowGrantedList.isNullOrEmpty()) {
                        showDialog(
                            dialogAllowText,
                            positiveButtonClickListener = {
                                permissionsLaunch()
                            },
                            negativeButtonClickListener = {
                                permissionRequestListener?.let {
                                    it(allGranted, grantedList, deniedList)
                                }
                            },
                            cancelListener = {
                                permissionRequestListener?.let {
                                    it(allGranted, grantedList, deniedList)
                                }
                            }
                        )
                    }
                    if (!shouldShowDeniedList.isNullOrEmpty()) {
                        showDialog(
                            dialogOpenSettingsButtonText,
                            positiveButtonClickListener = {
                                settingsLaunch()
                            },
                            negativeButtonClickListener = {
                                permissionRequestListener?.let {
                                    it(allGranted, grantedList, deniedList)
                                }
                            },
                            cancelListener = {
                                permissionRequestListener?.let {
                                    it(allGranted, grantedList, deniedList)
                                }
                            }
                        )
                    }
                }
            }
        }

        startSettingsForResult =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                requestPermissions(permissionsRequired)
                isOpenSettings = false
            }
    }

    fun isOpenSettings() = isOpenSettings
    fun isStartRequestMultiplePermissions() = isStartRequestMultiplePermissions
    fun setNegativeButtonVisible(visible: Boolean): PermissionAssist {
        negativeButtonVisible = visible
        return this
    }

    fun requestPermissions(
        permissions: Array<String>,
        requestListener: ((allGranted: Boolean, grantedList: ArrayList<String>, deniedList: ArrayList<String>) -> Unit)? = null
    ) {
        requestListener?.let {
            permissionRequestListener = it
        }
        if (!permissions.isNullOrEmpty()) {
            permissionsRequired = permissions.copyOf()
        }

        PermissionHelper.checkSelfAllPermission(
            activity,
            permissionsRequired
        ) { allGranted, grantedList, deniedList ->
            if (!allGranted) {
                PermissionHelper.shouldShowRequestAllPermissionRationale(
                    activity,
                    deniedList.toTypedArray()
                ) { allGranted, grantedList, deniedList ->
                    if (!grantedList.isNullOrEmpty()) {
                        showDialog(
                            dialogAllowText,
                            positiveButtonClickListener = {
                                permissionsLaunch()
                            },
                            negativeButtonClickListener = {
                                permissionRequestListener?.let {
                                    it(allGranted, grantedList, deniedList)
                                }
                            },
                            cancelListener = {
                                permissionRequestListener?.let {
                                    it(allGranted, grantedList, deniedList)
                                }
                            }
                        )
                    }
                    if (!deniedList.isNullOrEmpty()) {
                        permissionsLaunch()
                    }
                }
            } else {
                permissionRequestListener?.let {
                    it(allGranted, grantedList, deniedList)
                }
            }
        }
    }

    private fun settingsLaunch() {
        isOpenSettings = true
        startSettingsForResult?.launch(intentAppSetting(activity))
    }

    private fun intentAppSetting(context: Context): Intent? {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.fromParts("package", context.packageName, null)
        } else {
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.data = Uri.parse("package:" + context.packageName)
        }
        return intent
    }

    private fun showDialog(
        buttonText: String,
        positiveButtonClickListener: () -> Unit,
        negativeButtonClickListener: (() -> Unit)? = null,
        cancelListener: (() -> Unit)? = null,
    ) {
        showDialog(
            context = activity,
            title = dialogTitle,
            description = dialogDescription,
            negativeButtonText = dialogNegativeText,
            negativeButtonClickListener = {
                negativeButtonClickListener?.let {
                    it()
                }
            },
            positiveButtonText = buttonText,
            positiveButtonClickListener = {
                positiveButtonClickListener?.let {
                    it()
                }
            },
            cancelListener = {
                cancelListener?.let { _cancelListener ->
                    _cancelListener()
                }
            },
            cancelable = isCancelableDialog,
            negativeButtonVisible = negativeButtonVisible
        )
    }

    private fun showDialog(
        context: Context,
        title: String,
        description: String,
        negativeButtonText: String? = null,
        negativeButtonClickListener: (() -> Unit)? = null,
        positiveButtonText: String,
        positiveButtonClickListener: () -> Unit,
        cancelListener: (() -> Unit)? = null,
        cancelable: Boolean = true,
        negativeButtonVisible: Boolean = true
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(cancelable)
        builder.setTitle(title)
        builder.setMessage(description)
        builder.setPositiveButton(positiveButtonText) { dialog, _ ->
            dialog.dismiss()
            positiveButtonClickListener?.let {
                it()
            }
        }
        if (negativeButtonVisible) {
            builder.setNegativeButton(negativeButtonText) { dialog, _ ->
                dialog.dismiss()
                negativeButtonClickListener?.let {
                    it()
                }
            }
        }
        builder.setOnCancelListener {
            cancelListener?.let { _cancelListener ->
                _cancelListener()
            }
        }
        builder.show()
    }

    private fun permissionsLaunch() {
        isStartRequestMultiplePermissions = true

        requestMultiplePermissions?.launch(
            permissionsRequired
        )
    }

    /**
     * Set whether the permission request popup is cancelable.
     * @param [cancelable]
     *      If true, a cancelable popup will pop up.
     *      If false, the popup opens and cannot be canceled until it is allowed.
     * @author Sefa TOMBUL
     * @since 10/01/2023
     */
    fun cancelable(cancelable: Boolean): PermissionAssist {
        isCancelableDialog = cancelable
        return this
    }

    fun permissionDialogTitle(dialogTitle: String): PermissionAssist {
        this.dialogTitle = dialogTitle
        return this
    }

    fun permissionDialogDescription(dialogDescription: String): PermissionAssist {
        this.dialogDescription = dialogDescription
        return this
    }

    fun permissionDialogAllowText(allowText: String): PermissionAssist {
        this.dialogAllowText = allowText
        return this
    }

    fun permissionDialogOpenSettingsButtonText(dialogOpenSettingsButtonText: String): PermissionAssist {
        this.dialogOpenSettingsButtonText = dialogOpenSettingsButtonText
        return this
    }

    fun permissionDialogNegativeText(dialogNegativeText: String): PermissionAssist {
        this.dialogNegativeText = dialogNegativeText
        return this
    }

}