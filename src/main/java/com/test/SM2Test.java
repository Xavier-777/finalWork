package com.test;

import com.utils.FileUtils;
import com.utils.Util;
import com.utils.sm2.SM2EncDecUtils;
import com.utils.sm2.SM2KeyVO;
import org.junit.Test;

import java.io.IOException;


import static com.utils.sm2.SM2EncDecUtils.generateKeyPair;

public class SM2Test {
    @Test
    public void SM2WithString() throws IOException {
        String plainText = "ILoveYou11";
        byte[] sourceData = plainText.getBytes();

        //获取秘钥对
        SM2KeyVO sm2KeyVO = generateKeyPair();
        String pubk = Util.byteToHex(sm2KeyVO.getPublicKey().getEncoded());//获取公钥
        String prik = Util.byteToHex(sm2KeyVO.getPrivateKey().toByteArray());//获取私钥

        System.out.println("加密: ");
        //用16进制的pubk加密，获得密文：cipherText，密文是16进制字符串
        String cipherText = SM2EncDecUtils.encrypt(Util.hexToByte(pubk), sourceData);
        System.out.println(cipherText);

        //使用16进制的prik解密
        System.out.println("解密: ");
        plainText = new String(SM2EncDecUtils.decrypt(Util.hexToByte(prik), Util.hexToByte(cipherText)));
        System.out.println(plainText);
    }

    @Test
    public void SM2WithFile() throws IOException {
        //1. 读取文件
        byte[] sourceData = FileUtils.fileToByte("src/main/java/haha.txt");

        //2. 获取秘钥对
        SM2KeyVO sm2KeyVO = generateKeyPair();
        String pubk = Util.byteToHex(sm2KeyVO.getPublicKey().getEncoded());//获取公钥
        String prik = Util.byteToHex(sm2KeyVO.getPrivateKey().toByteArray());//获取私钥

        System.out.println("加密: ");
        //3. 用16进制的pubk加密，获得密文：cipherText，密文是16进制字符串
        String cipherText = SM2EncDecUtils.encrypt(Util.hexToByte(pubk), sourceData);
        //System.out.println(cipherText);//密文

        //4. 使用16进制的prik解密
        System.out.println("解密: ");
        byte[] decrypt = SM2EncDecUtils.decrypt(Util.hexToByte(prik), Util.hexToByte(cipherText));

        //5. 生成文件
        FileUtils.byteToFile("src/main/java/tar.txt", decrypt);

        System.out.println("成功");
    }

    //最终流程
    @Test
    public void SM2Final() throws IOException {
        //1. 读取文件
        byte[] sourceData = FileUtils.fileToByte("src/main/java/haha.txt");

        //2. 获取秘钥对
        SM2KeyVO sm2KeyVO = generateKeyPair();
        String pubk = Util.byteToHex(sm2KeyVO.getPublicKey().getEncoded());//获取公钥
        String prik = Util.byteToHex(sm2KeyVO.getPrivateKey().toByteArray());//获取私钥

        System.out.println("加密: ");
        //3. 用16进制的pubk加密，获得密文：cipherText，密文是16进制字符串
        String cipherText = SM2EncDecUtils.encrypt(Util.hexToByte(pubk), sourceData);

        //4. 发送cipherText到HDFS
        //5. 发送prik给hbase

        //------------------------------------------------

        //6. 到HDFS取cipherText
        //7. 到hbase取prik

        //8. 使用prik解密cipherText
        System.out.println("解密: ");
        byte[] decrypt = SM2EncDecUtils.decrypt(Util.hexToByte(prik), Util.hexToByte(cipherText));

        //9. 生成文件
        FileUtils.byteToFile("src/main/java/tar.txt", decrypt);

        System.out.println("成功");
    }
}
