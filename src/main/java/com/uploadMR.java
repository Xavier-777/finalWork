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

/**
 * 用MR上传文件
 */
public class uploadMR {
    private static String localPath = "G:\\haha.txt";//本地原生txt文件
    private static String url = "hdfs://192.168.43.122:9000";
    private static String hdfsPath = url + "/user/hadoop/haha.txt";//原生txt上传到hdfs
    private static int sumLines = 7500;//切片数量

    public static class uploadMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            context.write(key, value);
        }
    }


    public static class uploadReducer extends Reducer<LongWritable, Text, LongWritable, Text> {
        Text text = new Text();

        @Override
        protected void reduce(LongWritable key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            //for循环遍历，将得到的values值累加
            for (Text value : values) {
                String c = value.toString();
                text.set(c);
                context.write(null, text);
            }
            //将结果保存到context中，最终输出形式为"key" + "result"
        }
    }

    public static void Driver()throws Exception{
        Configuration conf = new Configuration();
        Long startTime, endTime, usedTime = null;

        System.out.println("start");

        //1.原文件上传到hdfs中
        startTime = System.currentTimeMillis();

        Job job = Job.getInstance(conf);

        /**
         * 重点
         * N是每个Mapper分配到的行数
         * */
        NLineInputFormat.setNumLinesPerSplit(job, 7500);
        job.setInputFormatClass(NLineInputFormat.class);
        job.setNumReduceTasks(100);//开100个ReduceTasks，最后生成100个文件

        job.setJarByClass(uploadMR.class);// 设置运行/处理该作业的类
        job.setMapperClass(uploadMR.uploadMapper.class);//设置实现了Map步的类
        job.setReducerClass(uploadMR.uploadReducer.class);//设置实现了Reduce步的类
        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(Text.class);
        job.setOutputKeyClass(LongWritable.class);//设置输出结果key的类型
        job.setOutputValueClass(Text.class);//设置输出结果value的类型

        //路径
        FileInputFormat.addInputPath(job, new Path(localPath));
        FileOutputFormat.setOutputPath(job, new Path(hdfsPath));

        job.waitForCompletion(true);

        endTime = System.currentTimeMillis();
        usedTime = (endTime - startTime) / 1000;
        System.out.println("上传文件到hdfs用时：:" + usedTime + "s");
    }

    public static void main(String[] args){
        try {
            uploadMR.Driver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
