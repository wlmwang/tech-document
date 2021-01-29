# Java源码解析

## 说明
* 文档针对 JDK 1.8 源码进行解析
	* native 方法来源 OpenJDK 1.8
* 文档伴随源码食用，效果最佳


## 集合框架 - collection
* 基础容器
	* 接口
		* List - Collection
		* Deque - Queue - Collection
		* Collection - Iterable
		* RandomAccess
		* NavigableSet - SortedSet - Set
		* Iterator
		* 特性
			* List - 有序可重复集合，可直接根据元素的索引来访问
				* 部分接口依赖 equals()，如 indexOf(o)/contains(o)/remove(o)/removeAll(c)/retainAll(c)
				* 部分接口依赖 compareTo()，当然也可以传入比较器，如 sort()
			* Queue - 有序可重复集合，不提供根据元素的索引来访问，使用 add()/offer(),push()/pop(),poll()/peek(),remove() 来访问
				* 部分接口依赖 equals()，如 indexOf(o)/contains(o)/remove(o)/removeAll(c)/retainAll(c)
				* PriorityQueue 底层是最小堆，需实现 Comparable<T> 接口，当然也可以传入比较器
				* Deque 是双端队列
			* Set - 代表无序不可重复集合，只能根据元素本身来访问
				* 依赖 equals(), hashCode()。通常情况下，你应该重新定义这两个接口的实现
			* SortedSet - 代表有序不可重复集合
				* 除依赖 equals(), hashCode() 外，还依赖 compareTo()。通常情况下，你应该重新定义这三个接口的实现
			* NavigableSet - SortedSet 的子接口，增加了范围查找、降序 Set，以及增强的获取的部分 Set 方法
			* Iterator - 本质上，迭代器的工作是辅助具体的容器进行访问索引的自动化管理，它不持有实际的数据
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
					* Arrays.asList() 创建的列表（java.util.Arrays.ArrayList），其 c.toArray() 返回的不是 Object[] 数组，而是 T[] 数组
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
				* 灵活与复杂同在
					
* 工具类
	* Arrays
		* asList()
			* 构建 Arrays.ArrayList<E> 实体，与 ArrayList<E> 基本一致，但底层使用的存储容器是 E[] 数组
				* 这会使得 toArray() 方法返回 T[] 类型数据。虽然该方法返回类型是 Object[]，但请记住这是容器，和泛型没什么区别
		* sort()/parallelSort()
			* 使用优化的快排算法，就地排序，各种重载版本
		* binarySearch()
			* 二分查找算法，要求数据源已排序，各种重载版本
		* copyOf()/copyOf()
			* 总是会分配一个新数组的内存空间，然后将旧的 original 内存数据按字节复制到新内存中
				* 更底层调用了 native 方法 Array.newArray，它由 JVM 实现，用来创建一个 componentType[length] 类型数组
				* 拷贝数组内存数据使用的是 System.arraycopy()，它是一个 native 方法，底层使用 memmove()，即，按字节拷贝
		* stream()
			* 返回基于数组参数构建的集合流，各种重载版本
		* equals()/hashCode()/toString()/fill()
			* 封装数组的常见的对象方法，简单处理 null 值情况，算法本身也是常规的迭代运算
	* Collections
		* synchronizedList(List<T> list)/synchronizedList(List<T> list, Object mutex)
			* 将非线程安全的 List 集合转换为线程安全的
			* 返回 Collections.SynchronizedRandomAccessList<E>/Collections.SynchronizedList<E> 对象
			* SynchronizedRandomAccessList - SynchronizedList - SynchronizedCollection
				* 默认构造，使用 this 对象锁作用同步锁
				* 底层将源 List<E> 方法包装一层 synchronized (mutex){...} 来达到线程安全
			* 注：如果使用迭代器，必须由使用人员自行添加同步手段
		* synchronizedSet(Set<T> s)/synchronizedSet(Set<T> s, Object mutex)
		* synchronizedSortedSet(Set<T> s)/synchronizedSortedSet(Set<T> s, Object mutex)
			* 将非线程安全的 Set/SortedSet 集合转换为线程安全的
			* 返回 Collections.SynchronizedSet<E>/Collections.SynchronizedSortedSet<E>
			* SynchronizedSet - SynchronizedCollection - Set | SynchronizedSortedSet - SynchronizedSet - SortedSet
				* 默认构造，使用 this 对象锁作用同步锁
				* 底层将源 Set<E>|SortedSet<E> 方法包装一层 synchronized (mutex){...} 来达到线程安全
			* 注：如果使用迭代器，必须由使用人员自行添加同步手段
		* synchronizedCollection()
			* 将非线程安全的 Collection 集合转换为线程安全的
			* synchronizedList/synchronizedSet/... 的父类。算法思想一致
			* 注：如果使用迭代器，必须由使用人员自行添加同步手段
		* synchronizedMap()
			* 将非线程安全的 Map 集合转换为线程安全的
			* 算法思想与 synchronizedList/synchronizedSet 没有本质区别。
				* 不过，相比集合，Map有视图，会返回线程安全版本，即 SynchronizedSet/SynchronizedCollection
			* 注：一般我们不使用这种封装的线程安全 Map 映射，而使用性能更好的 ConcurrentHashMap


