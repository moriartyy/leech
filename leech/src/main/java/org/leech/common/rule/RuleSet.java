package org.leech.common.rule;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RuleSet<T> {

	private Set<Rule<T>> rules = new HashSet<Rule<T>>();

	public RuleSet(Collection<Rule<T>> rules) {
		this.rules.addAll(rules);
	}
	
	public RuleSet() {
	}

	public void addRule(Rule<T> rule) {
		rules.add(rule);
	}
	
	public boolean eval(T o) {
		for (Rule<T> rule : rules) {
			if (!rule.eval(o)) {
				return false;
			}
		}
		return true;
	}
}
