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
		* 三个构造函数
			* ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue)
			* ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler)
			* ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler)
				* 当线程池小于 corePoolSize 时，新提交任务将创建一个新线程执行任务，即使此时线程池中存在空闲线程
				* 当线程池超过 corePoolSize 时，新提交任务将被放入 workQueue 中，等待线程池中任务调度执行
				* 当 workQueue 已满，且当前线程数小于 maximumPoolSize 时，新提交任务会创建新线程执行任务
				* 当提交任务数超过 maximumPoolSize 时，新提交任务由 RejectedExecutionHandler 处理；maximumPoolSize 表示最多能创建多少个线程
				* 当线程池中超过 corePoolSize 线程，空闲时间达到 keepAliveTime 时，将释放空闲线程，直到线程池中的线程数不超过 corePoolSize
				* 当设置 allowCoreThreadTimeOut(true) 时，线程池中 corePoolSize 线程空闲时间达到 keepAliveTime 也将关闭，直到线程池中的线程数为 0
				* 注1：当使用同步无界阻塞队列 SynchronousQueue 时，提交的任务（生产者）在未被消费之前，总是会新建线程来执行该任务（消费者）
				* 注2：当设置了 allowCoreThreadTimeOut 或工作线程超过了 maximumPoolSize，工作线程获取任务从死循环中跳出，从而退出空闲的工作线程
			* 理念：线程池大部分时间仅使用核心线程，即使有新的任务在等待执行，这就是并发核心线程数的节流阀；若挤压的任务非常多，线程池尝试运行更多的线程来执行，这是最大线程数的节流阀
		* ThreadPoolExecutor.ctl 字段是一个打包了两个概念字段的原子整型。其中高 3 位存储线程池状态，低 29 位存储线程数量
			* runStateOf(int c) 指示有效线程数。[0, (2^29)-1]
			* workerCountOf(int c) 指示线程池状态。[-1, 3]，其中 -1 为正在运行；0 为平滑关闭；1 为强制关闭；3 为已关闭
			* ThreadPoolExecutor.ctl 初始化时，值为正在运行 ThreadPoolExecutor.RUNNING，0个线程
		* ThreadPoolExecutor.Worker 为工作线程。其内部 Thread 为非“分离式”线程，即，进程会等待线程池中所有线程结束
			* ThreadPoolExecutor.Worker.firstTask 字段保存的一般为创建时提交的任务，除此之外，工作线程将从阻塞队列中获取任务来执行
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
					* 线程池的线程大小一旦达到最大值就会保持不变，如果某个线程因为执行异常而结束，那么线程池会补充一个新线程
					* 由于是无界队列，线程池最多创建 corePoolSize 个线程，maximumPoolSize 被忽略
			* 底层创建 ThreadPoolExecutor 线程池实例
				* 核心线程数量与最大线程数量相同 ThreadPoolExecutor.corePoolSize == ThreadPoolExecutor.maximumPoolSize
				* 每个线程的存活时间是无限的 ThreadPoolExecutor.keepAliveTime == 0
					* 获取任务来执行的函数是一个死循环
				* 阻塞队列是无界（最大为 Integer.MAX_VALUE）的 LinkedBlockingQueue<Runnable>
					* 新增任务时，如果所有线程都在繁忙，任务会进入阻塞队列中
			* 理念：线程池大部分时间仅使用核心线程，即使有新的任务在等待运行，严格控制并发线程数
	* static ExecutorService newCachedThreadPool()
		* 特性
			* 创建一个可缓存的线程池
				* 每次提交一个任务时，线程池可以智能的添加新线程来处理任务
					* 如果线程池的大小超过了处理任务所需要的线程，那么就会回收部分空闲（60s 不执行任务）的线程
					* 此线程池不会对线程大小做限制，线程池大小完全依赖于操作系统（或者说 JVM）能够创建的最大线程大小
			* 底层创建 ThreadPoolExecutor 线程池实例
				* 核心线程为 0，并不限最大线程数 ThreadPoolExecutor.maximumPoolSize == Integer.MAX_VALUE
				* 每个线程的存活时间为 60s，ThreadPoolExecutor.keepAliveTime == TimeUnit.SECONDS.toNanos(60)
				* 阻塞队列是同步无界的 SynchronousQueue<Runnable>（并使用非公平策略，即栈）
					* 当有新任务到来，则插入到 SynchronousQueue 中，由于 SynchronousQueue 是同步队列，因此会在池中寻找可用的线程来执行
					* 若有可用线程则执行，若没有可用线程则创建一个线程来执行该任务；若池中线程空闲时间超过指定时间，则该线程会被销毁
					* 注：提交的任务（生产者）在未被消费之前，总是会新建线程来执行该任务（消费者）
						* 新建任务时，如果队列中无消费线程，则立即返回 null 表示添加失败，以便新建一个线程来执行该任务，之后再队列中获取任务来执行
							* 关键点：添加时不阻塞等待线程消费任务，而直接返回失败，再新建线程来消费；新建线程消费任务时，是阻塞等待的（可设置超时时间）
							* SynchronousQueue.offer(command) -> TransferStack.transfer(command, true, 0) != null // 第二个参数不等待
							* SynchronousQueue.take() -> TransferStack.transfer(null, false, 0)	// 新建消费者节点，并无限阻塞等待一个任务
							* poll(keepAliveTime, TimeUnit.NANOSECONDS)  -> TransferStack.transfer(null, true, TimeUnit.NANOSECONDS.toNanos(keepAliveTime))
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
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	});
}

executor.shutdown();
System.out.println("main thread");
```