## 集合框架 - map
* 基础容器
	* 接口
		* NavigableMap - SortedMap - Map
		* Map
		* 特性
			* Map - 存储 k-v 键值对的无序集合；可根据 k 访问 v；键数据类型依赖 equals(), hashCode()
				* 部分接口会使“值”也依赖 equals()，如：containsValue。如果使用 EntrySet 视图，“值”会依赖 equals()
			* SortedMap - 存储 k-v 键值对的有序集合；可根据 k 访问 v；键数据类型除依赖 equals(), hashCode()，还依赖 compareTo()
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
* 迭代器
	* EntryIterator
	* HashIterator
	* KeyIterator
	* ValueIterator
	* EntrySpliterator
	* KeySpliterator
	* 解析
		* 不会直接使用，而使用键、值视图
		* 迭代器的解析，请看《集合框架 - Collection》
* 同步组件
	* ConcurrentHashMap
	* ConcurrentSkipListMap


## 流框架 - stream
* 注：Stream不像其他章节可以按照单独类顺序介绍；Stream若按整体介绍会变得更易理解，配合图文更佳。待续...

* 基础组件
	* Stream/IntStream/LongStream/...
	* PipelineHelper/ReferencePipeline/IntPipeline/LongPipeline/...
	* StreamSupport/Streams/Collectors/...
* 有状态/无状态
	* StatefulOp/StatelessOp
* 终结/非终结
	* min/max/count/forEach/toArray/reduce/[any|all|none]Match/find[First|Any]/...
	* filter/skip/distinct/limit/sorted/map/flatMap/peek/...
* 工具类
	* StreamSupport


