package com.test.entity;

public class Token {
	
	public Type type;
	public String value;
	
	public Token(Type type) {
		this.type = type;
	}
	
	public Token(Type type, String value) {
		this.type = type;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return '[' + type.name() + "\t: " + value + "\t]";
	}
}
