package org.leech.common.reflect;

import java.lang.reflect.Method;
import java.net.URL;

public class ClassLoaders {

	public static void addUrl(URL url) {
		addUrl(url, Thread.currentThread().getContextClassLoader());
	}
	
	public static void addUrl(URL url, ClassLoader classLoader) {
		addUrls(new URL[] { url }, classLoader);
	}
	
	public static void addUrls(URL[] urls, ClassLoader classLoader) {
		try {
			Class<?> classLoaderClass = classLoader.getClass();
			Method addURL = null;
			while (!classLoaderClass.equals(Object.class)) {
				try {
					addURL = classLoaderClass.getDeclaredMethod("addURL", URL.class);
					break;
				} catch (NoSuchMethodException e) {
					classLoaderClass = classLoaderClass.getSuperclass();
				}
			}
			
			if (addURL == null) {
				throw new RuntimeException("No addURL method in classLoader.");
			}
			
			if (!addURL.isAccessible()) {
				addURL.setAccessible(true);
			}
			
			for (URL url : urls) {
				addURL.invoke(classLoader, url);
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
}
