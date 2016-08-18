package org.leech.common.env;

import org.leech.common.io.Path;
import org.leech.common.settings.Settings;

public class Environment {
    
    private Path homeDir;
    private Path configDir;
    private Path logsDir;
	private Path baseDir;
	private Path pluginsDir;
    
    public Environment(Settings settings) {
    	
		this.homeDir = Path.of(settings.get("path.home"));
		this.baseDir = Path.of(settings.get("path.base"));
		this.configDir = baseDir.append("config");
		this.logsDir = Path.of(settings.get("path.logs", baseDir.append("logs").toFile().getAbsolutePath()));
		this.pluginsDir = Path.of(settings.get("path.plugins", baseDir.append("plugins").toFile().getAbsolutePath()));
    }
    
    public Path pluginsDir() {
        return pluginsDir;
    }

    public Path configDir() {
        return configDir;
    }

    public Path logsDir() {
        return logsDir;
    }
    
    public Path homeDir() {
        return homeDir;
    }
    
    public Path baseDir() {
        return baseDir;
    }
}
