# 线程框架 - executor

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
		* 三个构造函数
			* ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue)
			* ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler)
			* ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler)
				* 当线程池小于 corePoolSize 时，新提交任务将创建一个新线程来执行任务，即使此时线程池中存在空闲线程
				* 当线程池超过 corePoolSize 时，新提交任务将被放入 workQueue 中，等待线程池中空闲线程来调度执行
				* 当 workQueue 已满，且当前线程数小于 maximumPoolSize 时，新提交任务会创建新的线程来执行
				* 当提交任务数超过 maximumPoolSize 时，新提交任务由 RejectedExecutionHandler 处理；maximumPoolSize 表示最多能创建多少个线程
				* 当线程池超过 corePoolSize 线程，并有线程空闲时间达到 keepAliveTime 时，将释放该线程，直到线程池中的线程数不超过 corePoolSize
					* 当设置了 allowCoreThreadTimeOut 或工作线程超过了 corePoolSize 时，工作线程获取任务的方法将可能从死循环中返回，而退出该空闲线程
						* 空闲时间达到 keepAliveTime 为空闲线程；当设置了 allowCoreThreadTimeOut，池会关闭所有空闲线程，否则会保留最多 corePoolSize 个线程
				* 注：当使用同步无界阻塞队列 SynchronousQueue 时，提交任务的线程（生产者）在队列中没有消费者之前会立即失败，这样使它总是会先新建线程来待处理任务（消费者）
			* 理念：线程池大部分时间仅使用核心线程，即使有新的任务在等待执行，这就是并发核心线程数的节流阀；若挤压的任务非常多，线程池尝试运行更多的线程来执行，这是最大线程数的节流阀
		* ThreadPoolExecutor.ctl 字段是一个打包了两个概念字段的原子整型。其中高 3 位存储线程池状态，低 29 位存储线程数量
			* runStateOf(int c) 指示有效线程数。[0, (2^29)-1]
			* workerCountOf(int c) 指示线程池状态。[-1, 3]
				* RUNNING  --- 接受并处理任务
				* SHUTDOWN  --- 不接受新任务，但会处理老任务
				* STOP  --- 不接受新任务，也不处理老任务，并且会中断正在处理任务的线程
				* TIDYING  --- 所有任务结束，且工作线程为空，线程状态变为 TIDYING，并运行 terminated() 方法
				* TERMINATED  --- terminated() 方法执行结束
				* 状态切换
					* RUNNING -> SHUTDOWN  --- 当调用了 shutdown() 时  --- 平滑关闭
						* 工作线程循环获取任务时，检测到 SHUTDOWN 状态且队列为空时，立即跳出循环并递减线程数量
						* 注1：对空闲线程，shutdown() 方法中会立即循环发送中断请求给所有空闲的 worker 工作线程，防止空闲线程被阻塞在队列获取调用上
						* 注2：shutdown() 不等待先前提交的任务执行完成。使用 awaitTermination(long timeout, TimeUnit unit) 可以做到这一点
					* (RUNNING or SHUTDOWN) -> STOP  --- 当调用了 shutdownNow() 时  --- 强制关闭
						* 工作线程循环获取任务时，检测到 STOP 状态时，立即跳出循环并递减线程数量
						* 注：shutdownNow() 方法中会立即循环发送中断请求给所有 worker 工作线程
					* SHUTDOWN -> TIDYING  --- 当队列和工作线程都为空时
					* STOP -> TIDYING  --- 当工作线程为空时
					* TIDYING -> TERMINATED  --- 当钩子方法 terminated() 完成时
			* ThreadPoolExecutor.ctl 初始化时，值为正在运行 ThreadPoolExecutor.RUNNING，0个线程
		* ThreadPoolExecutor.Worker 为工作线程。其内部 Thread 为非“分离式”线程，即，进程会等待线程池中所有线程结束
			* ThreadPoolExecutor.Worker.firstTask 字段保存的一般为创建时提交的任务，除此之外，工作线程将从阻塞队列中获取任务来执行
			* ThreadPoolExecutor.addWorker(null, false) 为增加一个工作线程（消费者）；第一个参数为执行体即为生产者
* java.util.concurrent.Executors.DefaultThreadFactory
	* 继承
		* ThreadFactory
	* 解析
		* 一个创建非“分离式”线程的工厂类
