package com.bbtest.utils

import android.os.Environment
import org.apache.commons.io.FileUtils
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileFilter
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.ArrayList
import java.util.Scanner
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object FileUtil {
    @JvmStatic
    fun deleteFolder(folder: File): Boolean {
        var isSuccess = false
        try {
            if (folder.exists()) {
                val childFiles = folder.listFiles()
                if (childFiles != null) {
                    for (childFile in childFiles) {
                        if (childFile.isDirectory) {
                            deleteFolder(childFile)
                        } else {
                            isSuccess = childFile.delete()
                        }
                    }
                }
                isSuccess = folder.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isSuccess
    }

    @JvmStatic
    fun deleteFile(file: File): Boolean {
        var isSuccess = false
        try {
            if (file.exists()) {
                isSuccess = file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isSuccess
    }

    @JvmStatic
    fun createFolder(folder: File): Boolean {
        var isSuccess = false
        try {
            if (!folder.exists()) {
                isSuccess = folder.mkdir()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isSuccess
    }

    @JvmStatic
    fun createFile(file: File): Boolean {
        var isSuccess = false
        try {
            if (!file.exists()) {
                isSuccess = file.createNewFile()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isSuccess
    }

    @JvmStatic
    fun getFiles(folderPath: String): List<File> {
        val fileList = ArrayList<File>()
        val folder = File(folderPath)
        if (folder.exists() && folder.isDirectory) {
            val fileArray = folder.listFiles()
            if (fileArray != null) {
                for (file in fileArray) {
                    if (!file.isDirectory) {
                        fileList.add(file)
                    }
                }
            }
        } else {
            System.out.println("Folder does not exist or is not directory!$folderPath")
        }
        return fileList
    }

    @JvmStatic
    fun getAllFiles(folderPath: String): List<File> {
        val fileList = ArrayList<File>()
        val folder = File(folderPath)
        if (folder.exists() && folder.isDirectory) {
            val fileArray = folder.listFiles()
            if (fileArray != null) {
                for (file in fileArray) {
                    if (file.isDirectory) {
                        fileList.addAll(getAllFiles(file.absolutePath))
                    } else {
                        fileList.add(file)
                    }
                }
            }
        } else {
            System.out.println("Folder does not exist or is not directory!$folderPath")
        }
        return fileList
    }

    @JvmStatic
    fun getAllFiles(folderPath: String, filter: FileFilter): List<File> {
        val fileList = ArrayList<File>()
        val folder = File(folderPath)
        if (folder.exists() && folder.isDirectory) {
            val fileArray = folder.listFiles()
            if (fileArray != null) {
                for (file in fileArray) {
                    if (file.isDirectory) {
                        fileList.addAll(getAllFiles(file.toString(), filter))
                    } else if (filter.accept(file)) {
                        fileList.add(file)
                    }
                }
            }
        } else {
            System.out.println("Folder does not exist or is not directory!$folderPath")
        }
        return fileList
    }

    @JvmStatic
    fun readFile(file: File): String = readFile(file, StandardCharsets.UTF_8.displayName())

    @JvmStatic
    fun readFile(file: File, charsetName: Charset): String = readFile(file, charsetName.toString())

    @JvmStatic
    fun readFile(file: File, charsetName: String): String {
        val sb = StringBuilder()
        try {
            FileUtils.lineIterator(file, charsetName).use { lineIterator ->
                while (lineIterator.hasNext()) {
                    sb.append(lineIterator.next()).append("\n")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return sb.toString()
    }

    @JvmStatic
    fun readFile1(fileName: String): String {
        var result = ""
        try {
            val sb = StringBuilder()
            val file = File(fileName)
            if (file.exists()) {
                if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                    val inputStream = FileInputStream(fileName)
                    val buffer = ByteArray(1024)
                    var len = inputStream.read(buffer)
                    while (len > 0) {
                        sb.append(String(buffer, 0, len))
                        len = inputStream.read(buffer)
                    }
                    inputStream.close()
                }
            }
            result = sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    @JvmStatic
    fun readFile2(filePath: String): String {
        val sb = StringBuilder()
        val file = File(filePath)
        if (file.exists()) {
            try {
                BufferedReader(InputStreamReader(FileInputStream(file))).use { reader ->
                    reader.lineSequence().forEach { line ->
                        sb.append(line).append("\n")
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }

    @JvmStatic
    fun readFile2(filePath: String, charsetName: String): String {
        val sb = StringBuilder()
        val file = File(filePath)
        if (file.exists()) {
            try {
                BufferedReader(InputStreamReader(FileInputStream(file), charsetName)).use { reader ->
                    reader.lineSequence().forEach { line ->
                        sb.append(line).append("\n")
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }

    @JvmStatic
    fun readFile3(inFilePath: String, outFilePath: String): String {
        val sb = StringBuilder()
        val inFile = File(inFilePath)
        if (inFile.exists()) {
            try {
                val bis = BufferedInputStream(FileInputStream(inFile))
                val input = BufferedReader(InputStreamReader(bis, StandardCharsets.UTF_8), 10 * 1024 * 1024)
                val fw = FileWriter(outFilePath)
                while (input.ready()) {
                    val line = input.readLine()
                    fw.append("$line ")
                }
                input.close()
                fw.flush()
                fw.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }

    @JvmStatic
    fun scanLine(filePath: String, str: String): Boolean {
        var isContain = false
        try {
            FileInputStream(filePath).use { inputStream ->
                Scanner(inputStream).use { scanner ->
                    while (scanner.hasNextLine()) {
                        val line = scanner.next()
                        if (line.contains(str)) {
                            isContain = true
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isContain
    }

    @JvmStatic
    fun writeFile(str: String, filePath: String) {
        writeFile(str, filePath, StandardCharsets.UTF_8)
    }

    @JvmStatic
    fun writeFile(str: String, filePath: String, charsetName: Charset) {
        try {
            val file = File(filePath)
            FileOutputStream(file).use { fos ->
                OutputStreamWriter(fos, charsetName).use { osw ->
                    osw.write(str)
                    osw.flush()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun writeFile1(str: String, filePath: String) {
        try {
            val file = File(filePath)
            val fos = FileOutputStream(file)
            fos.write(str.toByteArray())
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    @Synchronized
    fun writeStrToFile(str: String, file: File) {
        writeStrToFile(str, file, StandardCharsets.UTF_8)
    }

    @JvmStatic
    @Synchronized
    fun writeStrToFile(str: String, file: File, charsetName: Charset) {
        writeStrToFile(str, file, charsetName.toString())
    }

    @JvmStatic
    @Synchronized
    fun writeStrToFile(str: String, file: File, charsetName: String) {
        try {
            val fos = FileOutputStream(file, true)
            val osw = OutputStreamWriter(fos, charsetName)
            val lines = str.split("\n")
            for (i in lines.indices) {
                if (i == lines.size - 1) {
                    if (lines[i].endsWith("\n")) {
                        osw.write(lines[i])
                    } else {
                        osw.write("${lines[i]}\n")
                    }
                } else {
                    osw.write("${lines[i]}\n")
                }
            }
            osw.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    @Synchronized
    fun writeStrToFile1(str: String, filePath: String) {
        try {
            val fw = FileWriter(filePath, true)
            val bw = BufferedWriter(fw)
            val lines = str.split("\n")
            for (i in lines.indices) {
                if (i == lines.size - 1) {
                    if (str.endsWith("\n")) {
                        bw.write("${lines[i]}\n")
                    } else {
                        bw.write(lines[i])
                    }
                } else {
                    bw.write("${lines[i]}\n")
                }
            }
            bw.close()
            fw.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    @Synchronized
    fun writeStrToFile2(str: String, filePath: String) {
        try {
            val file = File(filePath)
            if (file.exists()) {
                FileOutputStream(file, true).use { fos ->
                    BufferedWriter(OutputStreamWriter(fos)).use { out ->
                        out.write(str)
                        out.flush()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun writeStrToFile3(str: String, filePath: String) {
        try {
            val writer = FileWriter(filePath, true)
            writer.write(str)
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun writeStrToFile4(str: String, filePath: String) {
        try {
            val randomFile = RandomAccessFile(filePath, "rw")
            val fileLength = randomFile.length()
            randomFile.seek(fileLength)
            randomFile.writeUTF(str)
            randomFile.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun copyFile(srcFilePath: String, dstFilePath: String) {
        try {
            val srcFile = File(srcFilePath)
            if (srcFile.exists() && srcFile.isFile && srcFile.canRead()) {
                val input = FileInputStream(srcFilePath)
                val output = FileOutputStream(dstFilePath)
                val buffer = ByteArray(1024)
                var byteRead: Int
                while (input.read(buffer).also { byteRead = it } != -1) {
                    output.write(buffer, 0, byteRead)
                }
                input.close()
                output.flush()
                output.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun copyFileByChannel(inFilePath: String, outFilePath: String) {
        val inFile = File(inFilePath)
        val outFile = File(outFilePath)
        try {
            FileInputStream(inFile).use { inFileStream ->
                FileOutputStream(outFile).use { outFileStream ->
                    val inChannel: FileChannel = inFileStream.channel
                    val outChannel: FileChannel = outFileStream.channel
                    inChannel.transferTo(0, inChannel.size(), outChannel)
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun copyFileByStream(inFilePath: String, outFilePath: String) {
        val inFile = File(inFilePath)
        val outFile = File(outFilePath)
        try {
            BufferedInputStream(FileInputStream(inFile)).use { inStream ->
                BufferedOutputStream(FileOutputStream(outFile)).use { outStream ->
                    val buf = ByteArray(2048)
                    var i: Int
                    while (inStream.read(buf).also { i = it } != -1) {
                        outStream.write(buf, 0, i)
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun copyFolder(oldPath: String, newPath: String) {
        try {
            val newFile = File(newPath)
            if (!newFile.exists()) {
                newFile.mkdirs()
            }

            val oldFile = File(oldPath)
            val files = oldFile.list() ?: emptyArray()
            for (file in files) {
                val temp =
                    if (oldPath.endsWith(File.separator)) {
                        File(oldPath + file)
                    } else {
                        File(oldPath + File.separator + file)
                    }

                if (temp.isDirectory) {
                    copyFolder("$oldPath/$file", "$newPath/$file")
                } else if (temp.exists() && temp.isFile && temp.canRead()) {
                    val input = FileInputStream(temp)
                    val output = FileOutputStream("$newPath/${temp.name}")
                    val buffer = ByteArray(1024)
                    var byteRead: Int
                    while (input.read(buffer).also { byteRead = it } != -1) {
                        output.write(buffer, 0, byteRead)
                    }
                    input.close()
                    output.flush()
                    output.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun unZip(zipFilePath: String, destDir: String) {
        val dir = File(destDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val buffer = ByteArray(1024)
        try {
            val fis = FileInputStream(zipFilePath)
            val zis = ZipInputStream(fis)
            var ze: ZipEntry? = zis.nextEntry
            while (ze != null) {
                val fileName = ze.name.replace(";|\\\\|:|\\*|\\?|\"|<|>|\\|".toRegex(), "")
                val newFile = File(destDir + File.separator + fileName)
                File(requireNotNull(newFile.parent)).mkdirs()
                val fos = FileOutputStream(newFile)
                var len: Int
                while (zis.read(buffer).also { len = it } > 0) {
                    fos.write(buffer, 0, len)
                }
                fos.close()
                zis.closeEntry()
                ze = zis.nextEntry
            }
            zis.closeEntry()
            zis.close()
            fis.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun getEncode(filePath: String): String {
        val first3Bytes = ByteArray(3)
        try {
            BufferedInputStream(FileInputStream(filePath)).use { bis ->
                bis.mark(0)
                var read = bis.read(first3Bytes, 0, 3)
                if (read == -1) {
                    return "GBK"
                }
                if (first3Bytes[0] == 0xFF.toByte() && first3Bytes[1] == 0xFE.toByte()) {
                    return "UTF-16LE"
                }
                if (first3Bytes[0] == 0xFE.toByte() && first3Bytes[1] == 0xFF.toByte()) {
                    return "UTF-16BE"
                }
                if (first3Bytes[0] == 0xEF.toByte() &&
                    first3Bytes[1] == 0xBB.toByte() &&
                    first3Bytes[2] == 0xBF.toByte()
                ) {
                    return "UTF-8"
                }
                bis.reset()
                while (bis.read().also { read = it } != -1) {
                    if (read >= 0xF0) {
                        break
                    }
                    if (read in 0x80..0xBF) {
                        break
                    }
                    if (read in 0xC0..0xDF) {
                        read = bis.read()
                        if (read in 0x80..0xBF) {
                            continue
                        }
                        break
                    } else if (read in 0xE0..0xEF) {
                        read = bis.read()
                        if (read in 0x80..0xBF) {
                            read = bis.read()
                            if (read in 0x80..0xBF) {
                                return "UTF-8"
                            }
                            break
                        }
                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "GBK"
    }
}
