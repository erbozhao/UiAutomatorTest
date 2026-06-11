 package com.bbtest.perform.base;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileReader;
 
 public class GpuInfo
 {
   private static boolean mFileExists = false;
   private static boolean mFileChecked = false;
   private static final String GPU_File = "sys/class/kgsl/kgsl-3d0/gpubusy";
   
   public static double getGpuUsage()
   {
     double res = 0.0D;
     
     if (!mFileChecked) {
       mFileChecked = true;
       File f = new File("sys/class/kgsl/kgsl-3d0/gpubusy");
       mFileExists = f.exists();
     }
     
     if (mFileExists) {
       FileReader fr = null;
       BufferedReader localBufferedReader = null;
       try {
         fr = new FileReader("sys/class/kgsl/kgsl-3d0/gpubusy");
         localBufferedReader = new BufferedReader(fr, 128);
         String cpu = localBufferedReader.readLine();
         if (cpu != null) {
           cpu = cpu.trim().replaceAll(" +", " ");
           String cur = cpu.split(" ")[0].trim();
           String total = cpu.split(" ")[1].trim();
           if ((!cur.isEmpty()) && (!total.isEmpty()) && 
             (!total.equalsIgnoreCase("0"))) {
             res = Double.parseDouble(cur) / 
               Double.parseDouble(total);
           }
         }
       }
       catch (Exception e) {
         e.printStackTrace();
       }
       try {
         if (localBufferedReader != null) {
           localBufferedReader.close();
         }
         if (fr != null) {
           fr.close();
         }
       } catch (Exception e) {
         e.printStackTrace();
       }
     }
     return res;
   }
 }
