# Java源码解析

## 说明
	针对 Java 1.8 源码进行解析
	文档伴着源码食用，效果最佳


## 集合框架 - Collection
* 基础容器
	* 接口
		* List - Collection
		* Deque - Queue - Collection
		* Collection - Iterable
		* RandomAccess
		* 特性
			* List - 有序可重复集合，可直接根据元素的索引来访问
				* 部分接口依赖 equals()，如 indexOf(o)/contains(o)/remove(o)/removeAll(c)/retainAll(c)
				* 部分接口依赖 compareTo()，当然也可以传入比较器，如 sort()
			* Queue - 有序可重复集合，不提供根据元素的索引来访问，使用 add()/offer(),push()/pop(),poll()/peek(),remove() 来访问
				* 部分接口依赖 equals()，如 indexOf(o)/contains(o)/remove(o)/removeAll(c)/retainAll(c)
				* PriorityQueue 底层是最小堆，需实现 Comparable<T> 接口，当然也可以传入比较器
			* Set - 代表无序不可重复集合，只能根据元素本身来访问
				* 依赖 equals(), hashCode()。通常情况下，你应该重新定义这两个接口的实现
			* TreeSet - 代表有序不可重复集合
				* 除依赖 equals(), hashCode() 外，还依赖 compareTo()。通常情况下，你应该重新定义这三个接口的实现
	* 实现
		* ArrayList<E>
			* 继承
				* AbstractList - List
				* RandomAccess
				* Cloneable
				* Serializable
			* 解析
				* 可高效的进行随机访问（按索引访问）的列表结构，用 RandomAccess 标识
				* 底层使用 Object[] 数组存放列表元素
					* Java 对象实例（数组也是对象）带有类型信息，与存放的容器无关（需可转换）。比如 Object[] 可以存放任何对象，因为他是所有类的基类
						* 所以 toArray() 有两个不同的重载版本，分别返回 Object[] 和 T[] 数组
						* 其中泛型版本，传递的参数类型起到控制方法的返回类型，并且当长度足够时，直接使用该数组，否则底层会创建一个 T[] 数组
				* 默认构造的列表容量为 10，不过此时并没有分配内存（直到添加元素时分配），用一个 DEFAULTCAPACITY_EMPTY_ELEMENTDATA 常量来标识
					* 数组每次扩容，长度增加当前容量的 1.5 倍
				* 列表为空时（size == 0），`elementData` 指向空数组，用一个 EMPTY_ELEMENTDATA 常量来标识
				* modCount 属性字段用来标识列表的底层数组是否有结构性的变化。结构性变化指的是：增、删元素
					* 用来支持迭代器的快速失败算法，即，快速检测是否有并发更新，然后立即抛出 ConcurrentModificationException 异常
					* 实际上，如果一个列表创建了两个迭代器，其中一个调用了更新接口后，另一个迭代器将不可用，与是否是并发无关，可用来防止迭代器失效
				* `overflow-conscious code` 源码中的这条注释针对列表长度最大不会超过 Integer.MAX_VALUE
					* 超过时，int会溢出为负值，底层会直接抛出 OOM 异常
				* `c.toArray might (incorrectly) not return Object[] (see 6260652)` 源码中的这条注释针对的是
					* Arrays.asList() 创建的列表（java.util.Arrays$ArrayList），其 c.toArray() 返回的不是 Object[] 数组，而是 T[] 数组
				* 代码中很多地方都调用了：Arrays.copyOf(original, newLength, newType) / Arrays.copyOf(original, newLength)
					* 作用是：总是会分配一个新数组的内存空间，然后将旧的 original 内存数据按字节复制到新内存中
						* 更底层调用了 native 方法 Array.newArray，它由 JVM 实现，用来创建一个 componentType[length] 类型数组
						* 拷贝数组内存数据使用的是 System.arraycopy()，它是一个 native 方法，底层使用 memmove()，即，按字节拷贝
				* clone() 时，底层使用的 Object[] 数组会被自动拷贝，即，新建后复制，是按字节拷贝
				* AbstractList.SubList 是内部类，但它还是使用 parent 字段去引用父列表，主要是为实现 SubList 递归生成子列表的功能
				* AbstractList.ArrayListSpliterator 分隔迭代器，主要用来支持集合流的特性
					* 算法核心是对迭代器进行二分切割
				* writeObject/readObject
		* LinkedList<E>
			* 继承
				* AbstractSequentialList - List
				* Deque - Queue
				* Cloneable
				* Serializable
			* 解析
				* 可高效的进行插入、删除的列表结构，可作为队列结构（头尾操作）使用，用 Deque - Queue 标识
				* 底层使用 LinkedList.Node 实现双向链表
				* 与 ArrayDeque 相比，随机访问效率不高，但频繁的插入、删除效率会更佳，这也是线性表存在的意义
				* 虽然该列表也提供了随机访问接口，但效率低下，请不要使用
					* 从该列表没有实现 RandomAccess 接口，就提示了我们不应该对它进行随机访问
				* modCount 属性字段用来标识列表的底层数组是否有结构性的变化。结构性变化指的是：增、删元素
					* 用来支持迭代器的快速失败算法，即，快速检测是否有并发更新，然后立即抛出 ConcurrentModificationException 异常
					* 实际上，如果一个列表创建了两个迭代器，其中一个调用了更新接口后，另一个迭代器将不可用，与是否是并发无关，可用来防止迭代器失效
				* clone() 时，底层使用的链表节点也会被拷贝（循环调用 add() 方法）
				* LinkedList.LLSpliterator 分隔迭代器，主要用来支持集合流的特性
					* 算法核心是对迭代器进行二分切割
					* 注：trySplit() 方法返回的是一个数组分隔迭代器 Spliterators.ArraySpliterator
				* writeObject/readObject
		* ArrayDeque<E>
			* 继承
				* AbstractCollection - Collection
				* Deque - Queue
				* Cloneable
				* Serializable
			* 解析
				* 可高效的进行插入、删除的双端队列结构，用 Deque - Queue 标识
				* 底层使用 Object[] 数组存放列表元素
				* 与 LinkedList 相比，随机访问效率更高，但频繁的插入、删除效率欠佳，因为这可能会导致底层数组的扩容（内存要重新分配及拷贝）
				* head 和 tail 索引，支持高效的头尾操作。head 指向末端索引，tail 从起始索引开始
					* 每次 head == tail 时，自动扩容，底层的数组长度增加 1 倍。长度总是 2 的幂次方
					* 底层使用的数据结构也常被称为循环数组
		* PriorityQueue<E>
			* 继承
				* AbstractQueue - Queue
				* Cloneable
				* Serializable
			* 解析
				* 高效的最小堆实现，可当作优先队列使用，用 Queue 标识
					* 最小堆：任一非终端节点的数据值均不大于其子节点的值；添加、删除的时间复杂度为 O(logn)
				* 底层使用 Object[] 数组实现，其中存放队列元素
		* HashSet
			* 继承
				* AbstractSet
				* Cloneable
				* Serializable
			* 解析
				* 可高效的进行访问的散列结构，底层使用 HashMap 容器，详细说明请看 HashMap 描述
				* 集容器的元素，被保存至 HashMap<E, Object> 映射表的键中，值是一个“无用”的常量 PRESENT = new Object()
				* 迭代器返回的是 HashMap<E, Object> 的键视图
		* TreeSet
		* LinkedHashSet
