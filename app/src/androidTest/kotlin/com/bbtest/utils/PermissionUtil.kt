package com.bbtest.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.test.uiautomator.UiObject2
import com.bbtest.common.BaseCommon

class PermissionUtil private constructor() {
    fun requestFilePermissionIfNeed(context: Context, common: BaseCommon) {
        if (!checkPermission(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    val uri = Uri.parse("package:\$packageName")
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION, uri)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    try {
                        val uri = Uri.parse("package:\$packageName")
                        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    } catch (e1: Exception) {
                        val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.data = Uri.parse("package:\$packageName")
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                }

                common.sleep(common.TIMEOUT_LONG.toLong())
                val bbtest = common.getUiObject2ByText("BBTest") ?: return
                bbtest.click()
                common.sleep(common.TIMEOUT_MEDIUM.toLong())
                val desc = common.getUiObject2ByText("Allow access to manage all files") ?: return
                val item = desc.parent ?: return
                val bounds: Rect = item.visibleBounds
                val clickX = (bounds.right * 0.83).toInt()
                common.click(clickX, bounds.centerY())
                common.sleep(common.TIMEOUT_MEDIUM.toLong())
                checkPermission(context)
                common.home()
                common.sleep(common.TIMEOUT_SHORT.toLong())
            } else {
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
                if (context is Activity) {
                    ActivityCompat.requestPermissions(context, permissions, 0)
                }
            }
        }
    }

    fun requestFilePermissionIfNeed2(context: Context, common: BaseCommon) {
        if (!checkPermission(context, common)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                common.sleep(common.TIMEOUT_LONG.toLong())
                val bbtest = common.getUiObject2ByText("BBTest") ?: return
                bbtest.click()
                common.sleep(common.TIMEOUT_MEDIUM.toLong())
                val desc = common.getUiObject2ByText("Allow access to manage all files") ?: return
                val item = desc.parent ?: return
                val bounds: Rect = item.visibleBounds
                val clickX = (bounds.right * 0.83).toInt()
                common.click(clickX, bounds.centerY())
                common.sleep(common.TIMEOUT_MEDIUM.toLong())
                checkPermission(context)
                common.home()
                common.sleep(common.TIMEOUT_SHORT.toLong())
            } else {
                val permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
                if (context is Activity) {
                    ActivityCompat.requestPermissions(context, permissions, 0)
                }
            }
        }
    }

    private fun checkPermission(context: Context): Boolean {
        var hasFilePermission = false
        try {
            hasFilePermission =
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    ) == PackageManager.PERMISSION_GRANTED
                } else {
                    Environment.isExternalStorageManager()
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return hasFilePermission
    }

    private fun checkPermission(context: Context, common: BaseCommon): Boolean {
        var hasFilePermission = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val uri = Uri.parse("package:\$packageName")
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION, uri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (e: Exception) {
                try {
                    val uri = Uri.parse("package:\$packageName")
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } catch (e1: Exception) {
                    val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data = Uri.parse("package:\$packageName")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
            }

            common.sleep(common.TIMEOUT_LONG.toLong())
            val bbtest = common.getUiObject2ByText("BBTest") ?: return hasFilePermission
            val item = bbtest.parent ?: return hasFilePermission
            val childs = item.children
            for (child in childs) {
                if (TextUtils.equals(child.text, "Allowed")) {
                    return true
                }
            }
        } else {
            hasFilePermission =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ) == PackageManager.PERMISSION_GRANTED
        }
        return hasFilePermission
    }

    companion object {
        @Volatile
        private var sInstance: PermissionUtil? = null

        @JvmStatic
        fun getInstance(): PermissionUtil {
            if (sInstance == null) {
                synchronized(PermissionUtil::class.java) {
                    if (sInstance == null) {
                        sInstance = PermissionUtil()
                    }
                }
            }
            return sInstance!!
        }
    }
}
