package org.leech.common.io;

import com.google.common.base.Preconditions;

import java.io.File;

public class Path {

	private final File file;

	public static Path combine(File parent, String child) {
		return Path.of(new File(parent, child));
	}
	
	public static Path combine(String... parts) {
		Preconditions.checkNotNull(parts);
		Preconditions.checkArgument(parts.length > 0);

		if (parts.length == 1) {
			return of(parts[0]);
		} else {
			Path path = combine(parts[0]);
			for (int i = 2; i < parts.length; i++) {
				path = path.append(parts[i]);
			}
			return path;
		}
	}
	
	public static Path of(String path) {
		return of(new File(path));
	}

	public static Path of(File file) {
		return new Path(file);
	}

	public Path(File file) {
		this.file = file;
	}

	public Path append(String child) {
		return Path.of(new File(this.file, child));
	}

	public File toFile() {
		return this.file;
	}
}
