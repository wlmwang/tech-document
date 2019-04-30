# 安装 Vagrant(windows)

## 安装参考
* https://www.cnblogs.com/vishun/archive/2017/06/02/6932454.html
* https://www.cnblogs.com/davenkin/p/vagrant-virtualbox.html

## 常用命令
```
$ vagrant halt # 关闭虚拟机
$ vagrant reload # 重启虚拟机
$ vagrant destroy # 销毁当前虚拟机（即virtualbox虚拟机被删除）

$ vagrant box add {title} {url} # 添加本地box
$ vagrant box add my-box D:/boxes/centos65-x86_64-20140116.box 
$ vagrant box add ubuntu/trusty64 # 添加下载远程的box
$ vagrant box list
$ vagrant box remove {box-name} # 删除box-name镜像

$ vagrant init {box-name} # 初始化box-name
$ vagrant up # 启动虚拟机
$ vagrant reload [--provision] # 重启（运行provision）
$ vagrant provision # 运行provision
$ vagrant status # 查看虚拟机运行状态
$ vagrant destroy # 销毁当前虚拟机

$ vagrant ssh # SSH 至虚拟机
```
