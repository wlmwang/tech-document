
# 简介
* 在客户端我们可以用 PhotoShop 等 GUI 工具处理静态图片或者动态 GIF 图片。不过在服务器端对于 WEB 应用程序要处理图片格式转换，缩放裁剪，翻转扭曲，PDF解析等操作，GUI软件就很难下手了，所以此处需要召唤命令行工具来帮我们完成这些事。

* ImageMagick: 是一款创建、编辑、合成，转换图像的命令行工具。支持格式超过 200 种，包括常见的 PNG, JPEG, GIF, HEIC, TIFF, DPX, EXR, WebP, Postscript, PDF, SVG 等。功能包括调整，翻转，镜像(mirror)，旋转，扭曲，修剪和变换图像，调整图像颜色，应用各种特殊效果，或绘制文本，线条，多边形，椭圆和贝塞尔曲线等。

* 基本用法
ImageMagick 包括一组命令行工具来操作图片，安装好 ImageMagick 后，终端就可以使用如下命令了。
```
magick: 创建、编辑图像，转换图像格式，以及调整图像大小、模糊、裁切、除去杂点、抖动 ( dither )、绘图、翻转、合并、重新采样等。
convert: 等同于 magick 命令。
identify: 输出一个或多个图像文件的格式和特征信息，如分辨率、大小、尺寸、色彩空间等。
mogrify: 与 magick 功能一样，不过不需要指定输出文件，自动覆盖原始图像文件。
composite: 将一个图片或多个图片组合成新图片。
montage: 组合多个独立的图像来创建合成图像。每个图像都可以用边框，透明度等特性进行装饰。

compare: 从数学和视觉角度比较源图像与重建图像之间的差异。
display: 在任何 X server 上显示一个图像或图像序列。
animate: 在任何 X server 上显示图像序列。
import: 保存 X server 上的任何可见窗口并把它作为图像文件输出。可以捕捉单个窗口，整个屏幕或屏幕的任意矩形部分。
conjure: 解释并执行 MSL ( Magick Scripting Language ) 写的脚本。
stream: 一个轻量级工具，用于将图像或部分图像的一个或多个像素组件流式传输到存储设备。在处理大图像或原始像素组件时很有用。
```

# 系统环境
```
$ cat /etc/*release
CentOS Linux release 7.3.1611 (Core) 
```

# 编译环境 4.8+
```
$ yum install -y gcc-c++
```

# yum 源公共包
```
$ yum install -y libtool-ltdl-devel.x86_64 bzip2-devel.x86_64
```

# 依赖包安装
* 源码包列表
	* http://www.imagemagick.org/download/delegates/
* 附录中介绍 yum 安装方式


## 安装 freetype
```
$ wget https://www.imagemagick.org/download/delegates/freetype-2.8.1.tar.gz
$ tar -zxvf freetype-2.8.1.tar.gz
$ cd freetype-2.8.1/
$ ./autogen.sh
$ ./configure --prefix=/opt/web/phpc/module/freetype
$ make
$ make install
$ export CPPFLAGS="${CPPFLAGS} -I/opt/web/phpc/module/freetype/include"
$ export LDFLAGS="${LDFLAGS} -L/opt/web/phpc/module/freetype/lib"
$ export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}:/opt/web/phpc/module/freetype/lib/pkgconfig/"
```

## 安装 jpeg
```
$ wget https://www.imagemagick.org/download/delegates/jpegsrc.v9b.tar.gz
$ tar zxvf jpegsrc.v9b.tar.gz
$ cd jpeg-9b/
$ ./configure --enable-shared --enable-static --prefix=/opt/web/phpc/module/jpeg
$ make
$ make install
$ export CPPFLAGS="${CPPFLAGS} -I/opt/web/phpc/module/jpeg/include"
$ export LDFLAGS="${LDFLAGS} -L/opt/web/phpc/module/jpeg/lib"
```

## 安装 tiff
```
$ wget https://www.imagemagick.org/download/delegates/tiff-4.0.8.tar.gz
$ tar -zxvf tiff-4.0.8.tar.gz
$ cd tiff-4.0.8/
$ ./autogen.sh
$ ./configure --prefix=/opt/web/phpc/module/tiff
$ make
$ make install
$ export CPPFLAGS="${CPPFLAGS} -I/opt/web/phpc/module/tiff/include"
$ export LDFLAGS="${LDFLAGS} -L/opt/web/phpc/module/tiff/lib"
$ export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}:/opt/web/phpc/module/tiff/lib/pkgconfig/"
```

