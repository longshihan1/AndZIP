<h1> zip </h1>


内部包含ZIP，GZIP压缩实现（没有解压操作）。

‘’‘


    new ZIPFileUtils.Builder()
                            .setStragy(ZipStragy.ZIP)
                            .addEndFilter(".gz")
                            .addEndFilter(".zip")
                            .setListener(new ZipFileListener() {
                                @Override
                                public void getZipFilePath(String zipFilePath) {
                                    Log.d("测试压缩", zipFilePath);
                                }

                                @Override
                                public void failureZip() {
                                    Log.d("测试压缩", "失败");
                                }
                            })
                            .setOrginFilePath("/storage/emulated/0/flog/com.longshihan.flog")
                            .setDeleteOriginFile(true)
                            .build();




’‘’

采用Builder建筑者模式，内部的实现也是遵循此规范，方便书写。可以不实现ZIPFileUtils,只实现内部的也可，代码如下：

‘’‘

     ZipUtils.getInstance().setSourceFilePath(getOrginFilePath())//原始路径
                                  .setZipFilePath(getZipFilePath())//目标路径
                                  .setDeleteOriginFile(isDeleteOriginFile())//是否删除压缩过的文件
                                  .setEndFilters(endLists)//遵循尾过滤
                                  .setContainerFilters(containerLists);//遵循存在过滤
                          String path = ZipUtils.getInstance().save2ZIPFile();




’‘’


其中默认情况下（不输入目标路径）GZIP压缩后的格式是.gz,ZIP压缩后的格式是.zip。其中没有实现动态权限部分。
