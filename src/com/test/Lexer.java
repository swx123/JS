package com.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.test.entity.Token;
import com.test.entity.Type;

public class Lexer {

	private String content;
	private int p = 0;
	
	public Lexer(String content) {
		this.content = content;
	}
	
	public Lexer(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			fis.read(buffer);
			content = new String(buffer);
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Lexer lexer = new Lexer(new File("1.js"));
		Token[] lex = lexer.lex();
		for(Token token : lex) {
			System.out.println(token.toString());
		}
	}
	
	public Token[] lex() {
		ArrayList<Token> list = new ArrayList<Token>();
		int state = 0;
		StringBuilder sb = new StringBuilder();
		while(p < content.length()) {
			char c = content.charAt(p);
			switch(state) {
				case 0:// 初始
					if(isBlank(c)) {
						p++;
					} else if(isChar(c)) {
						state = 1;
					} else if(isNumber(c)) {
						state = 2;
					} else if(c == '"') {
						state = 4;
						p++;
					} else {
						sb.append(c);
						if(p < content.length() - 1) {
							char nc = content.charAt(p + 1);
							if((c == '=' && nc == '=')
									|| (c == '>' && nc == '=')
									|| (c == '<' && nc == '=')
									|| (c == '&' && nc == '&')
									|| (c == '|' && nc == '|')) {
								sb.append(nc);
								p++;
							}
						}
						Token token = new Token(Type.symbol, sb.toString());
						list.add(token);
						sb.setLength(0);
						p++;
					}
					break;
				case 1:// 标记符
					if(isChar(c) || isNumber(c)) {
						sb.append(c);
						p++;
					} else {
						String str = sb.toString();
						sb.setLength(0);
						Token token = null;
						if("var".equals(str)) {
							token = new Token(Type.var);
						} else if("function".equals(str)) {
							token = new Token(Type.function);
						} else {
							token = new Token(Type.id, str);
						}
						list.add(token);
						state = 0;
					}
					break;
				case 2:// 数字 小数点前
					if(isNumber(c)) {
						sb.append(c);
						p++;
					} else if(c == '.') {
						state = 3;
						sb.append(c);
						p++;
					} else {
						Token token = new Token(Type.number, sb.toString());
						sb.setLength(0);
						list.add(token);
						state = 0;
					}
					break;
				case 3:// 小数点后
					if(isNumber(c)) {
						sb.append(c);
						p++;
					} else {
						Token token = new Token(Type.number, sb.toString());
						sb.setLength(0);
						list.add(token);
						state = 0;
					}
					break;
				case 4:// 字符串
					if(c == '"') {
						Token token = new Token(Type.string, sb.toString());
						sb.setLength(0);
						list.add(token);
						state = 0;
					} else {
						sb.append(c);
					}
					p++;
					break;
			}
		}
		list.add(new Token(Type.end));
		Token[] tokens = new Token[list.size()];
		list.toArray(tokens);
		return tokens;
	}
	
	public boolean isNumber(char c) {
		return c >= '0' && c <= '9';
	}
	
	public boolean isChar(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}
	
	public boolean isBlank(char c) {
		return c == '\t' || c == '\n' || c == '\t' || c == '\r' || c == ' ';
	}
}
