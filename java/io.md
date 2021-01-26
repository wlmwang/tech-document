# IO框架
* 基础组件
	* 接口
		* FilterInputStream - InputStream
		* FilterOutputStream - OutputStream
		* FilterReader - Reader
		* FilterWriter - Writer
		* DataInput
		* DataOutput
		* FileChannel
		* Closeable
		* 特性
			* InputStream,OutputStream - 字节流，以 8 位（即 1 byte，8 bit）作为一个数据单元，数据流中最小的数据单元是字节
			* Reader,Writer - 字符流，以 16 位（即 1 char，2 byte，16 bit）作为一个数据单元，数据流中最小的数据单元是字符
				* Java 中的字符是 Unicode 编码，一个字符占用两个字节
			* FilterInputStream,FilterOutputStream,FilterReader,FilterWriter - 主要用在处理流上，将读写调用转发到实际的节点流上
				* 根据是否直接处理数据，IO 分为节点流和处理流。节点流是真正直接处理数据的；处理流是装饰加工节点流的
			* DataInput,DataOutput - 数据流，提供将基础数据类型写入到流中，或者读取出来（二进制读写接口支持）
			* FileChannel - 是一个连接到文件的通道，可以通过文件通道读写文件；它无法设置为非阻塞模式，它总是运行在阻塞模式下
			* Closeable - 若一个流实现了该接口，则该流可以使用 try with resource 语法结构来管理资源
	* 实现
		* FileInputStream
			* 继承
				* InputStream
			* 解析
				* 字节流、节点流
				* 非线程安全，不提供 mark()/reset() 的支持；直接 IO 支持，底层的 flush() 为空操作
				* 内部持有的 fd 字段是一个 FileDescriptor 类型的对象，它不同于 C/C++ 中 int/FILE 类型的文件描述符或句柄
					* 它是一种特定结构的不透明句柄，表示打开的文件、套接字或其他资源。概念和作用上与其他语言的文件描述符区别不大
					* 特别是，它会保存所有持有它的对象引用，从而可以做到其中任何一个流在关闭时，与之关联的所有流都可关闭
						* 比如 FileInputStream.close() 中，调用了 FileDescriptor.closeAll(), 它会关闭所有持有相同 fd 的流对象
							* FileInputStream 有个构造函数是由外部传递一个 FileDescriptor 对象，就可能有多个 FileInputStream 引用同一个 fd 对象
								* 当然你不应该自己创建 FileDescriptor 实例，但可以获取 FileInputStream 中的 fd 对象
		* FileOutputStream
			* 继承
				* OutputStream
			* 解析
				* 字节流、节点流
				* 非线程安全；直接 IO 支持，底层的 flush() 为空操作
				* 内部持有的 fd 字段是一个 FileDescriptor 类型的对象，它不同于 C/C++ 中 int/FILE 类型的文件描述符或文件句柄
					* 不过概念和作用上与其他语言的文件描述符区别不大。它是一种特定结构的不透明句柄，表示打开的文件、套接字或其他资源
					* 特别是，它会保存所有持有它的对象引用，从而可以做到其中任何一个流关闭时，与之关联的所有流都可关闭
						* 比如 FileInputStream.close() 中，就调用了 FileDescriptor.closeAll(), 它会关闭所有持有相同 fd 的流对象
							* FileInputStream 有个构造函数是由外部传递一个 FileDescriptor 对象，就可能有多个 FileInputStream 引用同一个 fd 对象
								* 当然你不应该自己创建 FileDescriptor 实例，但可以获取 FileInputStream 中的 fd 对象
		* ByteArrayInputStream
			* 继承
				* InputStream
			* 解析
				* 字节流、节点流
				* 线程安全，且提供 mark()/reset() 的支持；底层使用字节数组，并且它是由构造时外部传入的
				* ByteArrayInputStream.count 字段是“超尾”偏移索引
		* ByteArrayOutputStream
			* 继承
				* OutputStream
			* 解析
				* 字节流、节点流
				* 线程安全；底层使用字节数组，初始默认长度为 32，每次扩展时为原来的 2 倍
				* 可生成指定编码的 String 对象实例
		* PipedInputStream
		* PipedOutputStream
		* ObjectInputStream
		* ObjectOutputStream
		* BufferedInputStream
			* 继承
				* FilterInputStream
				* InputStream
			* 解析
				* 字节流、处理流；关闭时，自动关闭底层持有的 InputStream
				* 线程安全，提供 mark()/reset() 的支持。主要用于装饰 InputStream 来增加缓存特性
				* 底层使用字节数组缓存数据，初始默认长度为 8192
				* pos：缓存中已读取未处理数据开始偏移索引；count：缓存中已读取未处理据结尾偏移索引
					* buffer.length >= count >= pos
					* [pos, count] 为缓冲区中已读取未处的理据
					* 当 pos == count 时
						* 向数据源一次性的读取 8k 字节数据到缓冲区
						* 当设置了 marklimit，向数据源一次性的读取原来缓存长度的 2 倍的字节数据（注：扩容最大值为 marklimit）
							* 从设置 mark() 开始，读取数据超过 marklimit 后，markpos 会被重置会 -1
							* 当该 Stream 被其他线程异步关闭时，此处扩容时操作，会抛出 IOException 异常
		* BufferedOutputStream
			* 继承
				* FilterOutputStream
				* OutputStream
			* 解析
				* 字节流、处理流；关闭时，自动关闭底层持有的 OutputStream
				* 线程安全，主要用于装饰 OutputStream 来增加缓存特性
				* 底层使用字节数组缓存数据，初始默认长度为 8192
				* count：缓存中已写入数据未处理结尾偏移索引
					* buffer.length >= count
					* [0, count] 为缓冲区中已写入数据未处理的理据
					* 缓冲区的容量 buffer.length 从对象构建完成后，便不会自动扩容
						* 当调用批量写入的字节数据长度超过 buffer.length，会调用持有的 OutputStream 进行直接写入
		* DataInputStream
			* 继承
				* FilterInputStream
				* InputStream
				* DataInput
			* 解析
				* 字节流/字符流、处理流；二进制读取支持；关闭时，自动关闭底层持有的 InputStream
				* 线程安全，不提供 mark()/reset() 的支持。主要用于装饰 InputStream 来增加基础数据类型的读取（大端序）；也可按行读取（Unicode）或 UTF 编码字符串读取
					* 不过 `String readLine()/String readUTF()` 接口意味着数据将被当作字符处理，而非字节
					* UTF 编码是在 Unicode 基础上增加了一个 2 字节的长度头，格式为：|length|string|，每个字的长度占 1~3 字节
						* |3|abc|
						* |7|abc中文|
						* 该算法主要为了让字符串更符合网络的传输（想想 UTF-8。编码上两者有什么细微区别？）
		* DataOutputStream
			* 继承
				* FilterOutputStream
				* OutputStream
				* DataOutput
			* 解析
				* 字节流/字符流、处理流；二进制写入支持；关闭时，自动关闭底层持有的 OutputStream
				* 线程安全，不提供 mark()/reset() 的支持。主要用于装饰 OutputStream 来增加基础数据类型的写入（大端序）；也可写入字符串（Unicode）或 UTF 编码字符串读取
					* 不过 `void writeBytes(String s)/void writeChars(String s)/void writeUTF(String str)` 接口意味着数据将被当作字符处理，而非字节
						* 其中 writeBytes(s) 会丢弃每个字符的高八位
					* UTF 编码是在 Unicode 基础上增加了一个 2 字节的长度头，格式为：|length|string|，每个字的长度占 1~3 字节
						* |3|abc|
						* |7|abc中文|
						* 该算法主要为了让字符串更符合网络的传输（想想 UTF-8。编码上两者有什么细微区别？）
		* PushbackInputStream
			* 继承
				* FilterInputStream
				* InputStream
			* 解析
				* 字节流、处理流；关闭时，自动关闭底层持有的 InputStream
				* 非线程安全，不提供 mark()/reset() 的支持。主要用于装饰 InputStream 来增加字节的回退功能（压入指定 byte）
					* buf 缓冲字段是保存回退的字符，默认缓冲区长度为 1
					* pos 为已回退未读取的偏移索引值
		* FileReader
			* 继承
				* InputStreamReader
				* Reader
			* 解析
				* 字符流、处理流；关闭时，自动关闭底层持有的 InputStream
				* 线程安全，不提供 mark()/reset() 的支持。主要用于装饰 FileInputStream 来增加字符处理能力
					* 实际读取操作由 StreamDecoder 完成，StreamDecoder 装饰了 FileInputStream，在此基础上增加了编码处理
					* 线程安全，是基于持有的 FileInputStream 对象锁
				* InputStreamReader 是 Java IO 提供的一个转换流，它将 InputStream 接口装饰成 Reader 接口
		* FileWriter
			* 继承
				* OutputStreamWriter
				* Writer
			* 解析
				* 字符流、处理流；关闭时，自动关闭底层持有的 OutputStream
				* 线程安全，主要用于装饰 FileOutputStream 来增加字符处理能力
					* 实际读取操作由 StreamEncoder 完成，StreamEncoder 装饰了 FileOutputStream，在此基础上增加了解码处理
					* 线程安全，是基于持有的 FileOutputStream 对象锁
				* OutputStreamWriter 是 Java IO 提供的一个转换流，它将 OutputStream 接口装饰成 Writer 接口
		* CharArrayReader
			* 继承
				* Reader
			* 解析
				* 与 ByteArrayInputStream 基本相同，区别是 CharArrayReader 处理 char 类型数据
		* CharArrayWriter
			* 继承
				* Writer
			* 解析
				* 与 ByteArrayOutputStream 基本相同，区别是 CharArrayWriter 处理 char 类型数据
		* PipedReader
		* PipedWriter
		* StringReader
		* StringWriter
		* BufferedReader
			* 继承
				* Reader
			* 解析
				* 与 BufferedInputStream 基本相同，区别是 BufferedReader 处理 char 类型数据
		* BufferedWriter
			* 继承
				* Writer
			* 解析
				* 与 BufferedOutputStream 基本相同，区别是 BufferedWriter 处理 char 类型数据
		* PushbackReader
			* 继承
				* FilterReader
				* Reader
			* 解析
				* 与 PushbackInputStream 基本相同，区别是 PushbackReader 处理 char 类型数据
		* PrintStream
			* 继承
				* FilterOutputStream
				* OutputStream
				* Appendable
			* 解析
				* 字节流/字符流、处理流；关闭时，自动关闭底层持有的 OutputStream
				* 线程安全，主要用于装饰 OutputStream 来增加 print/println/printf/newLine 接口方式来使用输出流
					* 你可以直接传递文件名，使得输入流实际介质为文件
					* 由于底层在处理字符串接口使用了 BufferedWriter 特性，构造 PrintStream 实例时，你可以指定自动刷新参数
					* 底层持有 Writer 实例为：
						* new BufferedWriter(new OutputStreamWriter(out))
						* new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)))
				* 注：PrintStream 能做的事，PrintWriter 都能做，2个类的功能基本相同。不过 PrintWriter 还可基于 Writer 类型构造实例
					* 但 PrintWriter 出现的比较晚，较早的 System.out 使用的是 PrintStream 来实现的，所以为了兼容就没有废弃
					* 底层实现上，PrintStream 在处理基本类型的写入时，会跳过 BufferedWriter
		* PrintWriter
			* 继承
				* Writer
			* 解析
				* 字节流/字符流、处理流；关闭时，自动关闭底层持有的 OutputStream
				* 线程安全，主要用于装饰 OutputStream 来增加 print/println/printf/newLine 接口方式来使用输出流
					* 你可以直接传递文件名，使得输入流实际介质为文件
					* 由于底层在处理字符串接口使用了 BufferedWriter 特性，构造 PrintStream 实例时，你可以指定自动刷新参数
					* 底层持有 Writer 实例为：
						* new BufferedWriter(new OutputStreamWriter(out))
						* new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)))
				* 注：PrintStream 能做的事，PrintWriter 都能做，2个类的功能基本相同。不过 PrintWriter 还可基于 Writer 类型构造实例
					* 但 PrintWriter 出现的比较晚，较早的 System.out 使用的是 PrintStream 来实现的，所以为了兼容就没有废弃
					* 底层实现上，PrintStream 在处理基本类型的写入时，会跳过 BufferedWriter
		* RandomAccessFile
			* 继承
				* DataOutput
				* DataInput
				* Closeable
			* 解析
				* 它是 JAVA IO 流体系中功能最丰富的文件内容访问类，它提供了众多方法来访问文件内容；主要用于访问保存数据记录的文件
					* 它的构造函数还要一个表示以只读方式("r")，还是以读写方式("rw")打开文件的参数
					* 只有 RandomAccessFile 才有 seek 搜寻方法，而这个方法也只适用于文件
					* 注：RandomAccessFile 的绝大多数功能，但不是全部，已经被 JDK 1.4 的 nio 的"内存映射文件"给取代了
				* 非线程安全，它没有继承字节流、字符流家族中任何一个类。并且它实现了 DataInput、DataOutput 接口，也就意味着它可以读写二进制数据
		* sun.nio.ch.FileChannelImpl
			* java.nio.channels.FileChannel
* 工具类
	* Scanner
	* Path
	* Paths
	* File
	* Files
	* FileDescriptor
	* StreamEncoder
	* StreamDecoder
	* Charset
