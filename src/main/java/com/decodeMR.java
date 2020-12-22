package com;

import com.hbase.DBUtils;
import com.utils.Util;
import com.utils.sm2.SM2EncDecUtils;
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

/**
 * 解密
 */
public class decodeMR {
    private static String url = "hdfs://192.168.43.122:9000";
    private static String hdfsCipher = url + "/user/hadoop/haha_cipherText.txt";
    private static String target = url + "/user/hadoop/target.txt";//解密后的文件
    private static int sumLines=7500;//300M 7500  100Reducer    30M 750 10Reducer

    public static class decodeMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            context.write(key, value);
        }
    }

    public static class decodeReducer extends Reducer<LongWritable, Text, LongWritable, Text> {
        Text text = new Text();
        @Override
        protected void reduce(LongWritable key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            //for循环遍历，将得到的values值累加
            for (Text value : values) {
                String prikey = context.getConfiguration().get("prikey");
                String c = value.toString();
                byte s[] = SM2EncDecUtils.decrypt(Util.hexToByte(prikey), Util.hexToByte(c));
                text.set(new String(s, 0, s.length));
                context.write(null, text);
            }
            //将结果保存到context中，最终输出形式为"key" + "result"
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        Long startTime, endTime, usedTime = null;

        //1.从hbase中获取priKey
        startTime = System.currentTimeMillis();
        String priKFromHbase = DBUtils.getPriK("priMapping", "95001", "priKey", "");
        System.out.println(priKFromHbase);
        endTime = System.currentTimeMillis();
        usedTime = (endTime - startTime) / 1000;
        System.out.println("读取hbase完成,用时:" + usedTime + "s");

        //2.解密文件,上传到hdfs中
        startTime = System.currentTimeMillis();
        conf.set("prikey", priKFromHbase);
        Job job = Job.getInstance(conf);

        /**重点*/
        NLineInputFormat.setNumLinesPerSplit(job, 7500);
        job.setInputFormatClass(NLineInputFormat.class);
        job.setNumReduceTasks(100);//开10个ReduceTasks，最后生成10个文件

        job.setJarByClass(decodeMR.class);// 设置运行/处理该作业的类
        job.setMapperClass(decodeMR.decodeMapper.class);//设置实现了Map步的类
        job.setReducerClass(decodeMR.decodeReducer.class);//设置实现了Reduce步的类
        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(LongWritable.class);//设置输出结果key的类型
        job.setOutputValueClass(Text.class);//设置输出结果value的类型


        FileInputFormat.addInputPath(job, new Path(hdfsCipher));
        FileOutputFormat.setOutputPath(job, new Path(target));

        job.waitForCompletion(true);

        endTime = System.currentTimeMillis();
        usedTime = (endTime - startTime) / 1000;
        System.out.println("解密到hdfs中完成,用时:" + usedTime + "s");
    }
}
