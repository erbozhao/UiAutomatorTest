package com.bbtest.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Build;
import android.os.PatternMatcher;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.test.uiautomator.UiDevice;

import java.util.ArrayList;
import java.util.List;

public class WifiTools {

    public UiDevice device = null;
    private Context context = null;
    private WifiManager wifiManager = null;
    private ConnectivityManager connectivityManager = null;

    public WifiTools(UiDevice device, Context context) {
        this.device = device;
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void connectWifi(String ssid, String password) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            connectByP2P(ssid, password);
        } else {
            connectByConfig(ssid, password);
        }
    }

    public boolean isNetworkConnected() {
        if (context != null) {
            NetworkInfo mNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public boolean isNetworkAvailable() {
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {       // 当前网络是连接的
                return info.getState() == NetworkInfo.State.CONNECTED;      // 当前所连接的网络可用
            }
        }
        return false;
    }

    public boolean isWifiConnected() {
        if (context != null) {
            NetworkInfo mWiFiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public void openWifi() {
        if (!wifiManager.isWifiEnabled()) {
            // 用过命令打开wifi
            ShellCommand.execCmdByUiDevice(device, "svc wifi enable");
        }
    }

    public void startScantWifi() {
        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<ScanResult> scanResults = wifiManager.getScanResults();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(wifiScanReceiver, intentFilter);
        wifiManager.startScan();
    }

    // Android8以下 通过Config连接Wifi
    private void connectByConfig(String ssid, String password) {
        WifiConfiguration config = new WifiConfiguration();
        // 不需要密码的场景
//                config.SSID = "\"" + ssid + "\"";
//                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        // 以WEP加密的场景
//                config.hiddenSSID = true;
//                config.wepKeys[0] = "\"" + password + "\"";
//                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
//                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//                config.wepTxKeyIndex = 0;
        // 以WPA加密的场景，自己测试时，发现热点以WPA2建立时，同样可以用这种配置连接
        config.SSID = "\"" + ssid + "\"";
        config.preSharedKey = "\"" + password + "\"";
        config.hiddenSSID = true;
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.status = WifiConfiguration.Status.ENABLED;
        // 添加WifiConfiguration
        wifiManager.addNetwork(config);

        //保存连接信息
        wifiManager.saveConfiguration();

        wifiManager.disconnect();
        // 启用并尝试连接到 wifi
        wifiManager.enableNetwork(config.networkId, true);
        wifiManager.reconnect();
    }

    // Android10以上 通过P2P连接Wifi
    @RequiresApi(Build.VERSION_CODES.Q)
    private void connectByP2P(String ssid, String password) {
        NetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
                .setSsidPattern(new PatternMatcher("\"" + ssid + "\"", PatternMatcher.PATTERN_PREFIX))
                .setWpa2Passphrase(password)
                .build();

        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .setNetworkSpecifier(specifier)
                .build();

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                // do success processing here..
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUnavailable() {
                // do failure processing here..
                Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
            }
        };
        connectivityManager.requestNetwork(request, networkCallback);
    }

    // Android10以上，通过suggestion连接WIFI
    private void connectBySug(String ssid, String password) {
        // 无密码
        WifiNetworkSuggestion suggestion1 = new WifiNetworkSuggestion.Builder()
                .setSsid("\"" + ssid + "\"")
                .setIsAppInteractionRequired(true)
                .build();
        // WPA2
        WifiNetworkSuggestion suggestion2 =
                new WifiNetworkSuggestion.Builder()
                        .setSsid("\"" + ssid + "\"")
                        .setWpa2Passphrase("\"" + password + "\"")
                        .setIsAppInteractionRequired(true)
                        .build();
        // WPA3
        WifiNetworkSuggestion suggestion3 =
                new WifiNetworkSuggestion.Builder()
                        .setSsid("\"" + ssid + "\"")
                        .setWpa3Passphrase("\"" + password + "\"")
                        .setIsAppInteractionRequired(true)
                        .build();

        List<WifiNetworkSuggestion> suggestionsList = new ArrayList<>();
        suggestionsList.add(suggestion1);
        suggestionsList.add(suggestion2);
        suggestionsList.add(suggestion3);

        int status = wifiManager.addNetworkSuggestions(suggestionsList);
        if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            // do error handling here…
            Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
        }

        // Optional (Wait for post connection broadcast to one of your suggestions)
        IntentFilter intentFilter = new IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!intent.getAction().equals(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
                    return;
                }
                // do post connect processing here...
            }
        };
        context.registerReceiver(broadcastReceiver, intentFilter);
    }

}


