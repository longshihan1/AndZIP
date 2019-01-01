package com.longshihan.gzip;

import android.os.Build;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class GZIPUtils {

    static GZIPUtils instance;
    String sourceFilePath;
    String zipFilePath;
    boolean isDeleteOriginFile;
    List<String> endFilters;
    List<String> containerFilters;


    public static GZIPUtils getInstance(){
        if (instance==null){
            instance=new GZIPUtils();
        }
        return instance;
    }


    public GZIPUtils setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
        return this;
    }

    public GZIPUtils setZipFilePath(String zipFilePath) {
        this.zipFilePath = zipFilePath;
        return this;
    }

    public GZIPUtils setDeleteOriginFile(boolean deleteOriginFile) {
        isDeleteOriginFile = deleteOriginFile;
        return this;
    }



    public GZIPUtils setEndFilters(List<String> endFilters) {
        this.endFilters = endFilters;
        return this;
    }


    public GZIPUtils setContainerFilters(List<String> containerFilters) {
        this.containerFilters = containerFilters;
        return this;
    }


    public  String save2GZIPFile() throws Exception {
        if (TextUtils.isEmpty(sourceFilePath)){
            return null;
        }
        if (TextUtils.isEmpty(zipFilePath)){
            File gzipFile = createGZIPFile(sourceFilePath);
            zipFilePath=gzipFile.getAbsolutePath();
        }
        if (endFilters==null){
            endFilters=new ArrayList<>();
        }
        if (containerFilters==null){
            containerFilters=new ArrayList<>();
        }

        FileOutputStream fileOutputStream = null;
        GZIPOutputStream outZip = null;
        try {
            fileOutputStream = new FileOutputStream(zipFilePath);
            //创建ZIP
            outZip = new GZIPOutputStream(fileOutputStream);
            //创建文件
            File file = new File(sourceFilePath);
            //压缩
            ZipFiles(file.getParent() + File.separator, file.getName(), outZip,isDeleteOriginFile);
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.KITKAT) {
                outZip.finish();
            } else {
                outZip.flush();
            }
            return zipFilePath;
        } finally {
            closeQuietly(fileOutputStream);
            closeQuietly(outZip);
        }
    }


    public  void ZipFiles(String folderString, String fileString, GZIPOutputStream zipOutputSteam,boolean isDeleteOriginFile) throws Exception {
        if (zipOutputSteam == null) {
            return;
        }
        if (zipOutputSteam == null) {
            return;
        }
        boolean isExit=false;
        for (int i = 0; i <endFilters.size(); i++) {
            if (!TextUtils.isEmpty(endFilters.get(i))
                    &&fileString.endsWith(endFilters.get(i))){
                isExit=true;
                break;
            }
        }

        if (!isExit){
            for (int i = 0; i <containerFilters.size(); i++) {
                if (!TextUtils.isEmpty(containerFilters.get(i))
                        &&fileString.contains(containerFilters.get(i))){
                    isExit=true;
                    break;
                }
            }
        }
        if (isExit){
            return;
        }
        File file = new File(folderString + fileString);
        if (file.isFile()) {
            FileInputStream inputStream=null;
            try{
                inputStream = new FileInputStream(file);
                int len;
                byte[] buffer = new byte[4096];
                while ((len = inputStream.read(buffer)) != -1) {
                    zipOutputSteam.write(buffer, 0, len);
                }
            }finally {
                closeQuietly(inputStream);
                if (isDeleteOriginFile){
                    file.delete();
                }
            }

        } else {
            //文件夹
            String fileList[] = file.list();
            //没有子文件和压缩
            if (fileList.length <= 0) {
               return;
            }
            //子文件和递归
            for (int i = 0; i < fileList.length; i++) {
                ZipFiles(folderString + fileString + "/", fileList[i], zipOutputSteam,isDeleteOriginFile);
            }
        }
    }


    private  File createGZIPFile(String sourceFile) throws IOException {
        File tempFile = new File(sourceFile);
        if (tempFile.isFile()) {
            sourceFile = tempFile.getParent();
        } else if (tempFile.isDirectory() && !tempFile.exists()) {
            tempFile.mkdirs();
        }
        StringBuilder gzPathBuilder = new StringBuilder();
        gzPathBuilder.append(sourceFile);
        gzPathBuilder.append(File.separator);
        gzPathBuilder.append(DateUtils.getDate() + ".gz");
        File gzFile = new File(gzPathBuilder.toString());
        if (!gzFile.exists()) {
            gzFile.createNewFile();
        }
        return gzFile;
    }

    public  void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ignored) {

            }
        }
    }
}
