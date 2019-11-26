# 安装 Vagrant(windows)

## 安装参考
* https://www.cnblogs.com/vishun/archive/2017/06/02/6932454.html
* https://www.cnblogs.com/davenkin/p/vagrant-virtualbox.html

## 常用命令
```
$ vagrant box add {title} {url} # 添加 box
$ vagrant init {title} # 初始化 box
$ vagrant up # 启动box
```

```
* vagrant box add {title} {url} # 添加 box
$ vagrant box add my-box D:/boxes/centos65-x86_64-20140116.box 
$ vagrant box add ubuntu/trusty64 # 添加下载远程的box

* vagrant init {title} # 初始化 box
$ vagrant init my-box

* vagrant box remove {title} # 删除box-name镜像
$ vagrant box remove my-box

$ vagrant up # 启动虚拟机

$ vagrant box list
$ vagrant halt # 关闭虚拟机
$ vagrant reload # 重启虚拟机
$ vagrant destroy # 销毁当前虚拟机（即virtualbox虚拟机被删除）

$ vagrant reload [--provision] # 重启（运行provision）
$ vagrant provision # 运行provision
$ vagrant status # 查看虚拟机运行状态
$ vagrant destroy # 销毁当前虚拟机

$ vagrant ssh # SSH 至虚拟机
```
