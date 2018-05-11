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
package com.jiuxian.mossrose.compute;

import java.io.Serializable;

/**
 * Abstraction for grid compute
 *
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 */
public interface GridComputer extends AutoCloseable {

    interface ComputeFuture {
        /**
         * Wait for the GridComputer execution complete
         *
         * @return The execution result
         */
        Serializable join();
    }

    /**
     * 发送计算任务到网格
     *
     * @param taskId      任务id
     * @param computeUnit 计算单元
     * @return ComputeFuture
     */
    ComputeFuture execute(final String taskId, ComputeUnit computeUnit);

    /**
     * 计算网格的并行度
     *
     * @return 并行度
     */
    int concurrency();

    void init();
}
