package com.test;

import java.io.File;

import com.test.entity.Assignment;
import com.test.entity.CalcExpression;
import com.test.entity.ConstVal;
import com.test.entity.DeclarationStatement;
import com.test.entity.Expression;
import com.test.entity.Function;
import com.test.entity.Global;
import com.test.entity.InvokeExpression;
import com.test.entity.Scope;
import com.test.entity.Token;
import com.test.entity.Type;
import com.test.entity.Variable;

public class Parser {

	private Token[] tokens;
	private int p = 0;
	
	private Global global;
	
	public Parser(Token[] tokens) {
		this.tokens = tokens;
		global = new Global();
	}
	
	public static void main(String[] args) throws Exception {
		Lexer lexer = new Lexer(new File("1.js"));
		Parser parser = new Parser(lexer.lex());
		Global global = parser.parse();
		System.out.println(global.toString());
	}
	
	public Global parse() throws Exception {
		statements(global, true);
		return global;
	}
	
	private void statements(Scope scope, boolean isGlobal) throws Exception {
		while(true) {
			Token token = tokens[p];
			if(token.type == Type.symbol && ";".equals(token.value)) {
				match(Type.symbol, ";");
				token = tokens[p];
			}
			if(isGlobal && token.type == Type.end) {
				break;
			} else if(!isGlobal && token.type == Type.symbol && "}".equals(token.value)) {
				break;
			} else if(token.type == Type.var) {
				DeclarationStatement statement = declaration(scope);
				scope.statements.add(statement);
			} else if(token.type == Type.id) {
				Token next = tokens[p + 1];
				if(next.type == Type.symbol && "=".equals(next.value)) {
					Assignment assignment = assign(scope);
					scope.statements.add(assignment);
				} else {
					Expression expression = expression(scope);
					scope.statements.add(expression);
				}
			} else if(isGlobal && token.type == Type.function) {
				Function function = function(scope);
				global.functions.put(function.name, function);
				DeclarationStatement statement = new DeclarationStatement();
				statement.function = function;
				scope.statements.add(statement);
			} else {
				throw new Exception("Unexpected Token " + token.toString());
			}
		}
	}
	
	private DeclarationStatement declaration(Scope scope) throws Exception {
		match(Type.var, null);
		match(Type.id, null);
		Variable variable = new Variable();
		variable.name = tokens[p - 1].value;
		DeclarationStatement statement = new DeclarationStatement();
		statement.variable = variable;
		scope.variables.put(variable.name, variable);
		
		if(match(Type.symbol, "=") != null) {
			Expression expression = expression(scope);
			statement.expression = expression;
		} else {
			p--;
		}
		return statement;
	}
	
	private Assignment assign(Scope scope) throws Exception {
		Assignment assignment = new Assignment();
		Token token = match(Type.id, null);
		Variable variable = findVar(scope, token.value);
		assignment.variable = variable;
		match(Type.symbol, "=");
		Expression expression = expression(scope);
		assignment.expression = expression;
		return assignment;
	}
	
	private Function function(Scope scope) throws Exception {
		Function function = new Function();
		function.parent = scope;
		match(Type.function, null);
		Token id = match(Type.id, null);
		function.name = id.value;
		match(Type.symbol, "(");
		for(Token token = tokens[p];;) {
			match(Type.var, null);
			Token arg = match(Type.id, null);
			Variable argument = new Variable(arg.value);
			function.variables.put(argument.name, argument);
			function.arguments.add(argument);
			token = tokens[p];
			if(token.type == Type.symbol && ",".equals(token.value)) {
				match(Type.symbol, ",");
			} else {
				break;
			}
		}
		match(Type.symbol, ")");
		match(Type.symbol, "{");
		statements(function, false);
		match(Type.symbol, "}");
		return function;
	}
	
