package com.test.entity;

public class Variable extends Expression {

	public String name;
	public ConstVal value;
	
	public Variable() {}
	
	public Variable(String name) {
		this.name = name;
	}
	
	@Override
	public String toStr() {
		return name;
	}
}
