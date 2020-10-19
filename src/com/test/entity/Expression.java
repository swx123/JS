package com.test.entity;

public abstract class Expression implements Statement {

	public boolean inner;
	
	protected abstract String toStr();
	
	@Override
	public String toString() {
		if(inner) {
			return '(' + toStr() + ')';
		}
		return toStr();
	}
}
