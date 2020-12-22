package com.test;

import com.hadoop.ftp;
import org.junit.Test;

public class hadoopTest {
    private String localPath = "src/main/java/haha.txt";
    private String dfsPath = "/user/hadoop/hub/haha.txt";

    @Test
    public void testSend() {
        ftp.sendFile(localPath, dfsPath);
    }

    @Test
    public void getSend() {
        ftp.getFile(localPath, dfsPath);
    }
}