## 时间框架 - time
* 基础组件
	* 接口
		* Temporal
		* TemporalAdjuster
		* TemporalUnit
		* ChronoLocalDate
		* ChronoZonedDateTime
		* 特性
			* Temporal - 定义了如何读取和操纵以时间建模的对象的值
			* TemporalAdjuster - 用更精细的方式操纵日期，不再局限于一次只能改变它的一个值，并且还可按照需求定义自己的日期转换器
	* 实现
		* Instant
			* 不可变对象，代表时间线上的一个瞬时点（时间戳），该瞬间点精确到纳秒分辨率，底层使用 long 存储秒、int 存储纳秒
				* 时间戳：典型的以 Unix 纪元年时间（1970-01-01T00:00:00Z）开始所经历的秒数进行建模
			* 公共接口
				* static Instant now()
					* 获取当前时间戳实例
					* 内部使用 System.currentTimeMillis() 的 native 方法获取当前毫秒时间戳；时区被设置为 ZoneOffset.UTC
						* static native long currentTimeMillis()
							* OpenJDK - jdk\src\share\native\java\lang\System.c:currentTimeMillis
							* OpenJDK - hotspot\src\share\vm\prims\jvm.cpp:JVM_CurrentTimeMillis
							* OpenJDK - hotspot\src\os\linux\vm\os_linux.cpp:javaTimeMillis()
								* javaTimeMillis() 的核心就是使用 gettimeofday(&time, NULL) 获取 timeval time，返回毫秒值
						* static native long nanoTime()
							* OpenJDK - jdk\src\share\native\java\lang\System.c:nanoTime
							* OpenJDK - hotspot\src\share\vm\prims\jvm.cpp:JVM_NanoTime
							* OpenJDK - hotspot\src\os\linux\vm\os_linux.cpp:javaTimeNanos()
								* javaTimeNanos() 的核心就是使用 clock_gettime(CLOCK_MONOTONIC, &tp) 获取 timespec tp，返回纳秒值
				* static Instant ofEpochSecond() / static ofEpochMilli()
					* 根据秒、纳秒、毫秒参数创建时间戳实例；参数中的时间点为纪元后所经历的时间
				* static Instant from(TemporalAccessor temporal) 
					* 根据 temporal 实例创建一个时间戳实例
				* static Instant parse() 
					* 根据 IOS 字符串时间创建时间戳实例，比如 "2021-01-27T16:26:00Z"
				* boolean isSupported(TemporalField field)
					* 检查时间戳是否支持某个时间字段。field 可以是 ChronoField 枚举中的值
				* boolean isSupported(TemporalUnit unit)
					* 检查时间戳是否支持某个时间单位。field 可以是 ChronoUnit 枚举中的值
				* Instant with(TemporalField field, long newValue)
					* 基于当前时间戳返回调整后的时间戳对象，直接指定秒、纳秒、毫秒值。比如：将获取当前时间 100 秒后的时间戳实例：
						* Instant.now().with(TemporalField.INSTANT_SECONDS, Instant.now().getEpochSecond() + 100);
						* Instant.now().with(TemporalField.INSTANT_SECONDS, Instant.now().getLong(TemporalField.INSTANT_SECONDS) + 100);
				* Instant plus(long amountToAdd, TemporalUnit unit)
					* 基于当前时间戳返回调整后的时间戳对象，增加一个时间增量，可以是秒、纳秒、微妙、毫秒、分钟、小时、天。比如：获取当前时间 1 星期后的时间戳实例：
						* Instant.now().plus(7, ChronoUnit.DAYS);
				* Instant Instant.minus(long amountToSubtract, TemporalUnit unit)
					* 基于当前时间戳返回调整后的时间戳对象，减去一个时间增量，可以是秒、纳秒、微妙、毫秒、分钟、小时、天。比如：获取当前时间 1 星期前的时间戳实例：
						* Instant.now().minus(7, ChronoUnit.DAYS);
					* 注：底层调用实现会将其转换成 Instant.plus(-amountToSubtract, unit);
				* long until(Temporal endExclusive, TemporalUnit unit)
					* 计算当前时间戳到 endExclusive 时间戳之间的时间差，返回一个整形值，单位由 unit 指定。比如：获取 1 秒后时间戳与当前时间戳差值
						* Instant.now().until(Instant.now().plusMillis(1000), ChronoUnit.SECONDS);
				* ZonedDateTime atZone(ZoneId zone)
					* 获取当前时间戳上附加时区的日期实例。比如：获取将当前时间戳附加上中国时区
						* Instant.now().atZone(ZoneId.of("+08"));	// ZoneId.of("Asia/Shanghai")
						* Instant.now().atZone(TimeZone.getTimeZone("GMT+08:00").toZoneId());	// TimeZone.getTimeZone("Asia/Shanghai")
						* Instant.now().atZone(ZoneOffset.of("+08"));	// ZoneOffset.ofHours(8)
		* LocalDate
			* 不可变对象，代表一个日期，它不存储或表示时间或时区。年份范围 [-999,999,999, +999,999,999]，月份范围 [1,12]，日子范围 [1,31]
			* 它是对日期的描述，可用于生日；它不能代表时间线上的即时信息，而没有附加信息，如偏移或时区
		* LocalTime
			* 不可变对象，代表一个时间，纳秒精度；它不存储或表示日期或时区。
			* 它是在挂钟上看到的当地时间的描述；它不能代表时间线上的即时信息，而没有附加信息，如偏移或时区
		* LocalDateTime
			* 不可变对象，代表日期时间，时间表示为纳秒精度，通常被视为年月日时分秒；它不存储或表示时区
			* 它是对日子的描述，如用于生日，结合当地时间在挂钟上看到的；它不能代表时间线上的即时信息，而没有附加信息，如偏移或时区
		* ZonedDateTime
			* 不可变对象，代表日期时间，时间表示为纳秒精度，通常被视为年月日时分秒；它在 LocalDateTime 基础上增加了时区信息
			* 公共接口
				* ZonedDateTime.parse("2021-01-27T20:02:00.000+08:00")
					* 从字符串创建 ZonedDateTime
				* ZonedDateTime.now(zoneId)
					* 获取其他时区的当前日期和时间
		* Duration
		* Period
