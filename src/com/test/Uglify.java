package com.test;

import java.io.File;

import com.test.entity.Function;
import com.test.entity.Global;
import com.test.entity.Variable;

public class Uglify {

	private Global global;
	private int count = 1;
	
	public Uglify(Global global) {
		this.global = global;
	}
	
	public static void main(String[] args) throws Exception {
		Lexer lexer = new Lexer(new File("1.js"));
		Parser parser = new Parser(lexer.lex());
		Uglify uglify = new Uglify(parser.parse());
		System.out.println(uglify.uglify());
	}
	
	public String uglify() {
		for(String key : global.variables.keySet()) {
			Variable variable = global.variables.get(key);
			variable.name = name();
		}
		for(String key : global.functions.keySet()) {
			Function function = global.functions.get(key);
			function.name = name();
		}
		for(String key : global.functions.keySet()) {
			Function function = global.functions.get(key);
			int n = count;
			for(String k : function.variables.keySet()) {
				Variable variable = function.variables.get(k);
				variable.name = name();
			}
			count = n;
		}
		return global.toString();
	}
	
	private String name() {
		String name = "";
		int n = count++;
		while(n > 0) {
			char c = (char)(n % 26 - 1 + 'a');
			name = c + name;
			n = n / 26;
		}
		return name;
	}
}
