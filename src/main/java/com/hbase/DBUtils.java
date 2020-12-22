package com.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;

import java.io.IOException;

public class DBUtils {
    private static Configuration configuration;
    private static Connection connection;
    private static Admin admin;

    //建立连接
    private static void init() {
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "192.168.143.122,192.168.43.241,192.168.43.166,192.168.43.64");
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("hbase.rootdir", "hdfs://192.168.43.122:9000/hbase");
        configuration.set("hbase.master", "192.168.143.122:60000");
        try {
            connection = ConnectionFactory.createConnection(configuration);
            admin = connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //关闭连接
    private static void close() {
        try {
            if (admin != null) {
                admin.close();
            }
            if (null != connection) {
                connection.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向某一行的某一列插入数据
     *
     * @param tableName 表名
     * @param rowKey    行键
     * @param colFamily 列族名
     * @param col       列名（如果其列族下没有子列，此参数可为空）
     * @param val       值
     * @throws IOException
     */
    public static void insertRow(String tableName, String rowKey, String colFamily, String col, String val) throws IOException {
        init();
        Table table = connection.getTable(TableName.valueOf(tableName));
        Put put = new Put(rowKey.getBytes());
        put.addColumn(colFamily.getBytes(), col.getBytes(), val.getBytes());
        table.put(put);
        table.close();
        close();
        System.out.println("已插入到hbase中");
    }

    /**
     * 根据行键rowkey查找数据
     *
     * @param tableName 表名
     * @param rowKey    行键
     * @param colFamily 列族名
     * @param col       列名
     * @throws IOException
     */
    public static void getData(String tableName, String rowKey, String colFamily, String col) throws IOException {
        init();
        Table table = connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(rowKey.getBytes());
        get.addColumn(colFamily.getBytes(), col.getBytes());
        Result result = table.get(get);

        showCell(result);

        table.close();
        close();
    }

    /**
     * 格式化输出
     *
     * @param result
     */
    public static void showCell(Result result) {
        Cell[] cells = result.rawCells();
        for (Cell cell : cells) {
            System.out.println("RowName:" + new String(CellUtil.cloneRow(cell)) + " ");
            System.out.println("Timetamp:" + cell.getTimestamp() + " ");
            System.out.println("column Family:" + new String(CellUtil.cloneFamily(cell)) + " ");
            System.out.println("row Name:" + new String(CellUtil.cloneQualifier(cell)) + " ");
            System.out.println("value:" + new String(CellUtil.cloneValue(cell)) + " ");
        }
    }

    /**
     * 根据行键rowkey与colFamily查找value，这个方法不确定
     *
     * @param tableName 表名
     * @param rowKey    行键
     * @param colFamily 列族名
     * @param col       列名
     * @throws IOException
     */
    public static String getPriK(String tableName, String rowKey, String colFamily, String col) throws IOException {
        init();
        String priK = null;
        Table table = connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(rowKey.getBytes());
        get.addColumn(colFamily.getBytes(), col.getBytes());
        Result result = table.get(get);

        Cell[] cells = result.rawCells();
        for (Cell cell : cells) {
            priK = new String(CellUtil.cloneValue(cell));
            break;
        }

        table.close();
        close();
        return priK;
    }
}
