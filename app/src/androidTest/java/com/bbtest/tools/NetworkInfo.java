package com.bbtest.tools;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

public class NetworkInfo {
    private Context context = getApplicationContext();

    public void closeWifi() {
        operateWifi(false);
    }

    public void openWifi() {
        operateWifi(true);
    }

    public void operateWifi(boolean isOpenWifi) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        if (isOpenWifi) {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
        } else if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }


    public boolean isWiFiEnabled(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        } catch (Throwable ignored) {
        }
        return false;
    }

    public boolean configApState(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = null;
        try {
            // if WiFi is on, turn it off
            if (isWiFiEnabled(context)) {
                wifimanager.setWifiEnabled(false);
            }
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, !isWiFiEnabled(context));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
