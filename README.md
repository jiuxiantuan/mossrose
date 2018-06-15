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
 * Example: https://github.com/jiuxiantuan/mossrose-example
 * Spring Boot Starter: https://github.com/jiuxiantuan/mossrose-spring-boot-starter
 * Spring Boot Example: https://github.com/jiuxiantuan/mossrose-spring-boot-example

## Requirement

* Zookeeper
* Java 8
* Spring 3.x+ 

## Installation
```
<dependency>
  <groupId>com.jiuxian</groupId>
  <artifactId>mossrose</artifactId>
  <version>2.4.3-RELEASE</version>
</dependency>
```

## Key concept

#### SimpleJob
  简单任务
#### DistributedJob
 分布式任务，通过Slicer将作业分隔成多个子任务，子任务在集群内分布执行
#### StreamingJob
 分布式流式任务，解决分片非常多时DistributedJob内存占用大的问题
#### MapReduceJob
 MapReduce任务
#### MossroseProcess
 多个MossroseProcess组成集群，集群保证有且只有一个节点竞选成为主节点，主节点负责触发作业；所有节点都是工作节点，主节点触发的任务会在所有工作节点上分布执行
#### MossroseConfig
 Mossrose配置，包括集群元信息和任务元信息


## Quick Start

#### Implement a simple job
```
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

#### Config the job in spring
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans" 
	xmlns:mossrose="https://jiuxiantuan.github.io/mossrose"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
		https://jiuxiantuan.github.io/mossrose https://jiuxiantuan.github.io/mossrose/mossrose.xsd">

	<bean class="com.jiuxian.jobs.bean.BusinessBean" />
	<bean id="simpleExampleJob" class="com.jiuxian.jobs.job.SimpleExampleJob" />
	<bean id="distributedExampleJob" class="com.jiuxian.jobs.job.DistributedExampleJob" />
	<bean id="streamingExampleJob" class="com.jiuxian.jobs.job.StreamingExampleJob" />

	<mossrose:springholder />
	<mossrose:config>
		<mossrose:cluster name="mossrose-example" discovery-zk="localhost:2181" />
		<mossrose:jobs>
			<mossrose:job id="SimpleExampleJob" cron="0/5 * * * * ?" job-bean-name="simpleExampleJob" group="example" />
			<mossrose:job id="DistributedExampleJob" cron="0/15 * * * * ?" job-bean-name="distributedExampleJob" group="example" />
			<mossrose:job id="StreamingExampleJob" cron="0/20 * * * * ?" job-bean-name="streamingExampleJob" group="example"
				description="分布式流式任务示例" />
		</mossrose:jobs>
	</mossrose:config>
	<mossrose:process />
	<mossrose:ui />

</beans>

```
#### Start the job
```
applicationContext.getBean(MossroseProcess.class).run();
```

## Distributed Job
#### Implement a distributed job
```
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
public class SomeStreamingJob implements StreamingJob<String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SomeStreamingJob.class);

	@Override
	public Streamer<String> streamer() {
		return new Streamer<String>() {

			private List<String> list = Lists.newArrayList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < list.size();
			}

			@Override
			public String next() {
				return list.get(index++);
			}
		};
	}

	@Override
	public Executor<String> executor() {
		return new Executor<String>() {

			@Override
			public void execute(String item) {
				LOGGER.info(Thread.currentThread() + " StreamingJob: " + item);
			}
		};
	}

}
```

## MapReduce Job
#### Implement a map/reduce job
```
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