* 工具类
	* Year
	* Month
	* DayOfWeek
	* YearMonth
	* MonthDay
	* Clock
	* Clock.SystemClock
	* Clock.FixedClock
	* Clock.OffsetClock
	* Clock.TickClock
	* ChronoField
	* ChronoUnit
	* ZoneId
		* ZoneId.getAvailableZoneIds()
	* DateTimeFormatter
		* DateTimeFormatter.ISO_DATE
		* DateTimeFormatter.ofPattern("yyyy/MM/dd")


## io框架 - bio
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
				* FileInputStream.fd 字段是一个 FileDescriptor 类型的对象，它是 Java 虚拟机用来对文件定位的。对它的解析请看下面“工具类”一段
				* static native void initIDs()
					* OpenJDK - jdk\src\share\native\java\io\FileInputStream.c:Java_java_io_FileInputStream_initIDs()
						* 用于初始化 FileInputStream 中 fd 字段 ID，之后可根据该 ID 和 FileInputStream 实例获取 fd 字段的引用
				* native void open0(String path)
					* OpenJDK - jdk\src\share\native\java\io\FileInputStream.c:Java_java_io_FileInputStream_open0()
					* OpenJDK - jdk\src\solaris\native\java\io\io_util_md.c:fileOpen()
					* OpenJDK - jdk\src\solaris\native\java\io\io_util_md.c:handleOpen()
						* open0() 的核心就是使用 open64(path, O_RDONLY, 0666) 打开文件，返回的描述符被设置到 FileInputStream.fd.fd 字段上
				*  native void close0()
					* OpenJDK - jdk\src\share\native\java\io\FileInputStream.c:Java_java_io_FileInputStream_close0()
					* OpenJDK - jdk\src\solaris\native\java\io\io_util_md.c:fileClose()
						* close0() 的核心就是获取 FileInputStream.fd.fd 并使用 close() 关闭该文件描述符，并将其值设置为 -1
						* 注：如果是标准输入流0，标准输出流1，标准错误流2，则不关闭他们，而是将其重定向到 /dev/null，使用 dup2()
				* native long skip0(long n)
					* OpenJDK - jdk\src\share\native\java\io\FileInputStream.c:Java_java_io_FileInputStream_skip0()
					* OpenJDK - jdk\src\solaris\native\java\io\io_util_md.h:IO_Lseek
						* skip0() 的核心就是获取 FileInputStream.fd.fd 并使用 lseek()/lseek64() 定位偏移
				* native int read0() / native int readBytes(byte b[], int off, int len)
					* OpenJDK - jdk\src\share\native\java\io\FileInputStream.c:Java_java_io_FileInputStream_read0()
					* OpenJDK - jdk\src\share\native\java\io\FileInputStream.c:Java_java_io_FileInputStream_readBytes()
					* OpenJDK - jdk\src\share\native\java\io\io_util.c:readSingle()
					* OpenJDK - jdk\src\share\native\java\io\io_util.c:readBytes()
					* OpenJDK - jdk\src\solaris\native\java\io\io_util_md.h:IO_Read
					* OpenJDK - jdk\src\solaris\native\java\io\io_util_md.c:handleRead()
						* 核心就是获取 FileInputStream.fd.fd 并使用 read(fd, buf, len) 读取数据，返回读取字节数，若为 0，则表示已经读取到流末尾
				* native int available0()
					* OpenJDK - jdk\src\share\native\java\io\FileInputStream.c:Java_java_io_FileInputStream_available0()
					* OpenJDK - jdk\src\solaris\native\java\io\io_util_md.h:IO_Available
					* OpenJDK - jdk\src\solaris\native\java\io\io_util_md.c:handleAvailable()
						* 核心就是获取 FileInputStream.fd.fd ，然后区分 fd 的类型，分别处理：
							* 如果是常规的文件，则使用 fstat64() 获取总字节数，减去使用 lseek()/lseek64() 获取的当前位置偏移量
							* 如果是 socket、pipe、字符设备等文件，则使用 ioctl 获取接受缓冲区总字节数，减去使用 lseek()/lseek64() 获取的当前位置偏移量
								* ioctl(fd, FIONREAD, &n) - 系统调用，得到 fd 描述符缓冲区里有多少字节要被读取，然后将字节数放入 n 里
		* FileOutputStream
			* 继承
				* OutputStream
			* 解析
				* 字节流、节点流
				* 非线程安全；直接 IO 支持，底层的 flush() 为空操作
				* FileOutputStream.fd 字段是一个 FileDescriptor 类型的对象，它是 Java 虚拟机用来对文件定位的。对它的解析请看下面“工具类”一段
				* static native void initIDs() / native void open0(String path, boolean append) / native void close0()
					* 与 FileInputStream 一样，解析请看上文。其中 open 使用 O_WRONLY | O_CREAT 模式打开
						* 注：这里 open0() 有一个 append 参数，底层 open() 函数的 flags 在 append=true 设置为 O_APPEND，否则为 O_TRUNC
				* native void write(int b, boolean append) / native void writeBytes(byte b[], int off, int len, boolean append)
					* OpenJDK - jdk\src\share\native\java\io\FileInputStream.c:Java_java_io_FileOutputStream_write0()
					* OpenJDK - jdk\src\share\native\java\io\FileInputStream.c:Java_java_io_FileOutputStream_writeBytes()
					* OpenJDK - jdk\src\share\native\java\io\io_util.c:writeSingle()
					* OpenJDK - jdk\src\share\native\java\io\io_util.c:writeBytes()
					* OpenJDK - jdk\src\solaris\native\java\io\io_util_md.h:IO_Append
					* OpenJDK - jdk\src\solaris\native\java\io\io_util_md.h:IO_Write
					* OpenJDK - jdk\src\solaris\native\java\io\io_util_md.c:handleWrite()
						* 核心就是获取 FileOutputStream.fd.fd 并使用 write(fd, buf, len) 写入数据，返回写入字节数，若为 -1，表示写入失败
						* handleWrite 会循环执行 write 系统调用，除非发生异常，否则会将全部数据写入成功后才返回
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
	* StreamEncoder
	* StreamDecoder
	* Charset
	* FileDescriptor
		* FileDescriptor 表示一个文件描述符。可以是一个打开的普通文件或者 socket 或者字节序列
			* 主要用在 FileInputStream 和 FileOutputStream 中，应用程序不应该直接创建 FileDescriptor 实例
		* FileDescriptor.fd 是文件的描述符。即，操作系统中的整型的文件描述符
		* FileDescriptor.handle 是文件的句柄。即，操作系统用中来操作文件资源的句柄
			* OpenJDK - jdk\src\windows\native\java\io\FileDescriptor_md.c:Java_java_io_FileDescriptor_set()
			* OpenJDK - jdk\src\windows\native\java\io\io_util_md:SET_HANDLE
			* 目前只在 windows 下使用，并只有 FileDescriptor.in/out/err 用户获取标准 IO 资源，比如 GetStdHandle(STD_INPUT_HANDLE)
				* 在 solaris 上 handle 为 -1
		* static native void initIDs()
			* OpenJDK - jdk\src\share\native\java\io\FileDescriptor_md.c:Java_java_io_FileDescriptor_initIDs()
				* 用于初始化 FileDescriptor 中 fd 字段 ID，之后可根据该 ID 和 FileDescriptor 实例获取 fd 字段的引用
		* FileDescriptor 会保存所有持有它的流对象引用，从而可以做到其中任何一个流在关闭时，与之关联的所有流都可关闭。简易版观察者模式
			* 比如 FileInputStream.close() 时，会调用了 FileDescriptor.closeAll(), 它会关闭所有持有它的流对象
				* 注：FileInputStream 有个构造函数是由外部传递一个 FileDescriptor 对象，这就可能有多个 FileInputStream 引用同一个 fd 对象
				* 你不应该自己创建 FileDescriptor 实例，因为系统得文件描述符由流对象 native 关联的，但可以获取 FileInputStream 中的 fd 对象
					* 具体关联操作请查看上面 FileInputStream 的解析


## net框架 - bio
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

## net框架 - nio


## net框架 - http


## 线程框架 - thread


## 线程池框架 - executor


## Fork-Join框架


## 日志框架 - logger


## 序列化机制


## 类加载机制


## JDBC 库

