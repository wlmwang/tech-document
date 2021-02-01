# 线程框架 - thread
* 基础组件
	* 接口
		* Runnable
	* 实现
		* Thread
			* enum Thread.State
				* 线程对象实例刚创建出来还没 start()，处于 Thread.State.NEW
				* 线程在 JVM 中运行了，需要去竞争资源，例如 CPU，处于 Thread.State.RUNNABLE
				* 线程等待获取对象监视器锁，锁被别人拿着就阻塞，处于 Thread.State.BLOCKED
				* 线程进入等待池了，等待唤醒，处于 Thread.State.WAITING
				* 指定了超时时间等待，处于 Thread.State.TIMED_WAITING
				* 线程终止，处于 Thread.State.TERMINATED
			* 接口
				* Thread()
					* 线程实例刚创建时，threadStatus 处于 NEW=0 状态
					* 如果父线程有需要继承的 ThreadLocal 局部变量就 copy 一下
				* start()
					* 启动线程，该线程进入 RUNNABLE 状态。当获得了执行任务所需要的资源后，JVM 便会调用 target 的 run() 方法
					* 注：永远不要对同一个线程对象执行两次 start() 方法
				* join() / join(long millis)
					* 等待线程终止，该线程进入 WAITING 状态。如果线程“活着”就一直等待。wait(0) 无超时，阻塞调用；wait(delay) 定时等待，阻塞调用
						* RUNNABLE, BLOCKED, WAITING, TIMED_WAITING 这四种状态，为线程“活着”
				* void setPriority(int newPriority)
					* 设置线程的优先级，优先级越大越先执行，最大 10，默认 5，最小 1
					* 注：调度器可以不理会这个信息。这个方法几乎没用，调试并发 bug 可能能派上用场
				* static native void sleep(long millis) / static void sleep(long millis, int nanos)
					* 强制挂起当前线程，提供毫秒和纳秒两种级别的控制
						* 和 Object.wait() 方法基本一致，唯一区别就是：sleep() 不会放弃任何占用的监视器锁
				* static native void yield()
					* 出让当前线程 cpu 资源，给其他线程使用，但是我占有的同步资源不让
					* 注：调度器可以不理会这个信息。这个方法几乎没用，调试并发 bug 可能能派上用场
* 工具类
	* ThreadGroup
		* Linux 把不同的 PID 与系统中每个进程或轻量级进程相关联。另一方面，一个多线程应用程序中的所有线程只有一个相同的 PID
		* 正因为多个线程有相同的 PID，所以引入了线程组的概念，而一个线程组中的所有线程使用和该线程组的领先进程相同的 PID,也就是该组中第一个轻量级进程的 PID,它被存入进程描述符的tgid字段中
	* ThreadLocalMap

