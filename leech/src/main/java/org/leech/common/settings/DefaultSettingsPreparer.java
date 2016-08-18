package org.leech.common.settings;

import org.anaconda.common.io.Files;

import java.io.File;

public class DefaultSettingsPreparer implements SettingsPreparer {
	
	public final static DefaultSettingsPreparer Instance = new DefaultSettingsPreparer();

	@Override
	public Settings prepareSettings() {
		String home = System.getProperty("anaconda_home", System.getProperty("user.dir"));
		String base = System.getProperty("anaconda_base", home);
		File configFile = Files.file(base, "config", "anaconda.properties");
		Settings settings = new PropertiesFileSettingsPreparer(configFile).prepareSettings();
		return Settings.builder()
				.put(settings)
				.put("path.home", home)
				.put("path.base", base)
				.build();
	}

}
