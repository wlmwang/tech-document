# 安装 Vagrant(windows)

## 安装参考
* https://www.cnblogs.com/vishun/archive/2017/06/02/6932454.html
* https://www.cnblogs.com/davenkin/p/vagrant-virtualbox.html

## 常用命令
```
三步走：
$ vagrant box add {title} {url} # 添加 box
$ vagrant init {title} # 初始化 box
$ vagrant up # 启动box
```

```
* vagrant box add {title} {url} # 添加 box
$ vagrant box add my-box D:/boxes/centos65-x86_64-20140116.box 
$ vagrant box add ubuntu/trusty64 # 添加下载远程的box（从Hashicorp官网上下载 ubuntu/trusty64 这个box）

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

## 配置
#### 端口转发（Port Forwarding）
* 在默认情况下，Vagrant 所创建的 Virtualbox 虚拟机使用的是 NAT 网络类型，即，外界是不能直接访问你的虚拟机的，就连 Host 机器也访问不了。
```
Vagrant.configure("2") do |config|
  config.vm.network "forwarded_port", guest: 8080, host: 8888
end
```
* 将 Host 机的 8888 端口转发到了虚拟机的 8080 端口，这样你便可以通过在 Host 机上访问 http://localhost:8888 来访问虚拟机的 Tomcat 了。
  * 对于 Virtualbox 来说，只有 NAT 类型的网络类型支持端口转发，这也是为什么 Vagrant 创建的 Virtualbox 虚拟机默认都有一个支持 NAT 的虚拟网卡，原因就是要能够支持 Vagrant 级别的端口转发。另外，Vagrant 在第一次尝试连接虚拟机时使用的也是 NAT。

#### 网络配置
* 在默认情况下，对于 Virtualbox 而言，Vagrant 将使用 Virtualbox 的 NAT 网络方式，这种方式允许虚拟机访问外部网络，但是不允许外界访问虚拟机，就连Host 机器也访问不了。
* 我们可以为虚拟机配置 private network 和 public network。在配置 private network 时，相当于虚拟机和 Host 机共同组成了一个单独的局域网，外界无法访问该局域网，但是虚拟机可以访问外界，Host机和虚拟机之间也可以互访。请注意，这里说的外界是指原本和Host处于同一局域网的其他机器。
  * 对于Virtualbox而言，此时虚拟机其实有两张网卡在工作，一种是Vagrant默认创建的NAT网卡，另一种是Host only类型的网卡提供private network。
```
使用 private network 时，我们可以给虚拟机指定固定的私有IP：
Vagrant.configure("2") do |config|
  config.vm.network "private_network", ip: "192.168.50.4"
end

当然也可以使用DHCP的方式动态分配IP：
Vagrant.configure("2") do |config|
  config.vm.network "private_network", type: "dhcp"
end
```

* 在使用 public network 时，虚拟机和 Host 在网络中具有同等的地位（共同使用 Host 机的物理网卡与外界通信），就相当于在 Host 所在网络中又多了一台计算机一样，此时虚拟机可以使用网络中的 DHCP 服务器获得与 Host 处于同一个网段的IP地址
```
配置默认采用 DHCP 方式配置public network：
Vagrant.configure("2") do |config|
  config.vm.network "public_network"
end
```

* 如果Host机器有多张网卡，此时运行vagrant up， Vagrant会询问需要使用那张网卡连接到网络，如果不想要这种交互，则可以在Vagrantfile中进行配置：
```
这里的 Wi-Fi(AirPort)表示使用了Mac笔记本的Airport连接到Wi-Fi
config.vm.network "public_network", bridge: [
  "en1: Wi-Fi (AirPort)",
]
```

* 除了DHCP，也可以使用静态IP：
```
Vagrant.configure("2") do |config|
  config.vm.network "public_network", ip: "192.168.0.5"
end
```

#### 共享文件夹
* 在默认情况下，Vagrant所创建的虚拟机已经为我们创建了一个共享文件夹，在虚拟机上是 /vagrant 目录，在 Host 机上则为 Vagrantfile 所在目录，当然你也可以额外添加另外的共享文件夹：
```
Vagrant.configure("2") do |config|
  config.vm.synced_folder "src/", "/srv/website"
end
第一个参数为 Host 机器上的目录，第二个参数为虚拟机上的目录
```

