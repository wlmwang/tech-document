# 基于 OpenSSL 自建 CA 和颁发 SSL 证书
* SSL 数字证书是 TLS/SSL 协议中用来进行身份验证的机制。是一种数字签名形式的文件，包含证书拥有者的公钥及第三方的证书信息。

## 证书分为2类
* 自签名证书和 CA 证书。
	* 一般自签名证书不能用来进行身份认证。如果一个 server 端使用自签名证书，client 端要么被设置为无条件信任任何证书，要么需要将自签名证书的公钥和私钥加入受信任列表。但这样一来就增加了 server 的私钥泄露风险。

## TLS 基于 CA 的身份认证基本原理
1. 首先验证方（客户端）需要信任 CA 机构的根证书(CAcert)，比如证书在操作系统的受信任证书列表中，或者用户通过"安装根证书"等方式将 CA 证书（内含公钥）加入受信任列表；
2. 然后 CA 对被验证方（服务端）的原始证书进行签名（私钥加密），生成最终的证书颁发给被验证方（卖给服务端）；
3. 验证时，验证方（客户端）通过握手协议获取颁发的证书，然后利用本地 CAcert 中包含的公钥进行解密，得到被验证方（服务端）的原始证书，最后再对该证书做进一步的验证，如域名是否一致，证书是否过期等。（根据 RSA 的加密原理，如果用 CAcert 的公钥解密成功，至少可以说明该证书的确是用 CAcert 的私钥加密的）。
```
本质上，终端用户（客户端）信任的是 CA 根证书，从而信任由这个 CA 机构根证书签发的网站（服务端） tls/ssl 证书。

如果你对哪个 CA 机构比较憎恨，可以将它的根证书删除，这样它签发的所有证书都不会受信任。

安装网站 tls/ssl 证书后（同时也有信任的根证书），地址栏一般会显示绿色小锁。
```

## 证书生成
* CA 要给别人颁发证书，首先自己得有一个作为根证书。
	1. 生成根私钥
		> $ openssl genrsa -out ca.key 2048
	2. 生成根证书
		> $ openssl req -new -x509 -key ca.key -out ca.crt
* 为服务端生成证书
	1. 生成私钥
		> $ openssl genrsa -out server.key 2048
	2. 生成签署请求证书（原始证书）。作为 https 证书，提示输入的字段 commone name 必须与要申请证书的域名一致
		> $ openssl req -new -key server.key -out server.csr
	3. csr 文件必须有 ca 的签名才可形成证书。可将此文件发送到 verisign 等地方由它验证（收费）。何不自己做 ca 签名！
		> $ openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out server.crt

## 证书（自认证 SSL 证书）生成完成。可直接提供给 nginx 使用
* 私钥：server.key
* 证书：server.crt

## 证书合并到 pem 文件（存放证书和私钥）。
* nginx/nodejs 可使用该格式启动（客户端也可使用。如 curl --cacert ca.pem ...）
	> $ cat server.key server.crt > server.pem


## 其他
* 但 tomcat 或 java 客户端/android 不能使用该格式的证书。它们需要 PKCS12 格式文件（该文件可以包含多个证书/私钥对，指定多个受信任的 server（也可以不包含证书），每个 server 有一个 alias name）。
* 最简单的只包含一个 alias 的文件生成（将 CA 证书及其私钥导出为文件，然后将其加入到客户端可信任列表）
> $ openssl pkcs12 -export -in ca.crt -inkey ca.key -out ca.p12

