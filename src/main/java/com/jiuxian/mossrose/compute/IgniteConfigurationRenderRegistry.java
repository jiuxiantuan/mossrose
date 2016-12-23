package com.jiuxian.mossrose.compute;

import java.util.List;

import org.apache.ignite.configuration.IgniteConfiguration;

import com.google.common.collect.Lists;

import jline.internal.Preconditions;

public class IgniteConfigurationRenderRegistry {

	private static final List<IgniteConfigurationRender> RENDERS = Lists.newCopyOnWriteArrayList();

	public static void register(IgniteConfigurationRender render) {
		RENDERS.add(Preconditions.checkNotNull(render));
	}

	public static void render(IgniteConfiguration igniteConfiguration) {
		for (IgniteConfigurationRender render : RENDERS) {
			render.render(igniteConfiguration);
		}
	}

}
