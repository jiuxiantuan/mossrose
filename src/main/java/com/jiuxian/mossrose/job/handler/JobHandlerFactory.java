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
package com.jiuxian.mossrose.job.handler;

import com.google.common.collect.Maps;
import com.jiuxian.mossrose.job.ExecutorJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;

/**
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 */
public final class JobHandlerFactory {

    private Map<Class<? extends ExecutorJob<Serializable>>, JobHandler> handlers;

    private static final String REGISTER_FILE = "META-INF/mossrose/mjob-handler.register";

    private static final Logger LOGGER = LoggerFactory.getLogger(JobHandlerFactory.class);

    private JobHandlerFactory() {
        LOGGER.info("Initial mjob handler factory.");
        try {
            final Properties props = new Properties();

            // Load register file contents
            final Enumeration<URL> registerFiles = this.getClass().getClassLoader().getResources(REGISTER_FILE);
            URL registerFile = null;
            while (registerFiles.hasMoreElements()) {
                registerFile = registerFiles.nextElement();
                try (InputStream in = registerFile.openStream()) {
                    props.load(in);
                }
            }

            // Initialize protocol beans
            handlers = Maps.newHashMap();
            for (Map.Entry<Object, Object> entry : props.entrySet()) {
                LOGGER.info("Init handler: {}", entry);
                @SuppressWarnings("unchecked") final Class<ExecutorJob<Serializable>> mJobClass = (Class<ExecutorJob<Serializable>>) Class.forName((String) entry.getKey());
                @SuppressWarnings("unchecked") final JobHandler mJobHandler = ((Class<JobHandler>) Class
                        .forName((String) entry.getValue())).newInstance();
                handlers.put(mJobClass, mJobHandler);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class Holder {
        private static JobHandlerFactory MJOB_HANDLER_FACTORY = new JobHandlerFactory();
    }

    public static JobHandlerFactory getInstance() {
        return Holder.MJOB_HANDLER_FACTORY;
    }

    public JobHandler getMJobHandler(Class<?> mJobClazz) {
        final Class<?>[] interfaces = mJobClazz.getInterfaces();
        if (interfaces != null) {
            for (final Class<?> interfass : interfaces) {
                final Optional<Entry<Class<? extends ExecutorJob<Serializable>>, JobHandler>> optionalEntry = handlers.entrySet()
                        .stream().filter(entry -> entry.getKey() == interfass).findFirst();
                if (optionalEntry.isPresent()) {
                    return optionalEntry.get().getValue();
                }
            }
        }
        throw new RuntimeException("Class " + mJobClazz + " is not a implementation of MJob");
    }
}
