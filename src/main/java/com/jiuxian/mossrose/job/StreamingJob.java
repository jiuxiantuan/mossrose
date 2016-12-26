/**
 * Copyright 2015-2020 jiuxian.com.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jiuxian.mossrose.job;

import java.io.Serializable;

/**
 * 
 * 流式作业接口
 * <p>
 * 用于解决大量任务切分的问题，为避免OOE，使用Streamer不断输出作业分片数据
 * 
 * <pre>
 * public class StreamingExampleJob implements StreamingJob<String> {
 * 
 * 	&#64;Override
 * 	public Streamer<String> streamer() {
 * 		return new Streamer<String>() {
 * 
 * 			private List<String> list = Lists.newArrayList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
 * 
 * 			// 用于保存分页的状态
 * 			private int index = 0;
 * 
 * 			&#64;Override
 * 			public boolean hasNext() {
 * 				return index < list.size() - 1;
 * 			}
 * 
 * 			&#64;Override
 * 			public String next() {
 * 				return list.get(index++);
 * 			}
 * 		};
 * 	}
 * 
 * 	&#64;Override
 * 	public Executor<String> executor() {
 * 		return new Executor<String>() {
 * 
 * 			&#64;Override
 * 			public void execute(String item) {
 * 				System.out.println("StreamingJob: " + item);
 * 			}
 * 		};
 * 	}
 * 
 * }
 * </pre>
 * 
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 * @param <T>
 */
public interface StreamingJob<T extends Serializable> extends MJob<T> {

	Streamer<T> streamer();

	interface Streamer<T> {
		boolean hasNext();

		T next();
	}
}
