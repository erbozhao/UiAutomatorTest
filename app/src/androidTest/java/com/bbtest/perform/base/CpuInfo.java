package com.bbtest.perform.base;

import androidx.test.uiautomator.UiDevice;

import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.ShellCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CpuInfo {

    private static UiDevice device = null;
    private static int pid = 0;

    public CpuInfo(UiDevice device, int pid) {
        this.device = device;
        this.pid = pid;
    }

    public String getCpuName() {
        String cpuName = "";
        try {
            String cmd = "cat /proc/cpuinfo";
            String result = ShellCommand.execCmdByUiDevice(device, cmd);
            String[] resultLines = result.split("\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    String[] resultLineParts = resultLine.trim().split(":");
                    if (resultLineParts[0].trim().equals("Hardware")) {
                        cpuName = resultLineParts[1].trim();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cpuName;
    }

    public int getCpuNum() {
        int cpuNum = 0;
        try {
            String keywords = "cpu[0-9]";
            String cmd = "ls /sys/devices/system/cpu/";
            String result = ShellCommand.execCmdByUiDevice(device, cmd);
            String[] resultLines = result.split("\n");
            for (String resultLine : resultLines) {
                if (!resultLine.equals("")) {
                    if (Pattern.compile(keywords).matcher(resultLine).find()) {
                        cpuNum++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cpuNum;
    }

    /*
     * 获取总的CPU信息（从系统启动到当前时刻的cpu信息）
     * 单位：jiffies  1jiffies=10ms
     */
    private static List<Long> getTotalCpuInfo() {
        List<Long> cpuInfo = new ArrayList<>();

        //一般取前7个数据来计算CPU使用率，后面三个数据可以忽略
        long user = 0;         //用户态运行时间（不包括nice值为进程）
        long nice = 0;         //nice值为负的进程所含cpu时间
        long system = 0;       //处于核心态的运行时间
        long idle = 0;         //除IO等待时间以外的其他等待
        long iowait = 0;       //IO等待时间
        long irq = 0;          //硬中断时间
        long softirq = 0;      //软中断时间
        long steal = 0;  //在虚拟环境上花费到其他操作系统的时间
        long guest = 0;        //Linux内核控制来宾操作系统时运行的一个虚拟CPU时间
        long guest_nice = 0;   //Time spent running a niced guest (virtual CPU for guest operating systems under the control of the Linux kernel).

        try {
            String cmd = "cat /proc/stat";
            String result = ShellCommand.execCmdByUiDevice(device, cmd);
            String[] resultLines = result.split("\n");
            for (String resultLine : resultLines) {
                if (!(resultLine.equals(""))) {
                    if (resultLine.trim().startsWith("cpu ")) {
                        String[] temp = resultLine.split("\\s+");
                        user = Long.parseLong(temp[1]);
                        nice = Long.parseLong(temp[2]);
                        system = Long.parseLong(temp[3]);
                        idle = Long.parseLong(temp[4]);
                        iowait = Long.parseLong(temp[5]);
                        irq = Long.parseLong(temp[6]);
                        softirq = Long.parseLong(temp[7]);
                        steal = Long.parseLong(temp[8]);
                        guest = Long.parseLong(temp[9]);
                        guest_nice = Long.parseLong(temp[10]);

                        cpuInfo.add(user);
                        cpuInfo.add(nice);
                        cpuInfo.add(system);
                        cpuInfo.add(idle);
                        cpuInfo.add(iowait);
                        cpuInfo.add(irq);
                        cpuInfo.add(softirq);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cpuInfo;
    }

    /*
     *  采样两个时间点的数据来计算CPU占用，比如间隔1秒
     *  总的cpu时间片：totalCpuTime= user + nice + system + idle + iowait + irq + softirq + steal + guest + guest_nice
     *  空闲时间片：idle
     *  注：
     *  1.多核CPU情况的计算不需要totalCPURate乘以CPU核数，因为这边已经包括了所有核数的数据
     *  2.有的时候会出现负值的情况，这种情况下要持续采样数据直到数据非负
     *  3.从测试情况来看，也会出现类似225%的CPU占用，此时CPU已经过载，CPU超过100%情况属于过载的异常值
     */
    public double getTotalCpu() {
        double totalCpu = 0;
        try {
            // 采样第一个时间点的数据
            List<Long> cpuTime1 = getTotalCpuInfo();
            long totalCpuTime1 = 0;
            for (Long tmpCpuTime1 : cpuTime1) {
                totalCpuTime1 += tmpCpuTime1;
            }
            long idleCpuTime1 = cpuTime1.get(3);

            // 等待1s()
            Thread.sleep(1 * 1000);

            // 采样第二个时间点的数据
            List<Long> cpuTime2 = getTotalCpuInfo();
            long totalCpuTime2 = 0;
            for (Long tmpCpuTime2 : cpuTime2) {
                totalCpuTime2 += tmpCpuTime2;
            }
            long idleCpuTime2 = cpuTime2.get(3);

            //计算cpu使用率
            long totalCpuTime = totalCpuTime2 - totalCpuTime1;
            long idleCpuTime = idleCpuTime2 - idleCpuTime1;
            double tempTotalCpu = (double) (totalCpuTime - idleCpuTime) * 100 / totalCpuTime;
            totalCpu = CommonUtil.keepDecimalPoint(tempTotalCpu, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalCpu;
    }

    /*
     * 获取指定进程的CPU信息（从系统启动到当前时刻的cpu信息）
     * 单位：jiffies  1jiffies=10ms
     */
    private static List<Long> getProCpuInfo() {
        List<Long> cpuInfo = new ArrayList<>();

        long utime = 0;      //用户态运行时间
        long stime = 0;      //核心态运行的时间
        long cutime = 0;      //所有已死线程在用户态运行的时间
        long cstime = 0;      // 所有已死在核心态运行的时间

        try {
            String cmd = "cat /proc/" + pid + "/stat";
            String result = ShellCommand.execCmdByUiDevice(device, cmd);
            String[] resultLines = result.split("\n");
            for (String resultLine : resultLines) {
                if (!(resultLine.equals(""))) {
                    String[] temp = resultLine.split("\\s+");
                    utime = Long.parseLong(temp[13]);
                    stime = Long.parseLong(temp[14]);
                    cutime = Long.parseLong(temp[15]);
                    cstime = Long.parseLong(temp[16]);

                    cpuInfo.add(utime);
                    cpuInfo.add(stime);
                    cpuInfo.add(cutime);
                    cpuInfo.add(cstime);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cpuInfo;
    }

    /*
     *  采样两个时间点的数据来计算CPU占用，比如间隔1秒
     *  总的cpu时间片：processCPUTime = utime + stime + cutime + cstime
     *  空闲时间片：idle
     */
    public double getProCpu() {
        double processCpu = 0;
        try {
            // 采样第一个时间点的数据
            List<Long> cpuTime1 = getTotalCpuInfo();
            List<Long> proCpuTime1 = getProCpuInfo();
            long totalCpuTime1 = 0;
            for (Long tmpCpuTime1 : cpuTime1) {
                totalCpuTime1 += tmpCpuTime1;
            }
            long totalProCpuTime1 = 0;
            for (Long tmpProCpuTime1 : proCpuTime1) {
                totalProCpuTime1 += tmpProCpuTime1;
            }

            // 等待1s
            Thread.sleep(1 * 1000);

            // 采样第二个时间点的数据
            List<Long> cpuTime2 = getTotalCpuInfo();
            List<Long> proCpuTime2 = getProCpuInfo();
            long totalCpuTime2 = 0;
            for (Long tmpCpuTime2 : cpuTime2) {
                totalCpuTime2 += tmpCpuTime2;
            }
            long totalProCpuTime2 = 0;
            for (Long tmpProCpuTime2 : proCpuTime2) {
                totalProCpuTime2 += tmpProCpuTime2;
            }

            //计算cpu使用率
            long totalCpuTime = totalCpuTime2 - totalCpuTime1;
            long proCpuTime = totalProCpuTime2 - totalProCpuTime1;
            double tempProCpu = (double) proCpuTime * 100 / totalCpuTime;
            processCpu = CommonUtil.keepDecimalPoint(tempProCpu, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processCpu;
    }

}
