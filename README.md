###一种大数据外部排序（硬盘排序）工具###

####简介：####对超大规模的数据进行排序，由于内存容量的限制，内存中一次性装不下全部的数据，
也就无法在内存中将这些数据一次性排序。硬盘排序（外部排序）就可以解决这个问题。

我写的这个工具是用来对海量的浮点数进行硬盘排序的。如果你手头有大量的浮点数（数据保存在硬盘文件中）
而且你觉得犯不着使用排个序就搭建一个Hadoop大数据框架，那就可以用我这个工具。

####原理：####
1 将原始的大数据文本文件，先拆分成很多的小文件存放在磁盘中
2 对每个小文件进行一次性的内部排序，将有序的小文件再放回磁盘
3 由于每个小文件都是有序的，接下来我们只需要将这些小文件合并成一个大文件就行了。合并的方法是查看
  每个小文件中的第一个数，取其中最小的数存入最终结果的大文件中。直至所有小文件的数据都被合并到大文
  件中。
4 最终得到的大文件就是有序的。

------------------------------------------------------------------------
###A kind of hard disk sorting (external sorting) tool###

####Introduction: ####For sorting data of very large scale, due to the limitation of memory capacity, the memory can not hold all the data at one time, so it is impossible to sort these data in memory at one time.
Disk sort (external sort) can solve this problem.

I wrote this tool to sort a large number of floating-point numbers on disk.
If you have a lot of floating-point numbers (data stored in hard disk files) and you feel like you don't need to build a Hadoop big data framework using sorting, this is a great tool to use.

####The principle of the tool is: ####
1 large data of the original text files, first split into many small files stored in the disk 
2 for each small files for one-off internal sorting, will return to orderly small file disk 
3 because each small file is orderly, and then we just need to these little merged into one big file.
The method of merging is to look at the first number in each small file and take the smallest number and store it in the larger file of the final result.
Until all the data from the smaller files is merged into the larger file.
4 The result is a large file that is ordered.
