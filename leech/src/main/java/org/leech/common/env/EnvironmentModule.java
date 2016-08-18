package org.leech.common.env;

import com.google.inject.Binder;
import com.google.inject.Module;

public class EnvironmentModule implements Module {

	private Environment environment;

	public EnvironmentModule(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void configure(Binder binder) {
		binder.bind(Environment.class).toInstance(environment);
	}

}
