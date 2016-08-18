package org.leech.common.settings;

import com.google.inject.Binder;
import com.google.inject.Module;

public class SettingsModule implements Module {
	
	private Settings settings;

	public SettingsModule(Settings settings) {
		this.settings = settings;
	}

	@Override
	public void configure(Binder binder) {
		binder.bind(Settings.class).toInstance(settings);
	}

}
