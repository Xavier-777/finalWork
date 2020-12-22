package com;

/**
 * MR的main方法
 */
public class MRMain {
    public static void main(String[] args) {
        try {
            textFactory.textBuilder();//生成大文件
            uploadMR.Driver();//上传大文件
            encodeMR.Driver();//对大文件加密
            decodeMR.Driver();//对大文件解密
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
