# mossrose
<h3>High-Avaliable Distributed Schedule Framework</h3>

<hr>

## Requirement

* Zookeeper
* Java 8
 
## Installation
```
<dependency>
  <groupId>com.jiuxian</groupId>
  <artifactId>mossrose</artifactId>
  <version>1.1.0-RELEASE</version>
</dependency>
```

## Key concept

* SimpleJob
 * 简单任务
* DistributedJob
 * 分布式任务，通过slice()方法将作业分隔成多个子任务，子任务在集群内分布执行
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

#### Config the job - mossrose.yaml
```
# Mossrose config info
---
cluster:
  name: mossrose-example    # 集群命名空间
  loadBalancingMode: ROUND_ROBIN    # 集群负载均衡策略，可选：ROUND_ROBIN/RANDOM
jobs:
  - id: 1   # 作业ID
    group: test # 作业分组(可选)
    cron: 0/5 * * * * ? # 作业cron表达式
    runInCluster: true  # 是否在集群中分布执行，如果为false，则只在主节点上执行
    main: com.jiuxian.mossrose.test.SomeJob # 作业类全名
```

#### Run mossrose main class
```
public class MainTest {

    @Test
	public void test() throws Exception {
		String zks = "localhost"; // zookeeper集群地址
		try (MossroseProcess process = new MossroseProcess(
				MossroseConfigFactory.fromClasspathYamlFile("mossrose.yaml"), 
				new ZookeeperClusterDiscovery("/mossrose/jobtest", zks), zks)) {
			process.run();

			try {
                // Block the unit test
				Thread.sleep(60 * 60 * 1000);
			} catch (InterruptedException e) {
			}
		}
	}

}
```

## Distributed Job
#### Implement a distributed job
```
public class SomeDistributedJob implements DistributedJob<String> {

    @Override
	public List<String> slice() {
		return Splitter.on(" ").splitToList("This is a test on the mossrose distributed job, how are you feeling?");
	}

	@Override
	public void execute(String item) {
		System.out.println(Thread.currentThread() + " DistributedJob: " + item);
	}

}
```
