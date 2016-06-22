/**
 *
 */
package com.tap.ilman.ta04.util;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author ilman
 */
public class LexUtility {

	private static boolean isAllDigit(String s) {
		System.out.print(" ..checking.." + s);
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isDigit(s.charAt(i))) {
				System.out.print(" NOT all is digit ");
				return false;
			}
		}
		System.out.print(" ALL is digit \n");
		return true;
	}

	private static boolean isAllLetter(String s) {
		System.out.print(" ..checking.." + s);
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isLetter(s.charAt(i))) {
				System.out.print(" NOT all is Letter ");
				return false;
			}
		}
		System.out.print(" ALL is Letter");
		return true;
	}

	private static Token typeOrOperand(String s) {
		if (isAllDigit(s))
			return Token.OPERAND;
		else
			return Token.SATUAN;
	}
	

	static Queue<Lexeme> getLex3(Param p) {

		int s = 0;
		// int e = 0;
		Queue<Lexeme> lexTable = new LinkedList<Lexeme>();
		LinkedList<Lexeme> ll = (LinkedList<Lexeme>) lexTable;
		// StringBuilder temp = new StringBuilder();

		for (int i = 0; i < p.input.length(); i++) {
			System.out
					.println("---------------------------------------------------------------");

			char c = p.input.charAt(i);
			// e=i;
			System.out.println("s: " + s + "  i: " + i + " substring: "
					+ p.input.substring(s, i));

			if (c == ':' || c == 'x' || c == '+' || c == '-') {
				if (i - s > 0) {
					System.out.println("ADD "
							+ typeOrOperand(p.input.substring(s, i)));
					lexTable.add(new Lexeme(
							typeOrOperand(p.input.substring(s, i)), s, i));
				}
				System.out.println("ADD operator");
				lexTable.add(new Lexeme(Token.OPERATOR, i));
				s = i + 1;
			}

			else if (c == '=') {
				if (i - s > 0) {
					System.out.println("ADD "
							+ typeOrOperand(p.input.substring(s, i)));
					lexTable.add(new Lexeme(
							typeOrOperand(p.input.substring(s, i)), s, i));
				}
				System.out.println("ADD equal");
				lexTable.add(new Lexeme(Token.EQUAL, i));
				s = i + 1;
			} else if (Character.isWhitespace(c)) {
				// lexTable.add(new Lexeme(Token.WHITESPACE, e));
				if (i - s > 0) {
					System.out.println("ADD "
							+ typeOrOperand(p.input.substring(s, i)));
					lexTable.add(new Lexeme(
							typeOrOperand(p.input.substring(s, i)), s, i));
		
				}
				System.out.println("whitespace, trying to peek "
						+ ll.peekLast());
				if (ll.peekLast().getToken() == Token.WHITESPACE) {
					System.out.println("...merging.." + ll.peekLast());
					ll.getLast().setEnd(i + 1);
				} else {
					System.out.println(" ADD "
							+ new Lexeme(Token.WHITESPACE, i)
							+ " <---first whittespace ");
					lexTable.add(new Lexeme(Token.WHITESPACE, i));
				}
				s = i + 1;

			} else if (c == ',') {
				if (i - s > 0) {
					System.out.println("adding "
							+ typeOrOperand(p.input.substring(s, i)));
					lexTable.add(new Lexeme(
							typeOrOperand(p.input.substring(s, i)), s, i));
				}
				System.out.println("adding commaa");
				lexTable.add(new Lexeme(Token.COMMA, i));
				s = i + 1;
			} else if (c == '.') {
				if (i - s > 0) {
					System.out.println("adding "
							+ typeOrOperand(p.input.substring(s, i)));
					lexTable.add(new Lexeme(
							typeOrOperand(p.input.substring(s, i)), s, i));
				}

				System.out.println("adding dot");
				lexTable.add(new Lexeme(Token.TITIK, i));
				s = i + 1;
			} else if (c == '(') {
				if (i - s > 0) {

					System.out.println("adding "
							+ typeOrOperand(p.input.substring(s, i)));
					lexTable.add(new Lexeme(
							typeOrOperand(p.input.substring(s, i)), s, i));

				}
				System.out.println("adding kurungbuka");
				lexTable.add(new Lexeme(Token.KURUNGBUKA, i));

				s = i + 1;
			} else if (c == ')') {
				if (i - s > 0) {
					System.out.println("adding "
							+ typeOrOperand(p.input.substring(s, i)));
					lexTable.add(new Lexeme(
							typeOrOperand(p.input.substring(s, i)), s, i));
				}
				System.out.println("adding kurungtutup");
				lexTable.add(new Lexeme(Token.KURUNGTUTUP, i));
				s = i + 1;
			} else if (Character.isDigit(c)) {
				if (i - s > 0) {
					if (isAllDigit(p.input.substring(s, i)))
						continue;
					if (!isAllDigit(p.input.substring(s, i))
							&& (c == 2 || c == 3)
							&& (Converter.isPossiblyPowered(p.input.substring(s,
									i)))) {

						continue;
					}

					if (!isAllDigit(p.input.substring(s, i))) {
						System.out
								.println("digit, but before me aren't, adding "
										+ typeOrOperand(p.input.substring(s, i)));
						lexTable.add(new Lexeme(typeOrOperand(p.input.substring(
								s, i)), s, i));
						s = i;
					}
				}

			} else if (Character.isLetter(c)) {

				if (i - s > 0) {
					if (isAllLetter(p.input.substring(s, i)))
						continue;

					if (!isAllLetter(p.input.substring(s, i))) {
						System.out
								.println("letter, but before me aren't, adding "
										+ typeOrOperand(p.input.substring(s, i)));
						lexTable.add(new Lexeme(typeOrOperand(p.input.substring(
								s, i)), s, i));
						s = i;
					}
				}
			} else {
				if (i - s > 0) {
					System.out.println("adding "
							+ typeOrOperand(p.input.substring(s, i)));
					lexTable.add(new Lexeme(
							typeOrOperand(p.input.substring(s, i)), s, i));
				}
				System.out.println("adding unknown");
				lexTable.add(new Lexeme(Token.UNKNOWN, i));
				s = i + 1;
			}
		}
		System.out.println("OUTSIDE LOOP");

		if (p.input.length() - s > 0) {

			System.out.println("adding LAST "
					+ typeOrOperand(p.input.substring(s, p.input.length())));
			lexTable.add(new Lexeme(typeOrOperand(p.input.substring(s,
					p.input.length())), s, p.input.length()));
			// s = 0;
			// temp = new StringBuilder();
		}

		lexTable = lexPrinter2(lexTable, p);
		return lexTable;
	}

	static Queue<Lexeme> lexPrinterMerged(Queue<Lexeme> lexTable, Param p) {
		Queue<Lexeme> m = new LinkedList<Lexeme>();

		System.out.println("---LEX--table--for " + p.input);
		int i = 1;
		System.out.println("size is: " + lexTable.size());
		while (!lexTable.isEmpty()) {
			Lexeme obj = lexTable.remove();
			m.add(obj);
			System.out.print(i + ") " + obj.getToken() + "\t");

			if (obj instanceof TypedLexeme) {
				System.out.println(((TypedLexeme) obj).toString());
			} else
				System.out.println("index: " + obj.getStart() + " to "
						+ obj.getEnd());
			i++;
		}
		System.out.println("DONE MERGING LEXEME");
		return m;
	}

	static Queue<Lexeme> lexPrinter2(Queue<Lexeme> lexTable, Param p) {
		Queue<Lexeme> m = new LinkedList<Lexeme>();

		System.out.println("---LEX--table--for " + p.input);
		int i = 1;
		while (!lexTable.isEmpty()) {
			Lexeme obj = lexTable.remove();
			m.add(obj);
			System.out.print(i + ") " + obj.getToken() + "\t");
			System.out
					.print(p.input.substring(obj.getStart(), obj.getEnd()) + "\t");
			System.out.println("index: " + obj.getStart() + " to "
					+ obj.getEnd());
			i++;
		}
		System.out.println("DONE collecting LEXEME");
		return m;
	}

}