* java.uitl.concurrent.ThreadPoolExecutor.AbortPolicy
	* 继承
		* RejectedExecutionHandler
	* 解析
		* 某人抛出 RejectedExecutionException 异常的拒绝处理器
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
					* 线程池的线程大小一旦达到最大值就会保持不变，如果某个线程因为执行异常而结束，那么线程池会补充一个新线程
					* 由于是无界队列（最大为 Integer.MAX_VALUE），线程池最多创建 corePoolSize 个线程，maximumPoolSize 被忽略
			* 底层创建 ThreadPoolExecutor 线程池实例
				* 核心线程数量与最大线程数量相同 ThreadPoolExecutor.corePoolSize == ThreadPoolExecutor.maximumPoolSize
				* 每个线程的存活时间是无限的 ThreadPoolExecutor.keepAliveTime == 0
					* 获取任务的方法是一个死循环
				* 阻塞队列是无界（最大为 Integer.MAX_VALUE）的 LinkedBlockingQueue<Runnable>
					* 新增任务时，如果所有线程都在繁忙，任务会进入阻塞队列中
			* 场景：线程池大部分时间仅使用核心线程，即使有新的任务在等待运行。可用来严格控制并发线程数
	* static ExecutorService newCachedThreadPool()
		* 特性
			* 创建一个可缓存的线程池
				* 每次提交一个任务时，线程池可以智能的添加新线程来处理任务
					* 如果线程池的大小超过了处理任务所需要的线程，那么就会回收部分空闲（60s 不执行任务）的线程
					* 此线程池不会对线程大小做限制，线程池大小完全依赖于操作系统（或者说 JVM）能够创建的最大线程大小
			* 底层创建 ThreadPoolExecutor 线程池实例
				* 核心线程为 0，并不限最大线程数 ThreadPoolExecutor.maximumPoolSize == Integer.MAX_VALUE
				* 每个线程的存活时间为 60s，ThreadPoolExecutor.keepAliveTime == TimeUnit.SECONDS.toNanos(60)
				* 阻塞队列是同步无界的 SynchronousQueue<Runnable>（并使用非公平策略，内部使用栈结构）
					* 当有新任务到来，则插入到 SynchronousQueue 中，由于 SynchronousQueue 是同步队列，因此会在池中寻找可用的线程来执行
						* 若有可用线程则执行，若没有可用线程则创建一个线程来执行该任务；若池中线程空闲时间超过指定时间，则该线程会被销毁
					* 注：提交任务的线程（生产者）在队列中没有消费者之前会立即失败，这样使它总是会先新建线程来待处理任务（消费者）
						* 新建任务时，如果队列中无消费线程，则立即返回 null 表示添加失败，以便新建一个线程来消费任务；新建工作线程时，进入循环阻塞等待直到有任务
							* SynchronousQueue.offer(command) -> TransferStack.transfer(command, true, 0)  // 第二个参数表示不等待
							* SynchronousQueue.take() -> TransferStack.transfer(null, false, 0)  // 新建消费者的节点，获取任务时会无限阻塞等待一个任务
							* SynchronousQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS)  -> TransferStack.transfer(null, true, TimeUnit.NANOSECONDS.toNanos(keepAliveTime))  // 新建消费者的节点，获取任务时会超时阻塞等待一个任务
			* 场景：线程池大多数时间都是工作在低谷期，不过系统在高峰与低谷期，任务并发量相差较大。可用来节省系统资源并兼顾系统在高峰期时的性能
	* static ExecutorService newSingleThreadExecutor()
		* 特性
			* 创建一个单线程的线程池
			* 它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序（FIFO, LIFO, 优先级）执行
			* 注：可以看作是 FixedThreadPool 单线程版本
	* static ScheduledExecutorService newScheduledThreadPool(int corePoolSize)
		* 特性
			* 创建一个固定大小的线程池，支持定时及周期性任务执行；当 corePoolSize == 0 时，仅会创建一个线程来执行任务
				* 每次提交一个任务时，总是会添加至 ScheduledThreadPoolExecutor.DelayedWorkQueue 队列中，并在必要时启动线程以运行它
					* 我们无法预先启动线程来运行任务，因为该任务可能还不应该运行
					* DelayedWorkQueue 是一个按照超时时间排序的最小堆优先队列（延时加上当前时间即为超时时间）。此处延时时间都为0，也就是没有延时
				* 每个线程的存活时间是无限的 ThreadPoolExecutor.keepAliveTime == 0
					* 获取任务的方法是一个死循环
				* ScheduledThreadPoolExecutor.scheduleAtFixedRate() 为周期执行任务接口
			* 底层创建 ScheduledThreadPoolExecutor 线程池实例，它是 ThreadPoolExecutor 子类
				* 核心线程为 corePoolSize，并不限最大线程数 ThreadPoolExecutor.maximumPoolSize == Integer.MAX_VALUE
				* 每个线程的存活时间是无限的 ThreadPoolExecutor.keepAliveTime == 0
					* 获取任务的方法是一个死循环

