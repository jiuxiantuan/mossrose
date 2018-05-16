/**
 * Copyright 2015-2020 jiuxian.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jiuxian.mossrose.job;

import com.jiuxian.mossrose.util.Tuple;

import java.io.Serializable;

/**
 * 流式作业接口
 * <p>
 * 用于解决大量任务切分的问题，为避免OOE，使用Streamer不断输出作业分片数据
 * <p>
 * Mossrose会根据当前计算集群的节点数动态设定子任务的并行度
 *
 * @param <T> Job data
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 */
public interface StreamingJob<T extends Serializable, M extends Serializable> extends ExecutorJob<T> {

    Streamer<T, M> streamer();

    interface Streamer<T, M> {

        /**
         * 如果返回元组为null，代表没有下一条数据了，M为自定义的获取下一条数据的标记
         *
         * @param mark
         * @return data and mark
         */
        Tuple<T, M> next(M mark);
    }
}
