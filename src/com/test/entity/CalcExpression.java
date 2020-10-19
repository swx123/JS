package com.test.entity;

public class CalcExpression extends Expression {

	public static final char plus = '+';
	public static final char minus = '-';
	public static final char times = '*';
	public static final char divide = '/';
	
	public char op;
	public Expression exp1;
	public Expression exp2;
	
	@Override
	public String toStr() {
		return exp1.toString() + op + exp2.toString();
	}
}
