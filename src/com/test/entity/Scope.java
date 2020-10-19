package com.test.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Scope {

	public List<Statement> statements;
	public Map<String, Variable> variables;
	
	public Scope parent;
	
	public Scope() {
		statements = new ArrayList<Statement>();
		variables = new LinkedHashMap<String, Variable>();
	}
	
	@Override
	public String toString() {
		String str = "";
		for(Statement statement : statements) {
			str += statement.toString() + ";";
		}
		return str;
	}
}
