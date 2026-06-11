package com.bbtest.utils;

import android.os.Environment;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 若操作文件失败，可以通过执行shell命令操作
 *
 * @author onuszhao
 */
public class FileUtil {

    public static boolean deleteFolder(File folder) {
        boolean isSuccess = false;
        try {
            if (folder.exists()) {
                // 通过文件删除
                File[] childFiles = folder.listFiles();
                for (File childFile : childFiles) {
                    if (childFile.isDirectory()) {
                        deleteFolder(childFile);
                    } else {
                        isSuccess = childFile.delete();
                    }
                }
                isSuccess = folder.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public static boolean deleteFile(File file) {
        boolean isSuccess = false;
        try {
            if (file.exists()) {
                // 通过文件删除
                isSuccess = file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public static boolean createFolder(File folder) {
        boolean isSuccess = false;
        try {
            if (!folder.exists()) {
                // 通过文件创建目录
                isSuccess = folder.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public static boolean createFile(File file) {
        boolean isSuccess = false;
        try {
            if (!file.exists()) {
                // 通过文件创建
                isSuccess = file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public static List<File> getFiles(String folderPath) {
        List<File> fileList = new ArrayList<>();
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] fileArray = folder.listFiles();
            for (File file : fileArray) {
                if (!file.isDirectory()) {
                    fileList.add(file);
                }
            }
        } else {
            System.out.println("Folder does not exist or is not directory!" + folderPath);
        }
        return fileList;
    }

    public static List<File> getAllFiles(String folderPath) {
        List<File> fileList = new ArrayList<>();
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] fileArray = folder.listFiles();
            for (File file : fileArray) {
                if (file.isDirectory()) {
                    fileList.addAll(getAllFiles(file.getAbsolutePath()));
                } else {
                    fileList.add(file);
                }
            }
        } else {
            System.out.println("Folder does not exist or is not directory!" + folderPath);
        }
        return fileList;
    }

    /**
     * 获取到指定文件夹内的所有文件（包括子文件夹内的文件）
     *
     * @param folderPath 指定查询的文件夹
     * @param filter     文件过滤设置
     * @return 获取到的文件集
     */
    public static List<File> getAllFiles(String folderPath, FileFilter filter) {
        List<File> fileList = new ArrayList<>();
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] fileArray = folder.listFiles();
            for (File file : fileArray) {
                if (file.isDirectory()) {
                    fileList.addAll(getAllFiles(file.toString(), filter));
                } else {
                    if (filter.accept(file)) {
                        fileList.add(file);
                    }
                }
            }
        } else {
            System.out.println("Folder does not exist or is not directory!" + folderPath);
        }
        return fileList;
    }

    // 此方案比较高效
    public static String readFile(File file) {
        return readFile(file, StandardCharsets.UTF_8.displayName());
    }

    public static String readFile(File file, Charset charsetName) {
        return readFile(file, charsetName.toString());
    }

    public static String readFile(File file, String charsetName) {
        StringBuilder sb = new StringBuilder();
        try {
            LineIterator lineIterator = FileUtils.lineIterator(file, charsetName);
            while (lineIterator.hasNext()) {
                String line = lineIterator.nextLine();
                sb.append(line + "\n");
            }
            lineIterator.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static String readFile1(String fileName) {
        String result = "";
        try {
            StringBuilder sb = new StringBuilder();

            File file = new File(fileName);
            if (file.exists()) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    //打开文件输入流
                    FileInputStream inputStream = new FileInputStream(fileName);
                    byte[] buffer = new byte[1024];
                    int len = inputStream.read(buffer);
                    //读取文件内容
                    while (len > 0) {
                        sb.append(new String(buffer, 0, len));
                        //继续将数据放到buffer中
                        len = inputStream.read(buffer);
                    }
                    //关闭输入流
                    inputStream.close();
                }
            }

            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }

    /**
     * 读取文件，并返回一个字符串（换行加\n）
     */
    public static String readFile2(String filePath) {
        StringBuilder sb = new StringBuilder();
        File file = new File(filePath);
        if (file.exists()) {
            BufferedReader bfr = null;
            try {
                bfr = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line = null;
                while ((line = bfr.readLine()) != null) {
                    sb.append(line + "\n");
                }
                bfr.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bfr != null) {
                    try {
                        bfr.close();
                    } catch (IOException e1) {
                    }
                }
            }
        }
        return sb.toString();
    }

    public static String readFile2(String filePath, String charsetName) {
        StringBuilder sb = new StringBuilder();
        File file = new File(filePath);
        if (file.exists()) {
            BufferedReader bfr = null;
            try {
                bfr = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
                String line = null;
                while ((line = bfr.readLine()) != null) {
                    sb.append(line + "\n");
                }
                bfr.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bfr != null) {
                    try {
                        bfr.close();
                    } catch (IOException e1) {
                    }
                }
            }
        }
        return sb.toString();
    }

    public static String readFile3(String inFilePath, String outFilePath) {
        StringBuilder sb = new StringBuilder();

        File inFile = new File(inFilePath);
        if (inFile.exists()) {
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inFile));
                //用10M的缓冲读取文本文件
                BufferedReader in = new BufferedReader(new InputStreamReader(bis, StandardCharsets.UTF_8), 10 * 1024 * 1024);
                FileWriter fw = new FileWriter(outFilePath);
                while (in.ready()) {
                    String line = in.readLine();
                    fw.append(line + " ");
                }
                in.close();
                fw.flush();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 一行一行的读取，不占用内存
     */
    public static boolean scanLine(String filePath, String str) {
        boolean isContain = false;
        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(filePath);
            sc = new Scanner(inputStream);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.contains(str)) {
                    isContain = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (sc != null) {
                    sc.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isContain;
    }

    public static void writeFile(String str, String filePath) {
        writeFile(str, filePath, StandardCharsets.UTF_8);
    }

    public static void writeFile(String str, String filePath, Charset charsetName) {
        try {
            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, charsetName);
            osw.write(str);
            osw.flush();
            osw.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeFile1(String str, String filePath) {
        try {
            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] bytes = str.getBytes();
            fos.write(bytes);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void writeStrToFile(String str, File file) {
        writeStrToFile(str, file, StandardCharsets.UTF_8);
    }

    public static synchronized void writeStrToFile(String str, File file, Charset charsetName) {
        writeStrToFile(str, file, charsetName.toString());
    }

    public static synchronized void writeStrToFile(String str, File file, String charsetName) {
        try {
            //在文件末尾追加写入
            FileOutputStream fos = new FileOutputStream(file, true);

            //指定编码格式写入文件 GBK,UTF-8
            OutputStreamWriter osw = new OutputStreamWriter(fos, charsetName);

            String[] lines = str.split("\n");
            for (int i = 0; i < lines.length; i++) {
                if (i == lines.length - 1) {
                    if (lines[i].endsWith("\n")) {
                        osw.write(lines[i]);
                    } else {
                        osw.write(lines[i] + "\n");
                    }
                } else {
                    osw.write(lines[i] + "\n");
                }
            }
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void writeStrToFile1(String str, String filePath) {
        try {
            FileWriter fw = new FileWriter(filePath, true);
            BufferedWriter bw = new BufferedWriter(fw);

            String[] lines = str.split("\n");
            for (int i = 0; i < lines.length; i++) {
                if (i == lines.length - 1) {
                    if (str.endsWith("\n")) {
                        bw.write(lines[i] + "\n");
                    } else {
                        bw.write(lines[i]);
                    }
                } else {
                    bw.write(lines[i] + "\n");
                }
            }
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用FileOutputStream，在构造FileOutputStream时，把第二个参数设为true
     */
    public static synchronized void writeStrToFile2(String str, String filePath) {
        BufferedWriter out = null;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                FileOutputStream fos = new FileOutputStream(file, true);
                out = new BufferedWriter(new OutputStreamWriter(fos));
                out.write(str);
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
     */
    public static void writeStrToFile3(String str, String filePath) {
        try {
            FileWriter writer = new FileWriter(filePath, true);
            writer.write(str);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开一个随机访问文件流，按读写方式写入
     */
    public static void writeStrToFile4(String str, String filePath) {
        try {
            // 打开一个随机访问文件流，按读写方式
            RandomAccessFile randomFile = new RandomAccessFile(filePath, "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            // 以utf-8格式写入
            randomFile.writeUTF(str);
//            randomFile.writeBytes(str);
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(String srcFilePath, String dstFilePath) {
        try {
            File srcFile = new File(srcFilePath);
            if (srcFile.exists() && srcFile.isFile() && srcFile.canRead()) {
                FileInputStream in = new FileInputStream(srcFilePath);
                FileOutputStream out = new FileOutputStream(dstFilePath);
                byte[] buffer = new byte[1024];
                int byteRead;
                while (-1 != (byteRead = in.read(buffer))) {
                    out.write(buffer, 0, byteRead);
                }
                in.close();
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 管道复制：通过使用文件通道的方式复制文件
     *
     * @param inFilePath：复制源文件的路径
     * @param outFilePath：复制到哪里的路径
     */
    public static void copyFileByChannel(String inFilePath, String outFilePath) {

        File inFile = new File(inFilePath);
        File outFile = new File(outFilePath);

        FileInputStream inFileStream;
        FileOutputStream outFileStream;
        try {
            inFileStream = new FileInputStream(inFile);
            outFileStream = new FileOutputStream(outFile);

            // 得到对应的文件通道
            FileChannel inChannel = inFileStream.getChannel();
            FileChannel outChannel = outFileStream.getChannel();

            // 连接两个通道，并且从in通道读取，然后写入out通道
            inChannel.transferTo(0, inChannel.size(), outChannel);

            inFileStream.close();
            inChannel.close();
            outFileStream.close();
            outChannel.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 流复制：通过普通的缓冲输入输出复制文件
     *
     * @param inFilePath：复制源文件的路径
     * @param outFilePath：复制到哪里的路径
     */
    public static void copyFileByStream(String inFilePath, String outFilePath) {
        File inFile = new File(inFilePath);
        File outFile = new File(outFilePath);

        InputStream inStream;
        OutputStream outStream;

        try {
            inStream = new BufferedInputStream(new FileInputStream(inFile));
            outStream = new BufferedOutputStream(new FileOutputStream(outFile));

            byte[] buf = new byte[2048];
            int i;
            while ((i = inStream.read(buf)) != -1) {
                outStream.write(buf, 0, i);
            }

            inStream.close();
            outStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制文件夹及其中的文件
     *
     * @param oldPath String 原文件夹路径 如：data/user/0/com.test/files
     * @param newPath String 复制后的路径 如：data/user/0/com.test/cache
     */
    public static void copyFolder(String oldPath, String newPath) {
        try {
            File newFile = new File(newPath);
            if (!newFile.exists()) {
                newFile.mkdirs();
            }

            File oldFile = new File(oldPath);
            String[] files = oldFile.list();
            File temp;
            for (String file : files) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file);
                } else {
                    temp = new File(oldPath + File.separator + file);
                }

                if (temp.isDirectory()) {   //如果是子文件夹
                    copyFolder(oldPath + "/" + file, newPath + "/" + file);
                } else if (temp.exists() && temp.isFile() && temp.canRead()) {
                    FileInputStream in = new FileInputStream(temp);
                    FileOutputStream out = new FileOutputStream(newPath + "/" + temp.getName());
                    byte[] buffer = new byte[1024];
                    int byteRead;
                    while ((byteRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, byteRead);
                    }
                    in.close();
                    out.flush();
                    out.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unZip(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName().replaceAll(";|\\\\|:|\\*|\\?|\"|<|>|\\|", "");
                File newFile = new File(destDir + File.separator + fileName);
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件的编码格式
     */
    public static String getEncode(String filePath) {
        byte[] first3Bytes = new byte[3];
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath))) {
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                return "GBK"; // 文件编码为 ANSI
            }

            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                return "UTF-16LE"; // 文件编码为 Unicode
            }

            if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                return "UTF-16BE"; // 文件编码为 Unicode big endian
            }

            if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
                return "UTF-8"; // 文件编码为 UTF-8
            }

            bis.reset();

            while ((read = bis.read()) != -1) {
                if (read >= 0xF0) {
                    break;
                }
                if (0x80 <= read && read <= 0xBF) {
                    break;
                }
                if (0xC0 <= read && read <= 0xDF) {
                    read = bis.read();
                    if (0x80 <= read && read <= 0xBF) {
                        // (0x80 - 0xBF),也可能在GB编码内
                        continue;
                    }

                    break;
                } else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
                    read = bis.read();
                    if (0x80 <= read && read <= 0xBF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            return "UTF-8";
                        }
                        break;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "GBK";
    }
}
