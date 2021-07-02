package com.liuyuanxu;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

public class TransFormater {

    public static void transFormater(String[] args) {

        // 使用之前请修改对应的层号id，注意和你的txt文件名匹配。
        String calculateFloor = "c5";

        // 输入文件位置,注意输入文件必须是txt文件
        File dataFile = new File("G:\\Download\\cnn参数\\LeNet C语言\\param data\\"+calculateFloor.toUpperCase(Locale.ROOT)+"_param.txt");
        // 输出文件的位置,注意是 .csv文件才方便导入到阿里云数据库
        File resultFile = new File("G:\\Download\\cnn参数\\LeNet C语言\\param data\\格式化的数据\\"+calculateFloor.toUpperCase(Locale.ROOT)+"_param.csv");

        // 从文件名提取对应层的数字编号
        String floorId = calculateFloor.substring(1);

        Double doubleData;
        String dataLine;
        String[] stringDatas;

        try {
            if(resultFile.createNewFile()) {
                System.out.println("文件创建成功！");
            } else {
                System.out.println("文件创建失败，请先删除旧文件");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        try (BufferedReader bfr = new BufferedReader(new FileReader(dataFile));
        BufferedWriter bfw = new BufferedWriter(new FileWriter(resultFile,true))) {
            while ((dataLine = bfr.readLine()) != null) {

                stringDatas = dataLine.split(" ");

                for (String a : stringDatas) {
                    bfw.write(a);
                    //bfw.write(",");
                    //bfw.write(floorId);
                    bfw.newLine();
                    //doubleData = Double.parseDouble(a);
                    //System.out.println(doubleData);
                }
                bfw.flush();// 一行刷新一次
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
