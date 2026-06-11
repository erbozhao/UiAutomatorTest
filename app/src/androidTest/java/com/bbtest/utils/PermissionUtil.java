package com.bbtest.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.test.uiautomator.UiObject2;

import com.bbtest.common.BaseCommon;

import java.util.List;

/**
 * @Author: onuszhao
 * @Date: 2024-05-30 15:50
 * @Description:
 */
public class PermissionUtil {

    static PermissionUtil sInstance = null;

    public static PermissionUtil getInstance() {
        if (sInstance == null) {
            synchronized (PermissionUtil.class) {
                if (sInstance == null) {
                    sInstance = new PermissionUtil();
                }
            }
        }
        return sInstance;
    }

    public void requestFilePermissionIfNeed(Context context, BaseCommon common) {
        if (!checkPermission(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    Uri uri = Uri.parse("package:$packageName");
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e) {
                    try {
                        Uri uri = Uri.parse("package:$packageName");
                        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception e1) {
                        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setData(Uri.parse("package:$packageName"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }

                common.sleep(common.TIMEOUT_LONG);
                UiObject2 bbtest = common.getUiObject2ByText("BBTest");
                if (bbtest == null) {
                    return;
                }
                bbtest.click();
                common.sleep(common.TIMEOUT_MEDIUM);
                UiObject2 desc = common.getUiObject2ByText("Allow access to manage all files");
                if (desc == null) {
                    return;
                }
                UiObject2 item = desc.getParent();
                if (item == null) {
                    return;
                }
                Rect bounds = item.getVisibleBounds();
                int clickX = (int) (bounds.right * 0.83);
                common.click(clickX, bounds.centerY());
                common.sleep(common.TIMEOUT_MEDIUM);
                checkPermission(context);
                common.home();
                common.sleep(common.TIMEOUT_SHORT);
            } else {
                String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (context instanceof Activity) {
                    ActivityCompat.requestPermissions((Activity) context, permissions, 0);
                }
            }
        }
    }

    public void requestFilePermissionIfNeed2(Context context, BaseCommon common) {
        if (!checkPermission(context, common)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                common.sleep(common.TIMEOUT_LONG);
                UiObject2 bbtest = common.getUiObject2ByText("BBTest");
                if (bbtest == null) {
                    return;
                }
                bbtest.click();
                common.sleep(common.TIMEOUT_MEDIUM);
                UiObject2 desc = common.getUiObject2ByText("Allow access to manage all files");
                if (desc == null) {
                    return;
                }
                UiObject2 item = desc.getParent();
                if (item == null) {
                    return;
                }
                Rect bounds = item.getVisibleBounds();
                int clickX = (int) (bounds.right * 0.83);
                common.click(clickX, bounds.centerY());
                common.sleep(common.TIMEOUT_MEDIUM);
                checkPermission(context);
                common.home();
                common.sleep(common.TIMEOUT_SHORT);
            } else {
                String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (context instanceof Activity) {
                    ActivityCompat.requestPermissions((Activity) context, permissions, 0);
                }
            }
        }
    }

    private Boolean checkPermission(Context context) {
        Boolean hasFilePermission = false;
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                hasFilePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            } else {
                hasFilePermission = Environment.isExternalStorageManager();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasFilePermission;
    }

    private Boolean checkPermission(Context context, BaseCommon common) {
        Boolean hasFilePermission = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Uri uri = Uri.parse("package:$packageName");
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                try {
                    Uri uri = Uri.parse("package:$packageName");
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e1) {
                    Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:$packageName"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }

            common.sleep(common.TIMEOUT_LONG);
            UiObject2 bbtest = common.getUiObject2ByText("BBTest");
            if (bbtest == null) {
                return hasFilePermission;
            }
            UiObject2 item = bbtest.getParent();
            if (item == null) {
                return hasFilePermission;
            }
            List<UiObject2> childs = item.getChildren();
            for (UiObject2 child: childs) {
                if (TextUtils.equals(child.getText(), "Allowed")){
                    return true;
                }
            }
        } else {
            hasFilePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return hasFilePermission;
    }
}