* 同步组件
	* CopyOnWriteArrayList
	* CopyOnWriteArraySet
	* ConcurrentSkipListSet
* 迭代器
	* 接口
		* ListIterator - Iterator
		* Iterator
		* Iterable
		* 特性
			* Iterable - 若一个容器实现了该接口，则它就可以使用 for-in 语法结构进行迭代遍历
			* Iterator - 通用迭代器，只包含 next(), hasNext(), remove(), forEachRemaining() 接口
				* 调用 remove() 前，需调用 next()；调用 next() 前，强烈建议先调用 hasNext()
				* forEachRemaining() 接口不可改变列表的结构，否则立即抛出 ConcurrentModificationException 异常
			* ListIterator - 列表迭代器，主要应用在与位置相关的容器上，比如 ArrayList, LinkedList, SubList
				* 相比 Iterator 接口，增加了向前迭代接口，如 hasPrevious(),previous(),previousIndex(),nextIndex()
				* 提供增、删、改、查接口，如 set(E e), add(E e)；不管概念还是实现上，这两个接口都与位置相关
			* 注：本质上，迭代器的工作是辅助具体的容器进行访问索引的自动化管理，它不持有实际的数据
	* 实现
		* ArrayList.Itr
		* ArrayList.ListItr
		* LinkedList.Itr
		* LinkedList.ListItr
		* ArrayDeque.DeqIterator
		* ArrayDeque.DescendingIterator
		* PriorityQueue.Itr
		* 解析
			* 创建迭代器时，立即拷贝原始容器的 modCount 到迭代器实例中
				* 后续任何迭代器操作都会比对迭代器与原始容器的 modCount 值是否相等，若不相等，会立即抛出 ConcurrentModificationException
				* 这么设计，可以解决并发问题，也可以解决迭代器失效问题
			* next() 与 remove() 必须成对使用。即，在 remove() 前，需要 next() 访问该元素；使用 next()，强烈建议先调用 hasNext()
				* 内部 lastRet 字段是上次访问的元素的索引，也是回退索引，remove() 后，迭代器的 cursor 将回退到该值，以防止迭代器失效
			* 综合以上的接口设计以及附加的限制，看似笨拙，实际上可以解决 C++ 中令人讨厌的迭代器失效问题
				* 在 C++ 中，迭代器 erase() 一般会直接放到容器自身或一个公共的算法函数上，使用上也没有任何限制，由此引发的问题程序员需处理
				* 所谓，欲求灵活，先受复杂
					
