package com.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ftp {

    /**
     * 将本地文件上传到hdfs
     *
     * @param localPath 本地文件路径
     * @param hdfsPath  hdfs的路径
     */
    public static void sendFile(String localPath, String hdfsPath) {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://192.168.43.122:9000");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");

        FileSystem fs = null;
        FileInputStream fis = null;
        FSDataOutputStream fos = null;
        try {
            fs = FileSystem.get(conf);
            fis = new FileInputStream(new File(localPath));
            fos = fs.create(new Path(hdfsPath));

            //拷贝到hdfs
            //IOUtils.copyBytes(fis, fos, conf);
            IOUtils.copyBytes(fis, fos, 40960,true);

            System.out.println("上传文件成功");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(fos);
            IOUtils.closeStream(fis);
            try {
                if (fs != null)
                    fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从hdfs中下载文件
     *
     * @param localPath 本地文件路径
     * @param hdfsPath  hdfs的路径
     */
    public static void getFile(String localPath, String hdfsPath) {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://192.168.43.122:9000");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        FileSystem fs = null;
        FSDataInputStream fis = null;
        FileOutputStream fos = null;
        try {
            // 1.获取对象
            fs = FileSystem.get(conf);

            // 2.获取输入流
            fis = fs.open(new Path(hdfsPath));

            // 3.获取输出流
            fos = new FileOutputStream(new File(localPath));
            // 4.流的拷贝
            IOUtils.copyBytes(fis, fos, conf);

            System.out.println("下载文件成功");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 5.关闭fs
            IOUtils.closeStream(fos);
            IOUtils.closeStream(fis);
            try {
                if (fs != null)
                    fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}