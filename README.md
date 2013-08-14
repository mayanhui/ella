<h1>Ella: HBase Cluster Monitor on Region,Table,Server Level.<br />
<em><sup><sup>'A Watchdog on HBase'</sup></sup></em></h1>

###More details, see wiki page(will include chinese wiki 中文使用手册，NOT AVAILABLE NOW！): 
https://github.com/mayanhui/ella/wiki

###################################################
## Ella ############
###################################################
Ella is a "Border Collie", a handsome and smart dog.

A watchdog, monitor hbase.

Watching list:

1. Table level request statistics (Write/Read/Total Count, TPS)
2. Region level request statistics (Hot Region)
3. Server level request statistics (Write/Read/Total Count, TPS, Hot Server)
4. zookeeper monitor
5. Chart display
6. master attribute(hbase version,author,...hadoop version...)
7. master and RPC statistics
8. Account system for login


##Environment
Only Support Linux.

## Framwork
* JDK 1.6 and above(http://www.oracle.com/technetwork/java/index.html)
* Jetty 8.1.11.v20130520 (http://www.eclipse.org/jetty/)
* HBase 0.94.0 (http://hbase.apache.org/)
* Hadoop 1.1.2 (http://hadoop.apache.org/)
* Maven 2.2.1 (http://maven.apache.org/)
* MySQL 5.5.29 (http://www.mysql.com/)
* JMX (http://zh.wikipedia.org/wiki/JMX)
* FastJson (https://github.com/alibaba/fastjson)
* Quartz 1.5 (http://www.quartz-scheduler.org/)
* D3 (https://github.com/mbostock/d3)
* gRaphael (http://g.raphaeljs.com/)

## Notice
<b>You must not install Ella on HBase Master machine.</b>

##How to Use
Please see:
https://github.com/mayanhui/ella/wiki/How-to-Use


## Roadmap
1. <b>Simple install and deploy steps. Change the index page to configuration page, input hbase jmx url and other configure.</b>
2. <b>Add detail page for server,region</b>
3. implement easy install script

## Ella First Page

![Ella Login Page](http://static.oschina.net/uploads/space/2013/0814/181852_duF0_818358.jpg)


![Ella First Page](http://static.oschina.net/uploads/space/2013/0720/142647_s5dz_818358.jpg)


##License
Released under the GPLv3 license. For full details, pleasesee the LICENSE file included in this distribution.


