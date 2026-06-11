package com.bbtest.perform.base;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import androidx.test.uiautomator.UiDevice;

import com.bbtest.tools.DeviceInfo;
import com.bbtest.utils.ShellCommand;

import java.util.HashMap;
import java.util.Map;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

/**
 * 模块电量（mAh） = 模块电流（mA）* 模块耗时（h）
 * 模块电量（mAh） = 电池容量（mA）* 百分比
 */
public class Batinfo {

    private UiDevice device = null;
    private boolean acPowere = false;         //是否连接AC（电源）充电线
    private boolean usbPowered = true;        //是否连接USB（PC或笔记本USB插口）充电
    private boolean wirelessPowered = false;  //是否使用了无线电源
    private int status = -1;                  //1-电池状态，2-为充电状态，其他为非充电状态
    private int level = -1;                   //电量（%），如58%
    private int scale = -1;                   //电量最大数值，如100
    private long voltage = -1;                //当前电压（mV），如3977mV
    private long currentNow = -1;             //当前电流（mA），如-335232mA
    private int temperature = -1;             //电池温度，单位为0.1摄氏度，如355

    private Context context = null;
    private BatteryManager batteryManager = null;

    public Batinfo(UiDevice device) {
        this.device = device;
    }

    public Batinfo(Context context) {
        this.context = context;
        this.batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
    }

    public Map<String, String> getBattery() {
        Map<String, String> battery = new HashMap<>();

        int level = 0;
        int scale = 0;
        String totalBatt = "";
        String current = "";
        String voltage = "";
        String temperature = "";

        int sdk = new DeviceInfo(device).getSdk();
        String cmd = null;
        if (sdk > 22) {
            cmd = "dumpsys battery"; // dumpsys batterystats
        } else {
            cmd = "dumpsys batterymanager";
        }
        String result = ShellCommand.execCmdByUiDevice(device, cmd);
        String[] resultLines = result.split("\n");
        for (String resultLine : resultLines) {
            if (!resultLine.equals("")) {
                if (resultLine.contains("level:")) {
                    String[] temp = resultLine.trim().split("\\s+");
                    level = Integer.parseInt(temp[1].trim());
                } else if (resultLine.contains("scale:")) {
                    String[] temp = resultLine.trim().split("\\s+");
                    scale = Integer.parseInt(temp[1].trim());
                } else if (resultLine.contains("current now:")) {
                    String[] temp = resultLine.trim().split("\\s+");
                    current = String.valueOf(Integer.parseInt(temp[2].trim()) * 1.0 / 1000);
                } else if (resultLine.contains("voltage:") && !resultLine.contains("Max")) {
                    String[] temp = resultLine.trim().split("\\s+");
                    voltage = String.valueOf(Integer.parseInt(temp[1].trim()) * 1.0 / 1000);
                } else if (resultLine.contains("temperature:")) {
                    String[] temp = resultLine.trim().split("\\s+");
                    temperature = String.valueOf(Integer.parseInt(temp[1].trim()) * 1.0 / 10);
                }
            }
        }

        if (scale != 0) {
            totalBatt = String.valueOf(level * 100 / scale);
        } else {
            totalBatt = String.valueOf(level);
        }

        battery.put("totalBatt", totalBatt);
        battery.put("current", current);
        battery.put("voltage", voltage);
        battery.put("temperature", temperature);

        return battery;
    }

    /**
     * 获取剩余电量(单位:mAH-微安小时)
     */
    public int getBatteryRemaining() {
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
    }

    /**
     * 获取当前电流-实时(单位:mA)
     */
    public int getBatteryCurrent() {
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
    }

    /**
     * 获取当前电流-平均(单位:mA)
     */
    public int getBatteryCurrentAvg() {
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
    }

    /**
     * 获取电量百分比
     */
    public int getBatteryPercentage() {
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    /**
     * 获取剩余能量(单位:纳瓦特小时)
     */
    public int getBatteryRemainingNavat() {
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER);
    }

    /**
     * 获取电池容量(单位:mAH)
     */
    public double getBatteryCapacity(Context context) {
        Object mPowerProfile;
        double batteryCapacity = 0;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
        try {
            mPowerProfile = Class.forName(POWER_PROFILE_CLASS).getConstructor(Context.class).newInstance(context);
            batteryCapacity = (double) Class.forName(POWER_PROFILE_CLASS).getMethod("getBatteryCapacity").invoke(mPowerProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return batteryCapacity;
    }

    private void demo() {
        Context context = getApplicationContext();
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        // 是否在充电
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
        // 怎么充
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        // 当前剩余电量
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        // 电量最大值
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        // 电量百分比
        float batLevel = (float) level / (float) scale;
    }


}

/**
 * 电池信息监控监听器
 */
class BatteryInfoBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            String battery = String.valueOf(level * 100 / scale);
            String voltage = String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) * 1.0 / 1000);
            String temperature = String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) * 1.0 / 10);
        }
    }
}