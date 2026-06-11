package com.bbtest.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CommonUtil {

    public static int getFirstNum(String str) {
        int num = -1;
        String tmpNum = "";
        boolean isStart = false;
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                isStart = true;
                tmpNum += str.charAt(i);
            } else {
                if (isStart) {
                    break;
                }
            }
        }
        if (!tmpNum.equals("")) {
            num = Integer.parseInt(tmpNum);
        }
        return num;
    }

    public static boolean isNumer(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 方案1：Double.valueOf(String.format("%.1f", 0.5624))
     * 方案2：DecimalFormat df = new DecimalFormat("0.00");
     * Double.valueOf(df.format(0.85621));
     * 方案3：BigDecimal bd = new BigDecimal(0.24684136);
     * bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
     */
    public static double keepDecimalPoint(double number, int point) {
        return Double.valueOf(String.format("%." + point + "f", number));
    }

    /**
     * 随机生成字符串
     */
    public static String randomStr(int length) {
        String alphabetsInUpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String alphabetsInLowerCase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";

        String allCharacters = alphabetsInLowerCase + alphabetsInUpperCase + numbers;
        StringBuffer randomString = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int randomIndex = new Random().nextInt(allCharacters.length());
            randomString.append(allCharacters.charAt(randomIndex));
        }
        return randomString.toString();
    }

    /**
     * 生成 [m,n] 的数字
     */
    public static int randomInt(int startNum, int endNum) {
        return new Random().nextInt(endNum - startNum + 1) + startNum;
    }

    public static String getCurYear() {
        return new SimpleDateFormat("yyyy").format(new Date());
    }

    public static String getCurTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(new Date());
    }

    public static String getCurTimeForFile() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return dateFormat.format(new Date());
    }

    public static String getCurTimeForLog() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        return dateFormat.format(new Date());
    }

    public static String getExceptionMsg(Exception e) {
        String exceptionMsg = "";
        try {
            String msg = e.getMessage();
            if (msg != null) {
                exceptionMsg += "   " + msg + "\n";
            }
            StackTraceElement[] stacks = e.getStackTrace();
            if (stacks != null) {
                for (int i = 0; i < stacks.length; i++) {
                    exceptionMsg += "   " + stacks[i].toString() + "\n";
                }
            }
            Throwable cause = e.getCause();
            if (cause != null) {
                exceptionMsg += "   Caused by: " + cause.getMessage() + "\n";
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return exceptionMsg;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
