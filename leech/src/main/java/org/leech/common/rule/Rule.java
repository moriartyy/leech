package org.leech.common.rule;

public interface Rule<T> {
	boolean eval(T o);
}
