package com.tap.ilman.ta04.util;

import java.util.LinkedList;
import java.util.Queue;



public class SintakProcessor {

	// static final int iIsi = 0;
	// static final int iToken = 1;
	// static final int iStart = 2;
	// static final int iEnd = 3;
	// private static final int iIsNegative = 4;
	// static final int iKategori = 5;
	// private static final int iType = 6;
	// private static final int iJMDMode = 7;

	// private static final int iIsCurrency = 7;
	// private static final int iRepClock = 8;
	// private static final int iDot = 9;
	// private static final int iDotHour = 10;

	// private static final int iDot = 5;
	private static Boolean allowZeroBefore = false;

	private static boolean isItPermissibleOperator(Token m) {
		if (m == Token.OPERATOR || m == Token.KURUNGBUKA || m == Token.KURUNGTUTUP) {
			return true;
		}
		return false;
	}

	static boolean cekZero(String s) {
		if (allowZeroBefore && s.startsWith("0"))
			return false;
		else
			return true;
	}

	static SemanticParam startAnalysisPT2(Queue<Lexeme> lexTable, Param p) {
		// mantikTable
		Queue<Lexeme> m = new LinkedList<Lexeme>();
		// start FSM
		SSM state = SSM.SYNTAX_START;
		Lexeme lexeme = null, lastLexeme = new Lexeme(Token.OPERAND, 0, 0);

		// StringBuilder merging = new StringBuilder();
		TypedLexeme tLex = null;
		ErrMes em = new ErrMes();
		int lvl = 0;
		String pesanError = "";
		while (!lexTable.isEmpty() && state != SSM.SYNTAX_ERROR) {
			lexeme = lexTable.remove();
			// m.add(lexeme);
			// lastLexeme = (lastLexeme == null) ? lexeme : lastLexeme;

			if (lexeme.getToken() == Token.KURUNGBUKA) {
				lvl++;
			}
			if (lexeme.getToken() == Token.KURUNGTUTUP) {
				lvl--;
			}
			pesanError = state.getErr();
			if (nullIt(state) || tLex == null) {
				tLex = new TypedLexeme(Token.OPERAND, 0, 0);
			}
			System.out.print("\ncurrent STATE " + state + " transition:" + lexeme);
			state = state.tokenSelanjutnya(m, lexeme, lastLexeme, tLex, p, lvl, em);
			System.out.print(" become " + state);
		}
		// System.out.println("DONE checking SYNTAX last state: "+state );
		LexUtility.lexPrinterMerged(m, p);
		if (!isTrueEnd(state) || state == SSM.SYNTAX_ERROR) {
			System.out.println(" "+p.input+" sintak masih SALAH "+em);
			
			return new SemanticParam(p, false, m, em);
		} else {
			System.out.println("sintak BENAR mulai analisa semantik");
			LexUtility.lexPrinterMerged(m, p);
						
			return new SemanticParam(p, true, m, em);
		}
	}

	private static boolean nullIt(SSM a) {
		switch (a) {
		case SYNTAX_EQUAL:
		case SYNTAX_OPERATOR:
		case SYNTAX_OPEN_PARENTHESIS:
		case SYNTAX_CLOSE_PARENTHESIS:

			return true;
		default:
			return false;
		}

	}

	private static boolean isTrueEnd(SSM a) {
		switch (a) {
		case SYNTAX_FINAL_JMD_TYPE:
		case SYNTAX_FINAL_UNTYPED_OPERAND:
		case SYNTAX_FINAL_UNTYPED_OPERAND_AFTER_COMMA:
		case SYNTAX_FINAL_UNTYPED_OPERAND_AFTER_DOT:
		case SYNTAX_FINAL_AFTERTYPE:
		case SYNTAX_FINAL_RP_OPERAND:
		case SYNTAX_FINAL_RP_OPERAND_AFTER_COMMA:
		case SYNTAX_FINAL_RP_OPERAND_AFTER_DOT:
			return true;
		default:
			return false;
		}

	}

	// static parts
	private static String statusKurung(int level) {
		if (level < 0)
			return "jawabanmu kelebihan kurung tutup";
		if (level > 0)
			return "jawabanmu kelebihan kurung buka";
		return "";
	}

	static void simpleErrSet(ErrMes err, Lexeme lexeme) {
		err.setStart(lexeme.getStart());
		err.setEnd(lexeme.getEnd());
	}


}

class ErrMes {
	private String mess = "";
	private int start;
	private int end;

	public static ErrMes creatErr(String msg, int s, int e) {
		ErrMes Er = new ErrMes();
		Er.setMess(msg);
		Er.setStart(s);
		Er.setEnd(e);
		return Er;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getMess() {
		return mess;
	}

	@Override
	public String toString() {
		return "ErrMes [mess=" + mess + ", start=" + start + ", end=" + end + "]";
	}

	public void setMess(String mess) {
		this.mess = mess;
	}
}

class SemanticParam{
	private Param p;
	private boolean Hasil ;
	private Queue<Lexeme> q;
	private ErrMes em ;
	
	public SemanticParam(Param p, boolean hasil, Queue<Lexeme> q, ErrMes em) {
		super();
		this.p = p;
		this.Hasil = hasil;
		this.q = q;
		this.em = em;
	}
	public Param getInput() {
		return p;
	}
	public  SemanticParam setInput(Param input) {
		this.p = input;
		return this;
	}
	public boolean isHasilSintakSudahBenar() {
		return Hasil;
	}
	public SemanticParam setHasil(boolean hasil) {
		Hasil = hasil;
		return this;
	}
	public Queue<Lexeme> getQ() {
		return q;
	}
	public SemanticParam setQ(Queue<Lexeme> q) {
		this.q = q;
		return this;
	}	
}