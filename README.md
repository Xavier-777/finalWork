# finalWork
### 作业内容：

1.随机生成大文本文件（以行方式存储），文件存储HDFS中，并将文件信息写入HBase

2.选择适用的加密算法（SM/AES）对生成的文件进行加密操作，密钥长度128位，加密后的文件存储HDFS中，密钥写入HBase

3.从HBase中读取相应的文件名和密钥，对文件进行解密操作，解密后的文件存储HDFS中

4.比较初始文件与解密后的文件内容

5.统计操作总时长及各操作步的时长

说明：

1.所有操作须在小组分布式Hadoop环境中运行

2.以附件方式提交相应的实验文档、演示PPT、源代码

3.最后一周进行现场考核，选定组员进行PPT讲解和程序运行

<br/>

### 代码解析：
1.hadoop中的ftp、hbase中的DBUtils、uploadMR、encodeMR、decodeMR用都需要修改回自己的ip以及自己的hdfs路径

2.hbase的表是在hbase这个包的hbaseExample下，即第17行，要最最最最先运行这创建表

3.执行顺序：1、textFactory 2、uploadMR 3、encodeMR 4、decodeMR

4.hadoop包存放hadoop的代码，hbase包存放hbase的代码，utils包存放SM2的代码，test包是测试代码