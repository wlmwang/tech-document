# 线程框架 - blockingQueue

## 基础容器
#### 接口
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
* java.util.concurrent.LinkedTransferQueue<E>
	* 一个由链表结构组成的无界阻塞队列
* java.util.concurrent.SynchronousQueue<E>
	* 一个不存储元素的阻塞队列（常用）
		* 对于每一个 take() 的线程会阻塞直到有一个 put() 的线程放入元素为止，反之亦然
		* 类似 peek() 操作或者迭代器操作是无效的，元素只能通过 put() 类操作或者 take() 类操作才有效


## 关键源码


## 示例代码
* SynchronousQueue
```
SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<>();
new Thread(() -> {
	System.out.println("consumer start");

	Integer object;
	try {
		object = 1;
		synchronousQueue.put(object);
		System.out.println("produced {}" + object);

		object = 2;
		synchronousQueue.put(object);
		System.out.println("produced {}" + object);
	} catch (InterruptedException ex) {
		// ...
	}
}).start();

new Thread(() -> {
	System.out.println("producer start");

	Integer object;
	try {
		object = synchronousQueue.take();
		System.out.println("consumed {}" + object);

		Thread.sleep(3000);

		object = synchronousQueue.take();
		System.out.println("consumed {}" + object);
	} catch (InterruptedException ex) {
		// ...
	}
}).start();

Thread.sleep(Duration.ofDays(1).toMillis());
```

