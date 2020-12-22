package com.utils;

import java.io.*;

public class FileUtils {
    /**
     * 将文件转化为Byte[]
     *
     * @param path 文件路径
     * @return  若返回空值，说明转化失败
     * @throws IOException
     */
    public static byte[] fileToByte(String path) {
        File file = new File(path);
        if (file.length() > Integer.MAX_VALUE) {
            System.out.println("This file is too big,we can't encode it now,maybe we will fix it in the future");
            return null;
        }

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        byte[] sourceData = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            sourceData = new byte[(int) file.length()];
            int offset = 0;
            int numRead = 0;

            //将文件写入byte中
            while (offset < sourceData.length
                    && (numRead = bis.read(sourceData, offset, sourceData.length - offset)) >= 0) {
                offset += numRead;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null)
                    bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sourceData;
    }

    /**
     * 将byte[]转化为文件
     *
     * @param path   文件路径
     * @param source
     */
    public static void byteToFile(String path, byte[] source) {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fos = new FileOutputStream(path);
            bos = new BufferedOutputStream(fos);
            bos.write(source);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null)
                    bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
