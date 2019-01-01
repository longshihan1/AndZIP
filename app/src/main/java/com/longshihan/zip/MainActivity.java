package com.longshihan.zip;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.longshihan.gzip.GZIPUtils;
import com.longshihan.gzip.ZIPFileUtils;
import com.longshihan.gzip.ZipFileListener;
import com.longshihan.gzip.ZipStragy;
import com.longshihan.gzip.ZipUtils;
import com.longshihan.permissionlibrary.CheckPermissionListener;
import com.longshihan.permissionlibrary.CheckPermissions;
import com.longshihan.permissionlibrary.Permission;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CheckPermissions checkPermissions = new CheckPermissions(this);
        checkPermissions.setLisener(new CheckPermissionListener() {
            @Override
            public void CheckPermissionResult(Permission permission) {
                try {
                    new ZIPFileUtils.Builder()
                            .setStragy(ZipStragy.ZIP)
                            .addEndFilter(".gz")
                            .addEndFilter(".zip")
                            .setListener(new ZipFileListener() {
                                @Override
                                public void getZipFilePath(String zipFilePath) {
                                    Log.d("测试", zipFilePath);
                                }

                                @Override
                                public void failureZip() {
                                    Log.d("测试", "失败");
                                }
                            })
                            .setOrginFilePath("/storage/emulated/0/flog/com.longshihan.flog")
                            .setDeleteOriginFile(true)
                            .build();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        checkPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    }
}
