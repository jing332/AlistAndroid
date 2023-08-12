package com.github.jing332.alistandroid.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.exoplayer2.util.MimeTypes;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Objects;
import java.util.zip.ZipFile;

public class FileUtil {
    private static final String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final long SIZE_DELTA = 1048576;
    private static final String TAG = "Util";

    public interface UnzippingCallback {
        void onProgressChanged(double unzippedSize);
    }

    public static String getStatus(int code) {
        switch (code) {
            case 1:
                return "SYNTHESIZE_STATUS_INIT_SUCC";
            case 2:
                return "SYNTHESIZE_STATUS_INIT_FAIL";
            case 3:
                return "SYNTHESIZE_STATUS_START";
            case 4:
                return "SYNTHESIZE_STATUS_END";
            case 5:
                return "SYNTHESIZE_STATUS_FAIL";
            case 6:
                return "SG_SYNTHESIZE_STATUS_ING";
            case 7:
                return "SG_SYNTHESIZE_STATUS_ENGINE_DESTROYED";
            case 8:
                return "SG_SYNTHESIZE_STATUS_ENGINE_DIMMED_TXT";
            default:
                return "UNKNOWN_CODE";
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        try {
            if (ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*    public static void checkCrashLog(Context context) {
        String str = context.getExternalFilesDir("log") + "/crash.log";
        String readFile = readFile(str);
        if (readFile == null) {
            return;
        }
        ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("MultiTTS崩溃日志", readFile));
        Toast.makeText(context, "检测到软件崩溃，日志已复制到剪贴板", Toast.LENGTH_LONG).show();
        new File(str).renameTo(new File(context.getExternalFilesDir("log") + "/" + DateFormat.format("yyyy-MM-dd_hh:mm:ss", System.currentTimeMillis()).toString() + ".log"));
    }*/

    public static void deleteFirstLine(File target) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(target, "rw");
            long filePointer = randomAccessFile.getFilePointer();
            if (randomAccessFile.readLine().startsWith("!!")) {
                byte[] bArr = new byte[1024];
                long filePointer2 = randomAccessFile.getFilePointer();
                while (true) {
                    int read = randomAccessFile.read(bArr);
                    if (read == -1) {
                        break;
                    }
                    randomAccessFile.seek(filePointer);
                    randomAccessFile.write(bArr, 0, read);
                    long j = read;
                    filePointer2 += j;
                    filePointer += j;
                    randomAccessFile.seek(filePointer2);
                }
                randomAccessFile.setLength(filePointer);
            }
            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String filePath, String data) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null) {
                    sb.append(readLine);
                    sb.append(System.lineSeparator());
                } else {
                    String sb2 = sb.toString();
                    bufferedReader.close();
                    return sb2;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }




    public static void writeZipFile(String src, String dst, String fileName) throws
            FileNotFoundException, IOException {
        ZipFile zipFile = new ZipFile(src);
        InputStream inputStream = zipFile.getInputStream(zipFile.getEntry(fileName));
        File file = new File(dst + fileName);
        if (!file.exists()) {
            new File(dst).mkdirs();
        }
        file.createNewFile();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(src));
        while (true) {
            int read = inputStream.read();
            if (read != -1) {
                bufferedOutputStream.write(read);
            } else {
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                inputStream.close();
                return;
            }
        }
    }

    public static String getPath(final Context context, final Uri uri) {
        Uri uri2 = null;
        if ((Build.VERSION.SDK_INT >= 19) && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                String[] split = DocumentsContract.getDocumentId(uri).split(":");
                if ("primary".equalsIgnoreCase(split[0])) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                return getDataColumn(context, ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(DocumentsContract.getDocumentId(uri)).longValue()), null, null);
            } else {
                if (isMediaDocument(uri)) {
                    String[] split2 = DocumentsContract.getDocumentId(uri).split(":");
                    String str = split2[0];
                    if ("image".equals(str)) {
                        uri2 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if (MimeTypes.BASE_TYPE_VIDEO.equals(str)) {
                        uri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if (MimeTypes.BASE_TYPE_AUDIO.equals(str)) {
                        uri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    return getDataColumn(context, uri2, "_id=?", new String[]{split2[1]});
                }
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else {
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[]
            selectionArgs) {
        Cursor cursor = null;
        try {
            Cursor query = context.getContentResolver().query(uri, new String[]{"_data"}, selection, selectionArgs, null);
            if (query != null) {
                try {
                    if (query.moveToFirst()) {
                        String string = query.getString(query.getColumnIndexOrThrow("_data"));
                        if (query != null) {
                            query.close();
                        }
                        return string;
                    }
                } catch (Throwable th) {
                    th = th;
                    cursor = query;
                    if (cursor != null) {
                        cursor.close();
                    }
                    throw th;
                }
            }
            if (query != null) {
                query.close();
            }
            return null;
        } catch (Throwable th2) {
//            th = th2;
        }


        return "";
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getUriFileName(Uri uri) {
        return new File(Objects.requireNonNull(uri.getPath())).getName();
    }

}