## 安装 libpng
```
$ wget https://www.imagemagick.org/download/delegates/libpng-1.6.31.tar.gz
$ tar -zxvf libpng-1.6.31.tar.gz
$ cd libpng-1.6.31/
$ ./configure --prefix=/opt/web/phpc/module/libpng --enable-maintainer-mode
$ make
$ make install
$ export CPPFLAGS="${CPPFLAGS} -I/opt/web/phpc/module/libpng/include"
$ export LDFLAGS="${LDFLAGS} -L/opt/web/phpc/module/libpng/lib"
$ export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}:/opt/web/phpc/module/libpng/lib/pkgconfig/"
```

## 安装 libde265
```
$ wget https://github.com/strukturag/libde265/releases/download/v1.0.3/libde265-1.0.3.tar.gz
$ tar -zxvf libde265-1.0.3.tar.gz
$ cd libde265-1.0.3/
$ ./autogen.sh
$ ./configure --prefix=/opt/web/phpc/module/libde265
$ make
$ make install
$ export CPPFLAGS="${CPPFLAGS} -I/opt/web/phpc/module/libde265/include"
$ export LDFLAGS="${LDFLAGS} -L/opt/web/phpc/module/libde265/lib"
$ export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}:/opt/web/phpc/module/libde265/lib/pkgconfig/"
```

## 安装 libheif
```
$ wget https://github.com/strukturag/libheif/releases/download/v1.5.1/libheif-1.5.1.tar.gz
$ tar -zxvf libheif-1.5.1.tar.gz
$ cd libheif-1.5.1/
$ ./autogen.sh
$ ./configure --prefix=/opt/web/phpc/module/libheif
$ make
$ make install
$ export CPPFLAGS="${CPPFLAGS} -I/opt/web/phpc/module/libheif/include"
$ export LDFLAGS="${LDFLAGS} -L/opt/web/phpc/module/libheif/lib"
$ export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}:/opt/web/phpc/module/libheif/lib/pkgconfig/"
```

## 安装 libwmf
```
$ wget https://www.imagemagick.org/download/delegates/libwmf-0.2.8.4.tar.gz
$ tar -zxvf libwmf-0.2.8.4.tar.gz
$ cd libwmf-0.2.8.4/
$ ./configure --prefix=/opt/web/phpc/module/libwmf
$ make
$ make install
$ export CPPFLAGS="${CPPFLAGS} -I/opt/web/phpc/module/libwmf/include"
$ export LDFLAGS="${LDFLAGS} -L/opt/web/phpc/module/libwmf/lib"
```

## 安装 libwebp
```
$ wget https://www.imagemagick.org/download/delegates/libwebp-0.6.0.tar.gz
$ tar -zxvf libwebp-0.6.0.tar.gz
$ cd libwebp-0.6.0/
$ ./autogen.sh
$ ./configure --prefix=/opt/web/phpc/module/libwebp
$ make
$ make install
$ export CPPFLAGS="${CPPFLAGS} -I/opt/web/phpc/module/libwebp/include"
$ export LDFLAGS="${LDFLAGS} -L/opt/web/phpc/module/libwebp/lib"
$ export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}:/opt/web/phpc/module/libwebp/lib/pkgconfig/"
```

## 安装 openjp2
```
$ wget https://www.imagemagick.org/download/delegates/openjpeg-2.3.0.tar.gz
$ tar -zxvf openjpeg-2.3.0.tar.gz
$ cd openjpeg-2.3.0/
$ mkdir build/
$ cd build/
$ cmake .. -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX=/opt/web/phpc/module/openjpeg
$ make
$ make install
$ export CPPFLAGS="${CPPFLAGS} -I/opt/web/phpc/module/openjpeg/include"
$ export LDFLAGS="${LDFLAGS} -L/opt/web/phpc/module/openjpeg/lib"
$ export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}:/opt/web/phpc/module/openjpeg/lib/pkgconfig/"
```