* 工具类
	* Arrays
	* Lists
	* Collections


## 集合框架 - Map
* 基础容器
	* 接口
		* AbstractMap - Map
		* NavigableMap - SortedMap
		* 特性
			* Map - 存储 k-v 键值对的无序集合；可根据 k 访问 v；键数据类型依赖 equals(), hashCode()
				* 部分接口会使“值”也依赖 equals()，如：containsValue。如果使用 EntrySet 视图，“值”会依赖 equals()
			* TreeMap - 存储 k-v 键值对的有序集合；可根据 k 访问 v；键数据类型除依赖 equals(), hashCode()，还依赖 compareTo()
	* 实现
		* HashMap<K,V>
			* 继承
				* AbstractMap
				* Map
				* Cloneable
				* Serializable
			* 解析
				* 默认构造函数，负载因子为 0.75，hash 桶的初始容量为 16，最大不能超过 2^30，并且一定是 2 的指数倍（非常重要！）
					* 第一次添加元素时，初始化容量为 16，扩容阈值 threshold 设置为容量的 0.75 倍
						* 负载因子作用：当映射表的总元素个数超过当前桶容量的 loadFactor 倍后会触发扩容；每次会扩容为现有容量的 2 倍
					* resize() 扩容方法，是一个精心设计过的算法，甚是巧妙！
						* threshold 在初始化后（容量乘以 loadFactor），之后 threshold 的变更只需经过左移一位即可，其值肯定也是等于当前容量乘以 loadFactor
						* 扩容后，每个元素无需用 e.hash & (newCap-1) 进行逐个计算桶索引，只需要计算 e.hash & oldCap，并进行判断：
							* 当为 0 时，说明之前该 hash 高位并没有被截断，重新 rehash 必然是原来的值，也就还在原来的桶中
							* 当不为 0 时，说明之前该 hash 高位被截断了，重新 rehash 必然是原来桶的索引加上原来桶容量的长度
					* tableSizeFor(cap) 也是一个精心设计过的算法，甚是巧妙！
						* tableSizeFor(cap) 返回一个大于 cap 的最小的 2 的指数倍的值
							* 桶容量限制成总是 2 的指数倍，可以将桶索引计算变成按位与运算；rehash 时也不用重新计算每个元素的桶索引
						* tableSizeFor(cap) 算法思路为，将 cap-1 后的值，最高位为 1 后面所有位也置 1，最后在 +1
							* 注：我见过很多使用循环来解决该问题的，包括 chromium/php 等源码，当然也可能是因为他们没有简便的逻辑右移运算符
				* HashMap<K,V> 使用 HashMap.Node<K,V> - Map.Entry<K,V> 数据结构包装键、值对
					* HashMap.Node<K,V> 中 next 字段用来支持拉链算法，HashMap 解决哈希冲突使用了拉链法
						* 注：Node 重写了 hashCode() 方法，让 k-v 都参与计算。主要是为了 HashMap.entrySet() 返回的 Set 集的唯一性要求
					* 为了进一步优化，当某个桶中的冲突元素大于等于 8，链表将被转化成红黑树
				* HashMap.remove() 有两个不同的重载版本，可以置顶 key 匹配或者 key-value 都匹配；replace也有两个版本，指定除了 key，是否还要 value 也要匹配
				* 键值视图：HashMap<K,V>.entrySet() 方法返回一个键的 Set<Map.Entry<K,V>> 集
					* 之后即可使用集的方式操作映射表。比如，删除元素、遍历迭代，分隔迭代、转换数组列表等
					* 第一次调用，会将视图实例缓存至 HashMap<K,V>.entrySet 字段中。底层实现类：HashMap<K,V>.EntrySet
					* 视图、及其迭代器操作会反映到原始映射表
						* 视图中，remove(Object o) 接口，Object 为 Map.Entry<K,V>，删除键、值都匹配的元素
					* Map.Entry<K,V> 重写了 equals() 方法，匹配时键、值都参与了 equals() 计算
				* 键视图：HashMap<K,V>.keySet() 方法返回一个键的 Set<K> 集
					* 之后即可使用集的方式操作键集。比如，删除元素、遍历迭代，分隔迭代、转成数组列表等
					* 第一次调用，会将视图实例缓存至 HashMap<K,V>.keySet 字段中。底层实现类：HashMap<K,V>.KeySet
					* 视图、及其迭代器操作会反映到原始映射表
				* 值视图：HashMap<K,V>.values() 方法返回一个值的 Collection<V> 集合
					* 之后即可使用集合的方式操作值集合。比如，删除元素、遍历迭代，分隔迭代、转成数组列表等
					* 第一次调用，会将视图实例缓存至 HashMap<K,V>.values 字段中。底层实现类：HashMap<K,V>.Values
					* 视图、及其迭代器操作会反映到原始映射表
						* 视图中，remove(Object o) 接口，Object 为 V，本质上是删除第一个与该“值对象”匹配的那个键
						* 视图中，迭代器删除接口，本质上是删除当前迭代到的 entrySet 的那个键
		* TreeMap
		* LinkedHashMap
		* IdentityHashMap
		* WeakHashMap
		* Hashtable
		* Dictionary
* 同步组件
	* ConcurrentHashMap
	* ConcurrentSkipListMap
* 迭代器
	* EntryIterator
	* HashIterator
	* KeyIterator
	* ValueIterator
	* EntrySpliterator
	* KeySpliterator


## IO框架
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


## stream框架
* 基础组件
	* Stream/IntStream/LongStream/...
	* PipelineHelper/ReferencePipeline/IntPipeline/LongPipeline/...
	* StreamSupport/Streams/Collectors/...
* 有状态/无状态
	* StatefulOp/StatelessOp
* 终结/非终结
	* min/max/count/forEach/toArray/reduce/[any|all|none]Match/find[First|Any]/...
	* filter/skip/distinct/limit/sorted/map/flatMap/peek/...


## 网络框架 - BIO


## 网络框架 - NIO


## 网络框架 - HTTP


## 时间框架


## 日志框架


## 线程框架
	

## 线程池框架


## Fork-Join框架


## 序列化机制


## 类加载机制


## JDBC 库