## 关键源码


## 示例代码
* FixedThreadPool
```
// FixedThreadPool
ExecutorService executor = Executors.newFixedThreadPool(4);
for (int i = 0; i < 10; i++) {
	final int ii = i;
	executor.execute(() -> {
		System.out.println("线程名称：" + Thread.currentThread().getName() + "，执行" + ii);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	});
}

executor.awaitTermination(50000, TimeUnit.MILLISECONDS);
executor.shutdown();
System.out.println("main thread");

Output:
线程名称：pool-1-thread-2，执行1
main thread
线程名称：pool-1-thread-1，执行0
线程名称：pool-1-thread-3，执行2
线程名称：pool-1-thread-4，执行3
// --- sleep 3s
线程名称：pool-1-thread-2，执行4
线程名称：pool-1-thread-3，执行5
线程名称：pool-1-thread-1，执行6
线程名称：pool-1-thread-4，执行7
// --- sleep 3s
线程名称：pool-1-thread-2，执行8
线程名称：pool-1-thread-1，执行9
```

* CachedThreadPool
```
// CachedThreadPool
ExecutorService executor = Executors.newCachedThreadPool();
for (int i = 0; i < 10; i++) {
	final int ii = i;
	executor.execute(() -> {
		System.out.println("线程名称：" + Thread.currentThread().getName() + "，执行" + ii);
	});
}

executor.awaitTermination(50000, TimeUnit.MILLISECONDS);
executor.shutdown();
System.out.println("main thread");

Output:
线程名称：pool-1-thread-1，执行0
线程名称：pool-1-thread-2，执行1
线程名称：pool-1-thread-3，执行2
线程名称：pool-1-thread-1，执行9
main thread
线程名称：pool-1-thread-4，执行3
线程名称：pool-1-thread-5，执行4
线程名称：pool-1-thread-6，执行5
线程名称：pool-1-thread-7，执行6
线程名称：pool-1-thread-8，执行7
线程名称：pool-1-thread-9，执行8
```

* ScheduledThreadPool
```
// ScheduledThreadPool
ExecutorService executor = Executors.newScheduledThreadPool(4);
for (int i = 0; i < 10; i++) {
	final int ii = i;
	executor.execute(() -> {
		System.out.println("线程名称：" + Thread.currentThread().getName() + "，执行" + ii);
	});
}

executor.awaitTermination(50000, TimeUnit.MILLISECONDS);
executor.shutdown();
System.out.println("main thread");

Output:
线程名称：pool-1-thread-1，执行0
main thread
线程名称：pool-1-thread-1，执行2
线程名称：pool-1-thread-1，执行3
线程名称：pool-1-thread-1，执行4
线程名称：pool-1-thread-1，执行5
线程名称：pool-1-thread-2，执行1
线程名称：pool-1-thread-2，执行7
线程名称：pool-1-thread-2，执行8
线程名称：pool-1-thread-2，执行9
线程名称：pool-1-thread-1，执行6


// schedule
ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
for (int i = 0; i < 10; i++) {
	final int ii = i;
	executor.scheduleAtFixedRate(() -> {
		System.out.println("线程名称：" + Thread.currentThread().getName() + "，执行" + ii);
	}, 0, 3, TimeUnit.SECONDS);
}

executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
executor.shutdown();
System.out.println("main thread");

Output:
线程名称：pool-1-thread-1，执行0
线程名称：pool-1-thread-1，执行1
线程名称：pool-1-thread-1，执行2
线程名称：pool-1-thread-1，执行3
线程名称：pool-1-thread-1，执行4
线程名称：pool-1-thread-1，执行5
线程名称：pool-1-thread-1，执行6
线程名称：pool-1-thread-1，执行7
线程名称：pool-1-thread-1，执行8
线程名称：pool-1-thread-1，执行9
线程名称：pool-1-thread-1，执行0
线程名称：pool-1-thread-2，执行1
线程名称：pool-1-thread-1，执行4
线程名称：pool-1-thread-3，执行3
线程名称：pool-1-thread-4，执行2
线程名称：pool-1-thread-1，执行6
线程名称：pool-1-thread-3，执行7
线程名称：pool-1-thread-2，执行5
线程名称：pool-1-thread-1，执行9
线程名称：pool-1-thread-4，执行8
main thread
```
