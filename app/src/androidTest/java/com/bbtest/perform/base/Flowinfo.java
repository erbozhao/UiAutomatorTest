package com.bbtest.perform.base;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.test.uiautomator.UiDevice;

import com.bbtest.MainActivity;
import com.bbtest.utils.ShellCommand;

import java.util.Calendar;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

/*
 * 方法A. 查看 /proc/net/xt_qtaguid/stats
 * 方法B. 调用接口TrafficStats.getUidRxBytes()和TrafficStats.getUidTxBytes()
 * 方法C. 进入 /proc/uid_stat/[uid命名的目录]/tcp_snd 和 /proc/uid_stat/[uid命名的目录]/tcp_rcv
 * 方法D. 进入/proc/self/net/dev读取（适用于2.2之前）或 /proc/[pid]/net/dev
 * 目前手机管家的流量监控的做法是，优先级 A > B > C。
 */
public class Flowinfo {

    private static UiDevice device = null;
    private static int uid = 0;
    private static String flowInfo = "";

    public Flowinfo(UiDevice device, int uid) {
        this.device = device;
        this.uid = uid;
        readFlowInfo();
    }

    public void readFlowInfo() {
        String cmd = "cat /proc/net/xt_qtaguid/stats";
        flowInfo = ShellCommand.execCmdByUiDevice(device, cmd);
        if (flowInfo.equals("") || (flowInfo.contains("Permission") && flowInfo.contains("denied"))) {
            flowInfo = ShellCommand.execCmdBySu(cmd);
        }
    }

    public long getFlow() {
        // 总流量
        long flow = 0;
        // 后台流量
        long backFlow = 0;
        // 前台流量
        long foreFlow = 0;

        if (uid != 0) {
            String[] flowInfoLines = flowInfo.split("\n");
            for (String flowInfoLine : flowInfoLines) {
                if (flowInfoLine.contains("0x0") && flowInfoLine.contains(uid + " 0")) {
                    // 后台流量
                    String[] flowInfoPart = flowInfoLine.split("\\s+");
                    long backRcvFlow = Long.parseLong(flowInfoPart[5]);
                    long backSndFlow = Long.parseLong(flowInfoPart[7]);
                    backFlow += backRcvFlow + backSndFlow;
                } else if (flowInfoLine.contains("0x0") && flowInfoLine.contains(uid + " 1")) {
                    // 前台流量
                    String[] tmpFlow = flowInfoLine.split("\\s+");
                    long foreRcvFlow = Long.parseLong(tmpFlow[5]);
                    long foreSndFlow = Long.parseLong(tmpFlow[7]);
                    foreFlow += foreRcvFlow + foreSndFlow;
                }
            }
        }

        flow = backFlow + foreFlow;
        return flow;
    }

    public long getSndFlow() {
        // 总发送流量
        long sndflow = 0;
        // 后台发送流量
        long backSndFlow = 0;
        // 前台发送流量
        long foreSndFlow = 0;

        if (uid != 0) {
            String[] flowInfoLines = flowInfo.split("\n");
            for (String flowInfoLine : flowInfoLines) {
                if (flowInfoLine.contains("0x0") && flowInfoLine.contains(uid + " 0")) {
                    // 后台流量
                    String[] flowInfoPart = flowInfoLine.split("\\s+");
                    backSndFlow += Long.parseLong(flowInfoPart[7]);
                } else if (flowInfoLine.contains("0x0") && flowInfoLine.contains(uid + " 1")) {
                    // 前台流量
                    String[] tmpFlow = flowInfoLine.split("\\s+");
                    foreSndFlow += Long.parseLong(tmpFlow[7]);
                }
            }
        }

        sndflow = backSndFlow + foreSndFlow;
        return sndflow;
    }

