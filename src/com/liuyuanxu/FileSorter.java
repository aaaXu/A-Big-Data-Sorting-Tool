package com.liuyuanxu;

import java.io.*;
import java.util.*;

/**
 * @author LiuYuanxu
 * Date 2021.4.10
 */
public class FileSorter {

    File inFile;
    File outDir;
    File sortedDataFile;

    /**
     *
     * @param inFileName 数据文件必须放在和java程序同一个路径中
     * @throws IOException 如果数据文件不存在，该方法会抛出异常
     */
    public FileSorter(String inFileName) throws IOException {

        // 分割文件名，去掉文件后缀。
        String preInFileName = inFileName.split("\\.")[0];

        this.inFile = new File(System.getProperty("user.dir")+File.separator+inFileName);
        if (this.inFile.exists() && this.inFile.isFile()) {
            this.outDir = new File(System.getProperty("user.dir") + File.separator + "outdata");

            if (!outDir.exists()) {
                outDir.mkdir();
            }

            // 创建排序结果文件
            sortedDataFile = new File(System.getProperty("user.dir") + File.separator + "outdata" + File.separator + preInFileName +"_sorted.txt");
            if (sortedDataFile.exists()) {// 如果存在了，先删在创建新的
                sortedDataFile.delete();
                sortedDataFile.createNewFile();
            } else {
                sortedDataFile.createNewFile();
            }


        } else {
            throw new IOException("指定的数据文件不存在！请检查文件名！");
        }
    }


    /**
     *
     * @param size 每个文件包含的数据的个数
     * @return 大文件划分成的小文件的个数，也是小文件对应的队列的个数
     */
    public int split(int size) {
        int totalCount = 0;
        int pageSize = size;
        int pageIndex = -1;

        double doubleData;
        String stringData;

        // 用于存放小文件的数据，内存中。
        List<Double> small = new ArrayList<>(pageSize);// 使用ArrayList是为了使用自带的排序
        BufferedWriter outDataWriter = null;
        File smallFile = null;

        try (BufferedReader sourceDataReader = new BufferedReader(new FileReader(this.inFile));) {
            System.out.println("数据文件打开成功！开始分割文件！");

            while ((stringData = sourceDataReader.readLine()) != null) {
                // 要求一行只有一个数据
                totalCount ++;
                doubleData = Double.parseDouble(stringData);
                small.add(doubleData);

                // 达到一个小文件的数量了，写出到文件
                if (totalCount % pageSize == 0) {
                    pageIndex ++;// 小文件序号从0开始
                    smallFile = new File(this.outDir.getAbsolutePath() + File.separator + pageIndex + ".txt");
                    if (resetFile(smallFile)) {
                        System.out.println("创建第" +pageIndex+ "个小文件，" + "参数量 " + small.size());
                    }

                    outDataWriter = new BufferedWriter(new FileWriter(smallFile, true));

                    small.sort(Comparator.naturalOrder());// 小文件排序

                    for (int i = 0; i < small.size(); i ++) {
                        outDataWriter.write(Double.toString(small.get(i)));
                        outDataWriter.newLine();
                    }

                    outDataWriter.flush();
                    outDataWriter.close();
                    small.clear();
                }
            }

            // 读完文件可能small里面还有一些数量不足pageSize的数据。
            if (!small.isEmpty()) {
                pageIndex ++;
                smallFile = new File(this.outDir.getAbsolutePath() + File.separator + pageIndex + ".txt");
                if (resetFile(smallFile)) {
                    System.out.println("创建第" +pageIndex+ "个小文件，" + "参数量 " + small.size());
                }
                outDataWriter = new BufferedWriter(new FileWriter(smallFile, true));

                small.sort(Comparator.naturalOrder());// 小文件排序

                for (int i = 0; i < small.size(); i ++) {
                    outDataWriter.write(Double.toString(small.get(i)));
                    outDataWriter.newLine();
                }

                outDataWriter.flush();
                outDataWriter.close();
                small.clear();
            }

        }catch(Exception e){
            System.out.println("数据源文件打开失败！");
            e.printStackTrace();
        }

        // System.out.println("分割完成： " + "参数总量" + totalCount + "|每个文件参数量" + pageSize + "|总文件数" + (pageIndex + 1));
        return pageIndex + 1;
    }

