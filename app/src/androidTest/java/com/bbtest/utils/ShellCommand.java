package com.bbtest.utils;

import android.app.Instrumentation;
import android.os.ParcelFileDescriptor;

import androidx.test.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class ShellCommand {

    public static String execCmd(String cmd) {
        String result = "";
        try {
            Process pro = Runtime.getRuntime().exec(cmd);
            BufferedReader bfr = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line = "";
            while ((line = bfr.readLine()) != null) {
                result += line + "\n";
            }
            bfr.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public static String execCmd(String[] cmds) {
        String result = "";
        try {
            // 重定向错误流
            ProcessBuilder proBuilder = new ProcessBuilder(cmds);
            proBuilder.redirectErrorStream(true);
            Process pro = proBuilder.start();
            BufferedReader bfr = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line = "";
            while ((line = bfr.readLine()) != null) {
                result += line + "\n";
            }
            bfr.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public static String execCmdByUiDevice(UiDevice device, String cmd) {
        String result = "";
        try {
            result = device.executeShellCommand(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String execCmdByNoRoot(String cmd) {
        String result = "";
        try {
            Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
            ParcelFileDescriptor pfd = instrumentation.getUiAutomation().executeShellCommand(cmd);
            FileInputStream fis = new ParcelFileDescriptor.AutoCloseInputStream(pfd);
            BufferedReader bfr = new BufferedReader(new InputStreamReader(fis));
            String line = "";
            while ((line = bfr.readLine()) != null) {
                result += line + "\n";
            }
            fis.close();
            bfr.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public static String execCmdBySh(String cmd) {
        String result = "";
        try {
            // /system/bin/sh
            Process pro = Runtime.getRuntime().exec("sh");
            DataOutputStream dos = new DataOutputStream(pro.getOutputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.close();
            BufferedReader bfr = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line = "";
            while ((line = bfr.readLine()) != null) {
                if (!line.equals("")) {
                    result += line + "\n";
                }
            }
            pro.waitFor();
            pro.destroy();
            bfr.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

    public static String execCmdBySu(String cmd) {
        String result = "";
        try {
            Process pro = Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(pro.getOutputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
//            dos.close();
            BufferedReader bfr = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line = "";
            while ((line = bfr.readLine()) != null) {
                if (!line.equals("")) {
                    result += line + "\n";
                }
            }
            pro.waitFor();
            pro.destroy();
            bfr.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return result;
    }

}
