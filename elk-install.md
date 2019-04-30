# 日志集中化方案

## 海量分布式日志集中化管理，一般思路大体上有：
1. 应用服务器记录日志到中央关系型数据库。
2. 应用服务器记录日志到本地磁盘，后台进程监控并发送日志到远程中央服务器（可再经过队列）进行落地（磁盘、搜索引擎等）。
3. 应用服务器请求队列，由队列消费方进行落地（磁盘、搜索引擎等）。
4. 应用服务器请求日志服务接口，由该服务选择选择记录介质（磁盘、搜索引擎等）。
	* 其中第 2、3 方案即为主流的 ELK Stack 技术栈。有稳定、分布式、海量存储、可扩展以及便于检索等优点。
	* 典型的一条日志数据流走向：
		> program -> rsyslog -> file(message.log) -> filebeat -> elasticsearch(localhost:9200) -> kibana

## 安装 ELK 6.0
* https://ken.io/note/elk-deploy-guide
* https://blog.51cto.com/baidu/1676798

## 安装 filebeat
* https://zhuanlan.zhihu.com/p/23049700

## rsyslog 配置
* https://www.karlzhou.com/articles/center-log-with-rsyslog/

## log4php 使用
* https://www.ddhigh.com/2017/04/27/log4php-syslog.html

