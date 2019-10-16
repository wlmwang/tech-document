## 安装 docker-ce（Docker Community Edition，docker 社区版）
```
官方安装文档：
https://docs.docker.com/install/linux/docker-ce/ubuntu/
https://docs.docker.com/install/linux/docker-ce/centos/
```

```
注意: 本镜像只提供 Debian/Ubuntu/Fedora/CentOS/RHEL 的 docker 软件包，非 dockerhub
https://mirror.tuna.tsinghua.edu.cn/help/docker-ce/
https://www.jianshu.com/p/c76c1ab6d6db
```

#### Debian/Ubuntu 用户
* 如果你过去安装过 docker，先删掉:
```
$ sudo apt-get remove docker docker-engine docker.io
```

* 首先安装依赖:
```
$ sudo apt-get install apt-transport-https ca-certificates curl gnupg2 software-properties-common
```

* 信任 Docker 的 GPG 公钥（根据你的发行版，下面的内容有所不同）:
```
1. ubuntu: 
    $ curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
2. debian
    $ curl -fsSL https://download.docker.com/linux/debian/gpg | sudo apt-key add -
```

* 对于 amd64 架构的计算机，添加软件仓库（根据你的发行版，下面的内容有所不同）:
```
1. ubuntu
    $ sudo add-apt-repository \
       "deb [arch=amd64] https://mirrors.tuna.tsinghua.edu.cn/docker-ce/linux/ubuntu \
       $(lsb_release -cs) \
       stable"
2. debian
    $ sudo add-apt-repository \
       "deb [arch=amd64] https://mirrors.tuna.tsinghua.edu.cn/docker-ce/linux/debian \
       $(lsb_release -cs) \
       stable"
```

* 最后安装
```
$ sudo apt-get update  # 更新apt-get源缓存
$ sudo apt-get install docker-ce
```

#### Fedora/CentOS/RHEL
* 如果你过去安装过 docker，先删掉:
```
$ sudo yum remove docker docker-common docker-selinux docker-engine
```

* 首先安装依赖:
```
$ sudo yum install -y yum-utils device-mapper-persistent-data lvm2
```

* 下载 repo 文件（根据你的发行版，下面的内容有所不同）:
```
1. CentOS/RHEL: 
    $ wget -O /etc/yum.repos.d/docker-ce.repo https://download.docker.com/linux/centos/docker-ce.repo
2. fedora
    $ wget -O /etc/yum.repos.d/docker-ce.repo https://download.docker.com/linux/fedora/docker-ce.repo
```

* 把软件仓库地址替换为 TUNA:
```
$ sudo sed -i 's+download.docker.com+mirrors.tuna.tsinghua.edu.cn/docker-ce+' /etc/yum.repos.d/docker-ce.repo
```

* 最后安装
```
sudo yum makecache fast # 更新yum源缓存
sudo yum install docker-ce
```

## 常见错误
### docker启动报错。docker: Error response from daemon: OCI runtime create failed: container_linux.go:348
> 原因：docker的版本和linux的内核版本不兼容

#### 方案一
* 升级 linux 内核，执行下列命令（注意：更新了内核后，需要重启系统）
    > apt-get install --install-recommends linux-generic-lts-xenial

#### 方案二
* 把 docker 降低到稳定的 17 版本
```
Debian/Ubuntu
* 卸载 docker-ce
$ apt-get autoremove docker-ce

* 显示稳定可使用版本
$ apt-cache madison docker-ce

* 安装稳定版本
$ sudo apt-get install docker-ce=17.12.1~ce-0~ubuntu
```

```
CentOS/RHEL: 
* 卸载 docker-ce
$ sudo yum remove docker-ce
$ sudo yum remove docker docker-common docker-selinux docker-engine

* 首先安装依赖:
$ sudo yum install -y yum-utils device-mapper-persistent-data lvm2

* 设置稳定的存储库（国内 yum 源镜像）
$ sudo yum-config-manager \
    --add-repo \
    https://mirrors.ustc.edu.cn/docker-ce/linux/centos/docker-ce.repo

* 更新 yum 源缓存
$ sudo yum makecache fast

* 安装docker
  > 注意不要用安装最新版本，采的坑命令如下（会导致之后docker跑不起来）：
  > $ sudo yum install docker-ce

  报错的原因：
  docker: Error response from daemon: OCI runtime create failed: unable to retrieve OCI runtime error (open 
  /run/docker/containerd/daemon/io.containerd.runtime.v1.linux/moby/262f67d9beb653ac60b1c7cb3b2e183d7595b4a4a93f0dcfb0ce689a588cedcd/log.json: 
  no such file or directory): docker-runc did not terminate sucessfully: unknown.
  ERRO[0000] error waiting for container: context canceled

# 正确的，应该选用一个可用的版本：
* 查看当前可用稳定安装包
$ yum list docker-ce --showduplicates | sort -r

* 选择一个版本
$ sudo yum install docker-ce-17.06.2.ce

```

```
* 测试、启动 docker
$ systemctl enable docker
$ systemctl start docker
$ docker run hello-world
```

```
# 卸载docker
$ sudo yum remove docker-ce-17.06.2.ce

* 主机上的图像，容器，卷或自定义配置文件不会自动删除。删除所有图像，容器和卷。
$ rm -rf /var/lib/docker

```

