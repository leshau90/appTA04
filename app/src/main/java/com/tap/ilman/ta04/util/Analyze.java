package com.tap.ilman.ta04.util;

import java.util.Queue;

/**
 * Created by Lenovo on 3/4/2015.
 */
public class Analyze {

	public static void justOne(String s) {
		System.out.println("input is "+s);
		
		Param in = new Param().setInput(s);
				
		Queue<Lexeme> m = LexUtility.getLex3(in);
		SintakProcessor.startAnalysisPT2(m,in);
		
		//StringBuilder sb = new StringBuilder();
//		PExpr Jawaban = MantikProcessor.createExpr((Queue) SintakProcessor
//				.startAnalysisP(SintakProcessor.OperandMerge(LexUtility
//						.getLex(s)))[1]);

		// System.out.println(Jawaban);

//		System.out.println(Jawaban.printTree(0));
//
//		System.out.println(Jawaban);

	}



//	public static String isItTrue(String Jawaban, String tulisanKunci) {
//		StringBuilder sb = new StringBuilder();
//		System.out.println("~~~~~~Kunci Jawaban~~~~~~~~");
//		PExpr Kunci = MantikProcessor.createExpr((Queue) SintakProcessor
//				.startAnalysisP(LexUtility.getLex2(tulisanKunci))[1]);
//		System.out.println("~~~~Kunci Jawaban~~~Selesai~~~~~");
//		// sb.append("sudah membuat objek kunci " + Kunci);
//		Object[] HasilAnalisaSintaktis = SintakProcessor
//				.startAnalysisP(LexUtility.getLex2(Jawaban));
//
//		if (!(boolean) HasilAnalisaSintaktis[0]) {
//			// return false;
//
//		} else {
//
//			PExpr MJawaban = MantikProcessor
//					.createExpr((Queue) HasilAnalisaSintaktis[1]);
//			sb.append("\nini akan di cek jawaban : " + MJawaban).append(
//					"\n dan kuncinya adalah : " + Kunci);
//			MJawaban.cekKurungBerlebih();
//			Kunci.cekKurungBerlebih();
//			if (MJawaban.equals(Kunci)) {
//				// return true;
//				sb.append("\nkunci dan jawaban cocok...");
//			} else {
//				// return false;
//				sb.append("\njawaban salah coba baca soal lagi...");
//			}
//		}
//		return sb.toString();
//	}

}
