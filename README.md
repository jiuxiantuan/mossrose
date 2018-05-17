# mossrose
<h3>轻量分布式作业框架</h3>

<hr>

![Mossrose Architecture](https://jiuxiantuan.github.io/mossrose/mossrose.jpg)

## 特性
 * 通过选举保证集群中只出现一个主节点，负责trigger作业
 * 所有集群节点参与作业计算任务
 * 内置多种作业类型，并预留了SPI接口支持扩展

## 文档
 * Wiki: https://github.com/jiuxiantuan/mossrose/wiki
 * Spring Boot Starter: https://github.com/jiuxiantuan/mossrose-spring-boot-starter
 * Example: https://github.com/jiuxiantuan/mossrose-spring-boot-example

## Requirement

* Zookeeper
* Java 8

## 任务类型

#### SimpleJob
  简单任务
#### DistributedJob
 分布式任务，通过Slicer将作业分隔成多个子任务，子任务在集群内分布执行
#### StreamingJob
 分布式流式任务，解决分片非常多时DistributedJob内存占用大的问题
#### MapReduceJob
 MapReduce任务

## Quick Start

#### Installation
```
<dependency>
    <groupId>com.jiuxian</groupId>
    <artifactId>mossrose-spring-boot-starter</artifactId>
    <version>1.1.0-RELEASE</version>
</dependency>
```

#### Implement a simple job
```
@Job(id = "SimpleExampleJob", cron = "0/5 * * * * ?", group = "example")
public class SimpleExampleJob implements SimpleJob {

	@Override
	public Executor executor() {
		return new Executor() {

			@Override
			public void execute() {
				LOGGER.info("SimpleJob");
			}
		};
	}

}
```

#### Config application.yml
```
mossrose:
  name: example
  discovery-zk: 192.168.5.99
  enable-ui: true

```
#### RUN IT


## Distributed Job
#### Implement a distributed job
```
@Job(id = "DistributedExampleJob", cron = "0 * * * * ?", group = "example")
public class SomeDistributedJob implements DistributedJob<String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SomeDistributedJob.class);

	@Override
	public Slicer<String> slicer() {
		return new Slicer<String>() {

			@Override
			public List<String> slice() {
				return Splitter.on(" ").splitToList("This is a test on the mossrose distributed job, how are you feeling?");
			}
		};
	}

	@Override
	public com.jiuxian.mossrose.job.DistributedJob.Executor<String> executor() {
		return new Executor<String>() {

			@Override
			public void execute(String item) {
				LOGGER.info(Thread.currentThread() + " DistributedJob: " + item);
			}
		};
	}

}
```

## Streaming Job
#### Implement a streaming job
DistributedJob需要把需要分布式执行的任务集合一次性的返回，在集合非常大的时候会存在内存的问题，StreamingJob解决了这个问题，任务可以以流的方式不断输出，以保证内存可以及时释放。
```
@Job(id = "StreamingExampleJob", cron = "0 * * * * ?", group = "example")
public class StreamingExampleJob implements StreamingJob<String, Integer> {

	private static final Logger LOGGER = LoggerFactory.getLogger(StreamingExampleJob.class);

	// 用于模拟一个数据源
	private static final List<String> LIST = Lists.newArrayList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

	@Override
	public Streamer<String, Integer> streamer() {
		return new Streamer<String, Integer>() {

            @Override
            public Tuple<String, Integer> next(Integer mark) {
                int index = mark != null ? mark + 1 : 0;
                if(index > LIST.size() - 1) {
                    return null;
                }

                return new Tuple(LIST.get(index), index);
            }

			private int index = 0;

		};
	}

	@Override
	public Executor<String> executor() {
		return new Executor<String>() {

			@Override
			public void execute(String item) {
				LOGGER.info("StreamingJob: " + item);
			}
		};
	}

}
```

## MapReduce Job
#### Implement a map/reduce job
```
@Job(id = "MapReduceExampleJob", cron = "0/20 * * * * ?", group = "example")
public class MapReduceExampleJob implements MapReduceJob<Integer, Integer> {

	@Override
	public com.jiuxian.mossrose.job.MapReduceJob.Mapper<Integer> mapper() {
		return new Mapper<Integer>() {

			@Override
			public List<Integer> map() {
				return Lists.newArrayList(1, 2, 3, 4, 5, 6, 7);
			}
		};
	}

	@Override
	public com.jiuxian.mossrose.job.MapReduceJob.Executor<Integer, Integer> executor() {
		return new Executor<Integer, Integer>() {

			@Override
			public Integer execute(Integer item) {
				return item * 2;
			}
		};
	}

	@Override
	public com.jiuxian.mossrose.job.MapReduceJob.Reducer<Integer> reducer() {
		return new Reducer<Integer>() {

			@Override
			public void reduce(List<Integer> rs) {
				LOGGER.info("Reduce result : {}", rs);
			}
		};
	}

}
```