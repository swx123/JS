package com.test.entity;

public class Assignment implements Statement {

	public Variable variable;
	public Expression expression;
	
	@Override
	public String toString() {
		String str = variable.name;
		str += "=" + expression.toString();
		return str;
	}
}
