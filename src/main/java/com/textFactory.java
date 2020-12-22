package com;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 建立一个大文件txt
 */
public class textFactory {
    public static void textBuilder() throws IOException {
        String path = "G:\\haha.txt";
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path));
        for (int i = 0; i < 15000000; i++) {
            bufferedWriter.append(i + " this is big file");
            if (i % 100 == 0) {
                bufferedWriter.append("\n");
            }
        }
        bufferedWriter.close();
    }

    public static void main(String[] args) {
        try {
            textBuilder();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
