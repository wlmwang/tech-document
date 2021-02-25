# 线程框架 - executor

## 基础容器
#### 接口


#### 实现
* java.uitl.concurrent.ForkJoinPool


## 工具类


## 关键源码


## 示例代码
```
public class forkJoinTest
{
	public static void main(String[] args) throws Exception
	{
		ForkJoinPool forkJoinPool = new ForkJoinPool();
		ForkJoinTask<Integer> result = forkJoinPool.submit(new SumTask(0, 2000));
		System.out.println("Fork/join sum: " + result.get());
	}

	static class SumTask extends RecursiveTask<Integer> {
		int startValue;
		int endValue;
		SumTask(int start, int end) {
			this.startValue = start;
			this.endValue = end;
		}

		@Override
		protected Integer compute() {
			if (endValue - startValue <= 500) {
				// 如果任务足够小，直接计算
				System.out.println("开始计算的部分：startValue=" + startValue + "; endValue=" + endValue + "; thread = "+ Thread.currentThread());
				Integer totalValue = 0;
				for(int index = this.startValue ; index <= this.endValue  ; index++) {
					totalValue += index;
				}
				return totalValue;
			}
			SumTask subtask1 = new SumTask(startValue, (startValue + endValue)/2);
			SumTask subtask2 = new SumTask((startValue + endValue)/2 + 1, endValue);
			subtask1.fork();
			subtask2.fork();
			return subtask1.join() + subtask2.join();
		}
	}
}
```
