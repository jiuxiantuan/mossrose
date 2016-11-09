package com.jiuxian.mossrose.job.handler;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.jiuxian.mossrose.job.MJob;

/**
 * 
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
public final class MJobHandlerFactory {

	private Map<Class<? extends MJob>, MJobHandler<? extends MJob>> handlers;

	private static final String REGISTER_FILE = "META-INF/mossrose/mjob-handler.register";

	private static final Logger LOGGER = LoggerFactory.getLogger(MJobHandlerFactory.class);

	private MJobHandlerFactory() {
		LOGGER.info("Initial mjob handler factory.");
		try {
			Properties props = new Properties();

			// Load register file contents
			Enumeration<URL> registerFiles = this.getClass().getClassLoader().getResources(REGISTER_FILE);
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
				@SuppressWarnings("unchecked")
				final Class<MJob> mJobClass = (Class<MJob>) Class.forName((String) entry.getKey());
				@SuppressWarnings("unchecked")
				final MJobHandler<MJob> mJobHandler = ((Class<MJobHandler<MJob>>) Class.forName((String) entry.getValue())).newInstance();
				handlers.put(mJobClass, mJobHandler);
			}
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	private static class Holder {
		private static MJobHandlerFactory MJOB_HANDLER_FACTORY = new MJobHandlerFactory();
	}

	public static MJobHandlerFactory getInstance() {
		return Holder.MJOB_HANDLER_FACTORY;
	}

	public MJobHandler<? extends MJob> getMJobHandler(Class<?> mJobClazz) {
		Class<?>[] interfaces = mJobClazz.getInterfaces();
		if (interfaces != null) {
			for (Class<?> interfass : interfaces) {
				Optional<Entry<Class<? extends MJob>, MJobHandler<? extends MJob>>> optionalEntry = handlers.entrySet().stream().filter((entry) -> {
					return entry.getKey() == interfass;
				}).findFirst();
				if (optionalEntry.isPresent()) {
					return optionalEntry.get().getValue();
				}
			}
		}
		throw new RuntimeException("Class " + mJobClazz + " is not a implementation of MJob");
	}
}
