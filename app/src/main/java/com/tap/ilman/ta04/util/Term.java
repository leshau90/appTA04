package com.tap.ilman.ta04.util;

public abstract class Term {
	private TokenM tipe = TokenM.ADDER;

	// private String clue = "";
	@Override
	abstract public String toString();

	abstract public String printTree(int spacelvl);

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		// result = prime * result + ((Clue == null) ? 0 : Clue.hashCode());
		result = prime * result + ((tipe == null) ? 0 : tipe.hashCode());
		return result;
	}

	public TokenM getToken() {
		return tipe;
	}

	public String getStringifiedToken() {
		if (tipe == TokenM.ADD) {
			return "+";
		}
		if (tipe == TokenM.ADDER) {
			return "+";
		}

		else if (tipe == TokenM.SUBTRACT) {
			return "-";
		} else if (tipe == TokenM.SUBTRACTOR) {
			return "-";
		} else if (tipe == TokenM.FACTOR) {
			return "x";
		} else if (tipe == TokenM.DENUMERATOR) {
			return ":";
		}

		else
			return "";
	}

	public void setToken(TokenM to) {
		this.tipe = to;
	}

	@Override
	public abstract boolean equals(Object obj);

	public abstract Term addParenthesizedExpr(Term t, TokenM k);

	// public abstract PExpr promoteMe();
	public abstract boolean cekKurungBerlebih();

	public abstract void setClue(String clue);
}

class SimpleTerm extends Term {
	private float value;
	private TypedLexeme valueLoc;
	private Lexeme operatorLoc;

	public TypedLexeme getValueLoc() {
		return valueLoc;
	}

	public void setValueLoc(TypedLexeme valueLoc, PExpr exp) {
		// this.value

		this.valueLoc = valueLoc;
		if (valueLoc.getKategori() != Kategori.jmd)
			this.value = Float.parseFloat(((exp.getInputString().input)
					.substring(valueLoc.getStart(), valueLoc.getEnd()))
					.replace(".", "").replace(',', '.'));
		else
			valueLoc.setJMDValues(exp.getInputString());
	}

	public Lexeme getOperatorLoc() {
		return operatorLoc;
	}

	public void setOperatorLoc(Lexeme operatorLoc) {
		this.operatorLoc = operatorLoc;
	}

	public float getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		if (this.getToken() == TokenM.ADDER)
			return "+" + this.getValue();
		else if (this.getToken() == TokenM.SUBTRACTOR)
			return "-" + this.getValue();
		else
			return String.valueOf(this.getValue());
	}

	@Override
	public String printTree(int spacelvl) {
		return MantikProcessor.printBranch(spacelvl) + Float.toString(this.value)
				+ " " + this.getToken().name()+" lexemeValue: "+ this.valueLoc  + "\n";
	}

	@Override
	public void setClue(String clue) {
	
	}

	@Override
	public boolean equals(Object obj) {
	
		// simple is always trying to be simple
		if (obj instanceof SimpleTerm
				&& this.getToken() == ((SimpleTerm) obj).getToken()) {
			boolean samaNilai = this.getValue() == ((SimpleTerm) obj)
					.getValue();
			boolean samaKategori = this.getValueLoc().getKategori() == ((SimpleTerm) obj)
					.getValueLoc().getKategori();
			boolean samaSatuan = this.getValueLoc().getSatuan() == ((SimpleTerm) obj)
					.getValueLoc().getSatuan();
			SimpleTerm j = (SimpleTerm) obj;

			// simple equalizer, where everything is the same
			if (samaNilai && samaKategori && samaSatuan) {
				return true;
			}

			else if (samaKategori) {
				if (this.getValueLoc().getKategori() != Kategori.unknown
						|| this.getValueLoc().getKategori() != Kategori.unknown) {
					if (this.getValueLoc().getKategori() == Kategori.jmd) {
						if (this.getValueLoc().bijectiveEqualJMD(
								j.getValueLoc())) {
							return true;
						} // equlize then convert
						else if (this.getValueLoc().conversionJMDEqual(
								j.getValueLoc())) {
							return true;
						} else
							return false;

					}
					// not a term with suboperand, term ini memiliki satuan
					else {
						if (Converter.converse(
								this.getValueLoc().getKategori(), this
										.getValueLoc().getSatuan(), this
										.getValue()) == Converter.converse(j
								.getValueLoc().getKategori(), j.getValueLoc()
								.getSatuan(), j.getValue()))

							return true;
						else
							return false;
					}
					// untypedOperand
				} else {
					if (this.getValue() == j.getValue())
						return true;
					else
						return false;
				}

			}// tidak sama katagori berarti salah
			else
				return false;
			// tidak sama tipe objek nya
		} else
			return false;
	}

	@Override
	public Term addParenthesizedExpr(Term t, TokenM k) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean cekKurungBerlebih() {
		return true;
	}
}