## 构建编译环境
```
$ export CPPFLAGS="${CPPFLAGS} -I/opt/web/phpc/module/freetype/include"
$ export LDFLAGS="${LDFLAGS} -L/opt/web/phpc/module/freetype/lib"
$ export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}:/opt/web/phpc/module/freetype/lib/pkgconfig/"

$ export CPPFLAGS="${CPPFLAGS} -I/opt/web/phpc/module/jpeg/include"
$ export LDFLAGS="${LDFLAGS} -L/opt/web/phpc/module/jpeg/lib"

$ export CPPFLAGS="${CPPFLAGS} -I/opt/web/phpc/module/png/include"
$ export LDFLAGS="${LDFLAGS} -L/opt/web/phpc/module/png/lib"
$ export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}:/opt/web/phpc/module/png/lib/pkgconfig/"

$ export CPPFLAGS="${CPPFLAGS} -I/opt/web/phpc/module/tiff/include"
$ export LDFLAGS="${LDFLAGS} -L/opt/web/phpc/module/tiff/lib"
$ export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}:/opt/web/phpc/module/tiff/lib/pkgconfig/"

$ export CPPFLAGS="${CPPFLAGS} -I/opt/web/phpc/module/libde265/include"
$ export LDFLAGS="${LDFLAGS} -L/opt/web/phpc/module/libde265/lib"
$ export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}:/opt/web/phpc/module/libde265/lib/pkgconfig/"

$ export CPPFLAGS="${CPPFLAGS} -I/opt/web/phpc/module/libheif/include"
$ export LDFLAGS="${LDFLAGS} -L/opt/web/phpc/module/libheif/lib"
$ export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}:/opt/web/phpc/module/libheif/lib/pkgconfig/"

$ export CPPFLAGS="${CPPFLAGS} -I/opt/web/phpc/module/libwmf/include"
$ export LDFLAGS="${LDFLAGS} -L/opt/web/phpc/module/libwmf/lib"

$ export CPPFLAGS="${CPPFLAGS} -I/opt/web/phpc/module/libwebp/include"
$ export LDFLAGS="${LDFLAGS} -L/opt/web/phpc/module/libwebp/lib"
$ export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}:/opt/web/phpc/module/libwebp/lib/pkgconfig/"

$ export CPPFLAGS="${CPPFLAGS} -I/opt/web/phpc/module/openjpeg/include"
$ export LDFLAGS="${LDFLAGS} -L/opt/web/phpc/module/openjpeg/lib"
$ export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}:/opt/web/phpc/module/openjpeg/lib/pkgconfig/"
```

# 安装 ImageMagick
```
$ wget https://www.imagemagick.org/download/ImageMagick.tar.gz
$ tar -xzvf ImageMagick.tar.gz
$ cd ImageMagick-7.0.9-2/
$ ./configure --prefix=/opt/web/phpc/module/imagemagick-7.9 --with-modules
$ make -j4
$ make install
```

## 验证 ImageMagick-7.9
```
$ cd /opt/web/phpc/module/imagemagick-7.9/bin
$ ./identify -list format |grep JPEG
	see part 5 which describes the image encoding (RLE, JPEG, JPEG-LS),
	and supplement 61 which adds JPEG-2000 encoding.
	JNG* PNG       rw-   JPEG Network Graphics
	JPE* JPEG      rw-   Joint Photographic Experts Group JFIF format (libjpeg 80)
	JPEG* JPEG      rw-   Joint Photographic Experts Group JFIF format (libjpeg 80)
	JPG* JPEG      rw-   Joint Photographic Experts Group JFIF format (libjpeg 80)
	JPS* JPEG      rw-   Joint Photographic Experts Group JFIF format (libjpeg 80)
	PGX* PGX       rw-   JPEG 2000 uncompressed format
	PJPEG* JPEG      rw-   Joint Photographic Experts Group JFIF format (libjpeg 80)
$ ./identify -list format |grep GIF
	GIF* GIF       rw+   CompuServe graphics interchange format
	GIF87* GIF       rw-   CompuServe graphics interchange format (version 87a)
$ ./identify -list format |grep WEBP
	WEBP* WEBP      rw-   WebP Image Format (libwebp 0.3.0 [0201])
$ ./identify -list format |grep HEIC
	HEIC* HEIC      rw-   High Efficiency Image Format
```

# 安装 PHP 扩展 imagick
$ wget http://pecl.php.net/get/imagick-3.4.4.tgz
$ tar -zxvf imagick-3.4.4.tgz
$ cd imagick-3.4.4/
$ /usr/bin/phpize
$ ./configure --with-php-config=/usr/bin/php-config --with-imagick=/opt/web/phpc/module/imagemagick-7.9
$ make
$ make install
$ php --ri imagick


# 附录
```
$ yum install -y libtool-ltdl-devel.x86_64 bzip2-devel.x86_64 glib2-devel.x86_64 zlib-devel.x86_64 freetype-devel.x86_64
$ yum install -y libjpeg-devel.x86_64 libpng-devel.x86_64 libtiff-devel.x86_64 libwmf-devel.x86_64 libwebp-devel.x86_64 openjpeg2-devel.x86_64
```
