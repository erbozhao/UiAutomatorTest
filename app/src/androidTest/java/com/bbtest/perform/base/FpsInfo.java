package com.bbtest.perform.base;


import androidx.test.uiautomator.UiDevice;

import com.bbtest.utils.CommonUtil;
import com.bbtest.utils.ShellCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 方案1：dumpsys SurfaceFlinger （不支持静态页面的检测，且出现卡顿后不易复现跟踪）
 * 方案2：dumpsys gfxinfo -- 需开启GPU呈现模式分析(Profile GPU rendering)（不支持静态页面的检测，且出现卡顿后不易复现跟踪）
 * 方案3：利用UI线程的Looper打印的日志匹配判断是否卡顿（不支持计算帧率，支持静态页面的检测，且出现卡顿后易复现跟踪）
 * 方案4：Choreographer.FrameCallback（支持计算帧率，支持静态页面的检测，且出现卡顿后不易复现跟踪）
 */
public class FpsInfo {

    private UiDevice device = null;
    private String pkgName = "";
    private String topActivity = "";

    private long totalFrames = 0;       // 统计的总帧数
    private long jankFrames = 0;        // jank次数，跳帧数
    private int extraJankTimes = 0;     // 额外的垂直同步次数
    private double fps;                  // fps值
    private double lostFrameRate = 0;    // 丢帧率

    public FpsInfo(UiDevice device, String pkgName) {
        this.device = device;
        this.pkgName = pkgName;
        getFpsByGfxinfo();
    }

    public FpsInfo(UiDevice device, String pkgName, String topActivity) {
        this.device = device;
        this.pkgName = pkgName;
//        this.topActivity = topActivity + "#0";
//        getFpsBySurfaceFlinger();
    }

    public double getFps() {
        return CommonUtil.keepDecimalPoint(fps, 2);
    }

    public long getJankFrames() {
        return jankFrames;
    }

    public double getLostFrameRate() {
        return CommonUtil.keepDecimalPoint(lostFrameRate, 2);
    }

    private void clearBfr() {
        // 获取数据之前清除Buffer缓冲区
        String cmd = "dumpsys SurfaceFlinger --latency-clear";
        ShellCommand.execCmdByUiDevice(device, cmd);
    }

    /*
     *  先操作再获取，保证已存入Buffer
     *  第一行数据，表示刷新的时间间隔refresh_period
     *  第一列：表示应用绘制图像的时间点
     *  第二列：SF将帧提交给H/W(硬件)绘制之前的垂直同步时间。
     *  第三列：在SF将帧提交给H/W的时间点，算是H/W接受完SF发来数据的时间点，绘制完成的时间点。
     */
    private void getFpsBySurfaceFlinger() {
        // 获取数据之前清除Buffer缓冲区
        String cmd = "dumpsys SurfaceFlinger --latency " + topActivity;
        String frameData = ShellCommand.execCmdByUiDevice(device, cmd);
        String[] frameLines = frameData.split("\n");
        int frameCount = frameLines.length - 1;  //需排除第一行
        long startTime = Long.parseLong(frameLines[1].trim().split("\\s+")[0].trim());
        long endTime = Long.parseLong(frameLines[frameLines.length - 1].trim().split("\\s+")[0].trim());

        // 1.刷新间隔(refresh_period)一般为16.67ms，而fps为60帧/s
        // 2.因为单位是纳秒，故需除以1000000转为毫秒进行计算
        fps = frameCount * 1000 / ((endTime - startTime) / 1000000.0);

        // 计算掉帧数
        long refresh_period = Long.parseLong(frameLines[0].trim());  //16666666/1000/1000 = 16.67ms(毫秒)

        // 过滤(B2-B1)/ refresh-period > 0.5的数据
        List<Double> firstDiffValues = new ArrayList<>();
        long firstUpTime = 0;
        long firstCurTime = 0;
        for (int i = 1; i < frameLines.length; i++) {
            String[] framePart = frameLines[i].split("\\s+");
            if (i == 1) {
                firstUpTime = Long.parseLong(framePart[1].trim());
            } else {
                firstCurTime = Long.parseLong(framePart[1].trim());
                double diffValue = (double) (firstCurTime - firstUpTime) / refresh_period;
                if (diffValue > 0.5) {
                    firstDiffValues.add(diffValue);
                }
                firstUpTime = firstCurTime;
            }
        }

        // 求掉帧：jankiness<0 and jankiness>=20的数据，超过20则很卡
        double secondUpTime = 0;
        double secondCurTime = 0;
        for (int i = 1; i < firstDiffValues.size(); i++) {
            if (i == 1) {
                secondUpTime = firstDiffValues.get(i);
            } else {
                secondCurTime = firstDiffValues.get(i);
                int jankiness = (int) Math.ceil((secondCurTime - secondUpTime) / refresh_period);
                if (jankiness < 0 || jankiness >= 20) {
                    jankFrames++;
                }
                secondUpTime = secondCurTime;
            }
        }

        // 计算丢帧率
        lostFrameRate = (double) jankFrames / totalFrames;

//    for (int i = 1; i < frameLines.length; i++) {
//      String[] framePart = frameLines[i].split("\\s+");
//      //掉帧jank：每一行都可以通过以下计算得到一个jankflag，如果当前行的jankflag与上一行的jankflag发生改变，那么就叫掉帧
//      long A = Long.parseLong(framePart[0].trim());
//      long B = Long.parseLong(framePart[1].trim());
//      long C = Long.parseLong(framePart[2].trim());
//      double jankflag = (double)(C - A) / refresh_period;
//      System.out.println("jankflag:" + jankflag);
//    }

    }

