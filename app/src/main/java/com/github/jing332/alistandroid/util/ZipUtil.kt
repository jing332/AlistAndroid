package com.github.jing332.alistandroid.util

import android.text.TextUtils
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.zip.Deflater
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.coroutines.coroutineContext

/* loaded from: classes2.dex */
object ZipUtil {
    const val TAG = "ZipUtils"

    suspend fun zipFolder(sourceFolder: File, zipFile: File) {
        zipFile.delete()
        zipFile.parentFile?.mkdirs()
        withContext(Dispatchers.IO) {
            zipFile.createNewFile()

            val fos = FileOutputStream(zipFile)
            val zos = ZipOutputStream(BufferedOutputStream(fos))

            zos.setLevel(Deflater.DEFAULT_COMPRESSION)
            zipFolder(sourceFolder, zos, "")
            zos.closeEntry()
            zos.close()
        }
    }

    @Throws(IOException::class)
    private suspend fun zipFolder(folder: File, zos: ZipOutputStream, parentPath: String) {
        val files = folder.listFiles()
        val buffer = ByteArray(4096)
        var bytesRead: Int

        withContext(Dispatchers.IO) {

            for (file in files!!) {
                if (!coroutineContext.isActive) break

                if (file.isDirectory) {
                    zipFolder(file, zos, parentPath + file.name + "/")
                } else {
                    val fis = FileInputStream(file)
                    val bis = BufferedInputStream(fis)
                    val entryPath = parentPath + file.name
                    val entry = ZipEntry(entryPath)
                    zos.putNextEntry(entry)
                    while (bis.read(buffer).also { bytesRead = it } != -1) {
                        if (!coroutineContext.isActive) break
                        zos.write(buffer, 0, bytesRead)
                    }
                    bis.close()
                    fis.close()
                }
            }
        }
    }

    /**
     * 查找文件并写到输出流
     */
    suspend fun findFile(
        zis: ZipInputStream,
        fileName: String,
        ous: OutputStream,
        onProgress: (name: String) -> Unit = {}
    ): Boolean {
        var entry: ZipEntry? = zis.nextEntry
        while (coroutineContext.isActive && entry != null) {
            onProgress(entry.name ?: "")
            Log.d(TAG, "findFile: ${entry.name}")
            if (fileName == entry.name) {
                var bytesRead: Int = 0
                val buffer = ByteArray(4096)
                while (coroutineContext.isActive
                    && zis.read(buffer).also { bytesRead = it } != -1
                ) {
                    ous.write(buffer, 0, bytesRead)
                }

                return true
            }

            zis.closeEntry()
            entry = zis.nextEntry
        }

        return false
    }

    /**
     * @param onProgress readCompressedSize 已经解压的压缩大小
     */
    suspend fun unzipFile(
        zis: ZipInputStream,
        destFolder: File,
        bufferSize: Int = 4096,

        onProgress: (readCompressedSize: Long, entry: ZipEntry?) -> Boolean
    ) {
        val buffer = ByteArray(bufferSize)
        var bytesRead = 0
        var totalBytesRead = 0L

        withContext(Dispatchers.IO) {
            var entry: ZipEntry? = zis.nextEntry
            while (coroutineContext.isActive && entry != null) {
                val entryName = entry.name
                val entryPath = destFolder.absolutePath + File.separator + entryName

                val isUnzip = onProgress(totalBytesRead, entry)
                if (isUnzip) {

                    Log.d(TAG, "unzipFile: $entryName")
                    if (entry.isDirectory) {
                        File(entryPath).mkdirs()
                    } else {
                        val file = File(entryPath)
                        file.parentFile?.mkdirs()
                        file.delete()
                        file.createNewFile()
                        FileOutputStream(file).use { fos ->
                            BufferedOutputStream(fos).use { bos ->
                                while (coroutineContext.isActive && zis.read(buffer)
                                        .also { bytesRead = it } != -1
                                ) {
                                    bos.write(buffer, 0, bytesRead)
                                }
                            }
                        }
                    }
                }

                zis.closeEntry()

                totalBytesRead += entry.compressedSize
                val next = zis.nextEntry
                if (next == null)
                    onProgress(totalBytesRead, null)

                entry = next
            }
            zis.close()
        }
    }


    @Throws(Exception::class)
    private fun addFileToZip(path: String, file: File, zos: ZipOutputStream) {
        val fileInputStream = FileInputStream(file)
        val str: String = if (TextUtils.isEmpty(path)) {
            file.name
        } else {
            path + "/" + file.name
        }
        zos.putNextEntry(ZipEntry(str))
        val bArr = ByteArray(1024)
        while (true) {
            val read = fileInputStream.read(bArr)
            if (read > 0) {
                zos.write(bArr, 0, read)
            } else {
                zos.closeEntry()
                fileInputStream.close()
                return
            }
        }
    }

    @Throws(Exception::class)
    private fun addFolderToZip(path: String, folder: File, zos: ZipOutputStream) {
        val listFiles = folder.listFiles()
        if (listFiles == null || listFiles.size <= 0) {
            return
        }
        for (file in listFiles) {
            if (file.isDirectory) {
                val name = if (TextUtils.isEmpty(path)) file.name else path + "/" + file.name
                zos.putNextEntry(ZipEntry("$name/"))
                addFolderToZip(name, file, zos)
            } else {
                addFileToZip(path, file, zos)
            }
        }
    }
}