	private Expression expression(Scope scope) throws Exception {
		Expression exp1 = term(scope);
		Expression exp = expression1(scope, exp1);
		return exp;
	}
	private Expression expression1(Scope scope, Expression exp1) throws Exception {
		Token token = tokens[p];
		if(token.type != Type.symbol || (!"+".equals(token.value) && !"-".equals(token.value))) {
			return exp1;
		}
		p++;
		CalcExpression exp = new CalcExpression();
		exp.exp1 = exp1;
		if("+".equals(token.value)) {
			exp.op = CalcExpression.plus;
		} else if("-".equals(token.value)) {
			exp.op = CalcExpression.minus;
		} else {
			throw new Exception("符号不对");
		}
		Expression exp2 = term(scope);
		exp.exp2 = exp2;
		Expression expression = expression1(scope, exp);
		return expression;
	}
	
	private Expression term(Scope scope) throws Exception {
		Expression exp1 = factor(scope);
		Expression expression = term1(scope, exp1);
		return expression;
	}
	private Expression term1(Scope scope, Expression exp1) throws Exception {
		Token token = tokens[p];
		if(token.type != Type.symbol || (!"*".equals(token.value) && !"/".equals(token.value))) {
			return exp1;
		}
		p++;
		CalcExpression exp = new CalcExpression();
		exp.exp1 = exp1;
		if("*".equals(token.value)) {
			exp.op = CalcExpression.times;
		} else if("/".equals(token.value)) {
			exp.op = CalcExpression.divide;
		} else {
			throw new Exception("符号不对");
		}
		Expression exp2 = term(scope);
		exp.exp2 = exp2;
		Expression expression = expression1(scope, exp);
		return expression;
	}
	
	private Expression factor(Scope scope) throws Exception {
		Token token = tokens[p];
		if(token.type == Type.symbol && "(".equals(token.value)) {
			match(Type.symbol, "(");
			Expression expression = expression(scope);
			match(Type.symbol, ")");
			expression.inner =true;
			return expression;
		} else if(token.type == Type.number) {
			match(Type.number, null);
			return new ConstVal(ConstVal.number, token.value);
		} else if(token.type == Type.string) {
			match(Type.string, null);
			return new ConstVal(ConstVal.string, token.value);
		} else if(token.type == Type.id) {
			Token next = tokens[p + 1];
			if(next.type == Type.symbol && "(".equals(next.value)) {
				return invoke(scope);
			} else {
				match(Type.id, null);
				return findVar(scope, token.value);
			}
		} else {
			throw new Exception("Unexpected Token " + token.toString());
		}
	}
	
	private InvokeExpression invoke(Scope scope) throws Exception {
		Token token = match(Type.id, null);
		match(Type.symbol, "(");
		Function function = global.functions.get(token.value);
		InvokeExpression statement = new InvokeExpression(function);
		while(true) {
			token = tokens[p];
			if(token.type == Type.number) {
				ConstVal constVal = new ConstVal(ConstVal.number, token.value);
				statement.put(constVal);
				p++;
			} else if(token.type == Type.string) {
				ConstVal constVal = new ConstVal(ConstVal.string, token.value);
				statement.put(constVal);
				p++;
			} else if(token.type == Type.id) {
				if(tokens[p + 1].type == Type.symbol && "(".equals(tokens[p + 1].value)) {
					InvokeExpression invokeExpression = invoke(scope);
					statement.put(invokeExpression);
				} else {
					Variable variable = findVar(scope, token.value);
					statement.put(variable);
					p++;
				}
			} else {
				break;
			}
			token = tokens[p];
			if(!(token.type == Type.symbol && ",".equals(token.value))) {
				break;
			}
			match(Type.symbol, ",");
		}
		match(Type.symbol, ")");
		return statement;
	}
	
	private Token match(Type type, String value) throws Exception {
		Token token = tokens[p++];
		if(token.type != type) {
			throw new Exception("Unexpected Token " + token.toString());
		}
		if(value != null && !value.equals(token.value)) {
			throw new Exception("Unexpected Token " + token.toString());
		}
		return token;
	}
	private Variable findVar(Scope scope, String name) throws Exception {
		if(scope == null) {
			throw new Exception("变量未定义");
		}
		Variable variable = scope.variables.get(name);
		if(variable == null) {
			return findVar(scope.parent, name);
		}
		return variable;
	}
}
