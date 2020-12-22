package com;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * 建立一个大文件txt
 */
public class textFactory {
    public static void main(String[] args) {
        String path = "G:\\haha.txt";
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path));
            for (int i = 0; i < 15000000; i++) {
                bufferedWriter.append(i + " this is big file");
                if (i % 100 == 0) {
                    bufferedWriter.append("\n");
                }
            }
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
