# net框架 - aio

## 基础容器
#### 接口

#### 实现

## 工具类

## 关键源码

#### 示例代码
* 网络编程复杂且细节繁多，短时间内，没有办法看完并分析所有类，等后续慢慢补充吧。这里写一个经典示例来结束本节
```java
import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.concurrent.*;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;

public class plainAioTest
{
	public static void main(String[] args) throws Exception {
		new Thread(() -> {
            try {
                new PlainAioServer().serve(9090);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }).start();
        Thread.sleep(500);

        new PlainAioClient().request(9090);
	}
}

// 客户端
class PlainAioClient {
    AsynchronousChannelGroup group;

    public PlainAioClient() throws IOException {
        ExecutorService pool = Executors.newCachedThreadPool();
        group = AsynchronousChannelGroup.withCachedThreadPool(pool, 10);
    }

    public void request(int port) {
        try (AsynchronousSocketChannel asc = AsynchronousSocketChannel.open(pool)) {
            asc.connect(new InetSocketAddress(port), null, new CompletionHandler<Void, Void>()
            {
                @Override
                public void completed(final Void result, final Void attachment) {
                    try {
                        asc.write(ByteBuffer.wrap(("hello world" + ":" + LocalDateTime.now()).getBytes())).get();

                        ByteBuffer readBuffer = ByteBuffer.allocate(128);
                        asc.read(readBuffer).get();
                        String mess = new String(readBuffer.array());
                        System.out.println("客户端收取：" + mess);
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failed(final Throwable exc, final Void attachment) {
                    exc.printStackTrace();
                }
            });

            TimeUnit.MINUTES.sleep(Integer.MAX_VALUE);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// 服务端
class PlainAioServer {
    AsynchronousChannelGroup group;

    public PlainAioServer() throws IOException {
        ExecutorService pool = Executors.newCachedThreadPool();
        group = AsynchronousChannelGroup.withCachedThreadPool(pool, 10);
    }

    public void serve(int port) {
        try (AsynchronousServerSocketChannel assc = AsynchronousServerSocketChannel.open()) {
            assc.bind(new InetSocketAddress(port));

            CompletionHandler<AsynchronousSocketChannel, Object> handler = new CompletionHandler<AsynchronousSocketChannel, Object>()
            {
                @Override
                public void completed(final AsynchronousSocketChannel asc, final Object att) {
                    assc.accept(att, this);
                    try {
                        ByteBuffer readBuffer = ByteBuffer.allocate(128);
                        asc.read(readBuffer).get();
                        String mess = new String(readBuffer.array());
                        System.out.println("服务器收取：" + mess);

                        asc.write(ByteBuffer.wrap(("服务端回显 - " + mess + " ～ " + LocalDateTime.now() + "\n").getBytes())).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failed(final Throwable exc, final Object attachment) {
                    System.out.println("出错了：" + exc.getMessage());
                }
            };

            assc.accept(null, handler);
            TimeUnit.MINUTES.sleep(Integer.MAX_VALUE);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```
