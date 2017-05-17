<h1>Ella: HBase Cluster Monitor on Region,Table,Server Level.<br />
<em><sup><sup>'A Watchdog on HBase'</sup></sup></em></h1>

###More details, see wiki page(will include chinese wiki 中文使用手册，NOT AVAILABLE NOW！): 
https://github.com/mayanhui/ella/wiki

####################
###### Ella ########
####################


Ella is a "Border Collie", a handsome and smart dog.

A watchdog, monitor hbase.

Keyt Feature list:

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
* JDK 1.8 (http://www.oracle.com/technetwork/java/index.html)
* Jetty 8.1.11.v20130520 (http://www.eclipse.org/jetty)
* HBase 1.2.5 (http://hbase.apache.org)
* Hadoop 2.5.2 (http://hadoop.apache.org)
* MySQL 5.5.29 (http://www.mysql.com)

* Maven 3.2.3 (http://maven.apache.org)
* Quartz 1.5 (http://www.quartz-scheduler.org)

## Notice
<b>You must not install Ella on HBase Master machine.</b>

<b>Supported Browsers</b>

NVD3 runs best on WebKit based browsers.

* Google Chrome: latest version (preferred)
* Opera 15+ (preferred)
* Safari: latest version
* Firefox: latest version
* Internet Explorer: 9 and 10

##How to Use
Please see:
https://github.com/mayanhui/ella/wiki/How-to-Use


## Roadmap

<b>1. Table request details page:</b>
(1) add split page for total/write/read
(2) add periodic statistics with hour/day/week/month

1. Simple install and deploy steps. Change the index page to configuration page, input hbase jmx url and other configure.
2. Add detail page for server,region
3. Implement easy install script

## Ella First Page

![Ella Login Page](http://static.oschina.net/uploads/space/2013/0814/181852_duF0_818358.jpg)


![Ella First Page](http://static.oschina.net/uploads/space/2013/0720/142647_s5dz_818358.jpg)


##License
Released under the GPLv3 license. For full details, pleasesee the LICENSE file included in this distribution.