    public long getRcvFlow() {
        // 总接收流量
        long rcvFlow = 0;
        // 后台接收流量
        long backRcvFlow = 0;
        // 前台接收流量
        long foreRcvFlow = 0;

        if (uid != 0) {
            String[] flowInfoLines = flowInfo.split("\n");
            for (String flowInfoLine : flowInfoLines) {
                if (flowInfoLine.contains("0x0") && flowInfoLine.contains(uid + " 0")) {
                    // 后台流量
                    String[] flowInfoPart = flowInfoLine.split("\\s+");
                    backRcvFlow += Long.parseLong(flowInfoPart[5]);
                } else if (flowInfoLine.contains("0x0") && flowInfoLine.contains(uid + " 1")) {
                    // 前台流量
                    String[] tmpFlow = flowInfoLine.split("\\s+");
                    foreRcvFlow += Long.parseLong(tmpFlow[5]);
                }
            }
        }

        rcvFlow = backRcvFlow + foreRcvFlow;
        return rcvFlow;
    }

    /*
     * wifi:wlan0(不常见的还有mlan,ip6tnl,athwlan,tiwlan,eth)
     * GPRS:rmnet0(不常见的还有gsm_rmnet,veth,qmi,spdp_ip,vsnet,ccinet,ccmni,ppp,pdp,td_rmnet,netts,cc2mni)
     */
    public long getWifiFlow() {
        // 总流量
        long wifiFlow = 0;
        // 后台流量
        long wifiBackFlow = 0;
        // 前台流量
        long wifiForeFlow = 0;

        if (uid != 0) {
            String[] flowInfoLines = flowInfo.split("\n");
            for (String flowInfoLine : flowInfoLines) {
                //acct_tag_hex 一般都是0x0, 手机管家也只是取0x0的数据
                if (flowInfoLine.contains("0x0")) {
                    //第6和8列为 rx_bytes（接收数据）和tx_bytes（传输数据）
                    if (flowInfoLine.contains(uid + " 0") && flowInfoLine.contains("wlan0")) {
                        String[] tmpFlow = flowInfoLine.split("\\s+");
                        long wifiBackRcvFlow = Long.parseLong(tmpFlow[5]);
                        long wifiBackSndFlow = Long.parseLong(tmpFlow[7]);
                        wifiBackFlow = wifiBackRcvFlow + wifiBackSndFlow;
                    } else if (flowInfoLine.contains(uid + " 1") && flowInfoLine.contains("wlan0")) {
                        String[] tmpFlow = flowInfoLine.split("\\s+");
                        long wifiForeRcvFlow = Long.parseLong(tmpFlow[5]);
                        long wifiForeSndFlow = Long.parseLong(tmpFlow[7]);
                        wifiForeFlow = wifiForeRcvFlow + wifiForeSndFlow;
                    }
                }
            }
        }

        wifiFlow = wifiBackFlow + wifiForeFlow;
        return wifiFlow;
    }

    public long getWifiSndFlow() {
        // 总流量
        long wifiSndFlow = 0;
        // 后台流量
        long wifiBackSndFlow = 0;
        // 前台流量
        long wifiForeSndFlow = 0;

        if (uid != 0) {
            String[] flowInfoLines = flowInfo.split("\n");
            for (String flowInfoLine : flowInfoLines) {
                //acct_tag_hex 一般都是0x0, 手机管家也只是取0x0的数据
                if (flowInfoLine.contains("0x0")) {
                    //第6和8列为 rx_bytes（接收数据）和tx_bytes（传输数据）
                    if (flowInfoLine.contains(uid + " 0") && flowInfoLine.contains("wlan0")) {
                        String[] tmpFlow = flowInfoLine.split("\\s+");
                        wifiBackSndFlow = Long.parseLong(tmpFlow[7]);
                    } else if (flowInfoLine.contains(uid + " 1") && flowInfoLine.contains("wlan0")) {
                        String[] tmpFlow = flowInfoLine.split("\\s+");
                        wifiForeSndFlow = Long.parseLong(tmpFlow[7]);
                    }
                }
            }
        }

        wifiSndFlow = wifiBackSndFlow + wifiForeSndFlow;
        return wifiSndFlow;
    }

