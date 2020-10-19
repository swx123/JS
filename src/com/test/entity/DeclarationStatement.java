package com.test.entity;

public class DeclarationStatement implements Statement {

	public Variable variable;
	public Expression expression;
	
	public Function function;
	
	@Override
	public String toString() {
		String str = "";
		if(function != null) {
			str = function.toString();
		} else {
			str = "var " + variable.name;
			if(expression != null) {
				str += "=" + expression.toString();
			}
		}
		return str;
	}
}
