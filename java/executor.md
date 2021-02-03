# 线程框架 - thread

## 基础容器
#### 接口
* ExecutorService - Executor
* ScheduledExecutorService - ExecutorService
* BlockingQueue - Queue
* ThreadFactory
* RejectedExecutionHandler

#### 实现
* java.uitl.concurrent.ThreadPoolExecutor
	* 继承
		* AbstractExecutorService - ExecutorService - Executor
	* 解析
		* 接口
			* ThreadPoolExecutor(int corePoolSize,int maximumPoolSize,long keepAliveTime,TimeUnit unit,BlockingQueue<Runnable> workQueue)
				* 当线程池小于 corePoolSize 时，新提交任务将创建一个新线程执行任务，即使此时线程池中存在空闲线程；是预创建线程的意思
				* 当线程池达到 corePoolSize 时，新提交任务将被放入 workQueue 中，等待线程池中任务调度执行
				* 当 workQueue 已满，且 maximumPoolSize>corePoolSize 时，新提交任务会创建新线程执行任务
				* 当提交任务数超过 maximumPoolSize 时，新提交任务由 RejectedExecutionHandler 处理；maximumPoolSize 表示最多能创建多少个线程
				* 当线程池中超过 corePoolSize 线程，空闲时间达到 keepAliveTime 时，将释放空闲线程，直到线程池中的线程数不超过 corePoolSize
				* 当设置 allowCoreThreadTimeOut(true) 时，线程池中 corePoolSize 线程空闲时间达到 keepAliveTime 也将关闭，直到线程池中的线程数为 0
* java.uitl.concurrent.ThreadPoolExecutor.AbortPolicy
	* 继承
		* RejectedExecutionHandler
	* 解析
		* ...
* java.util.concurrent.Executors.DefaultThreadFactory
	* 继承
		* ThreadFactory
	* 解析
		* ...
* java.uitl.concurrent.ScheduledThreadPoolExecutor
	* 继承
		* ThreadPoolExecutor
	* 解析
		* ...

## 工具类
* java.util.concurrent.Executors
	* static ExecutorService newFixedThreadPool(int nThreads)
		* 特性
			* 创建固定大小的线程池
			* 每次提交一个任务就创建一个线程，直到线程达到线程池的最大大小
			* 线程池的大小一旦达到最大值就会保持不变，如果某个线程因为执行异常而结束，那么线程池会补充一个新线程
		* 描述
			* 创建可容纳固定数量线程的池子，每个线程的存活时间是无限的，当池子满了就不再添加线程了；如果池中的所有线程均在繁忙状态，
			* 对于新任务会进入阻塞队列中（无界的阻塞队列）
		* 解析
			* 一般如果线程池任务队列采用 LinkedBlockingQueue 队列的话，那么不会拒绝任何任务（因为其大小为 Integer.MAX_VALUE）
			* 这种情况下，ThreadPoolExecutor 最多仅会按照最小线程数 corePoolSize 来创建线程，也就是说线程池大小被忽略了
	* static ExecutorService newCachedThreadPool()
		* 特性
			* 创建一个可缓存的线程池
			* 如果线程池的大小超过了处理任务所需要的线程，那么就会回收部分空闲（60秒不执行任务）的线程
			* 当任务数增加时，此线程池又可以智能的添加新线程来处理任务
			* 此线程池不会对线程池大小做限制，线程池大小完全依赖于操作系统（或者说JVM）能够创建的最大线程大小
		* 描述
			* 当有新任务到来，则插入到 SynchronousQueue 中，由于 SynchronousQueue 是同步队列，因此会在池中寻找可用线程来执行，
			* 若有可以线程则执行，若没有可用线程则创建一个线程来执行该任务；若池中线程空闲时间超过指定时间，则该线程会被销毁
	* static ExecutorService newSingleThreadExecutor()
		* 特性
			* 创建一个单线程化的线程池
			* 它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序（FIFO, LIFO, 优先级）执行
		* 描述
			* 创建只有一个线程的线程池，当该线程正繁忙时，对于新任务会进入阻塞队列中（无界的阻塞队列）
	* static ScheduledExecutorService newScheduledThreadPool(int corePoolSize)
		* 特性
			* 创建一个定长线程池，支持定时及周期性任务执行
			* 延迟执行
		* 描述
			* 创建一个固定大小的线程池，线程池内线程存活时间无限制，线程池可以支持定时及周期性任务执行，如果所有线程均处于繁忙状态，
			* 对于新任务会进入 DelayedWorkQueue 队列中，这是一种按照超时时间排序的队列结构
* java.util.ArrayBlockingQueue<E>
	* 一个由数组结构组成的有界阻塞队列
* java.util.concurrent.LinkedBlockingQueue<E>
	* 一个由链表结构组成的有界阻塞队列（常用）
* java.util.concurrent.LinkedBlockingDeque<E>
	* 一个由链表结构组成的双向阻塞队列
* java.util.concurrent.PriorityBlockingQueue<E>
	* 一个支持优先级排序的无界阻塞队列
* java.util.DelayQueue<E>
	* 一个使用优先级队列实现的无界阻塞队列
* java.util.concurrent.SynchronousQueue<E>
	*  一个不存储元素的阻塞队列（常用）
* java.util.concurrent.LinkedTransferQueue<E>
	* 一个由链表结构组成的无界阻塞队列


* 一般如果线程池任务队列采用LinkedBlockingQueue队列的话，那么不会拒绝任何任务（因为其大小为Integer.MAX_VALUE），这种情况下，ThreadPoolExecutor最多仅会按照最小线程数corePoolSize来创建线程，也就是说线程池大小被忽略了。
* 如果线程池任务队列采用ArrayBlockingQueue队列，初始化设置了最大队列数。那么ThreadPoolExecutor的maximumPoolSize才会生效，那么ThreadPoolExecutor的maximumPoolSize才会生效会采用新的算法处理任务，
* 例如假定线程池的最小线程数为4，最大为8，ArrayBlockingQueue最大为10。随着任务到达并被放到队列中，线程池中最多运行4个线程（即核心线程数）直到队列完全填满，也就是说等待状态的任务小于等于10，ThreadPoolExecutor也只会利用4个核心线程线程处理任务。
* 如果队列已满，而又有新任务进来，此时才会启动一个新线程，这里不会因为队列已满而拒接该任务，相反会启动一个新线程。新线程会运行队列中的第一个任务，为新来的任务腾出空间。如果线程数已经等于最大线程数，任务队列也已经满了，则线程池会拒绝这个任务，默认拒绝策略是抛出异常。
* 这个算法背的理念是：该池大部分时间仅使用核心线程（4个），即使有适量的任务在队列中等待运行。这时线程池就可以用作节流阀。如果挤压的请求变得非常多，这时该池就会尝试运行更多的线程来清理；这时第二个节流阀—最大线程数就起作用了

## 关键源码

