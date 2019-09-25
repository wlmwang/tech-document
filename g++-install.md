## 升级gcc-4.8.2
* 源码安装(CentOS) （其他系统请自行升级）
    * 缺少编译环境请先安装老的 gcc
        * $ yum -y install gcc gcc-c++ #kernel-devel
        * yum install make  # 如果没有 $ make -v
* 升级步骤
```
$ cd /usr/local/src
$ wget http://ftp.gnu.org/gnu/gcc/gcc-4.8.2/gcc-4.8.2.tar.bz2
$ tar -jxvf gcc-4.8.2.tar.bz2
$ cd gcc-4.8.2

$ ./contrib/download_prerequisites # 下载供编译需求依赖项

$ cd ..
$ mkdir gcc-build-4.8.2 # 建立一个目录供编译出的文件存放
$ cd gcc-build-4.8.2
$ ../gcc-4.8.2/configure --enable-checking=release --enable-languages=c,c++ --disable-multilib # 生成Makefile文件

$ make -j4 # 极其耗时。-j4选项是make对多核处理器的优化
// 此步骤可能会有 glibc 出错信息。安装以下依赖库
// $ yum -y install glibc-devel.i686 glibc-devel

$ make
// $ yum remove gcc g++ gcc-c++
$ make install

$ gcc --version # 可能要reboot重启生效
$ g++ --version

$ cp stage1-x86_64-unknown-linux-gnu/libstdc++-v3/src/.libs/libstdc++.so.6.0.18 /usr/lib64 # 替换libstdc++运行库
$ ldconfig

$ strings /usr/lib64/libstdc++.so.6 | grep GLIBC
```

