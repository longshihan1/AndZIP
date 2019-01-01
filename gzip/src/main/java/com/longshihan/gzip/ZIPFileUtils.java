package com.longshihan.gzip;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 压缩
 */
public class ZIPFileUtils {

    private boolean isDeleteOriginFile;
    private String orginFilePath;
    private String zipFilePath;
    private ZipStragy stragy;
    private ZipFileListener listener;
    private Set<String> endFilters;

    public ZIPFileUtils(Builder builder){
        this.isDeleteOriginFile=builder.isDeleteOriginFile();
        this.orginFilePath=builder.getOrginFilePath();
        this.stragy=builder.getStragy();
        this.zipFilePath=builder.getZipFilePath();
    }


    public static class Builder{
        private boolean isDeleteOriginFile;
        private String orginFilePath;
        private String zipFilePath;
        private ZipStragy stragy;
        private ZipFileListener listener;
        private MyHandler handler;
        private Set<String> endFilters;
        private Set<String> containerFilters;

        public Builder(){
            endFilters=new TreeSet<>();
            containerFilters=new TreeSet<>();
        }
        public boolean isDeleteOriginFile() {
            return isDeleteOriginFile;
        }

        public String getOrginFilePath() {
            return orginFilePath;
        }

        public String getZipFilePath() {
            return zipFilePath;
        }

        public ZipStragy getStragy() {
            return stragy;
        }

        public Builder setDeleteOriginFile(boolean deleteOriginFile) {
            isDeleteOriginFile = deleteOriginFile;
            return this;
        }



        public Builder setOrginFilePath(String orginFilePath) {
            this.orginFilePath = orginFilePath;
            return this;
        }


        public Builder setZipFilePath(String zipFilePath) {
            this.zipFilePath = zipFilePath;
            return this;
        }


        public Builder setStragy(ZipStragy stragy) {
            this.stragy = stragy;
            return this;
        }


        public ZipFileListener getListener() {
            return listener;
        }

        public Builder setListener(ZipFileListener listener) {
            this.listener = listener;
            return this;
        }

        public void build(){
            if (getStragy()==null){
                setStragy(ZipStragy.ZIP);
            }
            if (TextUtils.isEmpty(getOrginFilePath())){
                return;
            }
            handler=new MyHandler(this);
            if (getStragy()==ZipStragy.ZIP){
              new Thread(new Runnable() {
                  @Override
                  public void run() {
                      try {
                          List<String> endLists = new ArrayList<>(endFilters);
                          List<String> containerLists = new ArrayList<>(containerFilters);
                          ZipUtils.getInstance().setSourceFilePath(getOrginFilePath())
                                  .setZipFilePath(getZipFilePath())
                                  .setDeleteOriginFile(isDeleteOriginFile())
                                  .setEndFilters(endLists)
                                  .setContainerFilters(containerLists);
                          String path = ZipUtils.getInstance().save2ZIPFile();
                          Message message = new Message();
                          message.what = 1;
                          message.obj = path;
                          handler.sendMessage(message);
                      } catch (Exception e) {
                          handler.sendEmptyMessage(0);
                      }
                  }
              }).start();
            }else if (getStragy()==ZipStragy.GZIP){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<String> endLists = new ArrayList<>(endFilters);
                            List<String> containerLists = new ArrayList<>(containerFilters);
                            GZIPUtils.getInstance().setSourceFilePath(getOrginFilePath())
                                    .setZipFilePath(getZipFilePath())
                                    .setDeleteOriginFile(isDeleteOriginFile())
                                    .setEndFilters(endLists)
                                    .setContainerFilters(containerLists);
                            String path= GZIPUtils.getInstance().save2GZIPFile();
                            Message message=new Message();
                            message.what=1;
                            message.obj=path;
                            handler.sendMessage(message);
                        } catch (Exception e) {
                            handler.sendEmptyMessage(0);
                        }
                    }
                }).start();

            }
        }

        public Set<String> getEndFilters() {
            return endFilters;
        }

        public Builder addEndFilter(String endFilter) {
            this.endFilters.add(endFilter);
            return this;
        }

        public Set<String> getContainerFilters() {
            return containerFilters;
        }

        public Builder addContainerFilter(String containerFilter) {
            this.containerFilters.add( containerFilter);
            return this;
        }
    }


   static class MyHandler extends Handler{
       WeakReference<Builder> mBuilder;

       MyHandler(Builder builder){
           mBuilder=new WeakReference<>(builder);
       }
        @Override
       public void handleMessage(Message msg) {
           if (msg!=null&&msg.what==1
                   &&msg.obj!=null&&TextUtils.isEmpty(msg.obj.toString())){
               String path= (String) msg.obj;
               if (mBuilder!=null&&mBuilder.get()!=null&&mBuilder.get().getListener()!=null){
                   mBuilder.get().getListener().getZipFilePath(path);
               }
           }else {
               if (mBuilder!=null&&mBuilder.get()!=null&&mBuilder.get().getListener()!=null){
                   mBuilder.get().getListener().failureZip();

               }
           }
       }
   }


}
