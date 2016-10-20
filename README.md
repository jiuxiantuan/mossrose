# mossrose
<h3>High-Avaliable Distributed Schedule System</h3>

<hr>

## Quick Start

#### Implement a job
<pre><code>
public class SomeJob implements SimpleJob {

    @Override
	public void execute() {
		System.out.println("SimpleJob: " + UUID.randomUUID());
	}

}
</code></pre>

#### Config the job - mossrose.yaml
<pre><code>
# Mossrose config info
---
cluster:
  name: mossrose-example
  loadBalancingMode: ROUND_ROBIN
jobs:
  - id: 1
    group: test
    cron: 0/5 * * * * ?
    runInCluster: true
    main: com.jiuxian.mossrose.test.SomeJob
</code></pre>

#### Run mossrose main class
<pre><code>
public class MainTest {

    @Test
	public void test() throws Exception {
		String zks = "localhost";
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
</code></pre>

## Distributed Job
#### Implement a distributed job
<pre><code>
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
</code></pre>
