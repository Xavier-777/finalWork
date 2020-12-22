package com;

import com.hadoop.ftp;
import com.hbase.DBUtils;
import com.utils.Util;
import com.utils.sm2.SM2EncDecUtils;
import com.utils.sm2.SM2KeyVO;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.NLineInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


import java.io.IOException;

import static com.utils.sm2.SM2EncDecUtils.generateKeyPair;

/**
 * 用MR加密
 */
public class encodeMR {

    private static String localPath = "G:\\haha.txt";//本地原生txt文件
    private static String url = "hdfs://192.168.43.122:9000";
    private static String hdfsPath = url + "/user/hadoop/haha.txt";//原生txt上传到hdfs
    private static String hdfsCipher = url + "/user/hadoop/haha_cipherText.txt";//将密文上传到hdfs中
    private static int sumLines = 7500;//300M 7500      30M 750

    public static class encodeMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            context.write(key, value);
        }
    }


    public static class encodeReducer extends Reducer<LongWritable, Text, LongWritable, Text> {
        Text text = new Text();

        @Override
        protected void reduce(LongWritable key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            String pubkey = context.getConfiguration().get("pubkey");

            //for循环遍历，将得到的values值累加
            for (Text value : values) {
                String c = value.toString();
                String s = SM2EncDecUtils.encrypt(Util.hexToByte(pubkey), c.getBytes());
                text.set(s);
                context.write(null, text);
            }
            //将结果保存到context中，最终输出形式为"key" + "result"
        }
    }

    public static void Driver() throws Exception {
        Configuration conf = new Configuration();
        Long startTime, endTime, usedTime = null;

        System.out.println("start");

        //1.生成密钥,存到hbase中
        startTime = System.currentTimeMillis();

        SM2KeyVO sm2KeyVO = generateKeyPair();
        String pubk = Util.byteToHex(sm2KeyVO.getPublicKey().getEncoded());//获取公钥
        String prik = Util.byteToHex(sm2KeyVO.getPrivateKey().toByteArray());//获取私钥
        DBUtils.insertRow("priMapping", "95001", "priKey", "", prik);

        endTime = System.currentTimeMillis();
        usedTime = (endTime - startTime) / 1000;
        System.out.println("写入hbase完成,用时:" + usedTime + "s");


        //2.加密文件,上传到hdfs中
        startTime = System.currentTimeMillis();
        conf.set("pubkey", pubk);
        Job job = Job.getInstance(conf);

        /**
         * 重点
         * N是每个Mapper分配到的行数
         * */
        NLineInputFormat.setNumLinesPerSplit(job, 7500);
        job.setInputFormatClass(NLineInputFormat.class);
        job.setNumReduceTasks(100);//开10个ReduceTasks，最后生成10个文件

        job.setJarByClass(encodeMR.class);// 设置运行/处理该作业的类
        job.setMapperClass(encodeMR.encodeMapper.class);//设置实现了Map步的类
        job.setReducerClass(encodeMR.encodeReducer.class);//设置实现了Reduce步的类
        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(LongWritable.class);//设置输出结果key的类型
        job.setOutputValueClass(Text.class);//设置输出结果value的类型

        FileInputFormat.addInputPath(job, new Path(hdfsPath));
        FileOutputFormat.setOutputPath(job, new Path(hdfsCipher));

        job.waitForCompletion(true);

        endTime = System.currentTimeMillis();
        usedTime = (endTime - startTime) / 1000;
        System.out.println("加密文件到hdfs完成,用时:" + usedTime + "s");
    }

    public static void main(String[] args) {
        try {
            encodeMR.Driver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
