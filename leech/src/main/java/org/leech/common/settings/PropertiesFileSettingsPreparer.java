package org.leech.common.settings;

import com.google.common.base.Charsets;

import java.io.File;
import java.io.Reader;
import java.util.Properties;

public class PropertiesFileSettingsPreparer implements SettingsPreparer {
	
	private final File file;

	public PropertiesFileSettingsPreparer(File file) {
		this.file = file;
	}

	@Override
	public Settings prepareSettings() {
		Properties settings = new Properties();
        Reader reader = null;
        try {
            reader = com.google.common.io.Files.newReader(file, Charsets.UTF_8);
            settings.load(reader);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load settings from file: " + file.getAbsolutePath(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e2) {
                }
            }
        }
		return Settings.builder().put(settings).build();
	}

}
