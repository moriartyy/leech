package org.leech.plugin;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.inject.Module;
import org.leech.common.env.Environment;
import org.leech.common.reflect.ClassLoaders;
import org.leech.common.reflect.Classes;
import org.leech.common.settings.Settings;
import org.leech.common.component.AbstractLifecycleComponent;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginService extends AbstractLifecycleComponent {
	
	private Settings settings;
	private Environment environment;
	private List<Plugin> plugins = new ArrayList<>();

	public PluginService(Settings settings, Environment environment) {
		this.settings = settings;
		this.environment = environment;
		loadPlugins();
	}
	
	private void registerPlugin(Plugin plugin) {
		this.plugins.add(plugin);
	}

	private void loadPlugins() {
		List<File> classPaths = getClassPaths();

		ClassLoaders.addUrls(Lists.transform(classPaths, new Function<File, URL>() {
			@Override
			public URL apply(File file) {
				try {
					return file.toURI().toURL();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}).toArray(new URL[classPaths.size()]), getClass().getClassLoader());

		for (File classPath : classPaths) {
			if (classPath.getName().endsWith(".jar")) {
				List<Class<?>> classes = Classes.forJar(classPath, getClass().getClassLoader());
				for (Class<?> clazz : classes) {
					try {
						if (Plugin.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers())) {
							Plugin plugin;
							try {
								plugin = (Plugin) clazz.getConstructor(Settings.class).newInstance(settings);
							} catch (NoSuchMethodException e) {
								plugin = (Plugin) clazz.getConstructor().newInstance();
							}
							if (plugin != null) {
								registerPlugin(plugin);
							}
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}
	
	private List<File> getClassPaths() {
		List<File> classPaths = new ArrayList<>();
		File[] dirs = environment.pluginsDir().toFile().listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
        if (dirs != null) {
            for (File dir : dirs) {
                classPaths.add(dir);
                File[] libFiles = dir.listFiles(new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".jar") || name.endsWith(".zip");
                    }
                });
                if (libFiles != null) {
                    classPaths.addAll(Arrays.asList(libFiles));
                }
            }
        }
		return classPaths;
	}
 
	public List<Plugin> plugins() {
		return plugins;
	}

	public void processModule(Module module) {
		for (Plugin plugin : plugins) {
			plugin.processModule(module);
		}
	}

	@Override
	protected void doStart() {
		
	}

	@Override
	protected void doStop() {
		
	}
}
