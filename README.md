# mossrose
<h3>High-Available Distributed Schedule Framework</h3>

<hr>

## 社区
 * QQ群：595011342

## 文档
 * Wiki: https://github.com/jiuxiantuan/mossrose/wiki

## Requirement

* Zookeeper
* Java 8
* Spring 3.x+ 
非Spring用户：[https://github.com/jiuxiantuan/mossrose/wiki/Use-mossrose-without-spring]
 
## Installation
```
<dependency>
  <groupId>com.jiuxian</groupId>
  <artifactId>mossrose-spring</artifactId>
  <version>1.0.1-RELEASE</version>
</dependency>
```

## Key concept

* SimpleJob
 * 简单任务
* DistributedJob
 * 分布式任务，通过Slicer将作业分隔成多个子任务，子任务在集群内分布执行
* StreamingJob
 * 分布式流式任务，解决分片非常多时DistributedJob内存占用大的问题
* MossroseProcess
 * 多个MossroseProcess组成集群，集群保证有且只有一个节点竞选成为主节点，主节点负责触发作业；所有节点都是工作节点，主节点触发的任务会在所有工作节点上分布执行
* MossroseConfig
 * Mossrose配置，包括集群元信息和任务元信息


## Quick Start

#### Implement a simple job
```
public class SomeJob implements SimpleJob {

    @Override
    public void execute() {
        System.out.println("SimpleJob: " + UUID.randomUUID());
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
		<mossrose:cluster name="mossrose-example" />
		<mossrose:jobs>
			<mossrose:job id="SimpleExampleJob" cron="0/5 * * * * ?" job-bean-name="simpleExampleJob" group="example" />
			<mossrose:job id="DistributedExampleJob" cron="0/15 * * * * ?" job-bean-name="distributedExampleJob" group="example" />
			<mossrose:job id="StreamingExampleJob" cron="0/20 * * * * ?" job-bean-name="streamingExampleJob" group="example"
				description="分布式流式任务示例" />
		</mossrose:jobs>
	</mossrose:config>
	<mossrose:process zks="#{configTookitProp['zk.address']}" />
	<mossrose:ui />

</beans>

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
				return index < list.size() - 1;
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