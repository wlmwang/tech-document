# net框架 - nio

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
import java.util.*;
import java.nio.channels.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class plainNioTest
{
	public static void main(String[] args) throws Exception {
		new Thread(() -> {
            try {
                new PlainNioServer().serve(9090);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }).start();
        Thread.sleep(500);

        new PlainNioClient().request(9090);
	}
}

// 客户端
class PlainNioClient {
    private final Selector selector;

    public PlainNioClient() throws IOException {
        selector = Selector.open();
    }

    public void request(int port) {
        try (SocketChannel sc = SocketChannel.open()) {
            sc.configureBlocking(false);
            boolean connected = sc.connect(new InetSocketAddress(port));

			/*// 阻塞连接
            if (!connected) {
                sc.configureBlocking(true);
                connected = sc.finishConnect();
                sc.configureBlocking(false);
            }
            if (!connected) {
                throw new NoConnectionPendingException();
            }
            sc.register(selector, SelectionKey.OP_READ);
            sendWrite(sc, "hello world1" + ":" + LocalDateTime.now());*/

            // 异步连接
            if (connected) {
                sc.register(selector, SelectionKey.OP_READ);
                sendWrite(sc, "hello world2" + ":" + LocalDateTime.now());
            } else {
                sc.register(selector, SelectionKey.OP_CONNECT);
            }

            while (selector.isOpen()) {
                try {
                    selector.select(1000);
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    SelectionKey key;
                    while (it.hasNext()) {
                        key = it.next();
                        it.remove();
                        handleEvent(key);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleEvent(SelectionKey key) {
        if (!key.isValid()) {
            return;
        }

        if (key.isConnectable()) {
            try {
                SocketChannel sc = (SocketChannel) key.channel();
                if (sc.finishConnect()) {
                    sc.register(selector, SelectionKey.OP_READ);
                    sendWrite(sc, "hello world3" + ":" + LocalDateTime.now());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (key.isReadable()) {
            handleRead(key);
        }
    }

    public void handleRead(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            int readBytes = sc.read(buffer);
            if (readBytes > 0) {
                buffer.flip();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                String content = new String(data);

                System.out.println("客户端收取：" + content);
            } else if (readBytes < 0) {
                sc.close();
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendWrite(SocketChannel sc, String content) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        if (content != null && content.length() != 0) {
            try {
                byte[] data = content.getBytes();
                buffer.put(data);
                buffer.flip();
                sc.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

// 服务端
class PlainNioServer {
    private final Selector selector;

    public PlainNioServer() throws IOException {
        selector = Selector.open();
    }

    public void serve(int port) {
        try (ServerSocketChannel ssc = ServerSocketChannel.open()) {
            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress(port));

            ssc.register(selector, SelectionKey.OP_ACCEPT);
            while (selector.isOpen()) {
                try {
                    selector.select(1000);
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();
                        handleEvent(key);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void handleEvent(SelectionKey key) {
        if (!key.isValid()) {
            return;
        }

        if (key.isAcceptable()) {
            try {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                if (sc != null) {
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.OP_READ);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (key.isReadable()) {
            handleRead(key);
        }
    }

    void handleRead(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        try {
            int readBytes = sc.read(buffer);
            if (readBytes > 0) {
                buffer.flip();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                String mess = new String(data);

                System.out.println("服务器收取：" + mess);
                handleWrite(sc, "服务端回显 - " + mess + " ～ " + LocalDateTime.now() + "\n");
            } else if (readBytes < 0) {
                sc.close();
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void handleWrite(SocketChannel sc, String responseContent) {
        if (responseContent != null && responseContent.length() > 0) {
            try {
                byte[] data = responseContent.getBytes(StandardCharsets.UTF_8);
                ByteBuffer buffer = ByteBuffer.allocate(data.length);
                buffer.put(data);
                buffer.flip();

                sc.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```
