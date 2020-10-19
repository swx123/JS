package com.test.entity;

import java.util.ArrayList;
import java.util.List;

public class InvokeExpression extends Expression {

	private List<Expression> arguments;
	private Function function;
	
	private int n;
	
	public InvokeExpression(Function function) {
		this.function = function;
		arguments = new ArrayList<Expression>();
	}
	
	public void put(Expression arg) throws Exception {
		arguments.add(arg);
	}

	@Override
	protected String toStr() {
		String str = function.name + '(';
		if(arguments.size() > 0) {
			for(int i = 0; i < arguments.size() - 1; i++) {
				str += arguments.get(i).toString() + ',';
			}
			str += arguments.get(arguments.size() - 1).toString();
		}
		str += ')';
		return str;
	}
}