    /**
     * 填充一个空的小文件队列
     * @param queIndex 小文件对应的队列序号
     * @param queues 全体队列的引用
     * @param skip 要跳过的字符数
     * @param readCounts 本次要读取的double数据的数量
     * @return 如果此次读到了任何数据，返回true，没读到数据，说明文件读完了，返回false。
     */
    public void addQueue (int queIndex, Map<Integer, Queue<Double>> queues, long[] skip, int readCounts) {

        String stringData;
        Double doubleData;

        File smallFile = new File(outDir.getAbsolutePath() + File.separator + queIndex + ".txt");
        try (BufferedReader bfr = new BufferedReader(new FileReader(smallFile))) {



            int i = 0;
            bfr.skip(skip[queIndex]); // 跳过的字符数,起始为0

            // 读取readCounts 个数据到小文件queIndex对应的队列中
            while ((i < readCounts) && (stringData = bfr.readLine()) != null) {
                i ++;
                doubleData = Double.parseDouble(stringData);
                queues.get(queIndex).offer(doubleData);
                skip[queIndex] += stringData.length() + 2;// 已测试，结尾的\r\n也算两个字符
            }

        } catch (Exception e) {
            System.out.println("addQueue方法 小文件"+ queIndex +"打开失败了");
            e.printStackTrace();
        }
    }

    public boolean resetFile(File file) {

        if (file.exists()) {
            if (file.delete()) {
                try {
                    return file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return false;// 旧文件删除失败
            }

        } else {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    /**
     * 删除中间结果小文件
     * @param pageCount 待删除的小文件数量
     */
    public void deleteSmallFiles(int pageCount) {
        File smallFile = null;
        for (int i = 0; i < pageCount; i ++) {
            smallFile = new File(this.outDir.getAbsolutePath() + File.separator + i + ".txt");
            if (smallFile.exists())
                smallFile.delete();
        }
    }

    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        int totalDataCounts = 0;// 参与排序的数据总量

        try {
            FileSorter fileSorter = new FileSorter(args[0]);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileSorter.sortedDataFile, true));

            // 一个小文件中的数据量，也是一次分割期间一次内存sort的数据量, 1w 约占内存 100M
            int pageSize = 10 * 10000;
            // 小队列一次从文件读取的数据量，小文件数 * queReadCount 等于归并期间内存数据量。pagesize可以大，但是queReadCounts不能太大
            int queReadCounts = 100;
            int pageCount = fileSorter.split(pageSize);// pagecount 也是队列数量。

            Queue<Double> batch = new LinkedList<>();

            // 每个队列在读取文件的时候需要跳过的字符数量
            long[] skip = new long[pageCount];

            // 全体小文件队列
            Map<Integer, Queue<Double>> queues = new HashMap<>(pageCount);

            for (int i = 0; i < pageCount; i ++) {
                queues.put(i, new LinkedList<Double>());
            }


            double headata;
            double minHeadData = Double.MAX_VALUE;
            int queIndexOfMinHead = -1;
            while (!queues.isEmpty()) {// 所有队列都空，结束排序。

                // 一轮for下来，可以找到各个队列中头元素最小的队列。只是看一遍，并没有出队。
                for (int i = 0; i < pageCount; i ++) {
                    // 首先应包含这个队列
                    if (queues.containsKey(i)) {

                        // 如果空，先去小文件里面读
                        if (queues.get(i).isEmpty()) {
                            fileSorter.addQueue(i, queues, skip, queReadCounts);
                        }
                        // 如果读完还是空，说明文件被读完了，移除这条队列
                        if (queues.get(i).isEmpty()) {
                            queues.remove(i);
                            // System.out.println("移除第" +i+ "个小文件队列");
                            continue;
                        }

                        // 能走到这里说明有这个队列且队列不空。即使一开始可能空的但是又去文件里拿到了。
                        headata = queues.get(i).peek();
                        if (headata < minHeadData) {
                            minHeadData = headata;
                            queIndexOfMinHead = i;
                        }
                    }
                }

                // 走到这里，有可能在刚才的循环里，queues移除了全部队列，所以要检查一下。只要queues不空，minHeadData就一定有值。
                if (!queues.isEmpty()) {
                    minHeadData = queues.get(queIndexOfMinHead).poll();// 弹出
                    bufferedWriter.write(Double.toString(minHeadData));// bufferedWritter自带缓冲
                    bufferedWriter.newLine();
                    totalDataCounts ++;
                }
                // 重置，开始下一轮。
                minHeadData = Double.MAX_VALUE;
                queIndexOfMinHead = -1;
            }

            bufferedWriter.flush();
            bufferedWriter.close();
            fileSorter.deleteSmallFiles(pageCount);

            long end = System.currentTimeMillis();
            System.out.println(fileSorter.inFile.toString() + "：" + totalDataCounts + "条数据排序完成,耗时 " + (end - start) + "ms");
            System.out.println("排序结果见：" + fileSorter.sortedDataFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
