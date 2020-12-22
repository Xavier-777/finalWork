package com;

import com.hadoop.ftp;
import com.hbase.DBUtils;
import com.utils.FileUtils;
import com.utils.Util;
import com.utils.sm2.SM2EncDecUtils;
import com.utils.sm2.SM2KeyVO;

import java.io.IOException;

import static com.utils.sm2.SM2EncDecUtils.generateKeyPair;

public class decodeStart {
    private static String hdfsCipher="/user/hadoop/haha_cipherText.txt";
    private static String getFromhdfsFile="src/main/java/getFromHdfs.txt";//从hdfs获取原生txt
    private static String target="src/main/java/tar.txt";//解密后的文件

    public static void main(String[] args) throws IOException {
        //--------------------解密----------------------------//
        long start = System.currentTimeMillis();

        //1. 到HDFS取cipherText
        ftp.getFile(getFromhdfsFile,hdfsCipher);
        byte[] getFromHdfs = FileUtils.fileToByte(getFromhdfsFile);
        String s = new String(getFromHdfs);//读出txt中的密文
        System.out.println(s);

        //2. 到hbase取prik
        /*************这里需要注意，取hbase的数据，应该是用文件名来比对，但是从hbase写数据太卡了，我就砍了这一步***************/
        String priKFromHbase = DBUtils.getPriK("priMapping", "95001", "priKey", "");
        System.out.println(priKFromHbase);

        //3. 使用priKFromHbase解密cipherText
        System.out.println("解密: ");
        byte[] decrypt = SM2EncDecUtils.decrypt(Util.hexToByte(priKFromHbase), Util.hexToByte(s));

        //4. 生成文件
        FileUtils.byteToFile(target, decrypt);

        System.out.println("成功");

        long end = System.currentTimeMillis();
        String sum = String.valueOf((end - start) / 1000);
        System.out.println("总耗时：" + sum + "S");
    }
}
