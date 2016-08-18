package org.leech.plugin;

import com.google.inject.Binder;
import com.google.inject.Module;

public class PluginModule implements Module {
	
	private PluginService pluginService;

	public PluginModule(PluginService pluginService) {
		this.pluginService = pluginService;
	}

	@Override
	public void configure(Binder binder) {
		binder.bind(PluginService.class).toInstance(pluginService);
	}
}
