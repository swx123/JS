package com.test.entity;

import java.util.ArrayList;
import java.util.List;

public class Function extends Scope {

	public String name;
	public List<Variable> arguments = new ArrayList<Variable>();
	
	@Override
	public String toString() {
		String str = "function " + name + '(';
		if(arguments.size() > 0) {
			for(int i = 0; i < arguments.size() - 1; i++) {
				str += "var " + arguments.get(i).name + ',';
			}
			str += "var " + arguments.get(arguments.size() - 1).name;
		}
		str += "){" + super.toString() + '}';
		return str;
	}
}
