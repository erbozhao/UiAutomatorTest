package com.bbtest

import android.Manifest
import android.app.AppOpsManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper())

    private val keyRequestCode = View.generateViewId()

    private val runnable = object : Runnable {
        override fun run() {
            try {
                Thread.sleep(600)
                handler.postDelayed(this, 500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.sdmsg).setOnClickListener {
            requestFilePermission()
        }

        findViewById<View>(R.id.phonemsg).setOnClickListener {
            if (Build.VERSION.SDK_INT >= 23) {
                val requestCodeContact = 101
                val permissions = arrayOf(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_PHONE_STATE,
                )
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, permissions, requestCodeContact)
                        Toast.makeText(applicationContext, "手机信息权限开启成功", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    } else {
                        Toast.makeText(applicationContext, "手机信息权限已开启", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        findViewById<View>(R.id.netmsg).setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Toast.makeText(applicationContext, "SDK过低，无需开启", Toast.LENGTH_SHORT).show()
            } else {
                val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                val mode = appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    packageName,
                )
                if (mode == AppOpsManager.MODE_ALLOWED) {
                    Toast.makeText(applicationContext, "开启成功", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    startActivity(intent)
                    Toast.makeText(applicationContext, "开启成功", Toast.LENGTH_SHORT).show()
                }
            }
        }

        findViewById<View>(R.id.pushmsg).setOnClickListener {
            if (NotificationManagerCompat.getEnabledListenerPackages(applicationContext).contains(packageName)) {
                Toast.makeText(applicationContext, "通知栏权限打开", Toast.LENGTH_SHORT).show()
                toggleNotificationListenerService(applicationContext)
            } else {
                goToNotificationAccessSetting(applicationContext)
            }
        }

        val model = Build.MODEL
        if (model == "HD1900") {
            findViewById<View>(R.id.test).visibility = View.VISIBLE

            findViewById<View>(R.id.test).setOnClickListener {
                try {
                    Log.i("BBTest", "start test")
                    val process = Runtime.getRuntime().exec(
                        "am instrument --user 0 -w -r -e debug false -e class 'com.bbtest.stable.MonkeyTest#testMonkey' com.bbtest.test/androidx.test.runner.AndroidJUnitRunner",
                    )
                    val reader = BufferedReader(InputStreamReader(process.inputStream))
                    var line = ""
                    while (reader.readLine().also { line = it ?: "" } != null) {
                        System.out.println("$line\n")
                    }
                    process.waitFor()
                    reader.close()
                    Log.i("BBTest", "end test")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun isEnabled(): Boolean {
        val pkgName = packageName
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":")
            for (name in names) {
                val componentName = ComponentName.unflattenFromString(name)
                if (componentName != null && TextUtils.equals(pkgName, componentName.packageName)) {
                    return true
                }
            }
        }
        return false
    }

    private fun requestFilePermission() {
        if (!hasFilePermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    val uri = Uri.parse("package:\$packageName")
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION, uri)
                    startActivityForResult(intent, keyRequestCode)
                } catch (e: Exception) {
                    try {
                        val uri = Uri.parse("package:\$packageName")
                        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        startActivityForResult(intent, keyRequestCode)
                    } catch (e1: Exception) {
                        val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.data = Uri.parse("package:\$packageName")
                        startActivityForResult(intent, keyRequestCode)
                    }
                }
            } else {
                val permissions = arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )
                ActivityCompat.requestPermissions(this, permissions, keyRequestCode)
            }
        }
    }

    private fun hasFilePermission(): Boolean {
        var hasFilePermission = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            hasFilePermission = Environment.isExternalStorageManager()
        } else {
            val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            )
            for (permission in permissions) {
                hasFilePermission = ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
                if (!hasFilePermission) {
                    return hasFilePermission
                }
            }
        }
        return hasFilePermission
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == keyRequestCode) {
            Log.d("onuszhao", "permissions=\${permissions}  grantResults=\$grantResults")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == keyRequestCode) {
            if (hasFilePermission()) {
                Toast.makeText(this, "已获取文件权限", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "未获取文件权限", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun goToNotificationAccessSetting(context: Context): Boolean {
            return try {
                val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } catch (e: ActivityNotFoundException) {
                try {
                    val intent = Intent()
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val componentName = ComponentName(
                        "com.android.settings",
                        "com.android.settings.Settings\$NotificationAccessSettingsActivity",
                    )
                    intent.component = componentName
                    intent.putExtra(":settings:show_fragment", "NotificationAccessSettings")
                    context.startActivity(intent)
                    true
                } catch (e1: Exception) {
                    e1.printStackTrace()
                    Toast.makeText(context, "对不起，您的手机暂不支持", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                    false
                }
            }
        }

        fun toggleNotificationListenerService(context: Context) {
            val packageManager = context.packageManager
            packageManager.setComponentEnabledSetting(
                ComponentName(context, PushMonitorService::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP,
            )
            packageManager.setComponentEnabledSetting(
                ComponentName(context, PushMonitorService::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP,
            )
        }
    }
}
