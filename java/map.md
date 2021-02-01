# 集合框架 - map
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
						* 扩容后，每个元素无需用 e.hash & (newCap-1) 进行逐个计算桶索引，只需要计算 e.hash & oldCap
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