    public long getWifiRcvFlow() {
        // 总流量
        long wifiRcvflow = 0;
        // 后台流量
        long wifiBackRcvFlow = 0;
        // 前台流量
        long wifiForeRcvFlow = 0;

        if (uid != 0) {
            String[] flowInfoLines = flowInfo.split("\n");
            for (String flowInfoLine : flowInfoLines) {
                //acct_tag_hex 一般都是0x0, 手机管家也只是取0x0的数据
                if (flowInfoLine.contains("0x0")) {
                    //第6和8列为 rx_bytes（接收数据）和tx_bytes（传输数据）
                    if (flowInfoLine.contains(uid + " 0") && flowInfoLine.contains("wlan0")) {
                        String[] tmpFlow = flowInfoLine.split("\\s+");
                        wifiBackRcvFlow = Long.parseLong(tmpFlow[5]);
                    } else if (flowInfoLine.contains(uid + " 1") && flowInfoLine.contains("wlan0")) {
                        String[] tmpFlow = flowInfoLine.split("\\s+");
                        wifiForeRcvFlow = Long.parseLong(tmpFlow[5]);
                    }
                }
            }
        }

        wifiRcvflow = wifiBackRcvFlow + wifiForeRcvFlow;
        return wifiRcvflow;
    }

    public long getGprsFlow() {
        long flow = 0;
        long backFlow = 0;
        long foreFlow = 0;

        if (uid != 0) {
            String[] flowInfoLines = flowInfo.split("\n");
            for (String flowInfoLine : flowInfoLines) {
                //acct_tag_hex 一般都是0x0, 手机管家也只是取0x0的数据
                if (flowInfoLine.contains("0x0")) {
                    //第6和8列为 rx_bytes（接收数据）和tx_bytes（传输数据）
                    if (flowInfoLine.contains(uid + " 0") && flowInfoLine.contains("rmnet0")) {
                        String[] tmpFlow = flowInfoLine.split("\\s+");
                        long backRcvFlow = Long.parseLong(tmpFlow[5]);
                        long backSndFlow = Long.parseLong(tmpFlow[7]);
                        backFlow = backRcvFlow + backSndFlow;
                    } else if (flowInfoLine.contains(uid + " 1") && flowInfoLine.contains("rmnet0")) {
                        String[] tmpFlow = flowInfoLine.split("\\s+");
                        long foreRcvFlow = Long.parseLong(tmpFlow[5]);
                        long foreSndFlow = Long.parseLong(tmpFlow[7]);
                        foreFlow = foreRcvFlow + foreSndFlow;
                    }
                }
            }
        }

        flow = backFlow + foreFlow;
        return flow;
    }

    public long getGprsSndFlow() {
        long gprsSndflow = 0;
        long gprsBackSndFlow = 0;
        long gprsForeSndFlow = 0;

        if (uid != 0) {
            String[] flowInfoLines = flowInfo.split("\n");
            for (String flowInfoLine : flowInfoLines) {
                //acct_tag_hex 一般都是0x0, 手机管家也只是取0x0的数据
                if (flowInfoLine.contains("0x0")) {
                    //第6和8列为 rx_bytes（接收数据）和tx_bytes（传输数据）
                    if (flowInfoLine.contains(uid + " 0") && flowInfoLine.contains("rmnet0")) {
                        String[] tmpFlow = flowInfoLine.split("\\s+");
                        gprsBackSndFlow = Long.parseLong(tmpFlow[7]);
                    } else if (flowInfoLine.contains(uid + " 1") && flowInfoLine.contains("rmnet0")) {
                        String[] tmpFlow = flowInfoLine.split("\\s+");
                        gprsForeSndFlow = Long.parseLong(tmpFlow[7]);
                    }
                }
            }
        }

        gprsSndflow = gprsBackSndFlow + gprsForeSndFlow;
        return gprsSndflow;
    }

