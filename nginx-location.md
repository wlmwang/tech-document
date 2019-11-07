# Nginx location 配置类别和匹配优先级

## 简介
* 可以实现 Nginx 服务器对不同 uri 请求执行不同的操作。比如，对静态资源开启浏览器缓存；对动态请求转发到指定服务器、脚本；对某些文件或文件夹禁止访问等等。
* 配置文件中一般有多个 location ，分别定义了不同的匹配模式，根据匹配结果获取不同的配置。
* location 的匹配模式都是前缀匹配。
* 匹配的 NGINX 变量：$request_uri

## 语法

#### 格式
```
location [ = | 空格 | ^~ | ~ | ~* | !~ | !~* ] uri {
	// ...
}
```

* 配置分为 3 类
	* 精确模式
		* 精确匹配（=）
	* 前缀模式
		* 普通匹配（空格）
		* 普通优先匹配（^~）
	* 正则模式
		* 大小写敏感正则匹配（~）
		* 大小写不敏感正则匹配（~*）
		* 大小写敏感正则不匹配（!~）
		* 大小写不敏感正则不匹配（!~*）

* 示例
```
1. 精确匹配
location = /static/img/file.jpg {
	//...
}

2. 普通匹配
location /static/img/ {
	//...
}

3. 普通优先匹配
location ^~ /static/img/ {
	//...
}

4. 大小写敏感正则匹配
location ~ /static/img/.*\.jpg$ {
	//...
}

5. 大小写不敏感正则匹配
location ~* /static/img/.*\.jpg$ {
	//...
}

6. 大小写敏感正则不匹配
location !~ /static/img/.*\.jpg$ {
	//...
}

7. 大小写不敏感正则不匹配
location !~* /static/img/.*\.jpg$ {
	//...
}

```

## 优先级
1. 先匹配精确模式（=），一旦匹配成功，直接返回。否则，继续匹配前缀模式
2. 前缀模式中使用【最长】前缀匹配原则。先选出最长匹配的普通匹配（空格）或优先匹配（^~）的最长 location
	a. 若最长前缀 location 是普通优先匹配（^~），则返回该最长前缀 location
	b. 若最长前缀 location 是普通匹配（空格），还需要继续匹配正则模式
3. 正则模式的原则是按照正则 location 定义【顺序】匹配。【第一个】匹配的 location 为正则模式结果
	a. 若正则模式匹配成功，返回正则模式结果；否则，返回前缀模式中的最长前缀 location

```
// 伪码
if 精确匹配结果
    return  精确匹配结果

最长前缀结果 = longest // 最长的 --- 普通匹配(空格) or 普通优先匹配(^~)

if 最长前缀结果 属于 普通优先匹配(^~)
    return 最长前缀结果

首个正则结果 = first // 首个 --- 大小写敏感匹配(~) or 大小写不敏感匹配(~*) or 大小写敏感不匹配(!~) or 大小写不敏感不匹配(!~*)

if 首个正则结果:
    return 首个正则结果
else:
    return 最长前缀结果
```

## 示例

#### 例1 --- 兜底匹配
```
location / {
	[ 配置 A ]
}
```
* 前缀模式，匹配所有 uri。同时它也是最短匹配，俗称兜底匹配。
	* 当其他所有 location 都不匹配时，命中该 location。

#### 例2 --- 优先匹配
```
location /static/ {
	[ 配置 A ]
}
location ^~ /static/img/ {
	[ 配置 B ]
}
```
* uri: "/static/img/logo.jpg"
	* 最长 location 优先匹配：配置 B

#### 例3 --- 正则匹配
```
location /static/ {
	[ 配置 A ]
}
location /static/img/ {
	[ 配置 B ]
}

location ~* /static/ {
	[ 配置 C ]
}
location ~* /static/img/ {
	[ 配置 D ]
}
```
* uri: "/static/img/logo.jpg"
	* 首个正则匹配：配置 C

#### 例4 --- 综合
```
location = / {
	[ 配置 A ]
}

location / {
	[ 配置 B ]
}

location /static/ {
	[ 配置 C ]
}

location ^~ /images/ {
	[ 配置 D ]
}

location ~* \.(gif|jpg|jpeg)$ {
	[ 配置 E ]
}
```
* uri: "/"
	* 精确匹配：配置 A
* uri: "/index.html"
	* 兜底匹配：配置 B
* uri: "/static/index.html"
	* 最长 location 匹配：配置 C
* uri: "/images/1.gif"
	* 非正则匹配：配置 D
* uri: "/documents/1.jpg"
	* 正则匹配：配置 E


## 特殊处理 location

#### 介绍
* 若前缀模式 location 以斜杠（/）结尾，并且请求设置由 proxy_pass,fastcgi_pass,uwsgi_pass,scgi_pass,memcached_pass 等处理器处理，则使用该处理器处理。
	* 注：对于末尾不带斜杠的 uri ，这时会返回 301 并重定向到带斜杠的 uri ，继而匹配该 location

#### 示例

###### 例子1
```
location /user/ {
	proxy_pass http://user.example.com;
}
```
* uri: /user/
	* 转发到 http://user.example.com
* uri: /user
	* 返回 301，并重定向到 /user/ ，进而匹配到配置节点，转发到 http://user.example.com

###### 例子2
```
location /user/ {
	proxy_pass http://user.example.com;
}
location = /user {
	proxy_pass http://login.example.com;
}
```
* uri: /user/
	* 转发到 http://user.example.com;
* uri: /user
	* 转发到 http://login.example.com;


## 内部 location

#### 作用
* 内部 location 不能被外部 Client 所访问，只能用于重定向。
* 它不能被嵌套，也不能包含嵌套的 location

#### 语法
```
location @name {
	// ...
}
```

#### 示例
```
error_page 404 = @fallback
location @fallback {
	// ...
}
```
