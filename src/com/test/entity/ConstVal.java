package com.test.entity;

public class ConstVal extends Expression {

	public static final int number = 1;
	public static final int string = 2;
	
	public int type;
	public String strVal;
	public Double numVal;
	
	public ConstVal(int type, String value) {
		this.type = type;
		this.strVal = value;
		if(type == number) {
			numVal = Double.valueOf(strVal);
		}
	}
	
	@Override
	public String toStr() {
		if(type == number) {
			return numVal.toString();
		} else {
			return '"' + strVal + '"';
		}
	}
}
