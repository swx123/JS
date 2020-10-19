package com.test.entity;

import java.util.HashMap;
import java.util.Map;

public class Global extends Scope {

	public Map<String, Function> functions;
	
	public Global() {
		super();
		functions = new HashMap<String, Function>();
	}
}