    private void getFpsByGfxinfo() {
        // 需开发者选项打开设置:GPU呈现模式分析(Profile GPU rendering)
        String cmd = "dumpsys gfxinfo " + pkgName + " reset";
        String frameData = ShellCommand.execCmdByUiDevice(device, cmd);
        String[] frameLines = frameData.split("\n");
        boolean isStart = false;
        String startKeywords1 = ".*Draw\\s+Prepare\\s+Process\\s+Execute.*";
        String startKeywords2 = ".*Draw\\s+Process\\s+Execute.*";
        String endKeywords1 = ".*Stats\\s+since";
        String endKeywords2 = ".*View hierarchy:.*";
        String endKeywords3 = ".*" + pkgName + ".*";
        for (String frameLine : frameLines) {
            if (!frameLine.equals("")) {
                // 先判断是否结束
                if (Pattern.compile(endKeywords1).matcher(frameLine).find()) {
                    isStart = false;
                } else if (Pattern.compile(endKeywords2).matcher(frameLine).find()) {
                    isStart = false;
                } else if (Pattern.compile(endKeywords3).matcher(frameLine).find()) {
                    isStart = false;
                }

                // 再开始处理数据
                if (isStart) {
                    // 一行为一帧，每一帧分别展示draw、prepare、process、execute的时间，总和为当前帧所消耗时间
                    // 一般2s多是120帧的数据，故每2~3s取一次数据为宜
                    String[] framePart = frameLine.trim().split("\\s+");
                    if (framePart.length == 3) {
                        totalFrames += 1;
                        // 计算一帧渲染耗时
                        float onceRenderTime = Float.parseFloat(framePart[0]) + Float.parseFloat(framePart[1]) + Float.parseFloat(framePart[2]);
                        // 以Android定义的60FPS为标准，若一帧的耗时超过16.67，则表示为掉帧
                        if (onceRenderTime > 16.67) {
                            jankFrames += 1;
                            // 统计额外花费垂直同步脉冲的次数
                            if (onceRenderTime % 16.67 == 0) {
                                extraJankTimes += onceRenderTime / 16.67 - 1;
                            } else {
                                extraJankTimes += Math.floor(onceRenderTime / 16.67); //向下取整即可
                            }
                        }
                    } else {
                        //统计总帧数
                        totalFrames += 1;
                        //计算一帧所花费的时间
                        float onceRenderTime = Float.parseFloat(framePart[0]) + Float.parseFloat(framePart[1]) + Float.parseFloat(framePart[2]) + Float.parseFloat(framePart[3]);
                        // 以Android定义的60FPS为标准，若一帧的耗时超过16.67，则表示为掉帧
                        if (onceRenderTime > 16.67) {
                            jankFrames += 1;
                            // 统计额外花费垂直同步脉冲的次数
                            if (onceRenderTime % 16.67 == 0) {
                                extraJankTimes += onceRenderTime / 16.67 - 1;
                            } else {
                                extraJankTimes += Math.floor(onceRenderTime / 16.67); //向下取整即可
                            }
                        }
                    }
                }

                // 最后判断是否开始
                if (Pattern.compile(startKeywords1).matcher(frameLine).find()) {
                    isStart = true;
                } else if (Pattern.compile(startKeywords2).matcher(frameLine).find()) {
                    isStart = true;
                }
            }
        }

        /*
         * 计算帧率:fps=m/(m+额外的同步脉冲)*60
         * 执行一次命令，总共收集m帧（理想情况下m=128）
         * m帧里面有些帧渲染超过了16.67ms，算一次jank，一旦有出现jank，需要用掉额外的垂直同步脉冲
         * 其他的没有超过16.67ms的按照一个脉冲时间来算（理想情况下一个脉冲可以渲染完一帧）
         */
        if (totalFrames > 0) {
            fps = (double) totalFrames / (totalFrames + extraJankTimes) * 60;
            lostFrameRate = (double) jankFrames / totalFrames;
        } else {
            fps = 60;
        }
    }

}
