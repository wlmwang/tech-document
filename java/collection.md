# 集合框架 - collection
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
					* 作用是：总是会分配一个新数组的内存空间，然后将旧的 original 内存数据按字节复制到新内存中。底层原理请看“工具类”中的解析
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
				* 拷贝数组内存数据使用的是 System.arraycopy()
				* static native void arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
					* jdk/src/share/native/java/lang/System.c:arraycopy()
					* hotspot/src/share/vm/prims/jvm.cpp:JVM_ArrayCopy
					* hotspot/src/share/vm/oops/objArrayKlass.cpp:copy_array() -> do_copy()
					* hotspot/src/share/vm/utilities/copy.hpp:conjoint_oops_atomic()
					* hotspot/src/os_cpu/solaris_x86/vm/copy_solaris_x86.inline.hpp:pd_conjoint_jints_atomic()
					* hotspot/src/cpu/zero/vm/copy_zero.hpp:_Copy_conjoint_jints_atomic()
						* 核心就是使用循环，逐个复制 src 数据到 dst 内存中
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

