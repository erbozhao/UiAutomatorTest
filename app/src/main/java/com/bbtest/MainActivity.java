package com.bbtest;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    Handler handler = new Handler(Looper.getMainLooper());

    private int KEY_REQUEST_CODE = View.generateViewId();

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(600);
                handler.postDelayed(runnable, 500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 动态获取权限，Android 6.0 新特性，一些保护权限，除了要在AndroidManifest中声明权限，还要使用如下代码动态获取
        findViewById(R.id.sdmsg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestFilePermission();
            }
        });

        // 动态获取Phone权限
        findViewById(R.id.phonemsg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    int REQUEST_CODE_CONTACT = 101;
                    String[] permissions = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_PHONE_STATE};
                    //验证是否许可权限
                    for (String permission : permissions) {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                            //申请权限
                            ActivityCompat.requestPermissions(MainActivity.this, permissions, REQUEST_CODE_CONTACT);
                            Toast.makeText(getApplicationContext(), "手机信息权限开启成功", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Toast.makeText(getApplicationContext(), "手机信息权限已开启", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        // 动态获取网络流量等信息的权限
        findViewById(R.id.netmsg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    // 无需开启
                    Toast.makeText(getApplicationContext(), "SDK过低，无需开启", Toast.LENGTH_SHORT).show();
                } else {
                    final AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
                    int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
                    if (mode == AppOpsManager.MODE_ALLOWED) {
                        Toast.makeText(getApplicationContext(), "开启成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 打开“有权查看使用情况的应用”页面
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "开启成功", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // 动态获取通知消息权限
        findViewById(R.id.pushmsg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断是否开启监听通知权限
                if (NotificationManagerCompat.getEnabledListenerPackages(getApplicationContext()).contains(getPackageName())) {
//            Intent serviceIntent = new Intent(this, PushMonitorService.class);
//            startService(serviceIntent);
                    Toast.makeText(getApplicationContext(), "通知栏权限打开", Toast.LENGTH_SHORT).show();
                    toggleNotificationListenerService(getApplicationContext());
                } else {
                    // 去开启 监听通知权限
//            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                    goToNotificationAccessSetting(getApplicationContext());
                }
            }
        });

        String model = android.os.Build.MODEL;
        if (model.equals("HD1900")) {
            findViewById(R.id.test).setVisibility(View.VISIBLE);

            findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Log.i("BBTest", "start test");
                        Process pro = Runtime.getRuntime().exec("am instrument --user 0 -w -r -e debug false -e class 'com.bbtest.stable.MonkeyTest#testMonkey' com.bbtest.test/androidx.test.runner.AndroidJUnitRunner");
                        BufferedReader bfr = new BufferedReader(new InputStreamReader(pro.getInputStream()));
                        String line = "";
                        while ((line = bfr.readLine()) != null) {
                            System.out.println(line + "\n");
                        }
                        pro.waitFor();
                        bfr.close();
                        Log.i("BBTest", "end test");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    // 判断是否打开了通知监听权限
    private boolean isEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean goToNotificationAccessSetting(Context context) {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            //普通情况下找不到的时候需要再特殊处理找一次
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                context.startActivity(intent);
                return true;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            Toast.makeText(context, "对不起，您的手机暂不支持", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
    }

    //先关闭再启动
    public static void toggleNotificationListenerService(Context context) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, PushMonitorService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(context, PushMonitorService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }


    private void requestFilePermission() {
        if (!hasFilePermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    Uri uri = Uri.parse("package:$packageName");
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION, uri);
                    startActivityForResult(intent, KEY_REQUEST_CODE);
                } catch (Exception e) {
                    try {
                        Uri uri = Uri.parse("package:$packageName");
                        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        startActivityForResult(intent, KEY_REQUEST_CODE);
                    } catch (Exception e1) {
                        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setData(Uri.parse("package:$packageName"));
                        startActivityForResult(intent, KEY_REQUEST_CODE);
                    }
                }
            } else {
                String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                ActivityCompat.requestPermissions(this, permissions, KEY_REQUEST_CODE);
            }
        }
    }

    private Boolean hasFilePermission() {
        Boolean hasFilePermission = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Manifest.permission.MANAGE_EXTERNAL_STORAGE
            hasFilePermission = Environment.isExternalStorageManager();
        } else {
            String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            for (String permission : permissions) {
                hasFilePermission = ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
                if (!hasFilePermission) {
                    return hasFilePermission;
                }
            }
        }
        return hasFilePermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == KEY_REQUEST_CODE) {
            Log.d("onuszhao", "permissions=${permissions}  grantResults=$grantResults");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KEY_REQUEST_CODE) {
            if (hasFilePermission()) {
                Toast.makeText(this, "已获取文件权限", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "未获取文件权限", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
