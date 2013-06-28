#!/bin/bash
*1. build single column index

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:mid

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:mid -s 20130101 -e 20130120 -v 1

*2. build multi single-column index together

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:mid,cf1:age,cf2:msg

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:mid,cf1:age,cf2:msg -s 20130101 -e 20130120 -v 3

*3. build combined-column index

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:mid,cf1:age,cf2:msg -si false

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:mid,cf1:age,cf2:msg -si false -s 20130101 -e 20130120 -v 1

*4. build json column index. single-field, combined-field index

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:msg -j area,type,category

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:msg -j area,type,category -s 20130101 -e 20130120 -v 1

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:msg -j area,type,category -si false

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c cf1:msg -j area,type,category -si false -s 20130101 -e 20130120 -v 1

*5. build rowkey only index

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c rowkey -r uid:1,mid:2,isrowkey:1

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c rowkey:cf1:content -r uid:1,mid:2,isrowkey:1

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c rowkey:cf1:content -r uid:1,mid:2,isrowkey:1 -s 20130101 -e 20130120

hadoop jar hbase-secondary-index-0.1.jar net.hbase.secondaryindex.mapred.Main -i demo_table -o demo_table_index -c rowkey:cf1:content -r uid:1,mid:2,isrowkey:1 -s 20130101 -e 20130120 -v 1


