# net框架 - bio
* 基础组件
	* 接口
	* 实现
		* InetAddress
			* 网络地址封装类，代表 IP address。有两个子类 Inet4Address/Inet6Address，分别用来处理 IPv4/IPv6
			* 公共接口
				* static InetAddress getByAddress(String host, byte[] addr)
					* 根据 addr 获取 InetAddress 实例，按 addr 长度（4/16字节）创建 Inet4Address 或 Inet6Address
					* addr 是大端序的网络地址
				* static InetAddress getByAddress(byte[] addr)
					* 根据 addr 获取 InetAddress 实例，内部调用 InetAddress.getByAddress(null, addr);
					* addr 是大端序的网络地址
				* static InetAddress[] getAllByName(String host) / static InetAddress getByName(String host)
					* 根据 host 域名，返回所有 InetAddress 实例；后者只返回第一个
				* static InetAddress getLoopbackAddress()
					* 返回回环地址 InetAddress 实例
				* static InetAddress getLocalHost()
					* 获取本机地址的 InetAddress 实例。
					* 首先获取 host 主机名，如果是 "localhost"，直接返回回环地址实例；否则查询 NS 记录获取 IP 地址创建 InetAddress
						* 底层会自动缓存查询到的地址记录，5s 超时（硬编码）
		* InetSocketAddress
			* socket 地址封装类。代表 IP address + port number 或者 hostname + port number。有个超类 SocketAddress
		* ServerSocket
			* 服务端 socket 封装类，狭隘的说就是 listen socket 操作类
			* 默认底层真正实现是通过 SocksSocketImpl - PlainSocketImpl 类来实现，也可以使用工厂 SocketImplFactory 创建 SocketImpl
			* 输入输出流底层原理：将 FileDescriptor 创建的 fd 和 native socket0() 创建的 socket fd 进行绑定，之后使用流进行输入输出
			* 公共接口
				* ServerSocket(int port, int backlog, InetAddress bindAddr)
					* 构建一个指定监听地址的 ServerSocket 实例。底层会自动完成经典服务端网络编程的三步骤
						* socket() / bind() / listen(backlog) // backlog 默认值为 50
						* 注：socket 地址必须是已解析的，即 ip 地址已经根据 host 已经查询过 ns 了
				* SocketAddress getLocalSocketAddress()
					* 获取服务端 socket 实例
				* Socket accept()
					* 线程安全，阻塞等待一个连接 socket 到来，返回该客户端 Socket 实例
				* void close()
					* 线程安全，关闭服务端 socket，释放相关资源，比如文件描述符
				* void setSoTimeout(int timeout)
					* 线程安全，设置服务端 socket 的超时时间，毫秒单位。设置后，accept() 超时将会抛出 SocketException 异常
				* void setReuseAddress(boolean on)
					* 设置 SocketOptions.SO_REUSEADDR 网络选项。不必等待 2MSL 后 TIME_WAIT 状态的连接释放后才可重用地址
				* void setReceiveBufferSize(int size)
					* 设置 SocketOptions.SO_RCVBUF 网络选项。定制接受缓冲区
					* 注：为什么没有发送缓冲区？因为这个是 listen socket 监听地址
		* Socket
			* 通用 socket 封装类，狭隘的说是 connect socket
			* 默认底层真正实现是通过 SocksSocketImpl - PlainSocketImpl 类来实现，也可以使用工厂 SocketImplFactory 创建 SocketImpl
			* 输入输出流底层原理：将 FileDescriptor 创建的 fd 和 native socket0() 创建的 socket fd 进行绑定，之后使用流进行输入输出
			* 公共接口
				* Socket(String host, int port)/Socket(InetAddress address, int port)/Socket(String host, int port, InetAddress localAddr, int localPort)
					* 构建一个指定客户端地址的 Socket 实例。底层会自动完成经典客户端网络编程的三步骤
						* socket() / bind() / connect()	// 只有指定了要绑定本机 socket 地址才会 bind()，一般由操作系统自行选择即可
				* void bind(SocketAddress bindpoint)
					* 手动绑定本机 socket 地址
				* void connect(SocketAddress endpoint) / void connect(SocketAddress endpoint, int timeout)
					* 手动连接服务器，timeout 为 0 时为阻塞直到连接成功
				* InetAddress getInetAddress() | int getPort() / SocketAddress getRemoteSocketAddress()
					* 获取对端地址实例
				* InetAddress getLocalAddress() | int getLocalPort() / SocketAddress getLocalSocketAddress()
					* 获取本端地址实例
				* InputStream getInputStream() / OutputStream getOutputStream()
					* 获取输入、输出实例
*工具类
	* PlainSocketImpl
		* 是一个代理类，由它代理 DualStackPlainSocketImpl、TwoStacksPlainSocketImpl 两种不同实现
		* 存在两种实现的原因是一个用于处理 Windows Vista 以下的版本，另一个用于处理 Windows Vista 及以上的版本
		* unix-like 的实现则不会这么繁琐，它不存在版本的问题，所以它直接由 PlainSocketImpl 类实现
	* SocksSocketImpl
		* 是 PlainSocketImpl 的子类；主要是实现了防火墙安全会话转换协议，包括 SOCKS V4 和 V5
	* SocketImpl
	* SocketInputStream
	* SocketOutputStream

#### 示例
* 网络编程复杂且细节繁多，短时间内，我没有办法看完并分析所有类，等后续慢慢补充吧。这里写一个经典示例来结束本节
```java
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.time.Duration;

public class plainBioTest
{
	public static void main(String[] args) throws Exception {
		new Thread(() -> {
			new PlainBioServer().serve(9090);
		}).start();

		for (int i = 0; i < 5; i++) {
			new Thread(() -> {
				new PlainBioClient().request(9090);
			}).start();
		}

		Thread.sleep(Duration.ofDays(1).toMillis());
	}
}

// 服务端
class PlainBioServer {
	public void serve(int port) {
		try (ServerSocket ss = new ServerSocket(port)) {
			for (;;) {
				Socket cs = ss.accept();
				new Thread(() -> {
					try {
						InputStream in = cs.getInputStream();
						OutputStream out = cs.getOutputStream();

						BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
						BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

						String mess = br.readLine();
						System.out.println("客户端：" + mess);

						bw.write(mess + " - " + LocalDateTime.now() + "\n");
						bw.flush();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}).start();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}

// 客户端
class PlainBioClient {
	public void request(int port) {
		try (Socket cs = new Socket(InetAddress.getLocalHost(), port)) {
			InputStream in = cs.getInputStream();
			OutputStream out = cs.getOutputStream();

			out.write(("hello world" + ":" + LocalDateTime.now() + "\n").getBytes("UTF-8"));
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String mess = br.readLine();
			System.out.println("服务端：" + mess);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
```

