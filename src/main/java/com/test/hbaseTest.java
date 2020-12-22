package com.test;

import com.hbase.DBUtils;
import org.junit.Test;

import java.io.IOException;

public class hbaseTest {
        /**
     * 插入数据
     *
     * @throws IOException
     */
    @Test
    public void putData() throws IOException {
        DBUtils.insertRow("priMapping", "95001", "fileName", "", "filename.txt");
        DBUtils.insertRow("priMapping", "95001", "priKey", "", "prikey_5555");
    }

    /**
     * 根据rowKey与colFamily希望能找到对应的val
     *
     * @throws IOException
     */
    @Test
    public void showData() throws IOException {
        String priK1 = DBUtils.getPriK("priMapping", "95001", "fileName", "");
        System.out.println("test中的"+priK1);
        String priK2 = DBUtils.getPriK("priMapping", "95001", "priKey", "");
        System.out.println("test中的"+priK2);
    }

    @Test
    public void getData() throws IOException {
        DBUtils.getData("priMapping", "95001", "fileName", "");
    }
}
