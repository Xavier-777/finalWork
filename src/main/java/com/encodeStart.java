package com;

import com.hadoop.ftp;
import com.hbase.DBUtils;
import com.utils.FileUtils;
import com.utils.Util;
import com.utils.sm2.SM2EncDecUtils;
import com.utils.sm2.SM2KeyVO;

import java.io.IOException;

import static com.utils.sm2.SM2EncDecUtils.generateKeyPair;

public class encodeStart {
    private static String localPath = "src/main/java/haha.txt";//本地原生txt文件
    private static String hdfsPath = "/user/hadoop/haha.txt";//原生txt上传到hdfs
    private static String localCipher = "src/main/java/haha_cipherText.txt";//生成本地密文
    private static String hdfsCipher = "/user/hadoop/haha_cipherText.txt";//将密文上传到hdfs中

    public static void main(String[] args) throws IOException {
        //-------------------加密-------------------//
        long start = System.currentTimeMillis();

        //0. 上传原文到hdfs
        ftp.sendFile(localPath, hdfsPath);

        //1. 读取本地文件
        byte[] sourceData = FileUtils.fileToByte(localPath);

        //2. 获取秘钥对
        SM2KeyVO sm2KeyVO = generateKeyPair();
        String pubk = Util.byteToHex(sm2KeyVO.getPublicKey().getEncoded());//获取公钥
        String prik = Util.byteToHex(sm2KeyVO.getPrivateKey().toByteArray());//获取私钥

        //3. 用16进制的pubk加密，获得密文：cipherText，密文是16进制字符串
        System.out.println("加密: ");
        String cipherText = SM2EncDecUtils.encrypt(Util.hexToByte(pubk), sourceData);//加密本地文件
        System.out.println(cipherText);//密文字符串
        byte[] cipherBytes = cipherText.getBytes();//密文的Bytes
        FileUtils.byteToFile(localCipher, cipherBytes);//用一个txt文件装密文

        //4. 将装密文的txt文件发送到HDFS
        ftp.sendFile(localCipher, hdfsCipher);

        //5. 发送prik给hbase
        // DBUtils.insertRow("priMapping", "95001", "fileName", "", "haha_cipherText.txt");
        DBUtils.insertRow("priMapping", "95001", "priKey", "", prik);

        long end = System.currentTimeMillis();
        String sum = String.valueOf((end - start) / 1000);
        System.out.println("总耗时：" + sum + "S");
    }
}
