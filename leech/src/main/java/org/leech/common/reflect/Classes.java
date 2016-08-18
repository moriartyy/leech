package org.leech.common.reflect;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Classes {
	
	public static ClassLoader classLoader() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			classLoader = Classes.class.getClassLoader();
		}
		return classLoader;
	}
	
	public static void main(String[] args) throws Exception {
		File file = new File("D:\\Workspace\\eclipse\\anaconda\\plugins\\appstore\\anaconda-appstore-0.0.1-SNAPSHOT.jar");
		
		ClassLoader classLoader = classLoader();
		ClassLoaders.addUrl(file.toURI().toURL(), classLoader);
		
		List<Class<?>> classes = forJar(file, classLoader);
		for (Class<?> class1 : classes) {
			System.out.println(class1.getName());
		}
	}
	
	public static List<Class<?>> forJar(File file) {
		return forJar(file, classLoader());
	}
	
	public static String toClassName(String path) {
    	StringBuilder classNameBuilder = new StringBuilder();
    	for (int i=0; i<path.length(); i++) {
    		char c = path.charAt(i);
    		if (c == '.') {
    			break;
    		} else if (c == '/' || c == '\\') {
    			classNameBuilder.append('.');
    		} else {
    			classNameBuilder.append(c);
    		}
    	}
    	return classNameBuilder.toString();
	}

	public static List<Class<?>> forJar(File file, ClassLoader classLoader) {
		List<Class<?>> classes = new ArrayList<>();
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(file);
			Enumeration<JarEntry> entrys = jarFile.entries();
			while (entrys.hasMoreElements()) {
	            JarEntry jarEntry = entrys.nextElement();
	            String entryName = jarEntry.getName();
	            if (entryName.endsWith(".class")) {
	            	String className = toClassName(entryName);
	            	classes.add(Class.forName(className, true, classLoader));
	            }
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return classes;
	}
}