    public long getGprsRcvFlow() {
        long gprsFlow = 0;
        long gprsBackRcvFlow = 0;
        long gprsForeRcvFlow = 0;

        if (uid != 0) {
            String[] flowInfoLines = flowInfo.split("\n");
            for (String flowInfoLine : flowInfoLines) {
                //acct_tag_hex 一般都是0x0, 手机管家也只是取0x0的数据
                if (flowInfoLine.contains("0x0")) {
                    //第6和8列为 rx_bytes（接收数据）和tx_bytes（传输数据）
                    if (flowInfoLine.contains(uid + " 0") && flowInfoLine.contains("rmnet0")) {
                        String[] tmpFlow = flowInfoLine.split("\\s+");
                        gprsBackRcvFlow = Long.parseLong(tmpFlow[5]);
                    } else if (flowInfoLine.contains(uid + " 1") && flowInfoLine.contains("rmnet0")) {
                        String[] tmpFlow = flowInfoLine.split("\\s+");
                        gprsForeRcvFlow = Long.parseLong(tmpFlow[5]);
                    }
                }
            }
        }

        gprsFlow = gprsBackRcvFlow + gprsForeRcvFlow;
        return gprsFlow;
    }

    private void demo() {
        long mobileRxBytes = TrafficStats.getMobileRxBytes();     //获取通过Mobile连接收到的字节总数，不包含WiFi
        long mobileRxPackets = TrafficStats.getMobileRxPackets(); //获取Mobile连接收到的数据包总数
        long mobileTxBytes = TrafficStats.getMobileTxBytes(); //Mobile发送的总字节数
        long mobileTxPackets = TrafficStats.getMobileTxPackets(); //Mobile发送的总数据包数
        long totalRxBytes = TrafficStats.getTotalRxBytes(); //获取总的接受字节数，包含Mobile和WiFi等
        long totalRxPackets = TrafficStats.getTotalRxPackets(); //总的接受数据包数，包含Mobile和WiFi等
        long totalTxBytes = TrafficStats.getTotalTxBytes(); //总的发送字节数，包含Mobile和WiFi等
        long totalTxPackets = TrafficStats.getTotalTxPackets(); //发送的总数据包数，包含Mobile和WiFi等
        /*
         * getUidRxBytes与getUidSendSize（<18），否则获取值为0
         * UsageStatsManager ( >21 )
         * NetworkStatsManager + NetworkStatsService ( > 23 )
         */
        long uidRxBytes = TrafficStats.getUidRxBytes(uid); //获取某个网络UID的接受字节数
        long uidTxBytes = TrafficStats.getUidTxBytes(uid); //获取某个网络UID的发送字节数
    }

    private void demo2() {
        try {
            Context context = getApplicationContext();
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(context.NETWORK_STATS_SERVICE);

            NetworkStats.Bucket bucket = null;
            // 获取到目前为止设备的Wi-Fi流量统计
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI, "", 0, System.currentTimeMillis());

            // 获取subscriberId
            TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            String subId = tm.getSubscriberId();

            NetworkStats summaryStats;
            long summaryRx = 0;
            long summaryTx = 0;
            NetworkStats.Bucket summaryBucket = new NetworkStats.Bucket();
            long summaryTotal = 0;

            //此处的type用于决定是获取Mobile通道的流量数据还是获取Wifi通道的流量数据
            //ConnectivityManager.TYPE_MOBILE
            //ConnectivityManager.TYPE_WIFI
            summaryStats = networkStatsManager.querySummary(ConnectivityManager.TYPE_MOBILE, subId, getTimesMonthmorning(), System.currentTimeMillis());
            do {
                summaryStats.getNextBucket(summaryBucket);
                int summaryUid = summaryBucket.getUid();
                if (uid == summaryUid) {
                    summaryRx += summaryBucket.getRxBytes();
                    summaryTx += summaryBucket.getTxBytes();
                }
                Log.i(MainActivity.class.getSimpleName(), "uid:" + summaryBucket.getUid() + " rx:" + summaryBucket.getRxBytes() +
                        " tx:" + summaryBucket.getTxBytes());
                summaryTotal += summaryBucket.getRxBytes() + summaryBucket.getTxBytes();
            } while (summaryStats.hasNextBucket());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得本月第一天0点时间
     */
    public long getTimesMonthmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTimeInMillis();
    }
}
