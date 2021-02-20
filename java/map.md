# 集合框架 - map

## 基础容器
#### 接口
* Map
* SortedMap - Map
* NavigableMap - SortedMap - Map
* 特性
	* Map - 存储 k-v 键值对的无序映射表；可根据 k 访问 v；键数据类型依赖 equals(), hashCode()
		* 部分接口会使“值”也依赖 equals()，如：containsValue。如果使用 EntrySet 视图，“值”会依赖 equals()
	* SortedMap - 存储 k-v 键值对的有序映射表；可根据 k 访问 v；键数据类型除依赖 equals(), hashCode()，还依赖 compareTo()
	* NavigableMap - 增强了范围查找。比如：获取（并删除）最小值、最大值；大于（等于）某个key的最小值；小于（等于）某个key的最大值等

#### 实现
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
				* 注：HashMap.Node 重写了 hashCode() 方法，让 k-v 都参与计算。主要是为了 HashMap.entrySet() 返回的 Set 集的唯一性要求
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
	* 继承
		* AbstractMap
		* NavigableMap - SortedMap
		* Map
		* Cloneable
		* Serializable
	* 解析
		* TreeMap<K,V> 使用 TreeMap.Entry<K,V> - Map.Entry<K,V> 数据结构包装键、值对
			* TreeMap.Entry<K,V> 中 left/right/parent 字段用来支持二叉查找树算法，color 字段用来支持红黑树算法
				* 注：TreeMap.Entry 重写了 hashCode() 方法，让 k-v 都参与计算。主要是为了 TreeMap.entrySet() 返回的 Set 集的唯一性要求
		* TreeMap 的三个视图：键值视图 TreeMap<K,V>.entrySet()、键视图 TreeMap<K,V>.keySet()、值视图 TreeMap<K,V>.values() 特性与 HashMap 描述大致相同
			* 区别是：这三者的迭代结果是按序的，都是按照 K 的大小
				* 关键算法：初始化迭代器时，将查找树中最小值保存至 PrivateEntryIterator.next 中，之后使用 TreeMap.successor() 遍历前驱节点（大于 next 的最小值）
		* 关于红黑树：本文档不会详细描述（如果确实有需要，可能会写个单独文档），这里只给出它的核心描述
			* 红黑树结构、特性（规则）并不复杂，还是容易理解的。但写一个红黑树绝非易事，其复杂性来源于插入、删除时节点颜色繁多状态，算法必须穷举所有状态做响应调整
			* 红黑树本质上是一个二叉查找树，增强的是它可以“自平衡”，从而能保证该二叉查找树没有任何一个节点深度过大，直观上的最佳平衡条件是每个节点的左右子树有着相同的高度
				* 理解红黑树，先要理解二叉查找树。因为即使从代码上看，红黑树也只是在二叉查找树的算法上增加了红黑平衡处理而已（你甚至可以分离出一个纯净的二叉查找树算法）
					* 二叉查找树：任何节点的键值一定大于其左子树中的每一个节点的键值，并小于其右子树中每一个节点的键值
				* 红黑树最核心的是：它可以“自平衡”。它用五个特性（规则）来保证：
					* 1. 每个结点要么是红的、要么是黑的
					* 2. 根结点是黑的
					* 3. 每个叶结点（叶结点即指树尾端 NIL 指针或 NULL 结点）都是黑的
					* 4. 如果一个结点是红的，那么它的两个儿子都是黑的
					* 5. 对于任意结点而言，其到叶结点树尾端 NIL 指针的每条路径都包含相同数目的黑结点
					* 注1：对于第 3 点，叶节点一般是作为一个子树结束的标志位，在 TreeMap 里就是一个 NULL。它不影响红黑树的平衡性
					* 注2：对于第 4、5 点，它保证了一棵含有 n 个节点的红黑树的高度至多为 2log(n)，即红黑相间；它查找、插入、删除的时间复杂度为 O(logn)
						* RB-tree 并不是一个完美平衡二叉查找树，高度至多为 2log(n)
						* AVL-tree 树也不是一个完美平衡二叉查找树，高度至多为 log(n) + 1
						* 完美平衡二叉查找树，高度为 log(n)
				* 红黑树能自平衡，通过三种重要操作：左旋、右旋和变色
					* 左旋：以某个结点作为支点，其右子结点变为旋转结点的父结点，右子结点的左子结点变为旋转结点的右子结点，左子结点保持不变
					* 右旋：以某个结点作为支点，其左子结点变为旋转结点的父结点，左子结点的右子结点变为旋转结点的左子结点，右子结点保持不变
					* 变色：结点的颜色由红变黑或由黑变红
					* 注1：左旋只影响旋转结点和其右子树的结构，把右子树的结点往左子树挪了
					* 注2：右旋只影响旋转结点和其左子树的结构，把左子树的结点往右子树挪了
					* 注3：三种操作都不会改变二叉查找树的特性。但左右旋可以改变树的结构（改变某个节点的深度），再配合变色使红黑树重新符合要求
* LinkedHashMap
* IdentityHashMap
* WeakHashMap
* Hashtable
* Dictionary
* ConcurrentHashMap
* ConcurrentSkipListMap

## 迭代器
#### 接口
	* EntryIterator
	* HashIterator
	* KeyIterator
	* ValueIterator
	* EntrySpliterator
	* KeySpliterator
	* 解析
		* 不会直接使用，而使用键、值、键值视图
		* 迭代器的解析，请看《集合框架 - Collection》

## 工具类
* Collections
	* synchronizedMap(Map<K,V> m)
	* synchronizedSortedMap(SortedMap<K,V> m)
		* 将非线程安全的 Map 集合转换为线程安全的
		* 算法思想与 synchronizedList/synchronizedSet 没有本质区别 - 请查看《集合框架 - collection》说明
			* 不过，相比集合，Map 有视图，会返回线程安全版本，即 SynchronizedSet/SynchronizedCollection
		* 注：一般我们不使用这种封装的线程安全 Map 映射，而使用性能更好的 ConcurrentHashMap

## 关键源码

