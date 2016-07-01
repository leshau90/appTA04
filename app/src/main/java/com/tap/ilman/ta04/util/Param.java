package com.tap.ilman.ta04.util;

public class Param {
	public String input;
	public Param setInput(String in){
		this.input  = in;
		return this;
	}

	static public Param[] getKeyAsParameterArray (String[]  s){
		Param[] f = new Param[s.length];

		for (int i=0;i<s.length;i++){
			System.out.println("Param.getKeyAsParameterArray: string to param: "+ s[i]);
			f[i]= new Param().setInput(s[i]);
		}
		return f;
	}

	@Override
	public String toString() {

		return input;
	}
}
