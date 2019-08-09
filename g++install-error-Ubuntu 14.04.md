## Ubuntu 14.04 install g++ 问题

### 安装 g++ 时出现错误
```
$ sudo apt-get install g++-4.8

The following packages have unmet dependencies:
g++-4.8 : Depends: g++ : Depends: g++-4.8 (>= 4.8.2-5~) but it is not going to be installed
          Depends: libstdc++-4.8-dev (= 4.8.4-2ubuntu1~14.04) but it is not going to be installed
E: Unable to correct problems, you have held broken packages.
```

### 问题分析
这个问题是因为 Ubuntu 14.04 的源过旧或不可访问导致，可以通过更新源解决

### 解决
* 备份原始源文件 source.list
> $ sudo cp /etc/apt/sources.list /etc/apt/sources.list.backup

* 依据 Ubuntu 的版本添加新的源
> $ lsb_release -a
```
No LSB modules are available.
Distributor ID: Ubuntu
Description:    Ubuntu 14.04.5 LTS
Release:        14.04
Codename:       trusty
```

### 依据版本号如 14.04 trusty 选择新的源文件，添加到 /etc/apt/source.list 文件尾部
* 选择 Ubuntu 官方更新服务器（欧洲，此为官方源，国内较慢，但无同步延迟问题，电信、移动/铁通、联通等公网用户可以使用)：
```
deb http://archive.ubuntu.com/ubuntu/ trusty main restricted universe multiverse
deb http://archive.ubuntu.com/ubuntu/ trusty-security main restricted universe multiverse
deb http://archive.ubuntu.com/ubuntu/ trusty-updates main restricted universe multiverse
deb http://archive.ubuntu.com/ubuntu/ trusty-proposed main restricted universe multiverse
deb http://archive.ubuntu.com/ubuntu/ trusty-backports main restricted universe multiverse
deb-src http://archive.ubuntu.com/ubuntu/ trusty main restricted universe multiverse
deb-src http://archive.ubuntu.com/ubuntu/ trusty-security main restricted universe multiverse
deb-src http://archive.ubuntu.com/ubuntu/ trusty-updates main restricted universe multiverse
deb-src http://archive.ubuntu.com/ubuntu/ trusty-proposed main restricted universe multiverse
deb-src http://archive.ubuntu.com/ubuntu/ trusty-backports main restricted universe multiverse
```

* 更新 ubuntu 源
> $ sudo apt-get update

* 再次安装 g++
> $ sudo apt-get install g++-4.8
> > 虽然这时还能看到部分源的包不能下载，但没关系，系统会自动的从新添加的源下载
